package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * L'attrezzo che l'alchimista impugna: decide danno ed elemento dell'attacco base.
 */
public class Strumento {
    private final String nome;
    private final int danno;
    private final Elemento elemento;

    /**
     * @param nome il nome mostrato nel gioco
     * @param danno il danno dell'attacco base, maggiore di zero
     * @param elemento l'elemento dei colpi, NEUTRO se lo strumento non ne ha uno
     */
    public Strumento(String nome, int danno, Elemento elemento) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        this.elemento = Objects.requireNonNull(elemento, "L'elemento è obbligatorio");
        if (danno <= 0) {
            throw new IllegalArgumentException("Il danno deve essere positivo");
        }
        this.danno = danno;
    }

    public String getNome() {
        return nome;
    }

    public int getDanno() {
        return danno;
    }

    public Elemento getElemento() {
        return elemento;
    }
}
