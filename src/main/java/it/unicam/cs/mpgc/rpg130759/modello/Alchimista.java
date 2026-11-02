package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Il personaggio del giocatore. Oltre a ciò che ha ogni combattente
 * impugna uno strumento, con cui fa l'attacco base.
 */
public class Alchimista extends Combattente {
    private static final int VITA_INIZIALE = 100;
    private static final int REAGENTI_INIZIALI = 20;
    private static final Strumento STRUMENTO_INIZIALE =
            new Strumento("Pestello di Legno", 6, Elemento.NEUTRO);

    private final Strumento strumento;

    /**
     * Crea un alchimista all'inizio della partita, con il pestello in mano e
     * senza elemento proprio, così nessun principio ha un vantaggio fisso contro di lui.
     *
     * @param nome il nome scelto dal giocatore
     */
    public Alchimista(String nome) {
        super(nome, Elemento.NEUTRO, VITA_INIZIALE, REAGENTI_INIZIALI);
        this.strumento = STRUMENTO_INIZIALE;
    }

    public Strumento getStrumento() {
        return strumento;
    }
}
