package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.List;
import java.util.Objects;

/**
 * Una partita intera: l'alchimista scende piano per piano e a ogni piano
 * affronta uno scontro. Tra un piano e l'altro guadagna esperienza, recupera
 * parte della vita e metà dei reagenti e, se sale di livello, può imparare
 * una delle formule proposte dal ricettario. È la classe con cui parla la grafica.
 */
public class Partita {
    private static final double RECUPERO_VITA_TRA_PIANI = 0.3;
    private static final double RECUPERO_REAGENTI_TRA_PIANI = 0.5;

    private final Alchimista alchimista;
    private final GeneratoreNemici generatore;
    private final Ricettario ricettario;
    private int piano;
    private Combattimento combattimento;
    private List<Azione> proposte = List.of();

    /**
     * Inizia la partita dal primo piano, con il suo scontro già pronto.
     *
     * @param alchimista il personaggio del giocatore
     * @param generatore chi decide i nemici dei piani
     * @param ricettario da dove arrivano le formule proposte salendo di livello
     */
    public Partita(Alchimista alchimista, GeneratoreNemici generatore, Ricettario ricettario) {
        this(alchimista, generatore, ricettario, 1);
    }

    /**
     * Riprende una partita da un certo piano, con lo scontro del piano appena iniziato.
     * Serve per continuare una partita salvata.
     *
     * @param alchimista il personaggio del giocatore
     * @param generatore chi decide i nemici dei piani
     * @param ricettario da dove arrivano le formule proposte salendo di livello
     * @param piano il piano da cui riprendere, da 1 a {@link Zona#ULTIMO_PIANO}
     * @throws IllegalArgumentException se il piano non esiste
     */
    public Partita(Alchimista alchimista, GeneratoreNemici generatore, Ricettario ricettario, int piano) {
        this.alchimista = Objects.requireNonNull(alchimista, "L'alchimista è obbligatorio");
        this.generatore = Objects.requireNonNull(generatore, "Il generatore di nemici è obbligatorio");
        this.ricettario = Objects.requireNonNull(ricettario, "Il ricettario è obbligatorio");
        if (piano < 1 || piano > Zona.ULTIMO_PIANO) {
            throw new IllegalArgumentException("Il piano deve essere tra 1 e " + Zona.ULTIMO_PIANO);
        }
        this.piano = piano;
        this.combattimento = new Combattimento(alchimista, generatore.creaNemico(piano));
    }

    public Alchimista getAlchimista() {
        return alchimista;
    }

    public int getPiano() {
        return piano;
    }

    /**
     * @return il seme del generatore dei nemici, da salvare per ricreare la partita uguale
     */
    public long getSeme() {
        return generatore.getSeme();
    }

    public Zona getZona() {
        return Zona.delPiano(piano);
    }

    public Combattimento getCombattimento() {
        return combattimento;
    }

    /**
     * La partita è persa se l'alchimista è sconfitto, vinta se ha superato
     * lo scontro dell'ultimo piano.
     *
     * @return lo stato attuale della partita
     */
    public StatoPartita getStato() {
        if (alchimista.isSconfitto()) {
            return StatoPartita.PERSA;
        }
        if (piano == Zona.ULTIMO_PIANO && combattimento.isVinto()) {
            return StatoPartita.VINTA;
        }
        return StatoPartita.IN_CORSO;
    }

    /**
     * Scende al piano successivo: l'alchimista guadagna l'esperienza dello scontro vinto
     * e perde gli effetti dello scontro, recupera il 30% della vita massima e metà
     * dei reagenti massimi, poi inizia lo scontro con il nuovo nemico.
     * Se l'alchimista è salito di livello prepara le formule da proporgli;
     * le proposte non scelte prima di scendere ancora vanno perse.
     *
     * @throws IllegalStateException se lo scontro del piano non è vinto o la partita è finita
     */
    public void passaAlPianoSuccessivo() {
        if (getStato() != StatoPartita.IN_CORSO) {
            throw new IllegalStateException("La partita è finita");
        }
        if (!combattimento.isVinto()) {
            throw new IllegalStateException("Lo scontro del piano non è ancora vinto");
        }
        boolean salito = alchimista.aggiungiEsperienza(getZona().getEsperienzaPerScontro());
        proposte = salito ? ricettario.proponi(alchimista) : List.of();
        alchimista.rimuoviEffetti();
        alchimista.cura((int) Math.round(alchimista.getVitaMassima() * RECUPERO_VITA_TRA_PIANI));
        alchimista.recuperaReagenti(
                (int) Math.round(alchimista.getReagentiMassimi() * RECUPERO_REAGENTI_TRA_PIANI));
        piano++;
        combattimento = new Combattimento(alchimista, generatore.creaNemico(piano));
    }

    /**
     * @return le formule proposte dopo l'ultimo livello guadagnato, in una lista
     *         non modificabile; vuota se non c'è niente da scegliere
     */
    public List<Azione> getProposte() {
        return proposte;
    }

    /**
     * Impara una delle formule proposte, se l'alchimista ha ancora spazio.
     *
     * @param nuova una delle formule proposte
     * @throws IllegalArgumentException se la formula non è tra le proposte
     * @throws IllegalStateException se l'alchimista conosce già il massimo delle formule
     */
    public void imparaFormula(Azione nuova) {
        controllaProposta(nuova);
        alchimista.impara(nuova);
        proposte = List.of();
    }

    /**
     * Impara una delle formule proposte dimenticandone una che l'alchimista conosce.
     *
     * @param nuova una delle formule proposte
     * @param dimenticata la formula da dimenticare
     * @throws IllegalArgumentException se la nuova non è tra le proposte
     *         o la vecchia non è conosciuta
     */
    public void imparaFormulaAlPostoDi(Azione nuova, Azione dimenticata) {
        controllaProposta(nuova);
        alchimista.sostituisci(dimenticata, nuova);
        proposte = List.of();
    }

    /**
     * Rinuncia alle formule proposte: l'alchimista tiene quelle che conosce.
     */
    public void rinunciaAllaFormula() {
        proposte = List.of();
    }

    private void controllaProposta(Azione formula) {
        if (!proposte.contains(formula)) {
            throw new IllegalArgumentException("La formula non è tra quelle proposte");
        }
    }
}
