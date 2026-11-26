package it.unicam.cs.mpgc.rpg130759.modello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Chi può partecipare a uno scontro: ha un nome, un elemento, dei punti vita,
 * dei reagenti (l'energia con cui paga le azioni), le mosse che conosce
 * e gli effetti che ha addosso.
 * Garantisce che vita e reagenti restino sempre tra 0 e il massimo.
 */
public abstract class Combattente {
    private final String nome;
    private final Elemento elemento;
    private int vitaMassima;
    private int reagentiMassimi;
    private int vita;
    private int reagenti;
    private final List<Effetto> effetti = new ArrayList<>();

    /**
     * Crea un combattente con vita e reagenti al massimo.
     *
     * @param nome il nome mostrato nel gioco
     * @param elemento l'elemento del combattente, mai null
     * @param vitaMassima i punti vita massimi, maggiori di zero
     * @param reagentiMassimi i reagenti massimi, non negativi
     */
    protected Combattente(String nome, Elemento elemento, int vitaMassima, int reagentiMassimi) {
        this.nome = Objects.requireNonNull(nome, "Il nome è obbligatorio");
        this.elemento = Objects.requireNonNull(elemento, "L'elemento è obbligatorio");
        if (vitaMassima <= 0) {
            throw new IllegalArgumentException("La vita massima deve essere positiva");
        }
        if (reagentiMassimi < 0) {
            throw new IllegalArgumentException("I reagenti massimi non possono essere negativi");
        }
        this.vitaMassima = vitaMassima;
        this.vita = vitaMassima;
        this.reagentiMassimi = reagentiMassimi;
        this.reagenti = reagentiMassimi;
    }

    public String getNome() {
        return nome;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public int getVitaMassima() {
        return vitaMassima;
    }

    public int getVita() {
        return vita;
    }

    public int getReagentiMassimi() {
        return reagentiMassimi;
    }

    public int getReagenti() {
        return reagenti;
    }

    /**
     * Ogni tipo di combattente decide da dove prende le sue mosse.
     *
     * @return le azioni che il combattente conosce, in una lista non modificabile
     */
    public abstract List<Azione> getAzioni();

    /**
     * Toglie punti vita, senza scendere sotto lo zero.
     *
     * @param danno i punti da togliere, non negativi
     */
    public void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        vita = Math.max(0, vita - danno);
    }

    /**
     * Subisce un colpo di un certo elemento: il danno viene moltiplicato
     * in base al vantaggio tra l'elemento del colpo e quello del combattente,
     * poi gli effetti attivi possono ridurlo.
     *
     * @param danno il danno prima del moltiplicatore, non negativo
     * @param elementoColpo l'elemento del colpo
     * @return il danno effettivamente calcolato
     */
    public int subisciColpo(int danno, Elemento elementoColpo) {
        int dannoFinale = (int) Math.round(danno * elementoColpo.moltiplicatoreContro(elemento));
        for (Effetto effetto : effetti) {
            dannoFinale = effetto.modificaDannoSubito(dannoFinale);
        }
        subisciDanno(dannoFinale);
        return dannoFinale;
    }

    /**
     * Calcola il danno di un colpo che il combattente sta per dare,
     * tenendo conto degli effetti attivi. Gli effetti che si consumano
     * quando vengono usati spariscono subito.
     *
     * @param danno il danno della mossa
     * @return il danno da infliggere
     */
    public int potenziaDanno(int danno) {
        int dannoFinale = danno;
        for (Effetto effetto : effetti) {
            dannoFinale = effetto.modificaDannoInflitto(dannoFinale);
        }
        effetti.removeIf(effetto -> effetto.isScaduto());
        return dannoFinale;
    }

    /**
     * @return gli effetti attivi, in una lista non modificabile
     */
    public List<Effetto> getEffetti() {
        return Collections.unmodifiableList(effetti);
    }

    /**
     * Aggiunge un effetto. Se ce n'è già uno con lo stesso nome lo sostituisce,
     * così lo stesso effetto non si somma più volte ma ricomincia da capo.
     *
     * @param effetto l'effetto da aggiungere
     */
    public void aggiungiEffetto(Effetto effetto) {
        Objects.requireNonNull(effetto, "L'effetto è obbligatorio");
        effetti.removeIf(attivo -> attivo.getNome().equals(effetto.getNome()));
        effetti.add(effetto);
    }

    /**
     * Toglie tutti gli effetti, per esempio quando finisce uno scontro.
     */
    public void rimuoviEffetti() {
        effetti.clear();
    }

    /**
     * Fa agire gli effetti di fine turno, consuma un turno di ognuno
     * e toglie quelli finiti.
     */
    public void applicaEffettiDiFineTurno() {
        for (Effetto effetto : effetti) {
            effetto.fineTurno(this);
        }
        effetti.removeIf(effetto -> effetto.isScaduto());
    }

    /**
     * @return false se un effetto impedisce al combattente di fare la sua mossa
     */
    public boolean puoAgire() {
        for (Effetto effetto : effetti) {
            if (effetto.impedisceDiAgire()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Alza la vita massima e i reagenti massimi. La vita e i reagenti attuali
     * crescono della stessa quantità. Serve a chi diventa più forte durante la partita,
     * come l'alchimista quando sale di livello.
     *
     * @param vitaInPiu i punti vita massimi da aggiungere, non negativi
     * @param reagentiInPiu i reagenti massimi da aggiungere, non negativi
     */
    protected void aumentaMassimi(int vitaInPiu, int reagentiInPiu) {
        if (vitaInPiu < 0 || reagentiInPiu < 0) {
            throw new IllegalArgumentException("I massimi non possono diminuire");
        }
        vitaMassima += vitaInPiu;
        vita += vitaInPiu;
        reagentiMassimi += reagentiInPiu;
        reagenti += reagentiInPiu;
    }

    /**
     * Restituisce punti vita, senza superare il massimo.
     *
     * @param punti i punti da restituire, non negativi
     */
    public void cura(int punti) {
        if (punti < 0) {
            throw new IllegalArgumentException("La cura non può essere negativa");
        }
        vita = Math.min(vitaMassima, vita + punti);
    }

    public boolean isSconfitto() {
        return vita == 0;
    }

    /**
     * Dice se i reagenti bastano per pagare un costo.
     *
     * @param costo i reagenti richiesti
     * @return true se il combattente ne ha abbastanza
     */
    public boolean haReagenti(int costo) {
        return reagenti >= costo;
    }

    /**
     * Toglie i reagenti necessari a un'azione.
     *
     * @param costo i reagenti da consumare, non negativi
     * @throws IllegalStateException se i reagenti non bastano
     */
    public void consumaReagenti(int costo) {
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        if (!haReagenti(costo)) {
            throw new IllegalStateException("Reagenti insufficienti");
        }
        reagenti -= costo;
    }

    /**
     * Aggiunge reagenti, senza superare il massimo.
     *
     * @param quantita i reagenti da aggiungere, non negativi
     */
    public void recuperaReagenti(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("La quantità non può essere negativa");
        }
        reagenti = Math.min(reagentiMassimi, reagenti + quantita);
    }

    /**
     * Usa un'azione contro un bersaglio: prima paga il costo, poi la esegue.
     *
     * @param azione l'azione da usare
     * @param bersaglio chi subisce l'azione
     * @throws IllegalStateException se i reagenti non bastano
     */
    public void usa(Azione azione, Combattente bersaglio) {
        consumaReagenti(azione.getCosto());
        azione.esegui(this, bersaglio);
    }
}
