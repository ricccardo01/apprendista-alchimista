# Classi e responsabilità

Qui descrivo le classi del progetto, divise per package. Aggiorno la pagina ogni volta che aggiungo una classe.

## Package `modello`

### `Elemento` (enum)

Rappresenta i tre principi di Paracelso (Zolfo, Mercurio, Sale) più l'elemento neutro, e contiene la regola dei vantaggi:

- Zolfo prevale su Mercurio, Mercurio su Sale, Sale su Zolfo;
- chi prevale fa il doppio del danno, chi colpisce un elemento che lo batte ne fa la metà;
- Neutro fa e subisce sempre danno normale.

Le altre classi usano un solo metodo pubblico, `moltiplicatoreContro(bersaglio)`, che restituisce 2, 0,5 oppure 1. Ho tenuto privato il metodo che decide chi prevale su chi: è un dettaglio interno e posso cambiarlo senza toccare il resto del programma.

Ho messo la regola in un unico posto così il combattimento e le formule chiedono a `Elemento` quanto vale un colpo e non ripetono la logica. Per aggiungere un nuovo elemento basta aggiungere un valore all'enum e il suo caso nello switch; se dimentico il caso, il compilatore me lo segnala.

### `Combattente` (classe astratta)

È la base comune dell'alchimista e dei nemici: tiene nome, elemento, punti vita e reagenti. Ho scelto una classe astratta perché nel gioco non esiste un combattente generico, esistono solo personaggi concreti.

La vita cambia solo con `subisciDanno` e `cura`, i reagenti solo con `consumaReagenti` e `recuperaReagenti`. Così restano sempre tra 0 e il massimo, e le sottoclassi non ripetono i controlli. Il costruttore rifiuta nome o elemento null, una vita massima non positiva e reagenti negativi.

All'inizio avevo messo i reagenti solo nell'alchimista. Poi mi sono accorto che le azioni ricevono un `Combattente` qualsiasi: per far pagare una formula o per far recuperare reagenti con la Distillazione avrei dovuto chiedere "sei un alchimista?" con `instanceof`, e ogni nuovo tipo di combattente mi avrebbe costretto a cambiare quei controlli. Ho spostato i reagenti in `Combattente`. Ora anche un nemico può avere mosse che costano reagenti.

Due metodi collegano il combattente alle azioni:

- `subisciColpo(danno, elemento)` applica il moltiplicatore tra l'elemento del colpo e quello del combattente, poi toglie la vita. Tengo il calcolo qui e le azioni non lo ripetono.
- `usa(azione, bersaglio)` paga il costo dell'azione e poi la esegue. Se i reagenti non bastano lancia un'eccezione e non succede niente, quindi la grafica dovrà chiedere prima `haReagenti`.

Il metodo astratto `getAzioni()` restituisce le mosse che il combattente conosce. Lo dichiaro qui ma non lo scrivo: l'alchimista e i nemici prendono le mosse da posti diversi, quindi ogni sottoclasse lo scrive a modo suo. Il combattimento potrà chiedere le mosse a qualsiasi combattente senza sapere di che tipo è. La lista che restituisco non si può modificare da fuori, così nessuno aggiunge mosse di nascosto.

### `Alchimista`

È il personaggio del giocatore. Eredita da `Combattente` vita e reagenti e aggiunge lo strumento che impugna. Comincia con il Pestello di Legno, elemento Neutro, 100 di vita e 20 reagenti. Con l'elemento Neutro subisce danno normale da tutti i principi. I numeri sono provvisori e li rivedrò nel bilanciamento.

All'inizio conosce cinque mosse: l'attacco con il pestello, la Distillazione e una formula per ogni principio (Fiala Ardente di Zolfo, Nebbia Mercuriale di Mercurio, Polvere Cristallizzante di Sale). Le tre formule costano 4 reagenti e fanno 10 di danno. Avendo un elemento per tipo, posso sfruttare il vantaggio contro qualsiasi nemico fin dal primo piano.

### `Azione` (interfaccia)

Una mossa che un combattente fa nel suo turno: ha un nome, un costo in reagenti e il metodo `esegui(chi, bersaglio)`. La uso sia per l'alchimista sia per i nemici. Per aggiungere una mossa scrivo una nuova classe che implementa `Azione` e il combattimento la userà senza modifiche.

`esegui` non paga il costo: lo paga `Combattente.usa`. Così ogni azione si occupa solo del suo effetto e non devo ripetere il pagamento in ogni classe.

### `Strumento`

L'attrezzo che l'alchimista tiene in mano: nome, danno ed elemento dell'attacco base. Uno strumento senza elemento usa Neutro, così non devo gestire valori null.

### `AttaccoBase`

Il colpo con lo strumento. Non costa niente e usa danno ed elemento dello strumento che riceve nel costruttore.

### `Colpo`

Una mossa che colpisce il bersaglio con un danno di un elemento. Le mosse che cambiano solo nei numeri (nome, costo, danno, elemento) sono oggetti diversi di questa stessa classe e non classi separate: per esempio la Fiala Ardente dell'alchimista o il morso di un nemico.

Prima la classe si chiamava `LancioFiala`. Quando ho scritto i nemici mi sono accorto che anche i loro attacchi funzionano allo stesso modo, ma chiamare "lancio di una fiala" il morso di un verme era sbagliato. Ho rinominato la classe e l'ho riusata invece di scriverne una copia.

### `Distillazione`

Chi la usa salta l'attacco e recupera 8 reagenti e 5 punti vita. Non costa niente e non tocca il bersaglio. Più avanti, quando aggiungerò gli effetti, ridurrà anche il danno del colpo successivo.

### `Nemico` (classe astratta)

Un combattente che il gioco controlla da solo. Tiene la lista delle sue mosse e dichiara il metodo astratto `scegliAzione()`: ogni tipo di nemico decide a modo suo cosa fare nel turno. Il nemico comune sceglie a caso, il Golem di Piombo alternerà attacco e pausa. Per un comportamento nuovo scrivo una sottoclasse e il combattimento continua a chiamare `scegliAzione()` senza modifiche.

Il costruttore rifiuta un nemico senza almeno una mossa gratuita. Se i reagenti finiscono, il nemico ha sempre qualcosa da fare e la scelta della mossa non resta mai senza opzioni.

### `NemicoComune`

A ogni turno sceglie a caso una mossa tra quelle che può pagare. I nove nemici comuni cambiano solo nei numeri e nelle mosse, quindi sono oggetti diversi di questa classe.

Il generatore casuale (`Random`) lo passo dal costruttore invece di crearlo dentro la classe. Nei test uso un `Random` con un seme fisso: con lo stesso seme esce sempre la stessa sequenza, quindi posso controllare il risultato.

### `Combattimento`

Gestisce uno scontro tra l'alchimista e un nemico. Decide l'ordine dei turni, controlla le mosse e dice quando lo scontro è finito. Ho tenuto queste regole fuori dai combattenti e fuori dalla grafica: l'alchimista sa usare una mossa ma non sa quando tocca a lui, e la grafica si limita a passare la mossa scelta dal giocatore. Un'interfaccia web o mobile potrebbe usare questa classe così com'è.

Il metodo `turnoAlchimista(azione)` gioca un turno intero:

1. controlla che lo scontro non sia finito e che l'alchimista conosca la mossa;
2. l'alchimista usa la mossa sul nemico (se i reagenti non bastano, `usa` lancia un'eccezione e il turno non parte);
3. se il nemico resta in piedi, sceglie una mossa con `scegliAzione()` e la usa sull'alchimista;
4. a fine turno tutti e due recuperano 2 reagenti.

L'alchimista muove per primo. Se sconfigge il nemico, il nemico non contrattacca, quindi `isVinto()` controlla solo se il nemico è sconfitto.

Il combattimento scrive nel registro una frase per ogni mossa, per esempio "Verme Cieco usa Morso: 5 danni a Alchimista". Calcolo il danno come differenza tra la vita del bersaglio prima e dopo la mossa, così funziona con qualsiasi azione senza cambiare l'interfaccia `Azione`. La grafica legge il registro con `getRegistro()`, che restituisce una lista non modificabile. Le frasi sono semplici stringhe e non dipendono da JavaFX.

## Package `grafica`

### `Main`

Avvia l'applicazione JavaFX e apre la finestra principale del gioco.

## Test

`ElementoTest` controlla il ciclo dei vantaggi, gli svantaggi, lo scontro tra elementi uguali e il comportamento di Neutro.

`CombattenteTest` controlla danni, cure, sconfitta, reagenti, il moltiplicatore dei colpi (anche l'arrotondamento: metà di 5 fa 3) e il metodo `usa`, compreso il caso in cui i reagenti non bastano. Una classe astratta non si può creare, quindi i test usano `CombattenteDiProva`, una piccola sottoclasse che esiste solo nella cartella dei test.

`AlchimistaTest` controlla lo stato iniziale, lo strumento di partenza, le cinque mosse iniziali e che la lista delle mosse non si possa modificare da fuori.

`AzioniTest` controlla cosa fanno attacco base, colpo e Distillazione, e che non si possano creare strumenti o colpi con valori non validi.

`NemicoComuneTest` controlla che il nemico usi tutte le sue mosse quando ha i reagenti, solo quelle gratuite quando non li ha, che con lo stesso seme faccia le stesse scelte e che non si possa creare un nemico senza mosse gratuite.

`CombattimentoTest` controlla un turno completo con i messaggi del registro, il vantaggio di elemento durante lo scontro, la vittoria (il nemico sconfitto non contrattacca), la sconfitta, il recupero dei reagenti a fine turno e i casi vietati: giocare dopo la fine, usare una mossa sconosciuta o una mossa che non si può pagare. Il nemico di prova ha una sola mossa, così so sempre cosa farà.
