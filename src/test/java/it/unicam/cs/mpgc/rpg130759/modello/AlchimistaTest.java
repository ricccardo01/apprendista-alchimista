package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void iniziaConIlPestelloDiLegno() {
        Alchimista alchimista = new Alchimista("Apprendista");

        assertEquals("Pestello di Legno", alchimista.getStrumento().getNome());
    }
}
