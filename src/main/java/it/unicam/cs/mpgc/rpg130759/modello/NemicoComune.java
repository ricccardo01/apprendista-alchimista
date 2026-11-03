package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Un nemico normale, che a ogni turno sceglie a caso una delle mosse che può pagare.
 * I nemici comuni cambiano solo nei numeri e nelle mosse, quindi sono
 * oggetti diversi di questa stessa classe.
 */
public class NemicoComune extends Nemico {
    private final Random caso;

    /**
     * @param nome il nome mostrato nel gioco
     * @param elemento l'elemento del nemico
     * @param vitaMassima i punti vita massimi, maggiori di zero
     * @param reagentiMassimi i reagenti massimi, non negativi
     * @param azioni le mosse del nemico, almeno una gratuita
     * @param caso il generatore di numeri casuali usato per scegliere la mossa
     */
    public NemicoComune(String nome, Elemento elemento, int vitaMassima, int reagentiMassimi,
                        List<Azione> azioni, Random caso) {
        super(nome, elemento, vitaMassima, reagentiMassimi, azioni);
        this.caso = Objects.requireNonNull(caso, "Il generatore casuale è obbligatorio");
    }

    @Override
    public Azione scegliAzione() {
        List<Azione> possibili = new ArrayList<>();
        for (Azione azione : getAzioni()) {
            if (haReagenti(azione.getCosto())) {
                possibili.add(azione);
            }
        }
        return possibili.get(caso.nextInt(possibili.size()));
    }
}
