package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Raddoppia il danno del prossimo colpo del portatore, poi si consuma.
 * Se nel turno successivo non colpisce, l'effetto si perde.
 */
public class Catalizzatore extends Effetto {
    private static final int DURATA = 2;
    private static final int MOLTIPLICATORE = 2;

    public Catalizzatore() {
        super("Catalizzatore", DURATA);
    }

    @Override
    public int modificaDannoInflitto(int danno) {
        termina();
        return danno * MOLTIPLICATORE;
    }
}
