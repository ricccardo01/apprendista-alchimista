# Persistenza

## Quando salvo

Salvo la partita solo tra un piano e l'altro, mai durante uno scontro. Così nel file bastano pochi dati e non devo salvare il nemico, il registro, i turni o gli effetti (Veleno, Corazza di Sale...): quando si cambia piano l'alchimista li perde comunque. Se chiudi il gioco a metà scontro, quando riprendi ricominci lo scontro di quel piano.

Il salvataggio è automatico, non c'è un pulsante "Salva". Avviene in due momenti:

- quando inizio una partita nuova, così sostituisce subito quella vecchia;
- ogni volta che l'alchimista scende al piano successivo.

Quando la partita finisce, vinta o persa, il salvataggio viene cancellato: non ha senso continuare una partita finita, e chi perde ricomincia da capo. Lo cancello appena lo scontro finisce, non quando il giocatore preme "Continua", altrimenti basterebbe chiudere la finestra dopo una sconfitta per riprovare il piano.

Nel menu il pulsante "Continua" compare solo se `carica()` trova una partita.

Se il file non si riesce a scrivere (per esempio la cartella è protetta), il gioco mostra un avviso e si continua a giocare.

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

- `GestoreSalvataggi` è l'interfaccia con `salva(partita)`, `carica()` e `cancella()`. La grafica conosce solo lei; il file JSON lo sceglie `Main`, in una riga sola. `carica()` restituisce un `Optional<Partita>`, un contenitore che può essere vuoto: chi lo usa deve gestire anche il caso "nessun salvataggio".
- `DatiPartita` è la copia semplice della partita da scrivere su file. Separa il formato del file dalle classi del modello: se aggiungo un campo al modello che non serve salvare, il file non cambia.
- `SalvataggioJson` scrive e legge il file con la libreria Gson, che trasforma un oggetto Java in testo JSON e viceversa. Gradle la scarica da solo, come JavaFX e JUnit.

Il modello non sa niente del salvataggio: non importa Gson e non conosce il package `persistenza`. Le dipendenze vanno in un solo verso, da `persistenza` verso `modello`.

## Dove sta il file

Nella cartella dell'utente, in `.apprendista-alchimista/salvataggio.json`. Il percorso lo costruisco con `System.getProperty("user.home")`, quindi funziona su Windows, macOS e Linux senza percorsi scritti a mano. Se la cartella manca, la creo al primo salvataggio.

## Se il salvataggio manca o è rovinato

`carica()` restituisce un `Optional` vuoto se il file non c'è, se è vuoto, se non è un JSON valido o se contiene valori impossibili (per esempio 500 punti vita). Il gioco parte comunque e si comincia una partita nuova. I valori li controllano i costruttori di `Alchimista` e `Partita`, gli stessi che usa il gioco: una partita caricata rispetta le stesse regole di una partita nuova.

## Un limite noto

Nei piani 4, 8 e 12, per ora, il nemico è uno dei tre della zona pescato a caso al momento, con lo stesso generatore casuale che i nemici usano per scegliere le mosse. Dopo un caricamento quel generatore riparte dal seme, quindi in quei piani può uscire un nemico diverso da quello di prima. Nei piani normali invece il nemico è sempre lo stesso: l'ho controllato con una prova automatica su 500 partite. Non l'ho corretto perché in quei piani arriveranno i guardiani, che sono fissi. La regola che ne ho ricavato: quello che è casuale e deve restare uguale dopo un caricamento va deciso dal seme all'inizio della partita.

## Estensioni future

- Per salvare in un altro formato o su un server basta una nuova classe che implementa `GestoreSalvataggi`.
- Quando aggiungerò livelli, formule imparate e reliquie, li aggiungerò a `DatiPartita`.
- Più salvataggi con un pulsante "Salva": `SalvataggioJson` riceve già il file dal costruttore, quindi potrei creare un gestore per ogni slot.
