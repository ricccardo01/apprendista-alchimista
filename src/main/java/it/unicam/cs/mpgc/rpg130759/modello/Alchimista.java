package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Il personaggio del giocatore. Oltre a ciò che ha ogni combattente
 * impugna uno strumento, con cui fa l'attacco base, guadagna esperienza,
 * sale di livello e conosce un numero limitato di formule.
 */
public class Alchimista extends Combattente {
    /** Il nome più lungo che il giocatore può scegliere. */
    public static final int LUNGHEZZA_MASSIMA_NOME = 10;

    /** Quante formule si possono conoscere insieme, oltre all'attacco base e alla Distillazione. */
    public static final int MASSIMO_FORMULE = 5;

    private static final int VITA_INIZIALE = 100;
    private static final int REAGENTI_INIZIALI = 20;
    private static final int VITA_PER_LIVELLO = 10;
    private static final int REAGENTI_PER_LIVELLO = 3;
    private static final int ESPERIENZA_PRIMO_LIVELLO = 100;
    private static final int AUMENTO_SOGLIA_PER_LIVELLO = 50;
    private static final Strumento STRUMENTO_INIZIALE =
            new Strumento("Pestello di Legno", 6, Elemento.NEUTRO);

    private final Strumento strumento;
    private final Azione attaccoBase;
    private final Azione distillazione;
    private final List<Azione> formule = new ArrayList<>();
    private int livello;
    private int esperienza;

    /**
     * Crea un alchimista all'inizio della partita: livello 1, con il pestello in mano,
     * senza elemento proprio (così nessun principio ha un vantaggio fisso contro di lui)
     * e con una formula per ogni principio.
     *
     * @param nome il nome scelto dal giocatore, da 1 a {@link #LUNGHEZZA_MASSIMA_NOME} caratteri
     * @throws IllegalArgumentException se il nome è vuoto o troppo lungo
     */
    public Alchimista(String nome) {
        this(nome, 1, 0, VITA_INIZIALE, REAGENTI_INIZIALI, Ricettario.formuleIniziali());
    }

    /**
     * Ricrea un alchimista con i dati di una partita salvata.
     *
     * @param nome il nome scelto dal giocatore
     * @param livello il livello, almeno 1
     * @param esperienza i punti raccolti verso il livello successivo,
     *                   da 0 a {@link #getEsperienzaPerSalire()} escluso
     * @param vita i punti vita, da 1 alla vita massima del livello
     * @param reagenti i reagenti, da 0 al massimo del livello
     * @param formule le formule conosciute, al massimo {@link #MASSIMO_FORMULE}, senza doppioni
     * @throws IllegalArgumentException se uno dei dati non è valido
     */
    public Alchimista(String nome, int livello, int esperienza, int vita, int reagenti,
                      List<Azione> formule) {
        super(controllaNome(nome), Elemento.NEUTRO, VITA_INIZIALE, REAGENTI_INIZIALI);
        this.strumento = STRUMENTO_INIZIALE;
        this.attaccoBase = new AttaccoBase(strumento);
        this.distillazione = new Distillazione();
        if (livello < 1) {
            throw new IllegalArgumentException("Il livello deve essere almeno 1");
        }
        for (int i = 1; i < livello; i++) {
            aumentaMassimi(VITA_PER_LIVELLO, REAGENTI_PER_LIVELLO);
        }
        this.livello = livello;
        if (esperienza < 0 || esperienza >= getEsperienzaPerSalire()) {
            throw new IllegalArgumentException("L'esperienza deve essere tra 0 e " + getEsperienzaPerSalire());
        }
        this.esperienza = esperienza;
        if (vita < 1 || vita > getVitaMassima()) {
            throw new IllegalArgumentException("La vita deve essere tra 1 e " + getVitaMassima());
        }
        if (reagenti < 0 || reagenti > getReagentiMassimi()) {
            throw new IllegalArgumentException("I reagenti devono essere tra 0 e " + getReagentiMassimi());
        }
        subisciDanno(getVitaMassima() - vita);
        consumaReagenti(getReagentiMassimi() - reagenti);
        Objects.requireNonNull(formule, "Le formule sono obbligatorie");
        if (formule.size() > MASSIMO_FORMULE) {
            throw new IllegalArgumentException("Si conoscono al massimo " + MASSIMO_FORMULE + " formule");
        }
        for (Azione formula : formule) {
            impara(formula);
        }
    }

    private static String controllaNome(String nome) {
        Objects.requireNonNull(nome, "Il nome è obbligatorio");
        if (nome.isBlank() || nome.length() > LUNGHEZZA_MASSIMA_NOME) {
            throw new IllegalArgumentException(
                    "Il nome deve avere da 1 a " + LUNGHEZZA_MASSIMA_NOME + " caratteri");
        }
        return nome;
    }

    public Strumento getStrumento() {
        return strumento;
    }

    public int getLivello() {
        return livello;
    }

    /**
     * @return i punti esperienza raccolti verso il livello successivo
     */
    public int getEsperienza() {
        return esperienza;
    }

    /**
     * La soglia cresce a ogni livello: 100 punti per il livello 2, 150 per il 3, 200 per il 4 e così via.
     *
     * @return i punti esperienza che servono per salire dal livello attuale al successivo
     */
    public int getEsperienzaPerSalire() {
        return ESPERIENZA_PRIMO_LIVELLO + AUMENTO_SOGLIA_PER_LIVELLO * (livello - 1);
    }

    /**
     * Aggiunge punti esperienza. Ogni volta che si raggiunge la soglia l'alchimista
     * sale di livello: la vita massima e i reagenti massimi crescono, e i punti
     * in più restano per il livello dopo.
     *
     * @param punti i punti guadagnati, non negativi
     * @return true se l'alchimista è salito di almeno un livello
     */
    public boolean aggiungiEsperienza(int punti) {
        if (punti < 0) {
            throw new IllegalArgumentException("L'esperienza non può essere negativa");
        }
        int livelloPrima = livello;
        esperienza += punti;
        while (esperienza >= getEsperienzaPerSalire()) {
            esperienza -= getEsperienzaPerSalire();
            livello++;
            aumentaMassimi(VITA_PER_LIVELLO, REAGENTI_PER_LIVELLO);
        }
        return livello > livelloPrima;
    }

    /**
     * @return le formule conosciute, senza l'attacco base e la Distillazione,
     *         in una lista non modificabile
     */
    public List<Azione> getFormule() {
        return Collections.unmodifiableList(formule);
    }

    /**
     * @param nome il nome di una formula
     * @return true se l'alchimista conosce una formula con quel nome
     */
    public boolean conosceFormula(String nome) {
        for (Azione formula : formule) {
            if (formula.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true se si può imparare un'altra formula senza dimenticarne una
     */
    public boolean haSpazioPerFormula() {
        return formule.size() < MASSIMO_FORMULE;
    }

    /**
     * Impara una formula nuova, se c'è spazio.
     *
     * @param formula la formula da imparare
     * @throws IllegalArgumentException se l'alchimista la conosce già
     * @throws IllegalStateException se conosce già il massimo delle formule
     */
    public void impara(Azione formula) {
        controllaNuova(formula);
        if (!haSpazioPerFormula()) {
            throw new IllegalStateException("Si conoscono già " + MASSIMO_FORMULE + " formule");
        }
        formule.add(formula);
    }

    /**
     * Dimentica una formula e al suo posto impara quella nuova.
     *
     * @param vecchia la formula da dimenticare, tra quelle conosciute
     * @param nuova la formula da imparare
     * @throws IllegalArgumentException se la vecchia non è conosciuta o la nuova lo è già
     */
    public void sostituisci(Azione vecchia, Azione nuova) {
        controllaNuova(nuova);
        int posizione = formule.indexOf(vecchia);
        if (posizione < 0) {
            throw new IllegalArgumentException("L'alchimista non conosce la formula da dimenticare");
        }
        formule.set(posizione, nuova);
    }

    private void controllaNuova(Azione formula) {
        Objects.requireNonNull(formula, "La formula è obbligatoria");
        if (conosceFormula(formula.getNome())) {
            throw new IllegalArgumentException("L'alchimista conosce già " + formula.getNome());
        }
    }

    /**
     * @return l'attacco base, la Distillazione e poi le formule, in una lista non modificabile
     */
    @Override
    public List<Azione> getAzioni() {
        List<Azione> azioni = new ArrayList<>();
        azioni.add(attaccoBase);
        azioni.add(distillazione);
        azioni.addAll(formule);
        return Collections.unmodifiableList(azioni);
    }
}
