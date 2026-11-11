package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Uno scontro tra l'alchimista e un nemico. Fa rispettare l'ordine dei turni,
 * dice quando lo scontro è finito e tiene un registro di ciò che succede,
 * che la grafica può mostrare al giocatore.
 */
public class Combattimento {
    private static final int REAGENTI_PER_TURNO = 2;

    private final Alchimista alchimista;
    private final Nemico nemico;
    private final List<String> registro = new ArrayList<>();
    private boolean elementoNemicoRivelato;

    /**
     * @param alchimista il personaggio del giocatore
     * @param nemico l'avversario
     */
    public Combattimento(Alchimista alchimista, Nemico nemico) {
        this.alchimista = Objects.requireNonNull(alchimista, "L'alchimista è obbligatorio");
        this.nemico = Objects.requireNonNull(nemico, "Il nemico è obbligatorio");
    }

    public Alchimista getAlchimista() {
        return alchimista;
    }

    public Nemico getNemico() {
        return nemico;
    }

    /**
     * @return i messaggi dello scontro in ordine, in una lista non modificabile
     */
    public List<String> getRegistro() {
        return Collections.unmodifiableList(registro);
    }

    /**
     * All'inizio il giocatore non sa di che elemento è il nemico:
     * lo scopre vedendo la sua prima mossa.
     *
     * @return true se il nemico ha già fatto almeno una mossa
     */
    public boolean isElementoNemicoRivelato() {
        return elementoNemicoRivelato;
    }

    public boolean isFinito() {
        return alchimista.isSconfitto() || nemico.isSconfitto();
    }

    /**
     * L'alchimista muove sempre per primo, quindi se il nemico è sconfitto
     * non ha potuto contrattaccare e l'alchimista è ancora in piedi.
     *
     * @return true se il nemico è stato sconfitto
     */
    public boolean isVinto() {
        return nemico.isSconfitto();
    }

    /**
     * Gioca un turno completo: l'alchimista usa l'azione scelta, poi il nemico,
     * se è ancora in piedi, risponde con una mossa sua. Alla fine del turno
     * entrambi recuperano qualche reagente.
     *
     * @param azione la mossa scelta dal giocatore, tra quelle dell'alchimista
     * @throws IllegalStateException se lo scontro è già finito o i reagenti non bastano
     * @throws IllegalArgumentException se l'alchimista non conosce l'azione
     */
    public void turnoAlchimista(Azione azione) {
        if (isFinito()) {
            throw new IllegalStateException("Il combattimento è già finito");
        }
        if (!alchimista.getAzioni().contains(azione)) {
            throw new IllegalArgumentException("L'alchimista non conosce questa azione");
        }
        esegui(alchimista, azione, nemico);
        if (!nemico.isSconfitto()) {
            esegui(nemico, nemico.scegliAzione(), alchimista);
            elementoNemicoRivelato = true;
        }
        if (!isFinito()) {
            alchimista.recuperaReagenti(REAGENTI_PER_TURNO);
            nemico.recuperaReagenti(REAGENTI_PER_TURNO);
        }
    }

    private void esegui(Combattente chi, Azione azione, Combattente bersaglio) {
        int vitaPrima = bersaglio.getVita();
        chi.usa(azione, bersaglio);
        int danno = vitaPrima - bersaglio.getVita();

        String messaggio = chi.getNome() + " usa " + azione.getNome();
        if (danno > 0) {
            messaggio += ": " + danno + " danni a " + bersaglio.getNome();
        }
        registro.add(messaggio);
        if (bersaglio.isSconfitto()) {
            registro.add(bersaglio.getNome() + " è sconfitto");
        }
    }
}
