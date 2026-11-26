package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Usa generatori di prova, così i nemici sono sempre prevedibili:
 * uno debole, che cade al primo colpo, e uno fortissimo, che vince sempre.
 */
class PartitaTest {
    private final Alchimista alchimista = new Alchimista("Alchimista");
    private final Ricettario ricettario = new Ricettario(new Random(42));

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
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario, 7);

        assertEquals(7, partita.getPiano());
        assertEquals(Zona.LABORATORIO, partita.getZona());
        assertEquals(42, partita.getSeme());
    }

    @Test
    void rifiutaUnPianoCheNonEsiste() {
        assertThrows(IllegalArgumentException.class, () -> new Partita(alchimista, nemiciDeboli, ricettario, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Partita(alchimista, nemiciDeboli, ricettario, Zona.ULTIMO_PIANO + 1));
    }

    private void vinciLoScontro(Partita partita) {
        partita.getCombattimento().turnoAlchimista(alchimista.getAzioni().get(0));
    }

    @Test
    void unaNuovaPartitaIniziaDalPrimoPiano() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);

        assertEquals(1, partita.getPiano());
        assertEquals(Zona.CANTINE, partita.getZona());
        assertEquals(StatoPartita.IN_CORSO, partita.getStato());
    }

    @Test
    void nonSiScendeSenzaAverVintoLoScontro() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);

        assertThrows(IllegalStateException.class, partita::passaAlPianoSuccessivo);
    }

    @Test
    void scendendoSiRecuperaIl30PerCentoDellaVitaEMetaDeiReagenti() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);
        Combattimento primoScontro = partita.getCombattimento();
        alchimista.subisciDanno(50);
        alchimista.consumaReagenti(15);
        vinciLoScontro(partita);

        partita.passaAlPianoSuccessivo();

        assertEquals(2, partita.getPiano());
        assertEquals(80, alchimista.getVita());
        assertEquals(15, alchimista.getReagenti());
        assertNotSame(primoScontro, partita.getCombattimento());
    }

    @Test
    void scendendoSiPerdonoGliEffettiDelloScontro() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);
        alchimista.aggiungiEffetto(new CorazzaDiSale());
        vinciLoScontro(partita);

        partita.passaAlPianoSuccessivo();

        assertTrue(alchimista.getEffetti().isEmpty());
    }

    @Test
    void ilRecuperoNonSuperaLaVitaMassima() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);
        alchimista.subisciDanno(10);
        vinciLoScontro(partita);

        partita.passaAlPianoSuccessivo();

        assertEquals(100, alchimista.getVita());
    }

    @Test
    void superatoLUltimoPianoLaPartitaEVinta() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);
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
    void scendendoSiGuadagnaLEsperienzaDellaZonaSenzaProposte() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);
        vinciLoScontro(partita);

        partita.passaAlPianoSuccessivo();

        assertEquals(Zona.CANTINE.getEsperienzaPerScontro(), alchimista.getEsperienza());
        assertTrue(partita.getProposte().isEmpty());
    }

    /**
     * Porta l'alchimista a un passo dal livello 2 e lo fa scendere di un piano,
     * così sale di livello e riceve le proposte.
     */
    private Partita partitaConProposte() {
        Partita partita = new Partita(alchimista, nemiciDeboli, ricettario);
        alchimista.aggiungiEsperienza(alchimista.getEsperienzaPerSalire() - 1);
        vinciLoScontro(partita);
        partita.passaAlPianoSuccessivo();
        return partita;
    }

    @Test
    void salendoDiLivelloArrivanoLeProposteDelRicettario() {
        Partita partita = partitaConProposte();

        assertEquals(2, alchimista.getLivello());
        assertEquals(Ricettario.NUMERO_PROPOSTE, partita.getProposte().size());
    }

    @Test
    void imparandoUnaFormulaPropostaLeProposteSpariscono() {
        Partita partita = partitaConProposte();
        Azione scelta = partita.getProposte().get(0);

        partita.imparaFormula(scelta);

        assertTrue(alchimista.getFormule().contains(scelta));
        assertTrue(partita.getProposte().isEmpty());
    }

    @Test
    void conIlMassimoDelleFormuleBisognaDimenticarneUna() {
        alchimista.impara(ricettario.trova("Corazza di Sale"));
        alchimista.impara(ricettario.trova("Catalizzatore"));
        Partita partita = partitaConProposte();
        Azione scelta = partita.getProposte().get(0);
        Azione fiala = alchimista.getFormule().get(0);

        assertThrows(IllegalStateException.class, () -> partita.imparaFormula(scelta));
        partita.imparaFormulaAlPostoDi(scelta, fiala);

        assertTrue(alchimista.getFormule().contains(scelta));
        assertFalse(alchimista.getFormule().contains(fiala));
        assertEquals(Alchimista.MASSIMO_FORMULE, alchimista.getFormule().size());
    }

    @Test
    void rinunciandoLeFormuleRestanoQuelle() {
        Partita partita = partitaConProposte();
        List<Azione> formulePrima = List.copyOf(alchimista.getFormule());

        partita.rinunciaAllaFormula();

        assertEquals(formulePrima, alchimista.getFormule());
        assertTrue(partita.getProposte().isEmpty());
    }

    @Test
    void nonSiImparaUnaFormulaNonProposta() {
        Partita partita = partitaConProposte();
        Azione nonProposta = null;
        for (Azione formula : ricettario.getFormule()) {
            if (!partita.getProposte().contains(formula) && !alchimista.conosceFormula(formula.getNome())) {
                nonProposta = formula;
            }
        }
        Azione daImparare = nonProposta;

        assertThrows(IllegalArgumentException.class, () -> partita.imparaFormula(daImparare));
    }

    @Test
    void seLAlchimistaVieneSconfittoLaPartitaEPersa() {
        Partita partita = new Partita(alchimista, nemiciFortissimi, ricettario);

        vinciLoScontro(partita);

        assertEquals(StatoPartita.PERSA, partita.getStato());
        assertThrows(IllegalStateException.class, partita::passaAlPianoSuccessivo);
    }
}
