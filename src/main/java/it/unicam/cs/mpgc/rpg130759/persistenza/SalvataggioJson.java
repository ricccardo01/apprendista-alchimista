package it.unicam.cs.mpgc.rpg130759.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Salva la partita in un file JSON, un formato di testo leggibile anche a occhio.
 * Di solito il file sta nella cartella dell'utente, così funziona su qualsiasi computer.
 */
public class SalvataggioJson implements GestoreSalvataggi {
    private static final Path FILE_PREDEFINITO = Path.of(
            System.getProperty("user.home"), ".apprendista-alchimista", "salvataggio.json");

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Usa il file nella cartella dell'utente: {@code .apprendista-alchimista/salvataggio.json}.
     */
    public SalvataggioJson() {
        this(FILE_PREDEFINITO);
    }

    /**
     * @param file il file in cui salvare; la cartella viene creata se manca
     */
    public SalvataggioJson(Path file) {
        this.file = Objects.requireNonNull(file, "Il file è obbligatorio");
    }

    @Override
    public void salva(Partita partita) {
        String testo = gson.toJson(DatiPartita.da(partita));
        try {
            Path cartella = file.toAbsolutePath().getParent();
            Files.createDirectories(cartella);
            Files.writeString(file, testo);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile salvare la partita", e);
        }
    }

    /**
     * Un file mancante, illeggibile o con dati impossibili vale come "nessun salvataggio":
     * il gioco parte comunque, con una partita nuova.
     */
    @Override
    public Optional<Partita> carica() {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            DatiPartita dati = gson.fromJson(Files.readString(file), DatiPartita.class);
            if (dati == null) {
                return Optional.empty();
            }
            return Optional.of(dati.creaPartita());
        } catch (IOException | JsonParseException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
