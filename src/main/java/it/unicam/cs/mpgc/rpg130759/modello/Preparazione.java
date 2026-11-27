package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Una mossa che non tocca il bersaglio: chi la usa si applica un effetto da solo,
 * per esempio una protezione o un potenziamento.
 */
public class Preparazione implements Azione {
    private final String nome;
    private final int costo;
    private final Supplier<Effetto> creaEffetto;

    /**
     * @param nome il nome della mossa
     * @param costo i reagenti consumati, non negativi
     * @param creaEffetto crea l'effetto da applicare a chi usa la mossa
     */
    public Preparazione(String nome, int costo, Supplier<Effetto> creaEffetto) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        this.creaEffetto = Objects.requireNonNull(creaEffetto, "L'effetto è obbligatorio");
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        this.costo = costo;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getCosto() {
        return costo;
    }

    /**
     * Per leggere la descrizione dell'effetto ne crea uno nuovo, che poi non viene usato.
     */
    @Override
    public String getDescrizione() {
        return "Chi la usa " + creaEffetto.get().getDescrizione();
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        chi.aggiungiEffetto(creaEffetto.get());
    }
}
