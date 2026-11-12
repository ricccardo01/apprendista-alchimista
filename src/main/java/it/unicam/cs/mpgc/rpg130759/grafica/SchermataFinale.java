package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import it.unicam.cs.mpgc.rpg130759.modello.StatoPartita;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * La fine della partita: dice se l'alchimista ha vinto o fino a dove è arrivato
 * e permette di tornare al menu.
 */
public class SchermataFinale {
    private final VBox radice = new VBox(Aspetto.SPAZIO);

    /**
     * @param partita la partita finita, vinta o persa
     * @param alMenu cosa fare quando il giocatore torna al menu
     */
    public SchermataFinale(Partita partita, Runnable alMenu) {
        Objects.requireNonNull(partita);
        Objects.requireNonNull(alMenu);
        String nome = partita.getAlchimista().getNome();

        Label titolo = new Label();
        Label descrizione = new Label();
        if (partita.getStato() == StatoPartita.VINTA) {
            titolo.setText("Vittoria");
            descrizione.setText(nome + " ha superato tutti i piani del laboratorio.");
        } else {
            titolo.setText("Sconfitta");
            descrizione.setText("Il viaggio di " + nome + " si ferma al piano " + partita.getPiano()
                    + ", nella zona " + Aspetto.nomeDi(partita.getZona()) + ".");
        }
        titolo.setStyle(Aspetto.STILE_TITOLO);
        descrizione.setStyle(Aspetto.STILE_TESTO);

        Button menu = new Button("Torna al menu");
        menu.setOnAction(evento -> alMenu.run());

        radice.getChildren().addAll(titolo, descrizione, menu);
        radice.setAlignment(Pos.CENTER);
    }

    /**
     * @return il contenuto da mettere nella finestra
     */
    public Parent getRadice() {
        return radice;
    }
}
