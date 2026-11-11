package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Random;

/**
 * Punto di avvio dell'applicazione: apre la finestra principale del gioco.
 */
public class Main extends Application {

    private static final int LARGHEZZA = 960;
    private static final int ALTEZZA = 600;
    private static final String NOME_ALCHIMISTA = "Apprendista";

    /**
     * Crea e mostra la finestra principale.
     *
     * @param finestra la finestra fornita da JavaFX all'avvio
     */
    @Override
    public void start(Stage finestra) {
        finestra.setTitle("L'Apprendista Alchimista");
        Partita partita = new Partita(new Alchimista(NOME_ALCHIMISTA), new GeneratoreNemiciPerZona(new Random()));
        SchermataCombattimento schermata = new SchermataCombattimento(partita.getCombattimento());
        finestra.setScene(new Scene(schermata.getRadice(), LARGHEZZA, ALTEZZA));
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
