package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Una mossa che un combattente può fare nel suo turno.
 * La usano sia l'alchimista sia i nemici: per aggiungere una mossa nuova
 * basta scrivere una nuova implementazione, senza modificare il resto.
 */
public interface Azione {

    /**
     * @return il nome mostrato nel gioco
     */
    String getNome();

    /**
     * @return i reagenti necessari per usare l'azione
     */
    int getCosto();

    /**
     * Applica l'effetto dell'azione. Non paga il costo: per quello si usa
     * {@link Combattente#usa(Azione, Combattente)}.
     *
     * @param chi chi fa l'azione
     * @param bersaglio chi la subisce
     */
    void esegui(Combattente chi, Combattente bersaglio);
}
