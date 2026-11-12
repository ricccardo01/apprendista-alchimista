package it.unicam.cs.mpgc.rpg130759.grafica;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Punto di avvio dell'applicazione: apre la finestra principale e mostra il menu.
 */
public class Main extends Application {

    private static final int LARGHEZZA = 960;
    private static final int ALTEZZA = 600;

    /**
     * Crea la finestra e affida al navigatore il cambio delle schermate.
     *
     * @param finestra la finestra fornita da JavaFX all'avvio
     */
    @Override
    public void start(Stage finestra) {
        finestra.setTitle("L'Apprendista Alchimista");
        // La scena nasce con un contenuto vuoto, che il navigatore sostituisce subito con il menu.
        Scene scena = new Scene(new Pane(), LARGHEZZA, ALTEZZA);
        new Navigatore(scena).mostraMenu();
        finestra.setScene(scena);
        finestra.show();
    }

    /**
     * Avvia l'applicazione JavaFX.
     *
     * @param args argomenti da riga di comando (non usati)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
