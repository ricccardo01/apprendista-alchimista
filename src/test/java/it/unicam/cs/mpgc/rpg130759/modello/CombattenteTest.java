package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CombattenteTest {

    private static class CombattenteDiProva extends Combattente {
        CombattenteDiProva(Elemento elemento, int vitaMassima) {
            super("Prova", elemento, vitaMassima);
        }
    }

    @Test
    void iniziaConLaVitaAlMassimo() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE, 50);

        assertEquals(50, combattente.getVita());
        assertFalse(combattente.isSconfitto());
    }

    @Test
    void ilDannoTogliePuntiVita() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE, 50);

        combattente.subisciDanno(20);

        assertEquals(30, combattente.getVita());
    }

    @Test
    void aZeroVitaSiESconfittiSenzaScendereSottoZero() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE, 50);

        combattente.subisciDanno(80);

        assertEquals(0, combattente.getVita());
        assertTrue(combattente.isSconfitto());
    }

    @Test
    void laCuraNonSuperaIlMassimo() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE, 50);
        combattente.subisciDanno(10);

        combattente.cura(30);

        assertEquals(50, combattente.getVita());
    }

    @Test
    void elementoNullNonEAmmesso() {
        assertThrows(NullPointerException.class, () -> new CombattenteDiProva(null, 50));
    }

    @Test
    void vitaMassimaNonPositivaNonEAmmessa() {
        assertThrows(IllegalArgumentException.class, () -> new CombattenteDiProva(Elemento.SALE, 0));
    }

    @Test
    void dannoECuraNegativiNonSonoAmmessi() {
        Combattente combattente = new CombattenteDiProva(Elemento.SALE, 50);

        assertThrows(IllegalArgumentException.class, () -> combattente.subisciDanno(-1));
        assertThrows(IllegalArgumentException.class, () -> combattente.cura(-1));
    }
}
