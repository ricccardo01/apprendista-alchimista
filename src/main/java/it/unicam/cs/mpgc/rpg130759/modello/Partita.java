package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * Una partita intera: l'alchimista scende piano per piano e a ogni piano
 * affronta uno scontro. Tra un piano e l'altro recupera parte della vita
 * e tutti i reagenti. È la classe con cui parla la grafica.
 */
public class Partita {
    private static final double RECUPERO_VITA_TRA_PIANI = 0.3;

    private final Alchimista alchimista;
    private final GeneratoreNemici generatore;
    private int piano;
    private Combattimento combattimento;

    /**
     * Inizia la partita dal primo piano, con il suo scontro già pronto.
     *
     * @param alchimista il personaggio del giocatore
     * @param generatore chi decide i nemici dei piani
     */
    public Partita(Alchimista alchimista, GeneratoreNemici generatore) {
        this.alchimista = Objects.requireNonNull(alchimista, "L'alchimista è obbligatorio");
        this.generatore = Objects.requireNonNull(generatore, "Il generatore di nemici è obbligatorio");
        this.piano = 1;
        this.combattimento = new Combattimento(alchimista, generatore.creaNemico(piano));
    }

    public Alchimista getAlchimista() {
        return alchimista;
    }

    public int getPiano() {
        return piano;
    }

    public Zona getZona() {
        return Zona.delPiano(piano);
    }

    public Combattimento getCombattimento() {
        return combattimento;
    }

    /**
     * La partita è persa se l'alchimista è sconfitto, vinta se ha superato
     * lo scontro dell'ultimo piano.
     *
     * @return lo stato attuale della partita
     */
    public StatoPartita getStato() {
        if (alchimista.isSconfitto()) {
            return StatoPartita.PERSA;
        }
        if (piano == Zona.ULTIMO_PIANO && combattimento.isVinto()) {
            return StatoPartita.VINTA;
        }
        return StatoPartita.IN_CORSO;
    }

    /**
     * Scende al piano successivo: l'alchimista recupera il 30% della vita massima
     * e tutti i reagenti, poi inizia lo scontro con il nuovo nemico.
     *
     * @throws IllegalStateException se lo scontro del piano non è vinto o la partita è finita
     */
    public void passaAlPianoSuccessivo() {
        if (getStato() != StatoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita è finita");
        }
        if (!combattimento.isVinto()) {
            throw new IllegalStateException("Lo scontro del piano non è ancora vinto");
        }
        alchimista.cura((int) Math.round(alchimista.getVitaMassima() * RECUPERO_VITA_TRA_PIANI));
        alchimista.recuperaReagenti(alchimista.getReagentiMassimi());
        piano++;
        combattimento = new Combattimento(alchimista, generatore.creaNemico(piano));
    }
}
