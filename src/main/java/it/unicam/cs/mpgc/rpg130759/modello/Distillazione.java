package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Chi la usa rinuncia ad attaccare per recuperare reagenti e un po' di vita.
 * Non costa reagenti e non tocca il bersaglio. Si può usare poche volte
 * per scontro, così non basta distillare all'infinito per non perdere mai.
 */
public class Distillazione implements Azione {
    private static final int REAGENTI_RECUPERATI = 8;
    private static final int VITA_RECUPERATA = 5;
    private static final int USI_PER_SCONTRO = 2;

    @Override
    public String getNome() {
        return "Distillazione";
    }

    @Override
    public int getCosto() {
        return 0;
    }

    @Override
    public int getUsiPerScontro() {
        return USI_PER_SCONTRO;
    }

    @Override
    public String getDescrizione() {
        return "Recupera " + REAGENTI_RECUPERATI + " reagenti e " + VITA_RECUPERATA + " punti vita";
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        chi.recuperaReagenti(REAGENTI_RECUPERATI);
        chi.cura(VITA_RECUPERATA);
    }
}
