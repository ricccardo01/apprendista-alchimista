package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

class AlchimistaTest {
    private final Ricettario ricettario = new Ricettario(new Random(42));

    private static List<String> nomi(List<Azione> azioni) {
        List<String> nomi = new ArrayList<>();
        for (Azione azione : azioni) {
            nomi.add(azione.getNome());
        }
        return nomi;
    }

    @Test
    void iniziaNeutroAlLivelloUnoConVitaEReagentiAlMassimo() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertEquals(Elemento.NEUTRO, alchimista.getElemento());
        assertEquals(1, alchimista.getLivello());
        assertEquals(0, alchimista.getEsperienza());
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

        assertEquals(List.of("Attacco con Pestello di Legno", "Distillazione", "Fiala Ardente",
                "Nebbia Mercuriale", "Polvere Cristallizzante"), nomi(alchimista.getAzioni()));
        assertEquals(3, alchimista.getFormule().size());
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
    void leAzioniELeFormuleNonSiPossonoModificareDaFuori() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertThrows(UnsupportedOperationException.class,
                () -> alchimista.getAzioni().add(new Distillazione()));
        assertThrows(UnsupportedOperationException.class,
                () -> alchimista.getFormule().add(new Distillazione()));
    }

    @Test
    void conPocaEsperienzaNonSaleDiLivello() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertFalse(alchimista.aggiungiEsperienza(99));

        assertEquals(1, alchimista.getLivello());
        assertEquals(99, alchimista.getEsperienza());
    }

    @Test
    void salendoDiLivelloCresconoIMassimiEIPuntiInPiuRestano() {
        Alchimista alchimista = new Alchimista("Allievo");
        alchimista.subisciDanno(50);

        assertTrue(alchimista.aggiungiEsperienza(130));

        assertEquals(2, alchimista.getLivello());
        assertEquals(30, alchimista.getEsperienza());
        assertEquals(150, alchimista.getEsperienzaPerSalire());
        assertEquals(110, alchimista.getVitaMassima());
        assertEquals(60, alchimista.getVita());
        assertEquals(23, alchimista.getReagentiMassimi());
    }

    @Test
    void conTantaEsperienzaSaleDiPiuLivelliInsieme() {
        Alchimista alchimista = new Alchimista("Allievo");

        alchimista.aggiungiEsperienza(300);

        assertEquals(3, alchimista.getLivello());
        assertEquals(50, alchimista.getEsperienza());
    }

    @Test
    void imparaUnaFormulaFinoAlMassimo() {
        Alchimista alchimista = new Alchimista("Allievo");

        alchimista.impara(ricettario.trova("Veleno Sulfureo"));
        alchimista.impara(ricettario.trova("Corazza di Sale"));

        assertTrue(alchimista.conosceFormula("Corazza di Sale"));
        assertFalse(alchimista.haSpazioPerFormula());
        assertThrows(IllegalStateException.class,
                () -> alchimista.impara(ricettario.trova("Catalizzatore")));
    }

    @Test
    void nonImparaDueVolteLaStessaFormula() {
        Alchimista alchimista = new Alchimista("Allievo");

        assertThrows(IllegalArgumentException.class,
                () -> alchimista.impara(ricettario.trova("Fiala Ardente")));
    }

    @Test
    void sostituendoUnaFormulaLaNuovaPrendeIlPostoDellaVecchia() {
        Alchimista alchimista = new Alchimista("Allievo");
        Azione nebbia = alchimista.getFormule().get(1);

        alchimista.sostituisci(nebbia, ricettario.trova("Sanguisuga d'Argento"));

        assertEquals(List.of("Fiala Ardente", "Sanguisuga d'Argento", "Polvere Cristallizzante"),
                nomi(alchimista.getFormule()));
        assertThrows(IllegalArgumentException.class,
                () -> alchimista.sostituisci(nebbia, ricettario.trova("Catalizzatore")));
    }

    @Test
    void siRicreaConIDatiSalvati() {
        List<Azione> formule = List.of(ricettario.trova("Fiala Ardente"), ricettario.trova("Catalizzatore"));

        Alchimista alchimista = new Alchimista("Allievo", 3, 40, 115, 25, formule);

        assertEquals("Allievo", alchimista.getNome());
        assertEquals(3, alchimista.getLivello());
        assertEquals(40, alchimista.getEsperienza());
        assertEquals(120, alchimista.getVitaMassima());
        assertEquals(115, alchimista.getVita());
        assertEquals(25, alchimista.getReagenti());
        assertEquals(formule, alchimista.getFormule());
    }

    @Test
    void rifiutaDatiSalvatiImpossibili() {
        List<Azione> formule = List.of();
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("Allievo", 1, 0, 0, 5, formule));
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("Allievo", 1, 0, 101, 5, formule));
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("Allievo", 1, 0, 50, -1, formule));
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("Allievo", 1, 0, 50, 21, formule));
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("Allievo", 0, 0, 50, 5, formule));
        assertThrows(IllegalArgumentException.class, () -> new Alchimista("Allievo", 1, 100, 50, 5, formule));
        assertThrows(IllegalArgumentException.class,
                () -> new Alchimista("Allievo", 1, 0, 50, 5, ricettario.getFormule()));
    }
}
