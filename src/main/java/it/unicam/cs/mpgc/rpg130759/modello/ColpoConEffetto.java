package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Un colpo che, oltre al danno, lascia un effetto addosso al bersaglio.
 * L'effetto viene creato nuovo a ogni uso, perché conta i suoi turni
 * e non può essere condiviso tra due lanci.
 */
public class ColpoConEffetto extends Colpo {
    private final Supplier<Effetto> creaEffetto;

    /**
     * @param nome il nome della mossa
     * @param costo i reagenti consumati, non negativi
     * @param danno il danno prima del moltiplicatore, maggiore di zero
     * @param elemento l'elemento del colpo
     * @param creaEffetto crea l'effetto da applicare al bersaglio, per esempio {@code () -> new Veleno()}
     */
    public ColpoConEffetto(String nome, int costo, int danno, Elemento elemento,
                           Supplier<Effetto> creaEffetto) {
        super(nome, costo, danno, elemento);
        this.creaEffetto = Objects.requireNonNull(creaEffetto, "L'effetto è obbligatorio");
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        super.esegui(chi, bersaglio);
        if (!bersaglio.isSconfitto()) {
            bersaglio.aggiungiEffetto(creaEffetto.get());
        }
    }
}
