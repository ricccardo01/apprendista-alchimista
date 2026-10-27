package it.unicam.cs.mpgc.rpg130759.grafica;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Punto di avvio dell'applicazione: apre la finestra principale del gioco.
 */
public class Main extends Application {

    private static final int LARGHEZZA = 960;
    private static final int ALTEZZA = 600;

    /**
     * Crea e mostra la finestra principale.
     *
     * @param finestra la finestra fornita da JavaFX all'avvio
     */
    @Override
    public void start(Stage finestra) {
        finestra.setTitle("L'Apprendista Alchimista");
        finestra.setScene(new Scene(new StackPane(), LARGHEZZA, ALTEZZA));
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
