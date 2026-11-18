package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Il menu iniziale: il giocatore continua la partita salvata, se c'è,
 * oppure sceglie il nome dell'alchimista e ne inizia una nuova, oppure esce dal gioco.
 */
public class SchermataMenu {
    private static final String NOME_PREDEFINITO = "Alchimista";
    private static final int LARGHEZZA_CAMPO = 260;

    private final Consumer<String> allaNuovaPartita;
    private final TextField campoNome = new TextField();
    private final VBox radice = new VBox(Aspetto.SPAZIO);

    /**
     * @param partitaSalvata la partita da continuare, oppure vuoto se non c'è nessun salvataggio
     * @param allaContinua cosa fare quando il giocatore continua, riceve la partita salvata
     * @param allaNuovaPartita cosa fare quando il giocatore inizia, riceve il nome scelto
     * @param allUscita cosa fare quando il giocatore esce dal gioco
     */
    public SchermataMenu(Optional<Partita> partitaSalvata, Consumer<Partita> allaContinua,
                         Consumer<String> allaNuovaPartita, Runnable allUscita) {
        Objects.requireNonNull(partitaSalvata);
        Objects.requireNonNull(allaContinua);
        this.allaNuovaPartita = Objects.requireNonNull(allaNuovaPartita);
        Objects.requireNonNull(allUscita);

        Label titolo = new Label("L'Apprendista Alchimista");
        titolo.setStyle(Aspetto.STILE_TITOLO);
        Label sottotitolo = new Label("Scendi nelle rovine del laboratorio del tuo maestro.");
        sottotitolo.setStyle(Aspetto.STILE_TESTO);
        radice.getChildren().addAll(titolo, sottotitolo);

        partitaSalvata.ifPresent(partita -> {
            Button continua = new Button("Continua: " + partita.getAlchimista().getNome()
                    + ", piano " + partita.getPiano());
            continua.setOnAction(evento -> allaContinua.accept(partita));
            radice.getChildren().add(continua);
        });

        campoNome.setPromptText(NOME_PREDEFINITO);
        campoNome.setMaxWidth(LARGHEZZA_CAMPO);
        campoNome.setTextFormatter(new TextFormatter<String>(modifica ->
                modifica.getControlNewText().length() <= Alchimista.LUNGHEZZA_MASSIMA_NOME ? modifica : null));
        campoNome.setOnAction(evento -> iniziaPartita());

        Button nuovaPartita = new Button("Nuova partita");
        nuovaPartita.setOnAction(evento -> iniziaPartita());
        Button esci = new Button("Esci");
        esci.setOnAction(evento -> allUscita.run());

        radice.getChildren().addAll(new Label("Nome dell'alchimista"), campoNome, nuovaPartita, esci);
        radice.setAlignment(Pos.CENTER);
    }

    /**
     * @return il contenuto da mettere nella finestra
     */
    public Parent getRadice() {
        return radice;
    }

    /**
     * Toglie gli spazi all'inizio e alla fine del nome; se non resta niente
     * usa il nome predefinito.
     */
    private void iniziaPartita() {
        String nome = campoNome.getText().strip();
        allaNuovaPartita.accept(nome.isEmpty() ? NOME_PREDEFINITO : nome);
    }
}
