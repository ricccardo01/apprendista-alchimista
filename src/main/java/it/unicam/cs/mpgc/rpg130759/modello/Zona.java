package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Le zone del laboratorio, dalla più alta alla più profonda.
 * Ogni zona occupa lo stesso numero di piani, uno dopo l'altro.
 * Più la zona è profonda, più esperienza dà ogni scontro vinto.
 */
public enum Zona {
    CANTINE(40), LABORATORIO(60), FORNACE(80);

    /** Quanti piani ha ogni zona. */
    public static final int PIANI_PER_ZONA = 4;

    /** L'ultimo piano dell'ultima zona: aggiungendo una zona cresce da solo. */
    public static final int ULTIMO_PIANO = values().length * PIANI_PER_ZONA;

    private final int esperienzaPerScontro;

    Zona(int esperienzaPerScontro) {
        this.esperienzaPerScontro = esperienzaPerScontro;
    }

    /**
     * @return i punti esperienza che l'alchimista guadagna vincendo uno scontro in questa zona
     */
    public int getEsperienzaPerScontro() {
        return esperienzaPerScontro;
    }

    /**
     * Trova la zona a cui appartiene un piano.
     *
     * @param piano il numero del piano, da 1 a {@link #ULTIMO_PIANO}
     * @return la zona del piano
     * @throws IllegalArgumentException se il piano non appartiene a nessuna zona
     */
    public static Zona delPiano(int piano) {
        if (piano < 1 || piano > ULTIMO_PIANO) {
            throw new IllegalArgumentException("Il piano " + piano + " non appartiene a nessuna zona");
        }
        return values()[(piano - 1) / PIANI_PER_ZONA];
    }

    /**
     * Dice in che posizione si trova un piano dentro la sua zona.
     *
     * @param piano il numero del piano, da 1 a {@link #ULTIMO_PIANO}
     * @return 0 per il primo piano della zona, fino a {@code PIANI_PER_ZONA - 1} per l'ultimo
     */
    public static int posizioneNellaZona(int piano) {
        delPiano(piano);
        return (piano - 1) % PIANI_PER_ZONA;
    }
}
