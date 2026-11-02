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

### `Alchimista`

È il personaggio del giocatore. Eredita da `Combattente` vita e reagenti e aggiunge lo strumento che impugna. Comincia con il Pestello di Legno, elemento Neutro, 100 di vita e 20 reagenti. Con l'elemento Neutro subisce danno normale da tutti i principi. I numeri sono provvisori e li rivedrò nel bilanciamento.

### `Azione` (interfaccia)

Una mossa che un combattente fa nel suo turno: ha un nome, un costo in reagenti e il metodo `esegui(chi, bersaglio)`. La uso sia per l'alchimista sia per i nemici. Per aggiungere una mossa scrivo una nuova classe che implementa `Azione` e il combattimento la userà senza modifiche.

`esegui` non paga il costo: lo paga `Combattente.usa`. Così ogni azione si occupa solo del suo effetto e non devo ripetere il pagamento in ogni classe.

### `Strumento`

L'attrezzo che l'alchimista tiene in mano: nome, danno ed elemento dell'attacco base. Uno strumento senza elemento usa Neutro, così non devo gestire valori null.

### `AttaccoBase`

Il colpo con lo strumento. Non costa niente e usa danno ed elemento dello strumento che riceve nel costruttore.

### `LancioFiala`

Una formula che lancia una fiala di un elemento. Le fiale che cambiano solo nei numeri (nome, costo, danno, elemento) sono oggetti diversi di questa stessa classe e non classi separate, per esempio la Fiala Ardente (Zolfo).

### `Distillazione`

Chi la usa salta l'attacco e recupera 8 reagenti e 5 punti vita. Non costa niente e non tocca il bersaglio. Più avanti, quando aggiungerò gli effetti, ridurrà anche il danno del colpo successivo.

## Package `grafica`

### `Main`

Avvia l'applicazione JavaFX e apre la finestra principale del gioco.

## Test

`ElementoTest` controlla il ciclo dei vantaggi, gli svantaggi, lo scontro tra elementi uguali e il comportamento di Neutro.

`CombattenteTest` controlla danni, cure, sconfitta, reagenti, il moltiplicatore dei colpi (anche l'arrotondamento: metà di 5 fa 3) e il metodo `usa`, compreso il caso in cui i reagenti non bastano. Una classe astratta non si può creare, quindi i test usano `CombattenteDiProva`, una piccola sottoclasse che esiste solo nella cartella dei test.

`AlchimistaTest` controlla lo stato iniziale e lo strumento di partenza.

`AzioniTest` controlla cosa fanno attacco base, fiala e Distillazione, e che non si possano creare strumenti o fiale con valori non validi.
