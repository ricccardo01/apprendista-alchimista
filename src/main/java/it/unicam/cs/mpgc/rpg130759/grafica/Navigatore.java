package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import it.unicam.cs.mpgc.rpg130759.modello.StatoPartita;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.util.Objects;
import java.util.Random;

/**
 * Decide quale schermata mostrare nella finestra. Le schermate non si conoscono
 * tra loro: quando il giocatore vuole andare avanti avvisano il navigatore,
 * che sceglie la schermata successiva in base allo stato della partita.
 */
public class Navigatore {
    private final Scene scena;
    private Partita partita;

    /**
     * @param scena il contenuto della finestra, che il navigatore sostituisce a ogni cambio
     */
    public Navigatore(Scene scena) {
        this.scena = Objects.requireNonNull(scena, "La scena è obbligatoria");
    }

    /**
     * Mostra il menu iniziale.
     */
    public void mostraMenu() {
        scena.setRoot(new SchermataMenu(this::iniziaPartita, Platform::exit).getRadice());
    }

    private void iniziaPartita(String nome) {
        partita = new Partita(new Alchimista(nome), new GeneratoreNemiciPerZona(new Random()));
        mostraCombattimento();
    }

    private void mostraCombattimento() {
        scena.setRoot(new SchermataCombattimento(partita.getCombattimento(), this::dopoLoScontro).getRadice());
    }

    /**
     * Se la partita è finita mostra la schermata finale, altrimenti fa scendere
     * l'alchimista al piano successivo e mostra quanto ha recuperato.
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
        int vitaRecuperata = alchimista.getVita() - vitaPrima;
        int reagentiRecuperati = alchimista.getReagenti() - reagentiPrima;
        scena.setRoot(new SchermataTraPiani(partita, vitaRecuperata, reagentiRecuperati,
                this::mostraCombattimento).getRadice());
    }
}
