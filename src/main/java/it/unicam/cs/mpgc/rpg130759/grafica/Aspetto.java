package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.Elemento;
import it.unicam.cs.mpgc.rpg130759.modello.Zona;

/**
 * Raccoglie in un posto solo come la grafica mostra le cose del modello:
 * i testi usati da più schermate, i colori degli elementi e gli stili condivisi.
 */
final class Aspetto {
    /** Spazio standard tra gli elementi delle schermate, in pixel. */
    static final int SPAZIO = 12;
    static final String STILE_TITOLO = "-fx-font-size: 28px; -fx-font-weight: bold;";
    static final String STILE_TESTO = "-fx-font-size: 16px;";

    private Aspetto() {
    }

    static String coloreDi(Elemento elemento) {
        return switch (elemento) {
            case ZOLFO -> "#e3c13b";
            case MERCURIO -> "#b5bac1";
            case SALE -> "#dcebf2";
            case NEUTRO -> "#a87b50";
        };
    }

    /**
     * Livello ed esperienza dell'alchimista, per esempio "Livello 2   Esperienza 35/150".
     */
    static String testoLivello(Alchimista alchimista) {
        return "Livello " + alchimista.getLivello() + "   Esperienza "
                + alchimista.getEsperienza() + "/" + alchimista.getEsperienzaPerSalire();
    }

    static String nomeDi(Zona zona) {
        return switch (zona) {
            case CANTINE -> "Cantine";
            case LABORATORIO -> "Laboratorio";
            case FORNACE -> "Fornace";
        };
    }
}
