package it.unicam.cs.mpgc.rpg130759.persistenza;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.Azione;
import it.unicam.cs.mpgc.rpg130759.modello.GeneratoreNemiciPerZona;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import it.unicam.cs.mpgc.rpg130759.modello.Ricettario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La copia semplice di una partita, con i soli dati che finiscono nel file.
 * Il seme basta a ricreare i nemici nello stesso ordine; delle formule
 * si salva solo il nome, perché il ricettario sa ricrearle.
 *
 * @param nome il nome dell'alchimista
 * @param livello il livello dell'alchimista
 * @param esperienza i punti esperienza verso il livello successivo
 * @param vita i punti vita dell'alchimista
 * @param reagenti i reagenti dell'alchimista
 * @param formule i nomi delle formule conosciute
 * @param piano il piano raggiunto
 * @param seme il seme del generatore dei nemici
 */
record DatiPartita(String nome, int livello, int esperienza, int vita, int reagenti,
                   List<String> formule, int piano, long seme) {

    /**
     * Copia i dati da salvare di una partita.
     *
     * @param partita la partita da copiare
     * @return i dati della partita
     */
    static DatiPartita da(Partita partita) {
        Alchimista alchimista = partita.getAlchimista();
        List<String> nomiFormule = new ArrayList<>();
        for (Azione formula : alchimista.getFormule()) {
            nomiFormule.add(formula.getNome());
        }
        return new DatiPartita(alchimista.getNome(), alchimista.getLivello(), alchimista.getEsperienza(),
                alchimista.getVita(), alchimista.getReagenti(), nomiFormule,
                partita.getPiano(), partita.getSeme());
    }

    /**
     * Ricrea la partita da questi dati.
     *
     * @return la partita, con lo scontro del piano appena iniziato
     * @throws IllegalArgumentException se i dati non descrivono una partita valida
     */
    Partita creaPartita() {
        if (nome == null || formule == null) {
            throw new IllegalArgumentException("Nel salvataggio mancano il nome o le formule");
        }
        Ricettario ricettario = new Ricettario(new Random());
        List<Azione> formuleConosciute = new ArrayList<>();
        for (String nomeFormula : formule) {
            formuleConosciute.add(ricettario.trova(nomeFormula));
        }
        Alchimista alchimista = new Alchimista(nome, livello, esperienza, vita, reagenti, formuleConosciute);
        return new Partita(alchimista, new GeneratoreNemiciPerZona(seme), ricettario, piano);
    }
}
