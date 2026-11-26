package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Tutte le formule del gioco: le tre iniziali e quelle che l'alchimista
 * può imparare salendo di livello. Propone formule nuove a caso
 * e sa ritrovare una formula dal suo nome, per ricaricare una partita salvata.
 * Per aggiungere una formula al gioco basta aggiungerla qui.
 */
public class Ricettario {
    /** Quante formule vengono proposte a ogni livello. */
    public static final int NUMERO_PROPOSTE = 2;

    private static final int COSTO_FORMULA_INIZIALE = 5;
    private static final int DANNO_FORMULA_INIZIALE = 10;

    private final List<Azione> formule;
    private final Random caso;

    /**
     * @param caso il generatore di numeri casuali, usato per le proposte
     *             e per le formule dal danno casuale (nei test si usa un seme fisso)
     */
    public Ricettario(Random caso) {
        this.caso = Objects.requireNonNull(caso, "Il generatore casuale è obbligatorio");
        List<Azione> tutte = new ArrayList<>(formuleIniziali());
        tutte.add(new ColpoConEffetto("Veleno Sulfureo", 5, 4, Elemento.ZOLFO, () -> new Veleno()));
        tutte.add(new ColpoConRinculo("Esplosione di Zolfo", 8, 18, Elemento.ZOLFO, 5));
        tutte.add(new ColpoAssorbente("Sanguisuga d'Argento", 6, 8, Elemento.MERCURIO));
        tutte.add(new ColpoInstabile("Mercurio Instabile", 5, 2, 20, Elemento.MERCURIO, caso));
        tutte.add(new Preparazione("Corazza di Sale", 5, () -> new CorazzaDiSale()));
        tutte.add(new ColpoConEffetto("Cristalli Pietrificanti", 7, 5, Elemento.SALE,
                () -> new Pietrificazione()));
        tutte.add(new Preparazione("Catalizzatore", 3, () -> new Catalizzatore()));
        this.formule = List.copyOf(tutte);
    }

    /**
     * Le formule con cui l'alchimista inizia la partita: una per ogni principio.
     *
     * @return tre formule nuove, in una lista non modificabile
     */
    public static List<Azione> formuleIniziali() {
        return List.of(
                formulaIniziale("Fiala Ardente", Elemento.ZOLFO),
                formulaIniziale("Nebbia Mercuriale", Elemento.MERCURIO),
                formulaIniziale("Polvere Cristallizzante", Elemento.SALE));
    }

    private static Azione formulaIniziale(String nome, Elemento elemento) {
        return new Colpo(nome, COSTO_FORMULA_INIZIALE, DANNO_FORMULA_INIZIALE, elemento);
    }

    /**
     * @return tutte le formule del ricettario, in una lista non modificabile
     */
    public List<Azione> getFormule() {
        return formule;
    }

    /**
     * Cerca una formula dal nome.
     *
     * @param nome il nome della formula
     * @return la formula con quel nome
     * @throws IllegalArgumentException se nel ricettario non c'è nessuna formula con quel nome
     */
    public Azione trova(String nome) {
        for (Azione formula : formule) {
            if (formula.getNome().equals(nome)) {
                return formula;
            }
        }
        throw new IllegalArgumentException("Nel ricettario non c'è la formula " + nome);
    }

    /**
     * Sceglie a caso le formule da proporre all'alchimista, tra quelle che non conosce.
     *
     * @param alchimista chi sta per imparare una formula
     * @return al massimo {@link #NUMERO_PROPOSTE} formule diverse, in una lista non modificabile;
     *         vuota se l'alchimista le conosce già tutte
     */
    public List<Azione> proponi(Alchimista alchimista) {
        List<Azione> sconosciute = new ArrayList<>();
        for (Azione formula : formule) {
            if (!alchimista.conosceFormula(formula.getNome())) {
                sconosciute.add(formula);
            }
        }
        Collections.shuffle(sconosciute, caso);
        return List.copyOf(sconosciute.subList(0, Math.min(NUMERO_PROPOSTE, sconosciute.size())));
    }
}
