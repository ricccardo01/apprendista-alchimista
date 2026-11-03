package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void conosceAttaccoDistillazioneEUnaFormulaPerOgniPrincipio() {
        Alchimista alchimista = new Alchimista("Apprendista");
        List<String> nomi = new ArrayList<>();
        for (Azione azione : alchimista.getAzioni()) {
            nomi.add(azione.getNome());
        }

        assertEquals(List.of("Attacco con Pestello di Legno", "Distillazione", "Fiala Ardente",
                "Nebbia Mercuriale", "Polvere Cristallizzante"), nomi);
    }

    @Test
    void leAzioniNonSiPossonoModificareDaFuori() {
        Alchimista alchimista = new Alchimista("Apprendista");

        assertThrows(UnsupportedOperationException.class,
                () -> alchimista.getAzioni().add(new Distillazione()));
    }
}
