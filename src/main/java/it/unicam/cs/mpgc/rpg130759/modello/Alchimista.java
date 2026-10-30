package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Il personaggio del giocatore. Oltre a ciò che ha ogni combattente
 * gestisce i reagenti, l'energia consumata dalle formule.
 */
public class Alchimista extends Combattente {
    private static final int VITA_INIZIALE = 100;
    private static final int REAGENTI_INIZIALI = 20;

    private final int reagentiMassimi;
    private int reagenti;

    /**
     * Crea un alchimista all'inizio della partita, senza elemento proprio
     * così nessun principio ha un vantaggio fisso contro di lui.
     *
     * @param nome il nome scelto dal giocatore
     */
    public Alchimista(String nome) {
        super(nome, Elemento.NEUTRO, VITA_INIZIALE);
        this.reagentiMassimi = REAGENTI_INIZIALI;
        this.reagenti = REAGENTI_INIZIALI;
    }

    public int getReagenti() {
        return reagenti;
    }

    public int getReagentiMassimi() {
        return reagentiMassimi;
    }

    /**
     * Dice se i reagenti bastano per pagare un costo.
     *
     * @param costo i reagenti richiesti
     * @return true se l'alchimista ne ha abbastanza
     */
    public boolean haReagenti(int costo) {
        return reagenti >= costo;
    }

    /**
     * Toglie i reagenti necessari a una formula.
     *
     * @param costo i reagenti da consumare, non negativi
     * @throws IllegalStateException se i reagenti non bastano
     */
    public void consumaReagenti(int costo) {
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        if (!haReagenti(costo)) {
            throw new IllegalStateException("Reagenti insufficienti");
        }
        reagenti -= costo;
    }

    /**
     * Aggiunge reagenti, senza superare il massimo.
     *
     * @param quantita i reagenti da aggiungere, non negativi
     */
    public void recuperaReagenti(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("La quantità non può essere negativa");
        }
        reagenti = Math.min(reagentiMassimi, reagenti + quantita);
    }
}
