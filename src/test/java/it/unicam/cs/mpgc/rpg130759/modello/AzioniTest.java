package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AzioniTest {

    @Test
    void lAttaccoBaseUsaDannoEdElementoDelloStrumentoESenzaCosto() {
        Strumento guanti = new Strumento("Guanti di prova", 6, Elemento.SALE);
        Azione attacco = new AttaccoBase(guanti);
        Combattente bersaglio = new CombattenteDiProva(Elemento.ZOLFO);

        attacco.esegui(new CombattenteDiProva(Elemento.NEUTRO), bersaglio);

        assertEquals(0, attacco.getCosto());
        assertEquals(38, bersaglio.getVita());
    }

    @Test
    void laFialaColpisceConIlSuoElemento() {
        Azione fiala = new Colpo("Fiala Ardente", 4, 10, Elemento.ZOLFO);
        Combattente bersaglio = new CombattenteDiProva(Elemento.SALE);

        fiala.esegui(new CombattenteDiProva(Elemento.NEUTRO), bersaglio);

        assertEquals(45, bersaglio.getVita());
    }

    @Test
    void laDistillazioneRidaReagentiEVitaSenzaToccareIlBersaglio() {
        Combattente chi = new CombattenteDiProva(Elemento.NEUTRO);
        Combattente bersaglio = new CombattenteDiProva(Elemento.NEUTRO);
        chi.consumaReagenti(10);
        chi.subisciDanno(10);

        new Distillazione().esegui(chi, bersaglio);

        assertEquals(18, chi.getReagenti());
        assertEquals(45, chi.getVita());
        assertEquals(50, bersaglio.getVita());
    }

    @Test
    void valoriNonValidiNelleAzioniNonSonoAmmessi() {
        assertThrows(IllegalArgumentException.class, () -> new Strumento("Rotto", 0, Elemento.NEUTRO));
        assertThrows(IllegalArgumentException.class, () -> new Colpo("Vuota", 4, 0, Elemento.ZOLFO));
        assertThrows(IllegalArgumentException.class, () -> new Colpo("Gratis", -1, 10, Elemento.ZOLFO));
        assertThrows(NullPointerException.class, () -> new AttaccoBase(null));
    }
}
