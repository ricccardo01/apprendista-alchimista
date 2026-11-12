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

Il nome lo sceglie chi gioca e deve avere da 1 a 10 caratteri. Il controllo è nel costruttore: un alchimista con un nome vuoto o troppo lungo non viene creato. La grafica impedisce già di scrivere più di 10 caratteri, ma la regola sta nel modello perché vale per qualsiasi interfaccia.

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

All'inizio dello scontro chi gioca non sa di che elemento è il nemico. Lo scopre dopo la prima mossa del nemico: `isElementoNemicoRivelato()` diventa vero appena il nemico ha mosso. Prima si può solo intuire, guardando quanti danni fanno le proprie fiale. Ho messo questa regola nel combattimento e non nella grafica perché fa parte del gioco: così vale per qualsiasi interfaccia e la posso controllare con un test.

Il combattimento scrive nel registro una frase per ogni mossa, per esempio "Verme Cieco usa Morso: 5 danni a Alchimista". Calcolo il danno come differenza tra la vita del bersaglio prima e dopo la mossa, così funziona con qualsiasi azione senza cambiare l'interfaccia `Azione`. La grafica legge il registro con `getRegistro()`, che restituisce una lista non modificabile. Le frasi sono semplici stringhe e non dipendono da JavaFX.

Il combattimento conta anche le statistiche dello scontro, che la grafica mostra alla fine: turni giocati, danni inflitti, danni subiti e reagenti spesi. I danni sono la vita persa davvero: se una fiala da 20 colpisce un nemico che ha solo 10 punti vita, contano 10. Le conto nel modello perché è lì che succedono i colpi, e così posso controllarle con un test.

### `Zona` (enum)

Le tre zone del laboratorio: Cantine, Laboratorio e Fornace. Ogni zona ha 4 piani. `delPiano(piano)` dice a quale zona appartiene un piano e `posizioneNellaZona(piano)` dice se è il primo, il secondo, il terzo o il quarto piano della zona. Faccio questo conto in un posto solo, così le altre classi non ripetono la divisione.

Calcolo l'ultimo piano moltiplicando il numero delle zone per i piani di ogni zona. Se aggiungo una quarta zona all'enum, il gioco arriva da solo fino al piano 16.

### `GeneratoreNemici` (interfaccia)

Ha un solo metodo, `creaNemico(piano)`, che restituisce il nemico di un piano. `Partita` conosce solo questa interfaccia e non sa come nascono i nemici. Se voglio regole diverse (un'altra zona, una modalità più difficile) scrivo una nuova implementazione e passo quella alla partita, senza modificare `Partita`. Lo stesso vale nei test: gli passo un generatore di prova che crea nemici prevedibili.

### `GeneratoreNemiciPerZona`

Contiene i numeri e le mosse dei nove nemici comuni. Ogni nemico ha una mossa gratuita e una più forte che costa 6 reagenti. Nelle zone più profonde i nemici hanno più vita e colpiscono più forte. I valori sono provvisori e li sistemerò nel bilanciamento.

Nei primi tre piani di ogni zona incontro tutti e tre i nemici della zona, uno per principio, ma in un ordine diverso a ogni partita. Ho scelto così per avere partite più varie: in ogni zona devo usare tutti e tre gli elementi. Per farlo, all'inizio della partita il generatore mescola la lista dei tre principi di ogni zona con `Collections.shuffle` e la salva in una `EnumMap`, una mappa che ha come chiavi i valori di un enum. Per ora anche il quarto piano di ogni zona ha un nemico comune, scelto a caso. Lì arriveranno i guardiani.

Ricevo il `Random` dal costruttore, come in `NemicoComune`, così nei test posso usare un seme fisso.

### `StatoPartita` (enum)

Dice se la partita è in corso, vinta o persa.

### `Partita`

Tiene insieme la partita intera: l'alchimista, il piano attuale e lo scontro in corso. La grafica parlerà solo con questa classe.

`passaAlPianoSuccessivo()` porta l'alchimista al piano dopo. Prima controlla che lo scontro sia vinto e che la partita non sia finita, poi restituisce all'alchimista il 30% della vita massima e tutti i reagenti, e chiede al generatore il nemico del nuovo piano. Senza questo recupero il gioco diventerebbe frustrante dopo pochi piani.

`getStato()` restituisce PERSA se l'alchimista è sconfitto e VINTA se ha battuto il nemico dell'ultimo piano. Per ora l'ultimo piano è il 12. Quando aggiungerò la Camera del Sigillo, la vittoria arriverà al piano 13.

La partita riceve l'alchimista e il generatore dal costruttore, invece di crearli lei. Così dipende da un'interfaccia e non da una classe concreta (principio di inversione delle dipendenze), e quando caricherò un salvataggio potrò passarle un alchimista già esistente.

## Package `grafica`

### `Main`

Avvia l'applicazione JavaFX: apre la finestra e chiede al `Navigatore` di mostrare il menu.

### `Navigatore`

Decide quale schermata mostrare. Le schermate non si conoscono tra loro: ognuna riceve dal navigatore cosa fare quando il giocatore vuole andare avanti (un `Runnable`, oppure un `Consumer<String>` per il menu, che passa il nome scelto). Per esempio, quando si preme "Continua" dopo uno scontro, il navigatore guarda lo stato della partita: se è finita mostra la schermata finale, altrimenti fa scendere l'alchimista al piano dopo e mostra la schermata tra i piani.

La finestra resta sempre la stessa e cambia solo il contenuto (`scena.setRoot(...)`). Per aggiungere una schermata nuova, come il negozio, basta scriverla e collegarla qui, senza toccare le altre.

### `SchermataMenu`

Il titolo del gioco, una casella per il nome dell'alchimista e i pulsanti "Nuova partita" ed "Esci". La casella non accetta più di 10 caratteri; se resta vuota il nome diventa "Alchimista".

### `SchermataTraPiani`

Compare dopo ogni scontro vinto. Dice quale piano è stato superato, quanta vita e quanti reagenti ha recuperato l'alchimista e se sta entrando in una nuova zona. Il recupero lo calcola `Partita`: il navigatore legge la vita prima e dopo il passaggio di piano e passa la differenza alla schermata.

### `SchermataFinale`

Dice se la partita è vinta o persa, e in caso di sconfitta il piano e la zona raggiunti. Il pulsante "Torna al menu" permette di iniziare una nuova partita.

### `Aspetto`

Raccoglie in un posto solo come la grafica mostra le cose del modello: i nomi e i colori degli elementi, i nomi delle zone e gli stili usati da più schermate. Prima colori e nomi erano dentro `SchermataCombattimento`; quando sono serviti anche altrove li ho spostati qui invece di copiarli.

### `SchermataCombattimento`

Mostra uno scontro: il nemico in alto, l'alchimista in basso, il registro dei messaggi a destra e un pulsante per ogni mossa. Nel testo del pulsante c'è il costo della mossa in reagenti, tra parentesi (per esempio "Fiala Ardente (4)"), e le mosse che non si possono pagare restano disattivate (lo controllo con `haReagenti`).

Quando si preme un pulsante, la schermata passa la mossa a `turnoAlchimista` e poi rilegge tutto dal modello: vita, reagenti, registro e se l'elemento del nemico è stato scoperto. Finché non è scoperto il riquadro del nemico ha solo un bordo tratteggiato; dopo prende il colore del suo elemento (Zolfo giallo, Mercurio grigio, Sale azzurrino), e una piccola legenda spiega i colori.

Quando lo scontro finisce, al posto dei pulsanti delle mosse compare un riepilogo: l'esito, le statistiche dello scontro e il pulsante "Continua".

La schermata non contiene regole del gioco. Per fare una versione web o mobile basterebbe riscrivere questa classe, lasciando il modello com'è.

## Test

`ElementoTest` controlla il ciclo dei vantaggi, gli svantaggi, lo scontro tra elementi uguali e il comportamento di Neutro.

`CombattenteTest` controlla danni, cure, sconfitta, reagenti, il moltiplicatore dei colpi (anche l'arrotondamento: metà di 5 fa 3) e il metodo `usa`, compreso il caso in cui i reagenti non bastano. Una classe astratta non si può creare, quindi i test usano `CombattenteDiProva`, una piccola sottoclasse che esiste solo nella cartella dei test.

`AlchimistaTest` controlla lo stato iniziale, lo strumento di partenza, le cinque mosse iniziali, che la lista delle mosse non si possa modificare da fuori e che il nome venga rifiutato se è vuoto o più lungo di 10 caratteri.

`AzioniTest` controlla cosa fanno attacco base, colpo e Distillazione, e che non si possano creare strumenti o colpi con valori non validi.

`NemicoComuneTest` controlla che il nemico usi tutte le sue mosse quando ha i reagenti, solo quelle gratuite quando non li ha, che con lo stesso seme faccia le stesse scelte e che non si possa creare un nemico senza mosse gratuite.

`CombattimentoTest` controlla un turno completo con i messaggi del registro, le statistiche dello scontro, il vantaggio di elemento durante lo scontro, la vittoria (il nemico sconfitto non contrattacca), la sconfitta, il recupero dei reagenti a fine turno, quando si scopre l'elemento del nemico (dopo la sua prima mossa, mai se muore prima di muovere) e i casi vietati: giocare dopo la fine, usare una mossa sconosciuta o una mossa che non si può pagare. Il nemico di prova ha una sola mossa, così so sempre cosa farà.

`ZonaTest` controlla a quale zona appartengono i piani, la posizione dentro la zona e che i piani fuori dalle zone vengano rifiutati.

`GeneratoreNemiciPerZonaTest` prova 20 semi diversi e controlla ogni volta che nei primi tre piani di ogni zona escano tutti e tre i principi. Controlla anche che ogni volta nasca un nemico nuovo con la vita piena e che i nemici della Fornace abbiano più vita di quelli delle Cantine.

`PartitaTest` usa due generatori di prova scritti come lambda: uno crea nemici che cadono al primo colpo, l'altro nemici imbattibili. Controlla l'inizio della partita, che non si possa scendere senza aver vinto, il recupero di vita e reagenti (anche che la vita non superi il massimo), la vittoria all'ultimo piano e la sconfitta.
