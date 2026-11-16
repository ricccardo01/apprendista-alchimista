# Persistenza

## Quando salvo

Salvo la partita solo tra un piano e l'altro, mai durante uno scontro. Così nel file bastano pochi dati e non devo salvare il nemico, il registro o i turni. Se chiudi il gioco a metà scontro, quando riprendi ricominci lo scontro di quel piano.

## Cosa salvo

Il file contiene cinque valori:

| Dato | Perché |
|---|---|
| `nome` | il nome scelto per l'alchimista |
| `vita` | i punti vita dell'alchimista |
| `reagenti` | i reagenti dell'alchimista |
| `piano` | il piano da cui riprendere |
| `seme` | il numero da cui nasce il generatore casuale dei nemici |

Esempio di file:

```json
{
  "nome": "Mira",
  "vita": 63,
  "reagenti": 20,
  "piano": 6,
  "seme": -4962768465676381896
}
```

## Il seme

In ogni zona incontro i tre nemici una volta sola, in un ordine mescolato a inizio partita. Se dopo il caricamento il gioco mescolasse di nuovo, potrei ritrovare un nemico già sconfitto. Potevo salvare l'ordine dei nove nemici; ho preferito salvare il seme. Un generatore casuale (`Random`) creato con lo stesso seme produce sempre la stessa sequenza di numeri, quindi il generatore dei nemici mescola le zone nello stesso ordine di prima. Mi basta un numero.

## Come è organizzato il codice

- `GestoreSalvataggi` è l'interfaccia con `salva(partita)` e `carica()`. `carica()` restituisce un `Optional<Partita>`, un contenitore che può essere vuoto: chi lo usa deve gestire anche il caso "nessun salvataggio".
- `DatiPartita` è la copia semplice della partita da scrivere su file. Separa il formato del file dalle classi del modello: se aggiungo un campo al modello che non serve salvare, il file non cambia.
- `SalvataggioJson` scrive e legge il file con la libreria Gson, che trasforma un oggetto Java in testo JSON e viceversa. Gradle la scarica da solo, come JavaFX e JUnit.

Il modello non sa niente del salvataggio: non importa Gson e non conosce il package `persistenza`. Le dipendenze vanno in un solo verso, da `persistenza` verso `modello`.

## Dove sta il file

Nella cartella dell'utente, in `.apprendista-alchimista/salvataggio.json`. Il percorso lo costruisco con `System.getProperty("user.home")`, quindi funziona su Windows, macOS e Linux senza percorsi scritti a mano. Se la cartella manca, la creo al primo salvataggio.

## Se il salvataggio manca o è rovinato

`carica()` restituisce un `Optional` vuoto se il file non c'è, se è vuoto, se non è un JSON valido o se contiene valori impossibili (per esempio 500 punti vita). Il gioco parte comunque e si comincia una partita nuova. I valori li controllano i costruttori di `Alchimista` e `Partita`, gli stessi che usa il gioco: una partita caricata rispetta le stesse regole di una partita nuova.

## Estensioni future

- Per salvare in un altro formato o su un server basta una nuova classe che implementa `GestoreSalvataggi`.
- Quando aggiungerò livelli, formule imparate e reliquie, li aggiungerò a `DatiPartita`.
- Più salvataggi con un pulsante "Salva": `SalvataggioJson` riceve già il file dal costruttore, quindi potrei creare un gestore per ogni slot.
