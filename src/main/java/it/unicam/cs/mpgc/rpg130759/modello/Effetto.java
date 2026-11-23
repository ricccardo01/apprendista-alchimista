package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * Qualcosa che resta addosso a un combattente per alcuni turni dopo una mossa.
 * Conta da solo i turni che gli restano; ogni effetto concreto cambia solo
 * i momenti dello scontro che gli interessano, gli altri restano come sono.
 * Per un effetto nuovo basta scrivere una sottoclasse.
 */
public abstract class Effetto {
    private final String nome;
    private int turniRimasti;

    /**
     * @param nome il nome mostrato nel gioco
     * @param durata per quanti turni dura, compreso quello in cui viene applicato
     */
    protected Effetto(String nome, int durata) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        if (durata <= 0) {
            throw new IllegalArgumentException("La durata deve essere positiva");
        }
        this.turniRimasti = durata;
    }

    public String getNome() {
        return nome;
    }

    public int getTurniRimasti() {
        return turniRimasti;
    }

    /**
     * @return true se l'effetto è finito e va tolto
     */
    public boolean isScaduto() {
        return turniRimasti == 0;
    }

    /**
     * Chiamato alla fine di ogni turno: applica ciò che l'effetto fa
     * in quel momento e consuma un turno.
     *
     * @param portatore chi ha addosso l'effetto
     */
    public final void fineTurno(Combattente portatore) {
        if (isScaduto()) {
            return;
        }
        allaFineDelTurno(portatore);
        turniRimasti--;
    }

    /**
     * Fa finire subito l'effetto, per quelli che si consumano quando vengono usati.
     */
    protected void termina() {
        turniRimasti = 0;
    }

    /**
     * Cosa succede a fine turno. Di base niente.
     *
     * @param portatore chi ha addosso l'effetto
     */
    protected void allaFineDelTurno(Combattente portatore) {
    }

    /**
     * Cambia il danno di un colpo ricevuto dal portatore. Di base lo lascia uguale.
     *
     * @param danno il danno già moltiplicato per l'elemento
     * @return il danno da subire
     */
    public int modificaDannoSubito(int danno) {
        return danno;
    }

    /**
     * Cambia il danno di un colpo dato dal portatore. Di base lo lascia uguale.
     *
     * @param danno il danno della mossa
     * @return il danno da infliggere
     */
    public int modificaDannoInflitto(int danno) {
        return danno;
    }

    /**
     * @return true se l'effetto impedisce al portatore di fare la sua mossa
     */
    public boolean impedisceDiAgire() {
        return false;
    }
}
