package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.List;

/**
 * Un combattente controllato dal gioco, che decide da solo la mossa del suo turno.
 * Ogni tipo di nemico sceglie a modo suo: per un comportamento nuovo basta
 * scrivere una sottoclasse, senza modificare il resto del gioco.
 */
public abstract class Nemico extends Combattente {
    private final List<Azione> azioni;

    /**
     * @param nome il nome mostrato nel gioco
     * @param elemento l'elemento del nemico
     * @param vitaMassima i punti vita massimi, maggiori di zero
     * @param reagentiMassimi i reagenti massimi, non negativi
     * @param azioni le mosse del nemico; almeno una deve essere gratuita,
     *               così ha sempre qualcosa da fare anche senza reagenti
     */
    protected Nemico(String nome, Elemento elemento, int vitaMassima, int reagentiMassimi,
                     List<Azione> azioni) {
        super(nome, elemento, vitaMassima, reagentiMassimi);
        this.azioni = List.copyOf(azioni);
        if (!haAzioneGratuita()) {
            throw new IllegalArgumentException("Il nemico deve avere almeno un'azione gratuita");
        }
    }

    private boolean haAzioneGratuita() {
        for (Azione azione : azioni) {
            if (azione.getCosto() == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Azione> getAzioni() {
        return azioni;
    }

    /**
     * Decide la mossa del turno, tra quelle che il nemico può pagare.
     *
     * @return l'azione scelta
     */
    public abstract Azione scegliAzione();
}
