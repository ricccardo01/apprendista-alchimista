package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.Objects;

/**
 * Il colpo dato con lo strumento impugnato. Non costa reagenti.
 */
public class AttaccoBase implements Azione {
    private final Strumento strumento;

    /**
     * @param strumento lo strumento con cui si colpisce
     */
    public AttaccoBase(Strumento strumento) {
        this.strumento = Objects.requireNonNull(strumento, "Lo strumento è obbligatorio");
    }

    @Override
    public String getNome() {
        return "Attacco con " + strumento.getNome();
    }

    @Override
    public int getCosto() {
        return 0;
    }

    @Override
    public void esegui(Combattente chi, Combattente bersaglio) {
        bersaglio.subisciColpo(strumento.getDanno(), strumento.getElemento());
    }
}
