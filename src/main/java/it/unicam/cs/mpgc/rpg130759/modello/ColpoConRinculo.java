package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Un colpo forte che ferisce anche chi lo usa. Il rinculo non fa mai scendere
 * sotto 1 punto vita, così chi lo usa non può cadere insieme al bersaglio.
 */
public class ColpoConRinculo extends Colpo {
    private final int rinculo;

    /**
     * @param nome il nome della mossa
     * @param costo i reagenti consumati, non negativi
     * @param danno il danno prima del moltiplicatore, maggiore di zero
     * @param elemento l'elemento del colpo
     * @param rinculo i punti vita persi da chi usa la mossa, maggiori di zero
     */
    public ColpoConRinculo(String nome, int costo, int danno, Elemento elemento, int rinculo) {
        super(nome, costo, danno, elemento);
        if (rinculo <= 0) {
            throw new IllegalArgumentException("Il rinculo deve essere positivo");
        }
        this.rinculo = rinculo;
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        super.esegui(chi, bersaglio);
        chi.subisciDanno(Math.min(rinculo, chi.getVita() - 1));
    }
}
