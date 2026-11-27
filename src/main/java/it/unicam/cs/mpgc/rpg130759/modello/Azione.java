package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Una mossa che un combattente può fare nel suo turno.
 * La usano sia l'alchimista sia i nemici: per aggiungere una mossa nuova
 * basta scrivere una nuova implementazione, senza modificare il resto.
 */
public interface Azione {

    /** Il valore di {@link #getUsiPerScontro()} per le azioni senza limite di usi. */
    int SENZA_LIMITE = Integer.MAX_VALUE;

    /**
     * @return il nome mostrato nel gioco
     */
    String getNome();

    /**
     * @return i reagenti necessari per usare l'azione
     */
    int getCosto();

    /**
     * Spiega al giocatore cosa fa l'azione, usando i suoi numeri veri:
     * se i numeri cambiano, la descrizione si aggiorna da sola.
     *
     * @return una frase breve, per esempio "10 danni (Zolfo)"
     */
    String getDescrizione();

    /**
     * Quante volte l'azione si può usare in uno scontro. Di base non c'è limite:
     * solo le azioni che hanno un limite riscrivono questo metodo.
     *
     * @return il numero massimo di usi per scontro, o {@link #SENZA_LIMITE}
     */
    default int getUsiPerScontro() {
        return SENZA_LIMITE;
    }

    /**
     * Applica l'effetto dell'azione. Non paga il costo: per quello si usa
     * {@link Combattente#usa(Azione, Combattente)}.
     *
     * @param chi chi fa l'azione
     * @param bersaglio chi la subisce
     */
    void esegui(Combattente chi, Combattente bersaglio);
}
