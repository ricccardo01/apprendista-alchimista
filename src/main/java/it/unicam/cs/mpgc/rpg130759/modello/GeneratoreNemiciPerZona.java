package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Crea i nemici comuni di ogni zona: tre per zona, uno per principio.
 * Nei primi piani di una zona si incontrano tutti e tre, una volta sola,
 * in un ordine mescolato all'inizio della partita.
 * Nelle zone più profonde i nemici hanno più vita e colpiscono più forte.
 * I numeri sono provvisori e si sistemano nel bilanciamento.
 */
public class GeneratoreNemiciPerZona implements GeneratoreNemici {
    private static final List<Elemento> PRINCIPI =
            List.of(Elemento.ZOLFO, Elemento.MERCURIO, Elemento.SALE);
    private static final int REAGENTI_NEMICO = 10;
    private static final int COSTO_MOSSA_FORTE = 6;

    private final Random caso;
    private final Map<Zona, List<Elemento>> ordinePerZona = new EnumMap<>(Zona.class);

    /**
     * Mescola subito l'ordine dei nemici di ogni zona.
     *
     * @param caso il generatore di numeri casuali, usato anche dai nemici per scegliere le mosse
     */
    public GeneratoreNemiciPerZona(Random caso) {
        this.caso = Objects.requireNonNull(caso, "Il generatore casuale è obbligatorio");
        for (Zona zona : Zona.values()) {
            List<Elemento> ordine = new ArrayList<>(PRINCIPI);
            Collections.shuffle(ordine, caso);
            ordinePerZona.put(zona, ordine);
        }
    }

    /**
     * Nei primi tre piani di una zona segue l'ordine mescolato.
     * Nell'ultimo piano della zona, finché non ci sono i guardiani, sceglie a caso.
     */
    @Override
    public Nemico creaNemico(int piano) {
        Zona zona = Zona.delPiano(piano);
        int posizione = Zona.posizioneNellaZona(piano);
        Elemento elemento;
        if (posizione < PRINCIPI.size()) {
            elemento = ordinePerZona.get(zona).get(posizione);
        } else {
            elemento = PRINCIPI.get(caso.nextInt(PRINCIPI.size()));
        }
        return creaNemico(zona, elemento);
    }

    private Nemico creaNemico(Zona zona, Elemento elemento) {
        return switch (zona) {
            case CANTINE -> switch (elemento) {
                case ZOLFO -> nemico("Ombra di Fuliggine", elemento, 28,
                        "Artiglio di Fumo", 6, "Nube di Fuliggine", 9);
                case MERCURIO -> nemico("Verme Cieco", elemento, 34,
                        "Morso", 5, "Stretta", 9);
                case SALE -> nemico("Ragno Bianco", elemento, 30,
                        "Morso", 5, "Puntura", 10);
                case NEUTRO -> throw nessunNemicoNeutro();
            };
            case LABORATORIO -> switch (elemento) {
                case ZOLFO -> nemico("Cane a Due Teste", elemento, 45,
                        "Morso", 8, "Doppio Morso", 14);
                case MERCURIO -> nemico("Omuncolo", elemento, 40,
                        "Graffio", 7, "Vapore di Mercurio", 13);
                case SALE -> nemico("Manichino di Gesso", elemento, 50,
                        "Pugno", 7, "Schianto", 12);
                case NEUTRO -> throw nessunNemicoNeutro();
            };
            case FORNACE -> switch (elemento) {
                case ZOLFO -> nemico("Divoratore di Cenere", elemento, 60,
                        "Morso Rovente", 11, "Fiammata", 17);
                case MERCURIO -> nemico("Apprendista Corrotto", elemento, 55,
                        "Fiala Rubata", 10, "Mercurio Bollente", 17);
                case SALE -> nemico("Mummia di Sale", elemento, 65,
                        "Colpo di Bende", 10, "Sale negli Occhi", 16);
                case NEUTRO -> throw nessunNemicoNeutro();
            };
        };
    }

    /**
     * Ogni nemico comune ha una mossa gratuita e una più forte che costa reagenti,
     * entrambe del suo elemento.
     */
    private Nemico nemico(String nome, Elemento elemento, int vita,
                          String mossaBase, int dannoBase, String mossaForte, int dannoForte) {
        List<Azione> azioni = List.of(
                new Colpo(mossaBase, 0, dannoBase, elemento),
                new Colpo(mossaForte, COSTO_MOSSA_FORTE, dannoForte, elemento));
        return new NemicoComune(nome, elemento, vita, REAGENTI_NEMICO, azioni, caso);
    }

    private static IllegalArgumentException nessunNemicoNeutro() {
        return new IllegalArgumentException("Non esistono nemici comuni neutri");
    }
}
