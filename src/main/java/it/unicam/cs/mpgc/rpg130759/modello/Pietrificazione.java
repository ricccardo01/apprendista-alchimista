package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Blocca il portatore, che salta la sua mossa nel turno in cui viene colpito.
 */
public class Pietrificazione extends Effetto {
    private static final int DURATA = 1;

    public Pietrificazione() {
        super("Pietrificazione", DURATA);
    }

    @Override
    public boolean impedisceDiAgire() {
        return true;
    }
}
