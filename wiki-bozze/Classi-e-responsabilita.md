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

È la base comune dell'alchimista e dei nemici: tiene nome, elemento e punti vita. Ho scelto una classe astratta perché nel gioco non esiste un combattente generico, esistono solo personaggi concreti.

La vita cambia solo con `subisciDanno` e `cura`, quindi resta sempre tra 0 e il massimo. Il controllo sta qui e le sottoclassi non lo ripetono. Il costruttore rifiuta nome o elemento null e una vita massima non positiva, così non posso creare un combattente non valido.

### `Alchimista`

È il personaggio del giocatore. Eredita da `Combattente` e aggiunge i reagenti, l'energia che pagano le formule: `haReagenti` dice se bastano, `consumaReagenti` li toglie e `recuperaReagenti` li aggiunge fino al massimo.

Se provo a spendere più reagenti di quelli disponibili ottengo un'eccezione. La grafica dovrà chiedere prima `haReagenti` e disattivare le formule che non posso pagare.

L'alchimista ha elemento Neutro: subisce danno normale da tutti i principi. I valori iniziali (100 di vita, 20 reagenti) sono provvisori e li rivedrò nel bilanciamento.

## Package `grafica`

### `Main`

Avvia l'applicazione JavaFX e apre la finestra principale del gioco.

## Test

`ElementoTest` controlla il ciclo dei vantaggi, gli svantaggi, lo scontro tra elementi uguali e il comportamento di Neutro.

`CombattenteTest` controlla danni, cure, sconfitta e valori non validi. Una classe astratta non si può creare, quindi nel test uso una piccola sottoclasse di prova.

`AlchimistaTest` controlla lo stato iniziale e il consumo e il recupero dei reagenti, compreso il tentativo di spenderne troppi.
