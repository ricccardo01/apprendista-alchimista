package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * Chi può partecipare a uno scontro: ha un nome, un elemento e dei punti vita.
 * Garantisce che la vita resti sempre tra 0 e il massimo.
 */
public abstract class Combattente {
    private final String nome;
    private final Elemento elemento;
    private final int vitaMassima;
    private int vita;

    /**
     * Crea un combattente con la vita al massimo.
     *
     * @param nome il nome mostrato nel gioco
     * @param elemento l'elemento del combattente, mai null
     * @param vitaMassima i punti vita massimi, maggiori di zero
     */
    protected Combattente(String nome, Elemento elemento, int vitaMassima) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        this.elemento = Objects.requireNonNull(elemento, "L'elemento è obbligatorio");
        if (vitaMassima <= 0) {
            throw new IllegalArgumentException("La vita massima deve essere positiva");
        }
        this.vitaMassima = vitaMassima;
        this.vita = vitaMassima;
    }

    public String getNome() {
        return nome;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public int getVitaMassima() {
        return vitaMassima;
    }

    public int getVita() {
        return vita;
    }

    /**
     * Toglie punti vita, senza scendere sotto lo zero.
     *
     * @param danno i punti da togliere, non negativi
     */
    public void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        vita = Math.max(0, vita - danno);
    }

    /**
     * Restituisce punti vita, senza superare il massimo.
     *
     * @param punti i punti da restituire, non negativi
     */
    public void cura(int punti) {
        if (punti < 0) {
            throw new IllegalArgumentException("La cura non può essere negativa");
        }
        vita = Math.min(vitaMassima, vita + punti);
    }

    public boolean isSconfitto() {
        return vita == 0;
    }
}
