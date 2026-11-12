package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class AlchimistaTest {

    @Test
    void iniziaNeutroConVitaEReagentiAlMassimo() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertEquals(Elemento.NEUTRO, alchimista.getElemento());
        assertEquals(alchimista.getVitaMassima(), alchimista.getVita());
        assertEquals(alchimista.getReagentiMassimi(), alchimista.getReagenti());
    }

    @Test
    void iniziaConIlPestelloDiLegno() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertEquals("Pestello di Legno", alchimista.getStrumento().getNome());
    }

    @Test
    void conosceAttaccoDistillazioneEUnaFormulaPerOgniPrincipio() {
        Alchimista alchimista = new Alchimista("Allievo");
        List<String> nomi = new ArrayList<>();
        for (Azione azione : alchimista.getAzioni()) {
            nomi.add(azione.getNome());
        }

        assertEquals(List.of("Attacco con Pestello di Legno", "Distillazione", "Fiala Ardente",
                "Nebbia Mercuriale", "Polvere Cristallizzante"), nomi);
    }

    @Test
    void rifiutaUnNomeVuotoOFattoSoloDiSpazi() {
        assertThrows(IllegalArgumentException.class, () -> new Alchimista(""));
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("   "));
    }

    @Test
    void accettaUnNomeLungoFinoAlMassimoERifiutaQuelliPiuLunghi() {
        String nomeMassimo = "a".repeat(Alchimista.LUNGHEZZA_MASSIMA_NOME);

        assertEquals(nomeMassimo, new Alchimista(nomeMassimo).getNome());
        assertThrows(IllegalArgumentException.class, () -> new Alchimista(nomeMassimo + "a"));
    }

    @Test
    void leAzioniNonSiPossonoModificareDaFuori() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertThrows(UnsupportedOperationException.class,
                () -> alchimista.getAzioni().add(new Distillazione()));
    }
}
