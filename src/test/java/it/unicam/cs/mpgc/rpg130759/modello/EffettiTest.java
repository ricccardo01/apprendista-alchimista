package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EffettiTest {
    private final CombattenteDiProva portatore = new CombattenteDiProva(Elemento.NEUTRO);

    @Test
    void ilVelenoTogliePuntiVitaAFineTurnoPerTreTurniPoiSparisce() {
        portatore.aggiungiEffetto(new Veleno());

        portatore.applicaEffettiDiFineTurno();
        portatore.applicaEffettiDiFineTurno();
        assertEquals(42, portatore.getVita());
        assertEquals(1, portatore.getEffetti().size());

        portatore.applicaEffettiDiFineTurno();
        assertEquals(38, portatore.getVita());
        assertTrue(portatore.getEffetti().isEmpty());
    }

    @Test
    void laCorazzaDimezzaIColpiRicevutiDopoIlMoltiplicatore() {
        CombattenteDiProva mercurio = new CombattenteDiProva(Elemento.MERCURIO);
        mercurio.aggiungiEffetto(new CorazzaDiSale());

        int danno = mercurio.subisciColpo(10, Elemento.ZOLFO);

        assertEquals(10, danno);
        assertEquals(40, mercurio.getVita());
    }

    @Test
    void laCorazzaNonFermaIlVeleno() {
        portatore.aggiungiEffetto(new CorazzaDiSale());
        portatore.aggiungiEffetto(new Veleno());

        portatore.applicaEffettiDiFineTurno();

        assertEquals(46, portatore.getVita());
    }

    @Test
    void ilCatalizzatoreRaddoppiaSoloIlColpoSuccessivo() {
        portatore.aggiungiEffetto(new Catalizzatore());

        assertEquals(20, portatore.potenziaDanno(10));
        assertTrue(portatore.getEffetti().isEmpty());
        assertEquals(10, portatore.potenziaDanno(10));
    }

    @Test
    void ilCatalizzatoreNonUsatoSiPerdeDopoDueFineTurno() {
        portatore.aggiungiEffetto(new Catalizzatore());

        portatore.applicaEffettiDiFineTurno();
        assertEquals(1, portatore.getEffetti().size());
        portatore.applicaEffettiDiFineTurno();

        assertTrue(portatore.getEffetti().isEmpty());
    }

    @Test
    void laPietrificazioneBloccaSoloFinoAFineTurno() {
        portatore.aggiungiEffetto(new Pietrificazione());
        assertFalse(portatore.puoAgire());

        portatore.applicaEffettiDiFineTurno();

        assertTrue(portatore.puoAgire());
    }

    @Test
    void loStessoEffettoNonSiSommaMaRicomincia() {
        portatore.aggiungiEffetto(new Veleno());
        portatore.applicaEffettiDiFineTurno();

        portatore.aggiungiEffetto(new Veleno());

        assertEquals(1, portatore.getEffetti().size());
        assertEquals(3, portatore.getEffetti().get(0).getTurniRimasti());
    }

    @Test
    void rimuoviEffettiLiToglieTutti() {
        portatore.aggiungiEffetto(new Veleno());
        portatore.aggiungiEffetto(new CorazzaDiSale());

        portatore.rimuoviEffetti();

        assertTrue(portatore.getEffetti().isEmpty());
    }

    @Test
    void gliEffettiNonSiModificanoDaFuori() {
        assertThrows(UnsupportedOperationException.class,
                () -> portatore.getEffetti().add(new Veleno()));
    }

    @Test
    void ilColpoConEffettoColpisceEDaUnEffettoNuovoAOgniBersaglio() {
        CombattenteDiProva chi = new CombattenteDiProva(Elemento.NEUTRO);
        CombattenteDiProva primo = new CombattenteDiProva(Elemento.NEUTRO);
        CombattenteDiProva secondo = new CombattenteDiProva(Elemento.NEUTRO);
        Azione velenoSulfureo = new ColpoConEffetto("Veleno Sulfureo", 5, 4, Elemento.ZOLFO,
                () -> new Veleno());

        chi.usa(velenoSulfureo, primo);
        chi.usa(velenoSulfureo, secondo);

        assertEquals(46, primo.getVita());
        assertEquals("Veleno", primo.getEffetti().get(0).getNome());
        assertNotSame(primo.getEffetti().get(0), secondo.getEffetti().get(0));
        assertEquals(10, chi.getReagenti());
    }

    @Test
    void ilColpoConEffettoNonLasciaEffettiSuUnBersaglioSconfitto() {
        CombattenteDiProva chi = new CombattenteDiProva(Elemento.NEUTRO);
        CombattenteDiProva bersaglio = new CombattenteDiProva(Elemento.NEUTRO, 3, 0);

        chi.usa(new ColpoConEffetto("Veleno Sulfureo", 5, 4, Elemento.ZOLFO, () -> new Veleno()),
                bersaglio);

        assertTrue(bersaglio.isSconfitto());
        assertTrue(bersaglio.getEffetti().isEmpty());
    }

    @Test
    void laPreparazioneDaLEffettoAChiLaUsaENonToccaIlBersaglio() {
        CombattenteDiProva chi = new CombattenteDiProva(Elemento.NEUTRO);
        CombattenteDiProva bersaglio = new CombattenteDiProva(Elemento.NEUTRO);

        chi.usa(new Preparazione("Corazza di Sale", 5, () -> new CorazzaDiSale()), bersaglio);

        assertEquals("Corazza di Sale", chi.getEffetti().get(0).getNome());
        assertTrue(bersaglio.getEffetti().isEmpty());
        assertEquals(50, bersaglio.getVita());
        assertEquals(15, chi.getReagenti());
    }

    @Test
    void valoriNonValidiNonSonoAmmessi() {
        assertThrows(NullPointerException.class, () -> portatore.aggiungiEffetto(null));
        assertThrows(NullPointerException.class,
                () -> new Preparazione("Senza effetto", 1, null));
        assertThrows(IllegalArgumentException.class,
                () -> new Preparazione("Costo negativo", -1, () -> new Veleno()));
    }
}
