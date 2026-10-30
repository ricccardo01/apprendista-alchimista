package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AlchimistaTest {

    @Test
    void iniziaNeutroConVitaEReagentiAlMassimo() {
        Alchimista alchimista = new Alchimista("Apprendista");

        assertEquals(Elemento.NEUTRO, alchimista.getElemento());
        assertEquals(alchimista.getVitaMassima(), alchimista.getVita());
        assertEquals(alchimista.getReagentiMassimi(), alchimista.getReagenti());
    }

    @Test
    void consumareReagentiLiToglie() {
        Alchimista alchimista = new Alchimista("Apprendista");
        int iniziali = alchimista.getReagenti();

        alchimista.consumaReagenti(5);

        assertEquals(iniziali - 5, alchimista.getReagenti());
    }

    @Test
    void nonSiPuoConsumarePiuReagentiDiQuantiSeNeHanno() {
        Alchimista alchimista = new Alchimista("Apprendista");
        int troppi = alchimista.getReagenti() + 1;

        assertFalse(alchimista.haReagenti(troppi));
        assertThrows(IllegalStateException.class, () -> alchimista.consumaReagenti(troppi));
        assertEquals(alchimista.getReagentiMassimi(), alchimista.getReagenti());
    }

    @Test
    void iReagentiBastanoAncheSeSonoEsattamenteIlCosto() {
        Alchimista alchimista = new Alchimista("Apprendista");

        assertTrue(alchimista.haReagenti(alchimista.getReagenti()));
    }

    @Test
    void recuperareReagentiNonSuperaIlMassimo() {
        Alchimista alchimista = new Alchimista("Apprendista");
        alchimista.consumaReagenti(5);

        alchimista.recuperaReagenti(100);

        assertEquals(alchimista.getReagentiMassimi(), alchimista.getReagenti());
    }

    @Test
    void costoEQuantitaNegativiNonSonoAmmessi() {
        Alchimista alchimista = new Alchimista("Apprendista");

        assertThrows(IllegalArgumentException.class, () -> alchimista.consumaReagenti(-1));
        assertThrows(IllegalArgumentException.class, () -> alchimista.recuperaReagenti(-1));
    }
}
