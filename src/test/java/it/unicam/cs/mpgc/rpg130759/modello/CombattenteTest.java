package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CombattenteTest {

    @Test
    void iniziaConVitaEReagentiAlMassimo() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE, 50, 20);

        assertEquals(50, combattente.getVita());
        assertEquals(20, combattente.getReagenti());
        assertFalse(combattente.isSconfitto());
    }

    @Test
    void ilDannoTogliePuntiVita() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);

        combattente.subisciDanno(20);

        assertEquals(30, combattente.getVita());
    }

    @Test
    void aZeroVitaSiESconfittiSenzaScendereSottoZero() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);

        combattente.subisciDanno(80);

        assertEquals(0, combattente.getVita());
        assertTrue(combattente.isSconfitto());
    }

    @Test
    void laCuraNonSuperaIlMassimo() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);
        combattente.subisciDanno(10);

        combattente.cura(30);

        assertEquals(50, combattente.getVita());
    }

    @Test
    void unColpoDiElementoVantaggiosoFaIlDoppio() {
        Combattente combattente = new CombattenteDiProva(Elemento.MERCURIO);

        int danno = combattente.subisciColpo(10, Elemento.ZOLFO);

        assertEquals(20, danno);
        assertEquals(30, combattente.getVita());
    }

    @Test
    void unColpoDiElementoSvantaggiosoFaLaMetaArrotondata() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);

        int danno = combattente.subisciColpo(5, Elemento.ZOLFO);

        assertEquals(3, danno);
        assertEquals(47, combattente.getVita());
    }

    @Test
    void consumareReagentiLiToglie() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);

        combattente.consumaReagenti(5);

        assertEquals(15, combattente.getReagenti());
    }

    @Test
    void nonSiPuoConsumarePiuReagentiDiQuantiSeNeHanno() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);

        assertTrue(combattente.haReagenti(20));
        assertFalse(combattente.haReagenti(21));
        assertThrows(IllegalStateException.class, () -> combattente.consumaReagenti(21));
        assertEquals(20, combattente.getReagenti());
    }

    @Test
    void recuperareReagentiNonSuperaIlMassimo() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);
        combattente.consumaReagenti(5);

        combattente.recuperaReagenti(100);

        assertEquals(20, combattente.getReagenti());
    }

    @Test
    void usareUnAzionePagaIlCostoEApplicaLEffetto() {
        Combattente chi = new CombattenteDiProva(Elemento.NEUTRO);
        Combattente bersaglio = new CombattenteDiProva(Elemento.NEUTRO);
        Azione fiala = new LancioFiala("Fiala di prova", 4, 10, Elemento.NEUTRO);

        chi.usa(fiala, bersaglio);

        assertEquals(16, chi.getReagenti());
        assertEquals(40, bersaglio.getVita());
    }

    @Test
    void senzaReagentiLAzioneNonVieneEseguita() {
        Combattente chi = new CombattenteDiProva(Elemento.NEUTRO, 50, 3);
        Combattente bersaglio = new CombattenteDiProva(Elemento.NEUTRO);
        Azione fiala = new LancioFiala("Fiala di prova", 4, 10, Elemento.NEUTRO);

        assertThrows(IllegalStateException.class, () -> chi.usa(fiala, bersaglio));
        assertEquals(3, chi.getReagenti());
        assertEquals(50, bersaglio.getVita());
    }

    @Test
    void valoriNonValidiNelCostruttoreNonSonoAmmessi() {
        assertThrows(NullPointerException.class, () -> new CombattenteDiProva(null));
        assertThrows(IllegalArgumentException.class, () -> new CombattenteDiProva(Elemento.SALE, 0, 20));
        assertThrows(IllegalArgumentException.class, () -> new CombattenteDiProva(Elemento.SALE, 50, -1));
    }

    @Test
    void valoriNegativiNeiMetodiNonSonoAmmessi() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE);

        assertThrows(IllegalArgumentException.class, () -> combattente.subisciDanno(-1));
        assertThrows(IllegalArgumentException.class, () -> combattente.cura(-1));
        assertThrows(IllegalArgumentException.class, () -> combattente.consumaReagenti(-1));
        assertThrows(IllegalArgumentException.class, () -> combattente.recuperaReagenti(-1));
    }
}
