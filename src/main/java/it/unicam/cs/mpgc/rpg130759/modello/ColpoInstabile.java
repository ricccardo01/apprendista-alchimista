package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;
import java.util.Random;

/**
 * Un colpo dal danno imprevedibile: a ogni uso il danno è scelto a caso
 * tra un minimo e un massimo, poi vale come un colpo normale.
 */
public class ColpoInstabile implements Azione {
    private final String nome;
    private final int costo;
    private final int dannoMinimo;
    private final int dannoMassimo;
    private final Elemento elemento;
    private final Random caso;

    /**
     * @param nome il nome della mossa
     * @param costo i reagenti consumati, non negativi
     * @param dannoMinimo il danno più basso, maggiore di zero
     * @param dannoMassimo il danno più alto, non minore del minimo
     * @param elemento l'elemento del colpo
     * @param caso il generatore di numeri casuali (nei test si usa un seme fisso)
     */
    public ColpoInstabile(String nome, int costo, int dannoMinimo, int dannoMassimo,
                          Elemento elemento, Random caso) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        this.elemento = Objects.requireNonNull(elemento, "L'elemento è obbligatorio");
        this.caso = Objects.requireNonNull(caso, "Il generatore casuale è obbligatorio");
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        if (dannoMinimo <= 0 || dannoMassimo < dannoMinimo) {
            throw new IllegalArgumentException("Il danno deve essere positivo e il massimo non minore del minimo");
        }
        this.costo = costo;
        this.dannoMinimo = dannoMinimo;
        this.dannoMassimo = dannoMassimo;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getCosto() {
        return costo;
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        int danno = dannoMinimo + caso.nextInt(dannoMassimo - dannoMinimo + 1);
        bersaglio.subisciColpo(chi.potenziaDanno(danno), elemento);
    }
}
