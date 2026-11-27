package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * A ogni fine turno toglie qualche punto vita, senza moltiplicatore di elemento.
 */
public class Veleno extends Effetto {
    private static final int DURATA = 3;
    private static final int DANNO_PER_TURNO = 4;

    public Veleno() {
        super("Veleno", DURATA);
    }

    @Override
    public String getDescrizione() {
        return "perde " + DANNO_PER_TURNO + " punti vita a fine turno per " + DURATA + " turni";
    }

    @Override
    protected void allaFineDelTurno(Combattente portatore) {
        portatore.subisciDanno(DANNO_PER_TURNO);
    }
}
