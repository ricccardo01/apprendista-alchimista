package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Uno scontro tra l'alchimista e un nemico. Fa rispettare l'ordine dei turni,
 * dice quando lo scontro è finito e tiene un registro di ciò che succede
 * e le statistiche dello scontro, che la grafica può mostrare al giocatore.
 */
public class Combattimento {
    /** Il nemico non sa distillare, quindi recupera da solo qualche reagente a ogni turno. */
    private static final int REAGENTI_NEMICO_PER_TURNO = 2;

    private final Alchimista alchimista;
    private final Nemico nemico;
    private final List<String> registro = new ArrayList<>();
    private final Map<Azione, Integer> usi = new HashMap<>();
    private boolean elementoNemicoRivelato;
    private int turni;
    private int dannoInflitto;
    private int dannoSubito;
    private int reagentiSpesi;

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

    /**
     * @return quanti turni sono stati giocati
     */
    public int getTurni() {
        return turni;
    }

    /**
     * @return i punti vita tolti al nemico dall'alchimista
     */
    public int getDannoInflitto() {
        return dannoInflitto;
    }

    /**
     * @return i punti vita tolti all'alchimista dal nemico
     */
    public int getDannoSubito() {
        return dannoSubito;
    }

    /**
     * @return i reagenti spesi dall'alchimista per pagare le sue mosse
     */
    public int getReagentiSpesi() {
        return reagentiSpesi;
    }

    /**
     * @param azione una mossa dell'alchimista
     * @return quante volte si può ancora usare in questo scontro,
     *         o {@link Azione#SENZA_LIMITE} se non ha limite
     */
    public int getUsiRimasti(Azione azione) {
        if (azione.getUsiPerScontro() == Azione.SENZA_LIMITE) {
            return Azione.SENZA_LIMITE;
        }
        return azione.getUsiPerScontro() - usi.getOrDefault(azione, 0);
    }

    /**
     * Dice se l'alchimista può usare una mossa adesso: deve avere i reagenti
     * e la mossa non deve aver esaurito gli usi dello scontro.
     *
     * @param azione una mossa dell'alchimista
     * @return true se la mossa si può usare
     */
    public boolean puoUsare(Azione azione) {
        return alchimista.haReagenti(azione.getCosto()) && getUsiRimasti(azione) > 0;
    }

    public boolean isFinito() {
        return alchimista.isSconfitto() || nemico.isSconfitto();
    }

    /**
     * Lo scontro è vinto se il nemico è sconfitto e l'alchimista è ancora in piedi.
     *
     * @return true se l'alchimista ha vinto
     */
    public boolean isVinto() {
        return nemico.isSconfitto() && !alchimista.isSconfitto();
    }

    /**
     * Gioca un turno completo: l'alchimista usa l'azione scelta, poi il nemico,
     * se è ancora in piedi e non è bloccato, risponde con una mossa sua.
     * Se nessuno è caduto agiscono gli effetti di fine turno e, se lo scontro
     * continua ancora, il nemico recupera qualche reagente. L'alchimista no:
     * per recuperarli deve distillare.
     * Se il nemico sconfigge l'alchimista, un veleno sul nemico
     * non fa più in tempo ad agire: non possono cadere tutti e due.
     *
     * @param azione la mossa scelta dal giocatore, tra quelle dell'alchimista
     * @throws IllegalStateException se lo scontro è già finito o la mossa non si può usare
     *         (reagenti insufficienti o usi esauriti)
     * @throws IllegalArgumentException se l'alchimista non conosce l'azione
     */
    public void turnoAlchimista(Azione azione) {
        if (isFinito()) {
            throw new IllegalStateException("Il combattimento è già finito");
        }
        if (!alchimista.getAzioni().contains(azione)) {
            throw new IllegalArgumentException("L'alchimista non conosce questa azione");
        }
        if (!puoUsare(azione)) {
            throw new IllegalStateException("La mossa non si può usare adesso");
        }
        usi.put(azione, usi.getOrDefault(azione, 0) + 1);
        turni++;
        reagentiSpesi += azione.getCosto();
        dannoInflitto += esegui(alchimista, azione, nemico);
        if (!nemico.isSconfitto()) {
            turnoNemico();
        }
        if (!isFinito()) {
            dannoSubito += effettiDiFineTurno(alchimista);
            dannoInflitto += effettiDiFineTurno(nemico);
        }
        if (!isFinito()) {
            nemico.recuperaReagenti(REAGENTI_NEMICO_PER_TURNO);
        }
    }

    /**
     * Il nemico fa la sua mossa, a meno che un effetto non lo blocchi.
     */
    private void turnoNemico() {
        if (nemico.puoAgire()) {
            dannoSubito += esegui(nemico, nemico.scegliAzione(), alchimista);
            elementoNemicoRivelato = true;
        } else {
            registro.add(nemico.getNome() + " è bloccato e salta il turno");
        }
    }

    /**
     * Fa agire gli effetti di fine turno di un combattente e scrive nel registro
     * la vita che ha perso.
     *
     * @return i punti vita persi dal combattente
     */
    private int effettiDiFineTurno(Combattente combattente) {
        int vitaPrima = combattente.getVita();
        combattente.applicaEffettiDiFineTurno();
        int danno = vitaPrima - combattente.getVita();
        if (danno > 0) {
            registro.add(combattente.getNome() + " perde " + danno + " punti vita a fine turno");
        }
        if (combattente.isSconfitto()) {
            registro.add(combattente.getNome() + " è sconfitto");
        }
        return danno;
    }

    /**
     * Fa usare un'azione e la scrive nel registro.
     *
     * @return i punti vita persi dal bersaglio
     */
    private int esegui(Combattente chi, Azione azione, Combattente bersaglio) {
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
        return danno;
    }
}
