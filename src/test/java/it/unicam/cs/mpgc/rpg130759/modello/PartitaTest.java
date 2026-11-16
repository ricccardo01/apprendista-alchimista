package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Usa generatori di prova, così i nemici sono sempre prevedibili:
 * uno debole, che cade al primo colpo, e uno fortissimo, che vince sempre.
 */
class PartitaTest {
    private final Alchimista alchimista = new Alchimista("Alchimista");

    /**
     * Crea a ogni piano lo stesso nemico, con la vita e il danno indicati.
     */
    private static class GeneratoreDiProva implements GeneratoreNemici {
        private final int vita;
        private final int danno;

        GeneratoreDiProva(int vita, int danno) {
            this.vita = vita;
            this.danno = danno;
        }

        @Override
        public Nemico creaNemico(int piano) {
            Azione colpo = new Colpo("Colpo", 0, danno, Elemento.NEUTRO);
            return new NemicoComune("Nemico di prova", Elemento.SALE, vita, 10,
                    List.of(colpo), new Random(42));
        }

        @Override
        public long getSeme() {
            return 42;
        }
    }

    private final GeneratoreNemici nemiciDeboli = new GeneratoreDiProva(1, 1);
    private final GeneratoreNemici nemiciFortissimi = new GeneratoreDiProva(1000, 1000);

    @Test
    void unaPartitaSalvataRiprendeDalPianoIndicato() {
        Partita partita = new Partita(alchimista, nemiciDeboli, 7);

        assertEquals(7, partita.getPiano());
        assertEquals(Zona.LABORATORIO, partita.getZona());
        assertEquals(42, partita.getSeme());
    }

    @Test
    void rifiutaUnPianoCheNonEsiste() {
        assertThrows(IllegalArgumentException.class, () -> new Partita(alchimista, nemiciDeboli, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Partita(alchimista, nemiciDeboli, Zona.ULTIMO_PIANO + 1));
    }

    private void vinciLoScontro(Partita partita) {
        partita.getCombattimento().turnoAlchimista(alchimista.getAzioni().get(0));
    }

    @Test
    void unaNuovaPartitaIniziaDalPrimoPiano() {
        Partita partita = new Partita(alchimista, nemiciDeboli);

        assertEquals(1, partita.getPiano());
        assertEquals(Zona.CANTINE, partita.getZona());
        assertEquals(StatoPartita.IN_CORSO, partita.getStato());
    }

    @Test
    void nonSiScendeSenzaAverVintoLoScontro() {
        Partita partita = new Partita(alchimista, nemiciDeboli);

        assertThrows(IllegalStateException.class, partita::passaAlPianoSuccessivo);
    }

    @Test
    void scendendoSiRecuperaIl30PerCentoDellaVitaETuttiIReagenti() {
        Partita partita = new Partita(alchimista, nemiciDeboli);
        Combattimento primoScontro = partita.getCombattimento();
        alchimista.subisciDanno(50);
        alchimista.consumaReagenti(15);
        vinciLoScontro(partita);

        partita.passaAlPianoSuccessivo();

        assertEquals(2, partita.getPiano());
        assertEquals(80, alchimista.getVita());
        assertEquals(20, alchimista.getReagenti());
        assertNotSame(primoScontro, partita.getCombattimento());
    }

    @Test
    void ilRecuperoNonSuperaLaVitaMassima() {
        Partita partita = new Partita(alchimista, nemiciDeboli);
        alchimista.subisciDanno(10);
        vinciLoScontro(partita);

        partita.passaAlPianoSuccessivo();

        assertEquals(100, alchimista.getVita());
    }

    @Test
    void superatoLUltimoPianoLaPartitaEVinta() {
        Partita partita = new Partita(alchimista, nemiciDeboli);
        for (int piano = 1; piano < Zona.ULTIMO_PIANO; piano++) {
            vinciLoScontro(partita);
            partita.passaAlPianoSuccessivo();
        }
        assertEquals(StatoPartita.IN_CORSO, partita.getStato());

        vinciLoScontro(partita);

        assertEquals(StatoPartita.VINTA, partita.getStato());
        assertThrows(IllegalStateException.class, partita::passaAlPianoSuccessivo);
    }

    @Test
    void seLAlchimistaVieneSconfittoLaPartitaEPersa() {
        Partita partita = new Partita(alchimista, nemiciFortissimi);

        vinciLoScontro(partita);

        assertEquals(StatoPartita.PERSA, partita.getStato());
        assertThrows(IllegalStateException.class, partita::passaAlPianoSuccessivo);
    }
}
