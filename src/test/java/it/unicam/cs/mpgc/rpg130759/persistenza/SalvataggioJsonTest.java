package it.unicam.cs.mpgc.rpg130759.persistenza;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Ogni test scrive in una cartella temporanea che JUnit crea e cancella da solo,
 * così i test non toccano il vero salvataggio nella cartella dell'utente.
 */
class SalvataggioJsonTest {
    @TempDir
    Path cartella;

    private Path file() {
        return cartella.resolve("salvataggio.json");
    }

    @Test
    void unaPartitaSalvataSiRicaricaUguale() {
        Partita partita = new Partita(new Alchimista("Mira", 63, 7), new GeneratoreNemiciPerZona(42), 6);
        SalvataggioJson salvataggio = new SalvataggioJson(file());

        salvataggio.salva(partita);
        Partita caricata = salvataggio.carica().orElseThrow();

        assertEquals("Mira", caricata.getAlchimista().getNome());
        assertEquals(63, caricata.getAlchimista().getVita());
        assertEquals(7, caricata.getAlchimista().getReagenti());
        assertEquals(6, caricata.getPiano());
        assertEquals(42, caricata.getSeme());
    }

    @Test
    void dopoIlCaricamentoINemiciDellaZonaSonoGliStessi() {
        Partita partita = new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(42), 2);
        SalvataggioJson salvataggio = new SalvataggioJson(file());

        salvataggio.salva(partita);
        Partita caricata = salvataggio.carica().orElseThrow();

        assertEquals(partita.getCombattimento().getNemico().getNome(),
                caricata.getCombattimento().getNemico().getNome());
    }

    @Test
    void ilNuovoSalvataggioSostituisceQuelloVecchio() {
        SalvataggioJson salvataggio = new SalvataggioJson(file());
        salvataggio.salva(new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1), 3));

        salvataggio.salva(new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1), 4));

        assertEquals(4, salvataggio.carica().orElseThrow().getPiano());
    }

    @Test
    void senzaFileNonCeNessunSalvataggio() {
        assertTrue(new SalvataggioJson(file()).carica().isEmpty());
    }

    @Test
    void creaLaCartellaDelSalvataggioSeManca() {
        Path fileInSottocartella = cartella.resolve("nuova").resolve("salvataggio.json");

        new SalvataggioJson(fileInSottocartella).salva(
                new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1)));

        assertTrue(Files.exists(fileInSottocartella));
    }

    @Test
    void unFileRovinatoValeComeNessunSalvataggio() throws IOException {
        Files.writeString(file(), "questo non è JSON {");

        assertTrue(new SalvataggioJson(file()).carica().isEmpty());
    }

    @Test
    void unFileConDatiImpossibiliValeComeNessunSalvataggio() throws IOException {
        Files.writeString(file(), "{\"nome\": \"Mira\", \"vita\": 500, \"reagenti\": 5, \"piano\": 3, \"seme\": 1}");

        assertTrue(new SalvataggioJson(file()).carica().isEmpty());
    }

    @Test
    void unFileVuotoOSenzaNomeValeComeNessunSalvataggio() throws IOException {
        SalvataggioJson salvataggio = new SalvataggioJson(file());

        Files.writeString(file(), "");
        assertTrue(salvataggio.carica().isEmpty());

        Files.writeString(file(), "{\"vita\": 50, \"reagenti\": 5, \"piano\": 3, \"seme\": 1}");
        assertTrue(salvataggio.carica().isEmpty());
    }
}
