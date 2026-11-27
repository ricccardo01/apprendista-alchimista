package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Un colpo che restituisce a chi lo usa metà della vita tolta al bersaglio
 * (arrotondata per difetto). Se il bersaglio è protetto, si recupera meno.
 */
public class ColpoAssorbente extends Colpo {

    /**
     * @param nome il nome della mossa
     * @param costo i reagenti consumati, non negativi
     * @param danno il danno prima del moltiplicatore, maggiore di zero
     * @param elemento l'elemento del colpo
     */
    public ColpoAssorbente(String nome, int costo, int danno, Elemento elemento) {
        super(nome, costo, danno, elemento);
    }

    @Override
    public String getDescrizione() {
        return super.getDescrizione() + ", e chi la usa recupera metà della vita tolta";
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        int vitaPrima = bersaglio.getVita();
        super.esegui(chi, bersaglio);
        chi.cura((vitaPrima - bersaglio.getVita()) / 2);
    }
}
