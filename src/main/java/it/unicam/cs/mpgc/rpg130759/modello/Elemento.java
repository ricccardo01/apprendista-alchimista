package it.unicam.cs.mpgc.rpg130759.modello;

/**
 * I Tre Principi di Paracelso, più l'elemento neutro.
 * Zolfo prevale su Mercurio, Mercurio prevale su Sale, Sale prevale su Zolfo.
 * Neutro non prevale su nessuno e nessuno prevale su di lui.
 */
public enum Elemento {
    ZOLFO, MERCURIO, SALE, NEUTRO;

    private static final double VANTAGGIO = 2.0;
    private static final double SVANTAGGIO = 0.5;
    private static final double NORMALE = 1.0;

    /**
     * Restituisce il moltiplicatore del danno quando questo elemento colpisce il bersaglio.
     *
     * @param bersaglio l'elemento di chi subisce il colpo
     * @return 2 se questo elemento prevale, 0,5 se il bersaglio prevale, altrimenti 1
     */
    public double moltiplicatoreContro(Elemento bersaglio) {
        if (prevaleSu(bersaglio)) {
            return VANTAGGIO;
        }
        if (bersaglio.prevaleSu(this)) {
            return SVANTAGGIO;
        }
        return NORMALE;
    }

    private boolean prevaleSu(Elemento altro) {
        return switch (this) {
            case ZOLFO -> altro == MERCURIO;
            case MERCURIO -> altro == SALE;
            case SALE -> altro == ZOLFO;
            case NEUTRO -> false;
        };
    }
}
