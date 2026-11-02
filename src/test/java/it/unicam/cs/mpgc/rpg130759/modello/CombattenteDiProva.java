package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * Sottoclasse minima di {@link Combattente}, usata solo nei test
 * perché una classe astratta non si può creare direttamente.
 */
class CombattenteDiProva extends Combattente {

    CombattenteDiProva(Elemento elemento, int vitaMassima, int reagentiMassimi) {
        super("Prova", elemento, vitaMassima, reagentiMassimi);
    }

    CombattenteDiProva(Elemento elemento) {
        this(elemento, 50, 20);
    }
}
