package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import it.unicam.cs.mpgc.rpg130759.modello.StatoPartita;
import it.unicam.cs.mpgc.rpg130759.persistenza.GestoreSalvataggi;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Random;

/**
 * Decide quale schermata mostrare nella finestra. Le schermate non si conoscono
 * tra loro: quando il giocatore vuole andare avanti avvisano il navigatore,
 * che sceglie la schermata successiva in base allo stato della partita.
 * Salva la partita all'inizio di ogni piano e cancella il salvataggio
 * appena la partita finisce, così non si può continuare una partita finita.
 */
public class Navigatore {
    private final Scene scena;
    private final GestoreSalvataggi salvataggi;
    private Partita partita;

    /**
     * @param scena il contenuto della finestra, che il navigatore sostituisce a ogni cambio
     * @param salvataggi chi salva e carica la partita
     */
    public Navigatore(Scene scena, GestoreSalvataggi salvataggi) {
        this.scena = Objects.requireNonNull(scena, "La scena è obbligatoria");
        this.salvataggi = Objects.requireNonNull(salvataggi, "Il gestore dei salvataggi è obbligatorio");
    }

    /**
     * Mostra il menu iniziale, con "Continua" se c'è una partita salvata.
     */
    public void mostraMenu() {
        scena.setRoot(new SchermataMenu(salvataggi.carica(), this::continuaPartita,
                this::iniziaPartita, Platform::exit).getRadice());
    }

    /**
     * Salva subito la partita nuova, così sostituisce quella salvata in precedenza.
     */
    private void iniziaPartita(String nome) {
        partita = new Partita(new Alchimista(nome), new GeneratoreNemiciPerZona(new Random().nextLong()));
        salva();
        mostraCombattimento();
    }

    private void continuaPartita(Partita salvata) {
        partita = salvata;
        mostraCombattimento();
    }

    private void mostraCombattimento() {
        scena.setRoot(new SchermataCombattimento(partita.getCombattimento(),
                this::allaFineDelloScontro, this::dopoLoScontro).getRadice());
    }

    /**
     * Se lo scontro ha chiuso la partita cancella subito il salvataggio:
     * chiudendo la finestra prima di "Continua" non si può riprovare il piano.
     */
    private void allaFineDelloScontro() {
        if (partita.getStato() != StatoPartita.IN_CORSO) {
            cancellaSalvataggio();
        }
    }

    /**
     * Se la partita è finita mostra la schermata finale, altrimenti fa scendere
     * l'alchimista al piano successivo, salva e mostra quanto ha recuperato.
     */
    private void dopoLoScontro() {
        if (partita.getStato() != StatoPartita.IN_CORSO) {
            scena.setRoot(new SchermataFinale(partita, this::mostraMenu).getRadice());
            return;
        }
        Alchimista alchimista = partita.getAlchimista();
        int vitaPrima = alchimista.getVita();
        int reagentiPrima = alchimista.getReagenti();
        partita.passaAlPianoSuccessivo();
        salva();
        int vitaRecuperata = alchimista.getVita() - vitaPrima;
        int reagentiRecuperati = alchimista.getReagenti() - reagentiPrima;
        scena.setRoot(new SchermataTraPiani(partita, vitaRecuperata, reagentiRecuperati,
                this::mostraCombattimento).getRadice());
    }

    /**
     * Se il salvataggio non riesce il gioco va avanti lo stesso, ma il giocatore viene avvisato.
     */
    private void salva() {
        try {
            salvataggi.salva(partita);
        } catch (UncheckedIOException e) {
            avvisa("Non è stato possibile salvare la partita. Puoi continuare a giocare, "
                    + "ma se chiudi il gioco i progressi di questo piano andranno persi.");
        }
    }

    private void cancellaSalvataggio() {
        try {
            salvataggi.cancella();
        } catch (UncheckedIOException e) {
            avvisa("Non è stato possibile cancellare il salvataggio della partita finita.");
        }
    }

    private static void avvisa(String messaggio) {
        Alert avviso = new Alert(Alert.AlertType.WARNING, messaggio);
        avviso.setHeaderText(null);
        avviso.showAndWait();
    }
}
