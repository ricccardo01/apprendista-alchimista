package it.unicam.cs.mpgc.rpg130759.persistenza;

import it.unicam.cs.mpgc.rpg130759.modello.Partita;

import java.util.Optional;

/**
 * Salva e carica la partita. La grafica conosce solo questa interfaccia:
 * per salvare in un altro modo (un altro formato, un database, un server
 * per la versione web) basta scrivere un'altra implementazione.
 * Si salva solo tra un piano e l'altro, quindi lo scontro in corso non viene salvato:
 * una partita caricata riprende dall'inizio dello scontro del suo piano.
 */
public interface GestoreSalvataggi {

    /**
     * Salva la partita, sostituendo il salvataggio precedente.
     *
     * @param partita la partita da salvare
     * @throws java.io.UncheckedIOException se il salvataggio non si può scrivere
     */
    void salva(Partita partita);

    /**
     * Carica la partita salvata.
     *
     * @return la partita salvata, oppure vuoto se non c'è nessun salvataggio
     *         o se il salvataggio non si può leggere
     */
    Optional<Partita> carica();

    /**
     * Cancella il salvataggio, per esempio quando la partita è finita.
     * Se non c'è nessun salvataggio non fa niente.
     *
     * @throws java.io.UncheckedIOException se il salvataggio esiste ma non si può cancellare
     */
    void cancella();
}
