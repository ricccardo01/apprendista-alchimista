package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

class NemicoComuneTest {
    private static final long SEME = 42;

    private final Azione morso = new Colpo("Morso", 0, 5, Elemento.MERCURIO);
    private final Azione sputo = new Colpo("Sputo Viscido", 6, 9, Elemento.MERCURIO);

    private NemicoComune vermeCieco(int reagentiMassimi) {
        return new NemicoComune("Verme Cieco", Elemento.MERCURIO, 30, reagentiMassimi,
                List.of(morso, sputo), new Random(SEME));
    }

    @Test
    void conIReagentiSceglieTraTutteLeSueMosse() {
        NemicoComune verme = vermeCieco(10);
        Set<Azione> scelte = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            scelte.add(verme.scegliAzione());
        }

        assertEquals(Set.of(morso, sputo), scelte);
    }

    @Test
    void senzaReagentiSceglieSoloLeMosseGratuite() {
        NemicoComune verme = vermeCieco(0);

        for (int i = 0; i < 50; i++) {
            assertSame(morso, verme.scegliAzione());
        }
    }

    @Test
    void conLoStessoSemeLeScelteSonoLeStesse() {
        NemicoComune primo = vermeCieco(10);
        NemicoComune secondo = vermeCieco(10);

        for (int i = 0; i < 20; i++) {
            assertSame(primo.scegliAzione(), secondo.scegliAzione());
        }
    }

    @Test
    void unNemicoSenzaMosseGratuiteNonEAmmesso() {
        assertThrows(IllegalArgumentException.class, () -> new NemicoComune("Ragno Bianco",
                Elemento.SALE, 30, 10, List.of(sputo), new Random(SEME)));
    }

    @Test
    void leMosseNonSiPossonoModificareDaFuori() {
        NemicoComune verme = vermeCieco(10);

        assertTrue(verme.getAzioni().contains(morso));
        assertThrows(UnsupportedOperationException.class, () -> verme.getAzioni().add(morso));
    }
}
