package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

class RicettarioTest {
    private final Ricettario ricettario = new Ricettario(new Random(42));

    @Test
    void contieneLeTreFormuleInizialiESetteDaImparare() {
        assertEquals(10, ricettario.getFormule().size());
        assertEquals(3, Ricettario.formuleIniziali().size());
    }

    @Test
    void trovaUnaFormulaDalNome() {
        assertEquals("Corazza di Sale", ricettario.trova("Corazza di Sale").getNome());
        assertThrows(IllegalArgumentException.class, () -> ricettario.trova("Formula inventata"));
    }

    @Test
    void proponeDueFormuleDiverseCheLAlchimistaNonConosce() {
        Alchimista alchimista = new Alchimista("Allievo");

        for (int prova = 0; prova < 50; prova++) {
            List<Azione> proposte = ricettario.proponi(alchimista);

            assertEquals(Ricettario.NUMERO_PROPOSTE, proposte.size());
            assertNotEquals(proposte.get(0), proposte.get(1));
            for (Azione formula : proposte) {
                assertFalse(alchimista.conosceFormula(formula.getNome()));
            }
        }
    }

    @Test
    void puoProporreUnaFormulaInizialeDimenticata() {
        Alchimista alchimista = new Alchimista("Allievo", 1, 0, 100, 20,
                List.of(ricettario.trova("Fiala Ardente"), ricettario.trova("Nebbia Mercuriale"),
                        ricettario.trova("Veleno Sulfureo"), ricettario.trova("Esplosione di Zolfo"),
                        ricettario.trova("Sanguisuga d'Argento")));
        boolean propostaLaPolvere = false;

        for (int prova = 0; prova < 100; prova++) {
            for (Azione formula : ricettario.proponi(alchimista)) {
                if (formula.getNome().equals("Polvere Cristallizzante")) {
                    propostaLaPolvere = true;
                }
            }
        }

        assertTrue(propostaLaPolvere);
    }

    @Test
    void leProposteNonSiPossonoModificareDaFuori() {
        List<Azione> proposte = ricettario.proponi(new Alchimista("Allievo"));

        assertThrows(UnsupportedOperationException.class, () -> proposte.add(new Distillazione()));
    }
}
