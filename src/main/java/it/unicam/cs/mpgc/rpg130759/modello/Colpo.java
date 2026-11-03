package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * Un'azione che colpisce il bersaglio con un danno di un certo elemento.
 * La usano le fiale dell'alchimista e gli attacchi dei nemici: le mosse che
 * cambiano solo nei numeri sono oggetti diversi di questa classe.
 */
public class Colpo implements Azione {
    private final String nome;
    private final int costo;
    private final int danno;
    private final Elemento elemento;

    /**
     * @param nome il nome della mossa
     * @param costo i reagenti consumati, non negativi
     * @param danno il danno prima del moltiplicatore, maggiore di zero
     * @param elemento l'elemento del colpo
     */
    public Colpo(String nome, int costo, int danno, Elemento elemento) {
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
