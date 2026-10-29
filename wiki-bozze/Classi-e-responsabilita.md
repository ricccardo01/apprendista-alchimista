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

## Package `grafica`

### `Main`

Avvia l'applicazione JavaFX e apre la finestra principale del gioco.

## Test

`ElementoTest` controlla il ciclo dei vantaggi, gli svantaggi, lo scontro tra elementi uguali e il comportamento di Neutro.
