package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.Azione;
import it.unicam.cs.mpgc.rpg130759.modello.Combattente;
import it.unicam.cs.mpgc.rpg130759.modello.Combattimento;
import it.unicam.cs.mpgc.rpg130759.modello.Elemento;
import it.unicam.cs.mpgc.rpg130759.modello.Nemico;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mostra uno scontro: il nemico in alto, l'alchimista in basso, il registro
 * a destra e un pulsante per ogni mossa. Non contiene regole del gioco:
 * passa la mossa scelta al combattimento e poi rilegge lo stato dal modello.
 */
public class SchermataCombattimento {
    private static final int SPAZIO = 12;
    private static final int LARGHEZZA_REGISTRO = 320;
    private static final int LARGHEZZA_BARRA = 300;
    private static final String STILE_NEMICO_NASCOSTO =
            "-fx-border-color: #888888; -fx-border-style: dashed; -fx-border-radius: 8;";

    private final Combattimento combattimento;
    private final BorderPane radice = new BorderPane();
    private final VBox riquadroNemico = new VBox(SPAZIO / 2);
    private final ProgressBar vitaNemico = new ProgressBar();
    private final Label datiNemico = new Label();
    private final Label elementoNemico = new Label();
    private final ProgressBar vitaAlchimista = new ProgressBar();
    private final Label datiAlchimista = new Label();
    private final ListView<String> registro = new ListView<>();
    private final Label esito = new Label();
    private final Map<Azione, Button> pulsanti = new LinkedHashMap<>();

    /**
     * Costruisce la schermata per uno scontro e la mostra nello stato iniziale.
     *
     * @param combattimento lo scontro da mostrare
     */
    public SchermataCombattimento(Combattimento combattimento) {
        this.combattimento = Objects.requireNonNull(combattimento, "Il combattimento è obbligatorio");
        radice.setPadding(new Insets(SPAZIO));
        radice.setCenter(creaCentro());
        radice.setRight(creaRegistro());
        radice.setBottom(creaPulsanti());
        aggiorna();
    }

    /**
     * @return il contenuto da mettere nella finestra
     */
    public Parent getRadice() {
        return radice;
    }

    private VBox creaCentro() {
        Nemico nemico = combattimento.getNemico();
        Label nomeNemico = new Label(nemico.getNome());
        nomeNemico.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        vitaNemico.setPrefWidth(LARGHEZZA_BARRA);
        riquadroNemico.getChildren().addAll(nomeNemico, elementoNemico, vitaNemico, datiNemico);
        riquadroNemico.setAlignment(Pos.CENTER);
        riquadroNemico.setPadding(new Insets(SPAZIO * 2));

        Label nomeAlchimista = new Label(combattimento.getAlchimista().getNome());
        nomeAlchimista.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        vitaAlchimista.setPrefWidth(LARGHEZZA_BARRA);
        VBox riquadroAlchimista = new VBox(SPAZIO / 2, nomeAlchimista, vitaAlchimista, datiAlchimista);
        riquadroAlchimista.setAlignment(Pos.CENTER);

        VBox centro = new VBox(SPAZIO * 2, riquadroNemico, creaLegenda(), riquadroAlchimista);
        centro.setAlignment(Pos.CENTER);
        centro.setPadding(new Insets(0, SPAZIO, 0, 0));
        return centro;
    }

    /**
     * Spiega al giocatore quale colore corrisponde a quale principio,
     * così quando l'elemento del nemico viene scoperto lo riconosce.
     */
    private HBox creaLegenda() {
        HBox legenda = new HBox(SPAZIO);
        legenda.setAlignment(Pos.CENTER);
        for (Elemento elemento : List.of(Elemento.ZOLFO, Elemento.MERCURIO, Elemento.SALE)) {
            Label voce = new Label(nomeDi(elemento));
            voce.setPadding(new Insets(2, 8, 2, 8));
            voce.setStyle("-fx-background-color: " + coloreDi(elemento) + "; -fx-background-radius: 4;");
            legenda.getChildren().add(voce);
        }
        return legenda;
    }

    private ListView<String> creaRegistro() {
        registro.setPrefWidth(LARGHEZZA_REGISTRO);
        registro.setFocusTraversable(false);
        return registro;
    }

    private VBox creaPulsanti() {
        HBox riga = new HBox(SPAZIO);
        riga.setAlignment(Pos.CENTER);
        for (Azione azione : combattimento.getAlchimista().getAzioni()) {
            Button pulsante = new Button(testoDi(azione));
            pulsante.setOnAction(evento -> scegli(azione));
            pulsanti.put(azione, pulsante);
            riga.getChildren().add(pulsante);
        }
        esito.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox fondo = new VBox(SPAZIO, esito, riga);
        fondo.setAlignment(Pos.CENTER);
        fondo.setPadding(new Insets(SPAZIO, 0, 0, 0));
        return fondo;
    }

    private void scegli(Azione azione) {
        combattimento.turnoAlchimista(azione);
        aggiorna();
    }

    /**
     * Rilegge dal modello vita, reagenti e registro e attiva solo
     * le mosse che l'alchimista può pagare.
     */
    private void aggiorna() {
        Alchimista alchimista = combattimento.getAlchimista();
        mostraDati(combattimento.getNemico(), vitaNemico, datiNemico);
        mostraDati(alchimista, vitaAlchimista, datiAlchimista);
        mostraElementoNemico();

        registro.getItems().setAll(combattimento.getRegistro());
        registro.scrollTo(registro.getItems().size() - 1);

        for (Map.Entry<Azione, Button> voce : pulsanti.entrySet()) {
            boolean pagabile = alchimista.haReagenti(voce.getKey().getCosto());
            voce.getValue().setDisable(combattimento.isFinito() || !pagabile);
        }

        if (combattimento.isVinto()) {
            esito.setText("Hai vinto lo scontro");
        } else if (combattimento.isFinito()) {
            esito.setText("Sei stato sconfitto");
        }
    }

    /**
     * Finché il combattimento non lo rivela, il riquadro del nemico resta
     * senza colore e l'elemento è un punto di domanda.
     */
    private void mostraElementoNemico() {
        if (combattimento.isElementoNemicoRivelato()) {
            Elemento elemento = combattimento.getNemico().getElemento();
            elementoNemico.setText("Elemento: " + nomeDi(elemento));
            riquadroNemico.setStyle("-fx-background-color: " + coloreDi(elemento) + "; -fx-background-radius: 8;");
        } else {
            elementoNemico.setText("Elemento: ?");
            riquadroNemico.setStyle(STILE_NEMICO_NASCOSTO);
        }
    }

    private static void mostraDati(Combattente combattente, ProgressBar barra, Label dati) {
        barra.setProgress((double) combattente.getVita() / combattente.getVitaMassima());
        dati.setText("Vita " + combattente.getVita() + "/" + combattente.getVitaMassima()
                + "   Reagenti " + combattente.getReagenti() + "/" + combattente.getReagentiMassimi());
    }

    private static String testoDi(Azione azione) {
        if (azione.getCosto() == 0) {
            return azione.getNome();
        }
        return azione.getNome() + " (" + azione.getCosto() + ")";
    }

    private static String coloreDi(Elemento elemento) {
        return switch (elemento) {
            case ZOLFO -> "#e3c13b";
            case MERCURIO -> "#b5bac1";
            case SALE -> "#dcebf2";
            case NEUTRO -> "#a87b50";
        };
    }

    private static String nomeDi(Elemento elemento) {
        return switch (elemento) {
            case ZOLFO -> "Zolfo";
            case MERCURIO -> "Mercurio";
            case SALE -> "Sale";
            case NEUTRO -> "Neutro";
        };
    }
}
