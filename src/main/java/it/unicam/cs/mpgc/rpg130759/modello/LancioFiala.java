package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * Una formula che lancia una fiala di un elemento contro il bersaglio.
 * Le fiale che cambiano solo nei numeri sono oggetti diversi di questa classe.
 */
public class LancioFiala implements Azione {
    private final String nome;
    private final int costo;
    private final int danno;
    private final Elemento elemento;

    /**
     * @param nome il nome della formula
     * @param costo i reagenti consumati, non negativi
     * @param danno il danno prima del moltiplicatore, maggiore di zero
     * @param elemento l'elemento della fiala
     */
    public LancioFiala(String nome, int costo, int danno, Elemento elemento) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        this.elemento = Objects.requireNonNull(elemento, "L'elemento è obbligatorio");
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        if (danno <= 0) {
            throw new IllegalArgumentException("Il danno deve essere positivo");
        }
        this.costo = costo;
        this.danno = danno;
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
        bersaglio.subisciColpo(danno, elemento);
    }
}
