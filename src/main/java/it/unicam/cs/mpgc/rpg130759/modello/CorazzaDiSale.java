package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Dimezza il danno dei colpi ricevuti per alcuni turni.
 */
public class CorazzaDiSale extends Effetto {
    private static final int DURATA = 3;
    private static final double RIDUZIONE = 0.5;

    public CorazzaDiSale() {
        super("Corazza di Sale", DURATA);
    }

    @Override
    public int modificaDannoSubito(int danno) {
        return (int) Math.round(danno * RIDUZIONE);
    }
}
