package it.unicam.cs.mpgc.rpg130759.grafica;

import it.unicam.cs.mpgc.rpg130759.modello.Alchimista;
import it.unicam.cs.mpgc.rpg130759.modello.Partita;
import it.unicam.cs.mpgc.rpg130759.modello.Zona;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * La pausa tra uno scontro e l'altro: dice quale piano è stato superato,
 * se l'alchimista è salito di livello, quanto ha recuperato e se sta entrando
 * in una nuova zona. Va mostrata dopo che la partita è già passata al piano successivo.
 */
public class SchermataTraPiani {
    private final VBox radice = new VBox(Aspetto.SPAZIO);

    /**
     * @param partita la partita, già passata al nuovo piano
     * @param salitoDiLivello true se l'alchimista è appena salito di livello
     * @param vitaRecuperata i punti vita recuperati riposando, senza quelli dati dal livello
     * @param reagentiRecuperati i reagenti recuperati riposando, senza quelli dati dal livello
     * @param alProseguire cosa fare quando il giocatore preme il pulsante per andare avanti
     */
    public SchermataTraPiani(Partita partita, boolean salitoDiLivello, int vitaRecuperata,
                             int reagentiRecuperati, Runnable alProseguire) {
        Objects.requireNonNull(partita);
        Objects.requireNonNull(alProseguire);
        int piano = partita.getPiano();
        Alchimista alchimista = partita.getAlchimista();

        Label titolo = new Label("Piano " + (piano - 1) + " superato");
        titolo.setStyle(Aspetto.STILE_TITOLO);
        radice.getChildren().add(titolo);
        if (salitoDiLivello) {
            Label livelloNuovo = new Label("Livello " + alchimista.getLivello()
                    + " raggiunto: vita e reagenti massimi aumentano.");
            livelloNuovo.setStyle(Aspetto.STILE_TESTO + " -fx-font-weight: bold;");
            radice.getChildren().add(livelloNuovo);
        }
        Label recupero = new Label("Ti riposi e recuperi " + vitaRecuperata + " punti vita e "
                + reagentiRecuperati + " reagenti.");
        Label stato = new Label("Vita " + alchimista.getVita() + "/" + alchimista.getVitaMassima()
                + "   Reagenti " + alchimista.getReagenti() + "/" + alchimista.getReagentiMassimi());
        Label livello = new Label(Aspetto.testoLivello(alchimista));
        Label zona = new Label(testoZona(piano));
        for (Label testo : new Label[] {recupero, stato, livello, zona}) {
            testo.setStyle(Aspetto.STILE_TESTO);
        }

        String testoPulsante = partita.getProposte().isEmpty()
                ? "Scendi al piano " + piano
                : "Scegli una formula nuova";
        Button avanti = new Button(testoPulsante);
        avanti.setOnAction(evento -> alProseguire.run());

        radice.getChildren().addAll(recupero, stato, livello, zona, avanti);
        radice.setAlignment(Pos.CENTER);
    }

    /**
     * @return il contenuto da mettere nella finestra
     */
    public Parent getRadice() {
        return radice;
    }

    /**
     * Se il nuovo piano è il primo della sua zona, annuncia il cambio di zona.
     */
    private static String testoZona(int piano) {
        String zonaAttuale = Aspetto.nomeDi(Zona.delPiano(piano));
        if (Zona.posizioneNellaZona(piano) == 0) {
            return "Entri in una nuova zona: " + zonaAttuale;
        }
        return "Zona: " + zonaAttuale;
    }
}
