package it.unicam.cs.mpgc.rpg130759.persistenza;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import it.unicam.cs.mpgc.rpg130759.modello.Ricettario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Ogni test scrive in una cartella temporanea che JUnit crea e cancella da solo,
 * così i test non toccano il vero salvataggio nella cartella dell'utente.
 */
class SalvataggioJsonTest {
    @TempDir
    Path cartella;

    private final Ricettario ricettario = new Ricettario(new Random(42));

    private Path file() {
        return cartella.resolve("salvataggio.json");
    }

    @Test
    void unaPartitaSalvataSiRicaricaUguale() {
        Alchimista alchimista = new Alchimista("Mira", 2, 40, 63, 7, List.of(
                ricettario.trova("Fiala Ardente"), ricettario.trova("Corazza di Sale")));
        Partita partita = new Partita(alchimista, new GeneratoreNemiciPerZona(42), ricettario, 6);
        SalvataggioJson salvataggio = new SalvataggioJson(file());

        salvataggio.salva(partita);
        Alchimista caricato = salvataggio.carica().orElseThrow().getAlchimista();

        assertEquals("Mira", caricato.getNome());
        assertEquals(2, caricato.getLivello());
        assertEquals(40, caricato.getEsperienza());
        assertEquals(63, caricato.getVita());
        assertEquals(7, caricato.getReagenti());
        assertEquals("Fiala Ardente", caricato.getFormule().get(0).getNome());
        assertEquals("Corazza di Sale", caricato.getFormule().get(1).getNome());
        assertEquals(2, caricato.getFormule().size());
    }

    @Test
    void laPartitaCaricataRiprendeDalloStessoPianoConLoStessoSeme() {
        Partita partita = new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(42), ricettario, 6);
        SalvataggioJson salvataggio = new SalvataggioJson(file());

        salvataggio.salva(partita);
        Partita caricata = salvataggio.carica().orElseThrow();

        assertEquals(6, caricata.getPiano());
        assertEquals(42, caricata.getSeme());
    }

    @Test
    void unaFormulaSconosciutaValeComeNessunSalvataggio() throws IOException {
        Files.writeString(file(), "{\"nome\": \"Mira\", \"livello\": 1, \"esperienza\": 0, \"vita\": 50, "
                + "\"reagenti\": 5, \"formule\": [\"Formula inventata\"], \"piano\": 3, \"seme\": 1}");

        assertTrue(new SalvataggioJson(file()).carica().isEmpty());
    }

    @Test
    void unSalvataggioDellaVersioneVecchiaValeComeNessunSalvataggio() throws IOException {
        Files.writeString(file(), "{\"nome\": \"Mira\", \"vita\": 50, \"reagenti\": 5, \"piano\": 3, \"seme\": 1}");

        assertTrue(new SalvataggioJson(file()).carica().isEmpty());
    }

    @Test
    void dopoIlCaricamentoINemiciDellaZonaSonoGliStessi() {
        Partita partita = new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(42), ricettario, 2);
        SalvataggioJson salvataggio = new SalvataggioJson(file());

        salvataggio.salva(partita);
        Partita caricata = salvataggio.carica().orElseThrow();

        assertEquals(partita.getCombattimento().getNemico().getNome(),
                caricata.getCombattimento().getNemico().getNome());
    }

    @Test
    void ilNuovoSalvataggioSostituisceQuelloVecchio() {
        SalvataggioJson salvataggio = new SalvataggioJson(file());
        salvataggio.salva(new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1), ricettario, 3));

        salvataggio.salva(new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1), ricettario, 4));

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
                new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1), ricettario));

        assertTrue(Files.exists(fileInSottocartella));
    }

    @Test
    void unFileRovinatoValeComeNessunSalvataggio() throws IOException {
        Files.writeString(file(), "questo non è JSON {");

        assertTrue(new SalvataggioJson(file()).carica().isEmpty());
    }

    @Test
    void unFileConDatiImpossibiliValeComeNessunSalvataggio() throws IOException {
        Files.writeString(file(), "{\"nome\": \"Mira\", \"livello\": 1, \"esperienza\": 0, \"vita\": 500, "
                + "\"reagenti\": 5, \"formule\": [], \"piano\": 3, \"seme\": 1}");

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

    @Test
    void dopoLaCancellazioneNonCeNessunSalvataggio() {
        SalvataggioJson salvataggio = new SalvataggioJson(file());
        salvataggio.salva(new Partita(new Alchimista("Mira"), new GeneratoreNemiciPerZona(1), ricettario));

        salvataggio.cancella();

        assertTrue(salvataggio.carica().isEmpty());
        assertFalse(Files.exists(file()));
    }

    @Test
    void cancellareSenzaSalvataggioNonDaErrore() {
        assertDoesNotThrow(() -> new SalvataggioJson(file()).cancella());
    }
}
