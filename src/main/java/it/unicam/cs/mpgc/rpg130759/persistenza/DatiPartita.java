package it.unicam.cs.mpgc.rpg130759.persistenza;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;

/**
 * La copia semplice di una partita, con i soli dati che finiscono nel file.
 * Il seme basta a ricreare i nemici nello stesso ordine.
 *
 * @param nome il nome dell'alchimista
 * @param vita i punti vita dell'alchimista
 * @param reagenti i reagenti dell'alchimista
 * @param piano il piano raggiunto
 * @param seme il seme del generatore dei nemici
 */
record DatiPartita(String nome, int vita, int reagenti, int piano, long seme) {

    /**
     * Copia i dati da salvare di una partita.
     *
     * @param partita la partita da copiare
     * @return i dati della partita
     */
    static DatiPartita da(Partita partita) {
        Alchimista alchimista = partita.getAlchimista();
        return new DatiPartita(alchimista.getNome(), alchimista.getVita(),
                alchimista.getReagenti(), partita.getPiano(), partita.getSeme());
    }

    /**
     * Ricrea la partita da questi dati.
     *
     * @return la partita, con lo scontro del piano appena iniziato
     * @throws IllegalArgumentException se i dati non descrivono una partita valida
     */
    Partita creaPartita() {
        if (nome == null) {
            throw new IllegalArgumentException("Nel salvataggio manca il nome");
        }
        Alchimista alchimista = new Alchimista(nome, vita, reagenti);
        return new Partita(alchimista, new GeneratoreNemiciPerZona(seme), piano);
    }
}
