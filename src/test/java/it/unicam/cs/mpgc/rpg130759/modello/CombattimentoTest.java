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
    void aFineTurnoEntrambiRecuperanoReagenti() {
        NemicoComune verme = vermeCieco(30, DANNO_MORSO);
        Combattimento scontro = new Combattimento(alchimista, verme);
        alchimista.consumaReagenti(10);
        verme.consumaReagenti(10);

        scontro.turnoAlchimista(attaccoBase());

        assertEquals(12, alchimista.getReagenti());
        assertEquals(2, verme.getReagenti());
    }

    @Test
    void ilRegistroNonSiModificaDaFuori() {
        Combattimento scontro = new Combattimento(alchimista, vermeCieco(30, DANNO_MORSO));

        assertThrows(UnsupportedOperationException.class,
                () -> scontro.getRegistro().add("Messaggio falso"));
    }
}
