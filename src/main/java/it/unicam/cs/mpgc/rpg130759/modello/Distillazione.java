package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Chi la usa rinuncia ad attaccare per recuperare reagenti e un po' di vita.
 * Non costa reagenti e non tocca il bersaglio.
 */
public class Distillazione implements Azione {
    private static final int REAGENTI_RECUPERATI = 8;
    private static final int VITA_RECUPERATA = 5;

    @Override
    public String getNome() {
        return "Distillazione";
    }

    @Override
    public int getCosto() {
        return 0;
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        chi.recuperaReagenti(REAGENTI_RECUPERATI);
        chi.cura(VITA_RECUPERATA);
    }
}
