package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.List;
import java.util.Objects;

/**
 * Il personaggio del giocatore. Oltre a ciò che ha ogni combattente
 * impugna uno strumento, con cui fa l'attacco base.
 */
public class Alchimista extends Combattente {
    /** Il nome più lungo che il giocatore può scegliere. */
    public static final int LUNGHEZZA_MASSIMA_NOME = 10;

    private static final int VITA_INIZIALE = 100;
    private static final int REAGENTI_INIZIALI = 20;
    private static final Strumento STRUMENTO_INIZIALE =
            new Strumento("Pestello di Legno", 6, Elemento.NEUTRO);
    private static final int COSTO_FORMULA_INIZIALE = 4;
    private static final int DANNO_FORMULA_INIZIALE = 10;

    private final Strumento strumento;
    private final List<Azione> azioni;

    /**
     * Crea un alchimista all'inizio della partita, con il pestello in mano e
     * senza elemento proprio, così nessun principio ha un vantaggio fisso contro di lui.
     * Conosce l'attacco base, la Distillazione e una formula per ogni principio.
     *
     * @param nome il nome scelto dal giocatore, da 1 a {@link #LUNGHEZZA_MASSIMA_NOME} caratteri
     * @throws IllegalArgumentException se il nome è vuoto o troppo lungo
     */
    public Alchimista(String nome) {
        super(controllaNome(nome), Elemento.NEUTRO, VITA_INIZIALE, REAGENTI_INIZIALI);
        this.strumento = STRUMENTO_INIZIALE;
        this.azioni = List.of(
                new AttaccoBase(strumento),
                new Distillazione(),
                formulaIniziale("Fiala Ardente", Elemento.ZOLFO),
                formulaIniziale("Nebbia Mercuriale", Elemento.MERCURIO),
                formulaIniziale("Polvere Cristallizzante", Elemento.SALE));
    }

    private static String controllaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome è obbligatorio");
        if (nome.isBlank() || nome.length() > LUNGHEZZA_MASSIMA_NOME) {
            throw new IllegalArgumentException(
                    "Il nome deve avere da 1 a " + LUNGHEZZA_MASSIMA_NOME + " caratteri");
        }
        return nome;
    }

    private static Azione formulaIniziale(String nome, Elemento elemento) {
        return new Colpo(nome, COSTO_FORMULA_INIZIALE, DANNO_FORMULA_INIZIALE, elemento);
    }

    public Strumento getStrumento() {
        return strumento;
    }

    @Override
    public List<Azione> getAzioni() {
        return azioni;
    }
}
