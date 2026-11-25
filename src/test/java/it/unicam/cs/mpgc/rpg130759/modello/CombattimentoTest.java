package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Il nemico di prova ha una sola mossa, così il suo turno è sempre prevedibile.
 */
class CombattimentoTest {
    private static final int DANNO_MORSO = 5;

    private final Alchimista alchimista = new Alchimista("Alchimista");

    private NemicoComune vermeCieco(int vita, int dannoMorso) {
        Azione morso = new Colpo("Morso", 0, dannoMorso, Elemento.MERCURIO);
        return new NemicoComune("Verme Cieco", Elemento.MERCURIO, vita, 10,
                List.of(morso), new Random(42));
    }

    private Azione mossa(String nome) {
        for (Azione azione : alchimista.getAzioni()) {
            if (azione.getNome().equals(nome)) {
                return azione;
            }
        }
        throw new IllegalArgumentException("Mossa non trovata: " + nome);
    }

    private Azione attaccoBase() {
        return mossa("Attacco con Pestello di Legno");
    }

    @Test
    void unTurnoColpisceIlNemicoEPoiLAlchimista() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));

        scontro.turnoAlchimista(attaccoBase());

        assertEquals(24, scontro.getNemico().getVita());
        assertEquals(95, alchimista.getVita());
        assertEquals(List.of(
                "Alchimista usa Attacco con Pestello di Legno: 6 danni a Verme Cieco",
                "Verme Cieco usa Morso: 5 danni a Alchimista"), scontro.getRegistro());
        assertFalse(scontro.isFinito());
    }

    @Test
    void ilRinculoFinisceNelRegistroENeiDanniSubiti() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Esplosione di Zolfo"));

        assertEquals(64, scontro.getNemico().getVita());
        assertEquals(90, alchimista.getVita());
        assertEquals(10, scontro.getDannoSubito());
        assertEquals(List.of(
                "Alchimista usa Esplosione di Zolfo: 36 danni a Verme Cieco",
                "Alchimista perde 5 punti vita",
                "Verme Cieco usa Morso: 5 danni a Alchimista"), scontro.getRegistro());
    }

    @Test
    void laSanguisugaScriveNelRegistroLaVitaRecuperata() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(attaccoBase());
        scontro.turnoAlchimista(mossa("Sanguisuga d'Argento"));

        assertEquals(94, alchimista.getVita());
        assertEquals("Alchimista usa Sanguisuga d'Argento: 8 danni a Verme Cieco",
                scontro.getRegistro().get(2));
        assertEquals("Alchimista recupera 4 punti vita", scontro.getRegistro().get(3));
    }

    @Test
    void leStatisticheContanoLaVitaPersaDavveroEIReagentiSpesi() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Fiala Ardente"));
        scontro.turnoAlchimista(mossa("Fiala Ardente"));

        assertTrue(scontro.isVinto());
        assertEquals(2, scontro.getTurni());
        assertEquals(30, scontro.getDannoInflitto());
        assertEquals(DANNO_MORSO, scontro.getDannoSubito());
        assertEquals(10, scontro.getReagentiSpesi());
    }

    @Test
    void lElementoDelNemicoSiScopreDopoLaSuaPrimaMossa() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));
        assertFalse(scontro.isElementoNemicoRivelato());

        scontro.turnoAlchimista(attaccoBase());

        assertTrue(scontro.isElementoNemicoRivelato());
    }

    @Test
    void seIlNemicoMuorePrimaDiMuovereLElementoRestaNascosto() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(5, DANNO_MORSO));

        scontro.turnoAlchimista(attaccoBase());

        assertFalse(scontro.isElementoNemicoRivelato());
    }

    @Test
    void ilVantaggioDiElementoValeAncheNelloScontro() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Fiala Ardente"));

        assertEquals(80, scontro.getNemico().getVita());
    }

    @Test
    void seIlNemicoVieneSconfittoNonContrattacca() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(6, DANNO_MORSO));

        scontro.turnoAlchimista(attaccoBase());

        assertEquals(100, alchimista.getVita());
        assertTrue(scontro.isFinito());
        assertTrue(scontro.isVinto());
        assertEquals("Verme Cieco è sconfitto", scontro.getRegistro().get(1));
    }

    @Test
    void seLAlchimistaVieneSconfittoLoScontroEPerso() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, 200));

        scontro.turnoAlchimista(attaccoBase());

        assertTrue(alchimista.isSconfitto());
        assertTrue(scontro.isFinito());
        assertFalse(scontro.isVinto());
    }

    @Test
    void dopoLaFineNonSiGiocaPiu() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(6, DANNO_MORSO));
        scontro.turnoAlchimista(attaccoBase());

        assertThrows(IllegalStateException.class, () -> scontro.turnoAlchimista(attaccoBase()));
    }

    @Test
    void nonSiUsaUnaMossaCheLAlchimistaNonConosce() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));
        Azione sconosciuta = new Colpo("Fulmine", 0, 50, Elemento.NEUTRO);

        assertThrows(IllegalArgumentException.class, () -> scontro.turnoAlchimista(sconosciuta));
        assertEquals(30, scontro.getNemico().getVita());
    }

    @Test
    void senzaReagentiLaFialaNonParteEIlNemicoNonMuove() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));
        alchimista.consumaReagenti(20);

        assertThrows(IllegalStateException.class,
                () -> scontro.turnoAlchimista(mossa("Fiala Ardente")));
        assertEquals(100, alchimista.getVita());
        assertTrue(scontro.getRegistro().isEmpty());
    }

    @Test
    void aFineTurnoSoloIlNemicoRecuperaReagenti() {
        NemicoComune verme = vermeCieco(30, DANNO_MORSO);
        Combattimento scontro = new Combattimento(alchimista, verme);
        alchimista.consumaReagenti(10);
        verme.consumaReagenti(10);

        scontro.turnoAlchimista(attaccoBase());

        assertEquals(10, alchimista.getReagenti());
        assertEquals(2, verme.getReagenti());
    }

    @Test
    void laDistillazioneSiUsaAlMassimoDueVoltePerScontro() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));
        Azione distillazione = mossa("Distillazione");
        assertEquals(2, scontro.getUsiRimasti(distillazione));

        scontro.turnoAlchimista(distillazione);
        scontro.turnoAlchimista(distillazione);

        assertEquals(0, scontro.getUsiRimasti(distillazione));
        assertFalse(scontro.puoUsare(distillazione));
        assertThrows(IllegalStateException.class, () -> scontro.turnoAlchimista(distillazione));
        assertEquals(2, scontro.getTurni());
    }

    @Test
    void gliUsiDellaDistillazioneRipartonoInUnNuovoScontro() {
        Combattimento primo = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));
        primo.turnoAlchimista(mossa("Distillazione"));
        primo.turnoAlchimista(mossa("Distillazione"));

        Combattimento secondo = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        assertTrue(secondo.puoUsare(mossa("Distillazione")));
    }

    @Test
    void leMosseSenzaLimiteNonFinisconoMai() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(attaccoBase());

        assertEquals(Azione.SENZA_LIMITE, scontro.getUsiRimasti(attaccoBase()));
    }

    @Test
    void ilNemicoPietrificatoSaltaIlTurnoELElementoRestaNascosto() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Cristalli Pietrificanti"));

        assertEquals(100, alchimista.getVita());
        assertEquals("Verme Cieco è bloccato e salta il turno", scontro.getRegistro().get(1));
        assertFalse(scontro.isElementoNemicoRivelato());
        assertTrue(scontro.getNemico().puoAgire());
    }

    @Test
    void ilVelenoAgisceAFineTurnoEContaTraIDanniInflitti() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Veleno Sulfureo"));

        assertEquals(88, scontro.getNemico().getVita());
        assertEquals(12, scontro.getDannoInflitto());
        assertEquals("Verme Cieco perde 4 punti vita a fine turno", scontro.getRegistro().get(2));
    }

    @Test
    void seIlVelenoSconfiggeIlNemicoLoScontroEVinto() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(12, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Veleno Sulfureo"));

        assertTrue(scontro.isVinto());
        assertEquals("Verme Cieco è sconfitto", scontro.getRegistro().get(3));
    }

    @Test
    void seLAlchimistaCadeIlVelenoNonFaInTempoAdAgire() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(10, 200));

        scontro.turnoAlchimista(mossa("Veleno Sulfureo"));

        assertEquals(2, scontro.getNemico().getVita());
        assertTrue(scontro.isFinito());
        assertFalse(scontro.isVinto());
    }

    @Test
    void laCorazzaProteggeGiaDallaRispostaDelNemico() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, 10));

        scontro.turnoAlchimista(mossa("Corazza di Sale"));

        assertEquals(95, alchimista.getVita());
    }

    @Test
    void ilCatalizzatoreRaddoppiaIlColpoDelTurnoDopo() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(100, DANNO_MORSO));

        scontro.turnoAlchimista(mossa("Catalizzatore"));
        scontro.turnoAlchimista(mossa("Fiala Ardente"));

        assertEquals(60, scontro.getNemico().getVita());
    }

    @Test
    void ilRegistroNonSiModificaDaFuori() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));

        assertThrows(UnsupportedOperationException.class,
                () -> scontro.getRegistro().add("Messaggio falso"));
    }
}
