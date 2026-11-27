package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.Azione;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Dopo un salto di livello mostra le formule proposte dal ricettario:
 * il giocatore ne impara una o rinuncia. Se l'alchimista conosce già il massimo
 * delle formule, prima di impararne una nuova sceglie quale dimenticare.
 * Le regole le controlla la partita: la schermata le passa solo la scelta.
 */
public class SchermataNuovaFormula {
    private static final int LARGHEZZA_RIQUADRO = 280;
    private static final String STILE_RIQUADRO =
            "-fx-border-color: #888888; -fx-border-radius: 8; -fx-padding: 12;";
    private static final String STILE_NOME = "-fx-font-size: 16px; -fx-font-weight: bold;";

    private final Partita partita;
    private final Runnable alTermine;
    private final VBox radice = new VBox(Aspetto.SPAZIO);

    /**
     * @param partita la partita, con le formule proposte pronte
     * @param alTermine cosa fare dopo che il giocatore ha imparato una formula o ha rinunciato
     */
    public SchermataNuovaFormula(Partita partita, Runnable alTermine) {
        this.partita = Objects.requireNonNull(partita, "La partita è obbligatoria");
        this.alTermine = Objects.requireNonNull(alTermine);
        radice.setAlignment(Pos.CENTER);
        mostraProposte();
    }

    /**
     * @return il contenuto da mettere nella finestra
     */
    public Parent getRadice() {
        return radice;
    }

    /**
     * Prima parte: le formule proposte, una accanto all'altra, e il pulsante per rinunciare.
     */
    private void mostraProposte() {
        Alchimista alchimista = partita.getAlchimista();
        Label titolo = new Label("Formula nuova");
        titolo.setStyle(Aspetto.STILE_TITOLO);
        String spiegazione = "Nel ricettario del maestro trovi queste formule: puoi impararne una.";
        if (!alchimista.haSpazioPerFormula()) {
            spiegazione += "\nConosci già " + Alchimista.MASSIMO_FORMULE
                    + " formule: per impararne una nuova dovrai dimenticarne una.";
        }
        Label testo = new Label(spiegazione);
        testo.setStyle(Aspetto.STILE_TESTO);

        HBox proposte = new HBox(Aspetto.SPAZIO * 2);
        proposte.setAlignment(Pos.CENTER);
        for (Azione formula : partita.getProposte()) {
            Button impara = new Button("Impara");
            impara.setOnAction(evento -> scegli(formula));
            proposte.getChildren().add(creaRiquadro(formula, impara));
        }

        Button rinuncia = new Button("Rinuncia");
        rinuncia.setOnAction(evento -> {
            partita.rinunciaAllaFormula();
            alTermine.run();
        });
        radice.getChildren().setAll(titolo, testo, proposte, rinuncia);
    }

    private void scegli(Azione nuova) {
        if (partita.getAlchimista().haSpazioPerFormula()) {
            partita.imparaFormula(nuova);
            alTermine.run();
        } else {
            mostraFormuleDaDimenticare(nuova);
        }
    }

    /**
     * Seconda parte, solo se non c'è spazio: le formule conosciute, ognuna con
     * il pulsante per dimenticarla, e il pulsante per tornare alle proposte.
     */
    private void mostraFormuleDaDimenticare(Azione nuova) {
        Label titolo = new Label("Quale formula dimentichi?");
        titolo.setStyle(Aspetto.STILE_TITOLO);
        Label testo = new Label("Al suo posto imparerai " + nuova.getNome() + ".");
        testo.setStyle(Aspetto.STILE_TESTO);

        VBox conosciute = new VBox(Aspetto.SPAZIO / 2);
        conosciute.setAlignment(Pos.CENTER_LEFT);
        conosciute.setMaxWidth(Region.USE_PREF_SIZE);
        for (Azione vecchia : partita.getAlchimista().getFormule()) {
            Button dimentica = new Button("Dimentica");
            dimentica.setOnAction(evento -> {
                partita.imparaFormulaAlPostoDi(nuova, vecchia);
                alTermine.run();
            });
            Label descrizione = new Label(testoDi(vecchia) + ": " + vecchia.getDescrizione());
            HBox riga = new HBox(Aspetto.SPAZIO, dimentica, descrizione);
            riga.setAlignment(Pos.CENTER_LEFT);
            conosciute.getChildren().add(riga);
        }

        Button indietro = new Button("Indietro");
        indietro.setOnAction(evento -> mostraProposte());
        radice.getChildren().setAll(titolo, testo, conosciute, indietro);
    }

    private static VBox creaRiquadro(Azione formula, Button pulsante) {
        Label nome = new Label(formula.getNome());
        nome.setStyle(STILE_NOME);
        Label costo = new Label("Costo: " + formula.getCosto() + " reagenti");
        Label descrizione = new Label(formula.getDescrizione());
        descrizione.setWrapText(true);
        VBox riquadro = new VBox(Aspetto.SPAZIO / 2, nome, costo, descrizione, pulsante);
        riquadro.setAlignment(Pos.TOP_CENTER);
        riquadro.setPrefWidth(LARGHEZZA_RIQUADRO);
        riquadro.setStyle(STILE_RIQUADRO);
        return riquadro;
    }

    private static String testoDi(Azione formula) {
        return formula.getNome() + " (" + formula.getCosto() + ")";
    }
}
