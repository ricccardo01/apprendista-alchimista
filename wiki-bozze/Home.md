# L'Apprendista Alchimista

Questa Wiki documenta il mio progetto d'esame di Metodologie di Programmazione (UNICAM, AA 2025/26).

L'Apprendista Alchimista è un gioco di ruolo a turni scritto in Java, con interfaccia grafica JavaFX. Il protagonista è un apprendista alchimista che scende nelle rovine del laboratorio del suo vecchio maestro. Le rovine sono divise in tre zone (Cantine, Laboratorio, Fornace) e ogni zona termina con un guardiano. Sconfitti i tre guardiani, le loro reliquie aprono la porta della Camera del Sigillo, dove aspetta l'Ouroboros.

Il combattimento si basa sui tre principi dell'alchimia di Paracelso: Zolfo, Mercurio e Sale. Ognuno è forte contro uno degli altri e debole contro il terzo.

## Organizzazione del progetto

Il codice è diviso in tre parti separate, tutte dentro il package `it.unicam.cs.mpgc.rpg130759`:

- `modello`: le regole del gioco, che non dipendono né dalla grafica né dal formato di salvataggio;
- `persistenza`: il salvataggio e il caricamento della partita;
- `grafica`: le finestre dell'interfaccia, realizzate con JavaFX.

Ho scelto questa divisione perché così la logica del gioco si potrebbe riusare anche con un'interfaccia diversa (per esempio web o mobile), cambiando solo la parte grafica.

## Strumenti usati

- Java 25
- Gradle (con Gradle Wrapper) per compilare ed eseguire il progetto
- JavaFX per l'interfaccia grafica
- JUnit per i test
- Git e GitHub per il controllo di versione
