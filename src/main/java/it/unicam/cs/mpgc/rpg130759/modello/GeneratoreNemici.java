package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Decide quale nemico si incontra a un certo piano.
 * La partita conosce solo questa interfaccia: per cambiare i nemici del gioco
 * (nuove zone, regole diverse) basta scrivere un'altra implementazione.
 */
public interface GeneratoreNemici {

    /**
     * Crea un nemico nuovo, con vita e reagenti al massimo.
     *
     * @param piano il piano in cui si svolge lo scontro
     * @return il nemico del piano
     */
    Nemico creaNemico(int piano);
}
