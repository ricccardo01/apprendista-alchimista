package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

class FormuleSpecialiTest {
    private final CombattenteDiProva chi = new CombattenteDiProva(Elemento.NEUTRO);
    private final CombattenteDiProva bersaglio = new CombattenteDiProva(Elemento.NEUTRO);

    @Test
    void ilColpoAssorbenteCuraMetaDellaVitaTolta() {
        chi.subisciDanno(20);
        Azione sanguisuga = new ColpoAssorbente("Sanguisuga", 6, 8, Elemento.MERCURIO);

        sanguisuga.esegui(chi, bersaglio);

        assertEquals(42, bersaglio.getVita());
        assertEquals(34, chi.getVita());
    }

    @Test
    void ilColpoAssorbenteCuraMenoSeIlBersaglioEProtetto() {
        chi.subisciDanno(20);
        bersaglio.aggiungiEffetto(new CorazzaDiSale());
        Azione sanguisuga = new ColpoAssorbente("Sanguisuga", 6, 8, Elemento.MERCURIO);

        sanguisuga.esegui(chi, bersaglio);

        assertEquals(46, bersaglio.getVita());
        assertEquals(32, chi.getVita());
    }

    @Test
    void ilColpoConRinculoFerisceAncheChiLoUsa() {
        Azione esplosione = new ColpoConRinculo("Esplosione", 8, 18, Elemento.ZOLFO, 5);

        esplosione.esegui(chi, bersaglio);

        assertEquals(32, bersaglio.getVita());
        assertEquals(45, chi.getVita());
    }

    @Test
    void ilRinculoNonFaMaiScendereSottoUnPuntoVita() {
        Azione esplosione = new ColpoConRinculo("Esplosione", 8, 18, Elemento.ZOLFO, 5);
        chi.subisciDanno(47);

        esplosione.esegui(chi, bersaglio);
        assertEquals(1, chi.getVita());

        esplosione.esegui(chi, bersaglio);
        assertEquals(1, chi.getVita());
    }

    @Test
    void unRinculoNonPositivoNonEAmmesso() {
        assertThrows(IllegalArgumentException.class,
                () -> new ColpoConRinculo("Esplosione", 8, 18, Elemento.ZOLFO, 0));
    }

    @Test
    void ilColpoInstabileFaSempreUnDannoTraMinimoEMassimo() {
        Azione instabile = new ColpoInstabile("Instabile", 5, 2, 20, Elemento.MERCURIO, new Random(42));
        CombattenteDiProva resistente = new CombattenteDiProva(Elemento.NEUTRO, 100_000, 0);

        for (int i = 0; i < 200; i++) {
            int vitaPrima = resistente.getVita();
            instabile.esegui(chi, resistente);
            int danno = vitaPrima - resistente.getVita();
            assertTrue(danno >= 2 && danno <= 20, "Danno fuori dai limiti: " + danno);
        }
    }

    @Test
    void ilColpoInstabileVienePotenziatoDalCatalizzatore() {
        Azione instabile = new ColpoInstabile("Instabile", 5, 7, 7, Elemento.MERCURIO, new Random(42));
        chi.aggiungiEffetto(new Catalizzatore());

        instabile.esegui(chi, bersaglio);

        assertEquals(36, bersaglio.getVita());
    }

    @Test
    void ilColpoInstabileRifiutaUnMassimoMinoreDelMinimo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ColpoInstabile("Instabile", 5, 10, 2, Elemento.MERCURIO, new Random(42)));
    }
}
