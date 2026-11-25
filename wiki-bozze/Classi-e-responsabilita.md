# Classi e responsabilità

Qui descrivo le classi del progetto, divise per package. Aggiorno la pagina ogni volta che aggiungo una classe.

## Package `modello`

### `Elemento` (enum)

Rappresenta i tre principi di Paracelso (Zolfo, Mercurio, Sale) più l'elemento neutro, e contiene la regola dei vantaggi:

- Zolfo prevale su Mercurio, Mercurio su Sale, Sale su Zolfo;
- chi prevale fa il doppio del danno, chi colpisce un elemento che lo batte ne fa la metà;
- Neutro fa e subisce sempre danno normale.

Le altre classi usano un solo metodo pubblico, `moltiplicatoreContro(bersaglio)`, che restituisce 2, 0,5 oppure 1. Ho tenuto privato il metodo che decide chi prevale su chi: è un dettaglio interno e posso cambiarlo senza toccare il resto del programma.

Ho messo la regola in un unico posto così il combattimento e le formule chiedono a `Elemento` quanto vale un colpo e non ripetono la logica. Per aggiungere un nuovo elemento basta aggiungere un valore all'enum e il suo caso nello switch; se dimentico il caso, il compilatore me lo segnala.

### `Combattente` (classe astratta)

È la base comune dell'alchimista e dei nemici: tiene nome, elemento, punti vita e reagenti. Ho scelto una classe astratta perché nel gioco non esiste un combattente generico, esistono solo personaggi concreti.

La vita cambia solo con `subisciDanno` e `cura`, i reagenti solo con `consumaReagenti` e `recuperaReagenti`. Così restano sempre tra 0 e il massimo, e le sottoclassi non ripetono i controlli. Il costruttore rifiuta nome o elemento null, una vita massima non positiva e reagenti negativi.

All'inizio avevo messo i reagenti solo nell'alchimista. Poi mi sono accorto che le azioni ricevono un `Combattente` qualsiasi: per far pagare una formula o per far recuperare reagenti con la Distillazione avrei dovuto chiedere "sei un alchimista?" con `instanceof`, e ogni nuovo tipo di combattente mi avrebbe costretto a cambiare quei controlli. Ho spostato i reagenti in `Combattente`. Ora anche un nemico può avere mosse che costano reagenti.

Due metodi collegano il combattente alle azioni:

- `subisciColpo(danno, elemento)` applica il moltiplicatore tra l'elemento del colpo e quello del combattente, poi toglie la vita. Tengo il calcolo qui e le azioni non lo ripetono.
- `usa(azione, bersaglio)` paga il costo dell'azione e poi la esegue. Se i reagenti non bastano lancia un'eccezione e non succede niente, quindi la grafica dovrà chiedere prima `haReagenti`.

Il combattente tiene anche la lista degli effetti che ha addosso (`getEffetti()`, non modificabile da fuori). `aggiungiEffetto` sostituisce un effetto con lo stesso nome invece di sommarlo: due Veleni non fanno il doppio, il secondo fa solo ricominciare il conto dei turni. Gli effetti entrano in gioco in quattro punti, e il combattente chiede a ognuno se vuole cambiare qualcosa:

- in `subisciColpo`, dopo il moltiplicatore dell'elemento (Corazza di Sale);
- in `potenziaDanno`, che le azioni chiamano prima di colpire (Catalizzatore);
- in `applicaEffettiDiFineTurno`, che il combattimento chiama alla fine di ogni turno (Veleno);
- in `puoAgire`, che dice se un effetto blocca la mossa (Pietrificazione).

Il metodo astratto `getAzioni()` restituisce le mosse che il combattente conosce. Lo dichiaro qui ma non lo scrivo: l'alchimista e i nemici prendono le mosse da posti diversi, quindi ogni sottoclasse lo scrive a modo suo. Il combattimento potrà chiedere le mosse a qualsiasi combattente senza sapere di che tipo è. La lista che restituisco non si può modificare da fuori, così nessuno aggiunge mosse di nascosto.

### `Alchimista`

È il personaggio del giocatore. Eredita da `Combattente` vita e reagenti e aggiunge lo strumento che impugna. Comincia con il Pestello di Legno, elemento Neutro, 100 di vita e 20 reagenti. Con l'elemento Neutro subisce danno normale da tutti i principi. I numeri sono provvisori e li rivedrò nel bilanciamento.

All'inizio conosce cinque mosse: l'attacco con il pestello, la Distillazione e una formula per ogni principio (Fiala Ardente di Zolfo, Nebbia Mercuriale di Mercurio, Polvere Cristallizzante di Sale). Le tre formule costano 5 reagenti e fanno 10 di danno. Avendo un elemento per tipo, posso sfruttare il vantaggio contro qualsiasi nemico fin dal primo piano.

Per ora l'alchimista conosce fin dall'inizio anche le quattro formule con effetti (Veleno Sulfureo, Cristalli Pietrificanti, Corazza di Sale e Catalizzatore) e le tre formule speciali (Sanguisuga d'Argento, Esplosione di Zolfo e Mercurio Instabile). È una soluzione provvisoria per poterle provare giocando. Quando aggiungerò i livelli, partirà con le tre formule iniziali e imparerà le altre salendo di livello.

Il nome lo sceglie chi gioca e deve avere da 1 a 10 caratteri. Il controllo è nel costruttore: un alchimista con un nome vuoto o troppo lungo non viene creato. La grafica impedisce già di scrivere più di 10 caratteri, ma la regola sta nel modello perché vale per qualsiasi interfaccia.

Un secondo costruttore ricrea l'alchimista con la vita e i reagenti di una partita salvata. Anche qui controllo i valori: con la vita a 0, oppure con più vita o più reagenti del massimo, l'alchimista non viene creato.

### `Azione` (interfaccia)

Una mossa che un combattente fa nel suo turno: ha un nome, un costo in reagenti e il metodo `esegui(chi, bersaglio)`. La uso sia per l'alchimista sia per i nemici. Per aggiungere una mossa scrivo una nuova classe che implementa `Azione` e il combattimento la userà senza modifiche.

`esegui` non paga il costo: lo paga `Combattente.usa`. Così ogni azione si occupa solo del suo effetto e non devo ripetere il pagamento in ogni classe.

`getUsiPerScontro()` dice quante volte l'azione si può usare in uno scontro. È un metodo `default`, cioè ha già un corpo nell'interfaccia: restituisce `SENZA_LIMITE` e le azioni senza limite non devono scriverlo. Solo chi ha un limite lo riscrive, per ora la Distillazione. Così ho aggiunto il limite senza toccare le altre azioni.

### `Strumento`

L'attrezzo che l'alchimista tiene in mano: nome, danno ed elemento dell'attacco base. Uno strumento senza elemento usa Neutro, così non devo gestire valori null.

### `AttaccoBase`

Il colpo con lo strumento. Non costa niente e usa danno ed elemento dello strumento che riceve nel costruttore.

### `Colpo`

Una mossa che colpisce il bersaglio con un danno di un elemento. Le mosse che cambiano solo nei numeri (nome, costo, danno, elemento) sono oggetti diversi di questa stessa classe e non classi separate: per esempio la Fiala Ardente dell'alchimista o il morso di un nemico.

Prima la classe si chiamava `LancioFiala`. Quando ho scritto i nemici mi sono accorto che anche i loro attacchi funzionano allo stesso modo, ma chiamare "lancio di una fiala" il morso di un verme era sbagliato. Ho rinominato la classe e l'ho riusata invece di scriverne una copia.

Prima di colpire, il colpo chiede a chi lo usa `potenziaDanno(danno)`, così un Catalizzatore attivo raddoppia il danno. Lo fa anche `AttaccoBase`.

### `ColpoConEffetto`

Un `Colpo` che, oltre al danno, lascia un effetto sul bersaglio: Veleno Sulfureo (Zolfo, 4 di danno più il Veleno, costa 5) e Cristalli Pietrificanti (Sale, 5 di danno più la Pietrificazione, costa 7). È una sottoclasse di `Colpo`: fa il colpo normale con `super.esegui` e poi aggiunge l'effetto. Se il bersaglio è già sconfitto l'effetto non serve e non lo aggiungo.

Il costruttore non riceve un effetto ma un `Supplier<Effetto>`, cioè una piccola funzione che crea un effetto nuovo ogni volta che la chiamo (per esempio `() -> new Veleno()`). Mi serve perché un effetto conta i suoi turni: se usassi sempre lo stesso oggetto, il secondo lancio riceverebbe un veleno già a metà.

### `Preparazione`

Una mossa che non tocca il bersaglio: chi la usa applica un effetto a se stesso. La usano Corazza di Sale (costa 5) e Catalizzatore (costa 3). Anche questa riceve un `Supplier<Effetto>`, per lo stesso motivo.

### Formule speciali: `ColpoAssorbente`, `ColpoConRinculo`, `ColpoInstabile`

Sono le formule che non cambiano solo nei numeri ma si comportano in modo diverso, per questo hanno una classe ciascuna. Ho dato alle classi un nome generico e non quello della formula, perché un giorno potrebbe usarle anche un nemico. I numeri sono provvisori:

| Classe | Formula | Costo | Cosa fa |
|---|---|---|---|
| `ColpoAssorbente` | Sanguisuga d'Argento (Mercurio) | 6 | 8 di danno; chi la usa recupera metà della vita tolta al bersaglio, arrotondata per difetto |
| `ColpoConRinculo` | Esplosione di Zolfo (Zolfo) | 8 | 18 di danno; chi la usa perde 5 punti vita |
| `ColpoInstabile` | Mercurio Instabile (Mercurio) | 5 | danno a caso tra 2 e 20, in media 11 |

`ColpoAssorbente` e `ColpoConRinculo` sono sottoclassi di `Colpo`: fanno il colpo normale con `super.esegui` e poi aggiungono la loro parte. La Sanguisuga calcola la cura dalla vita che il bersaglio ha perso davvero, quindi contro un nemico protetto dalla Corazza recupera meno.

Il rinculo dell'Esplosione non fa mai scendere sotto 1 punto vita. Così chi la usa non può cadere nello stesso momento del nemico, e vale ancora la regola che in uno scontro non possono essere sconfitti tutti e due. Usarla con poca vita resta comunque un rischio, perché poi basta un colpo del nemico.

`ColpoInstabile` invece implementa direttamente `Azione`, perché in `Colpo` il danno è un numero fisso. Riceve un `Random` nel costruttore, come `NemicoComune`: nei test posso passarne uno con un seme fisso. Il danno estratto passa comunque da `potenziaDanno`, quindi il Catalizzatore lo raddoppia come tutti gli altri colpi. Il valore casuale non va salvato, perché si salva solo tra un piano e l'altro e mai durante uno scontro.

### `Effetto` (classe astratta)

Qualcosa che resta addosso a un combattente per alcuni turni. Ha un nome e i turni che gli restano. Alla fine di ogni turno `fineTurno` fa agire l'effetto e toglie un turno; quando arriva a zero l'effetto è scaduto e il combattente lo toglie.

Nel progetto iniziale `Effetto` era un'interfaccia. L'ho fatta diventare una classe astratta perché tutti gli effetti contano i turni nello stesso modo: così il conteggio è scritto una volta sola. `fineTurno` è `final`: le sottoclassi non possono cambiare il conteggio, possono solo dire cosa succede in quel momento riscrivendo `allaFineDelTurno`.

La classe ha quattro metodi che di base non fanno niente: `allaFineDelTurno`, `modificaDannoSubito`, `modificaDannoInflitto` e `impedisceDiAgire`. Ogni effetto riscrive solo quello che gli interessa. Per aggiungere un effetto nuovo scrivo una sottoclasse e non modifico né `Combattente` né `Combattimento`.

La durata conta anche il turno in cui l'effetto viene applicato, perché il primo fine turno arriva subito dopo la mossa. I numeri sono provvisori:

| Effetto | Su chi | Cosa fa |
|---|---|---|
| `Veleno` | nemico | a fine turno toglie 4 punti vita, per 3 turni (12 in tutto) |
| `CorazzaDiSale` | chi la usa | dimezza il danno dei colpi ricevuti per 3 turni, compreso quello in cui la usa |
| `Pietrificazione` | nemico | il nemico salta la sua risposta in quel turno |
| `Catalizzatore` | chi lo usa | il colpo del turno dopo fa il doppio, poi l'effetto si consuma; se in quel turno non colpisco, si perde |

Il Veleno toglie vita con `subisciDanno`, senza moltiplicatore di elemento, quindi la Corazza non lo ferma.

### `Distillazione`

Chi la usa salta l'attacco e recupera 8 reagenti e 5 punti vita. Non costa niente e non tocca il bersaglio. È una cura e non protegge dai colpi: per difendersi c'è la Corazza di Sale.

Si può usare al massimo 2 volte per scontro. Senza limite, alternando Corazza di Sale e Distillazione si recuperava più vita di quanta se ne perdeva e lo scontro non finiva mai. Dato che l'alchimista non recupera più reagenti a fine turno, la Distillazione è l'unico modo per ricaricarli durante uno scontro: va usata al momento giusto.

### `Nemico` (classe astratta)

Un combattente che il gioco controlla da solo. Tiene la lista delle sue mosse e dichiara il metodo astratto `scegliAzione()`: ogni tipo di nemico decide a modo suo cosa fare nel turno. Il nemico comune sceglie a caso, il Golem di Piombo alternerà attacco e pausa. Per un comportamento nuovo scrivo una sottoclasse e il combattimento continua a chiamare `scegliAzione()` senza modifiche.

Il costruttore rifiuta un nemico senza almeno una mossa gratuita. Se i reagenti finiscono, il nemico ha sempre qualcosa da fare e la scelta della mossa non resta mai senza opzioni.

### `NemicoComune`

A ogni turno sceglie a caso una mossa tra quelle che può pagare. I nove nemici comuni cambiano solo nei numeri e nelle mosse, quindi sono oggetti diversi di questa classe.

Il generatore casuale (`Random`) lo passo dal costruttore invece di crearlo dentro la classe. Nei test uso un `Random` con un seme fisso: con lo stesso seme esce sempre la stessa sequenza, quindi posso controllare il risultato.

### `Combattimento`

Gestisce uno scontro tra l'alchimista e un nemico. Decide l'ordine dei turni, controlla le mosse e dice quando lo scontro è finito. Ho tenuto queste regole fuori dai combattenti e fuori dalla grafica: l'alchimista sa usare una mossa ma non sa quando tocca a lui, e la grafica si limita a passare la mossa scelta dal giocatore. Un'interfaccia web o mobile potrebbe usare questa classe così com'è.

Il metodo `turnoAlchimista(azione)` gioca un turno intero:

1. controlla che lo scontro non sia finito e che l'alchimista conosca la mossa;
2. l'alchimista usa la mossa sul nemico (se i reagenti non bastano, `usa` lancia un'eccezione e il turno non parte);
3. se il nemico resta in piedi, sceglie una mossa con `scegliAzione()` e la usa sull'alchimista; se un effetto lo blocca (`puoAgire()` è falso), salta il turno;
4. se nessuno dei due è caduto, agiscono gli effetti di fine turno (per esempio il Veleno);
5. se lo scontro continua ancora, il nemico recupera 2 reagenti. L'alchimista no: per recuperarli deve distillare.

Prima di tutto questo il turno controlla anche che la mossa si possa usare (`puoUsare`): servono i reagenti e la mossa non deve aver finito gli usi dello scontro. Il combattimento conta quante volte l'alchimista ha usato ogni mossa in una mappa (`HashMap`), e `getUsiRimasti` dice quante volte si può ancora usare. Gli usi li conto qui e non nella mossa, perché la stessa Distillazione serve in tutti gli scontri: con un nuovo combattimento il conto riparte da zero.

L'alchimista muove per primo. Se sconfigge il nemico, il nemico non contrattacca. Con il Veleno però poteva succedere che il nemico sconfiggesse l'alchimista e poi morisse avvelenato a fine turno. Ho deciso che gli effetti di fine turno agiscono solo se lo scontro non è già finito, quindi non possono cadere tutti e due: se l'alchimista cade, lo scontro è perso. Per sicurezza `isVinto()` controlla che il nemico sia sconfitto e che l'alchimista sia ancora in piedi.

Il danno del Veleno sul nemico conta tra i danni inflitti, e nel registro compare una frase come "Verme Cieco perde 4 punti vita a fine turno". Se il nemico è pietrificato il registro scrive "Verme Cieco è bloccato e salta il turno"; in quel caso il suo elemento resta nascosto, perché non ha mosso.

Per ora solo il nemico può essere bloccato: nessuna mossa dei nemici pietrifica l'alchimista. Se un giorno servisse, andrebbe aggiunto lo stesso controllo anche al turno dell'alchimista.

All'inizio dello scontro chi gioca non sa di che elemento è il nemico. Lo scopre dopo la prima mossa del nemico: `isElementoNemicoRivelato()` diventa vero appena il nemico ha mosso. Prima si può solo intuire, guardando quanti danni fanno le proprie fiale. Ho messo questa regola nel combattimento e non nella grafica perché fa parte del gioco: così vale per qualsiasi interfaccia e la posso controllare con un test.

Il combattimento scrive nel registro una frase per ogni mossa, per esempio "Verme Cieco usa Morso: 5 danni a Alchimista". Calcolo il danno come differenza tra la vita del bersaglio prima e dopo la mossa, così funziona con qualsiasi azione senza cambiare l'interfaccia `Azione`. La grafica legge il registro con `getRegistro()`, che restituisce una lista non modificabile. Le frasi sono semplici stringhe e non dipendono da JavaFX. Allo stesso modo controllo la vita di chi fa la mossa: se è cambiata, aggiungo una frase come "Alchimista recupera 4 punti vita" (Sanguisuga, Distillazione) o "Alchimista perde 5 punti vita" (rinculo dell'Esplosione).

Il combattimento conta anche le statistiche dello scontro, che la grafica mostra alla fine: turni giocati, danni inflitti, danni subiti e reagenti spesi. I danni sono la vita persa davvero: se una fiala da 20 colpisce un nemico che ha solo 10 punti vita, contano 10. Tra i danni subiti conto anche il rinculo dell'Esplosione, perché è vita che l'alchimista perde. Le conto nel modello perché è lì che succedono i colpi, e così posso controllarle con un test.

### `Zona` (enum)

Le tre zone del laboratorio: Cantine, Laboratorio e Fornace. Ogni zona ha 4 piani. `delPiano(piano)` dice a quale zona appartiene un piano e `posizioneNellaZona(piano)` dice se è il primo, il secondo, il terzo o il quarto piano della zona. Faccio questo conto in un posto solo, così le altre classi non ripetono la divisione.

Calcolo l'ultimo piano moltiplicando il numero delle zone per i piani di ogni zona. Se aggiungo una quarta zona all'enum, il gioco arriva da solo fino al piano 16.

### `GeneratoreNemici` (interfaccia)

`creaNemico(piano)` restituisce il nemico di un piano. `Partita` conosce solo questa interfaccia e non sa come nascono i nemici. Se voglio regole diverse (un'altra zona, una modalità più difficile) scrivo una nuova implementazione e passo quella alla partita, senza modificare `Partita`. Lo stesso vale nei test: gli passo un generatore di prova che crea nemici prevedibili.

`getSeme()` restituisce il seme, il numero da cui dipendono le scelte casuali del generatore. Con lo stesso seme il generatore propone gli stessi nemici negli stessi piani. Mi serve per ricreare la partita dopo un caricamento.

### `GeneratoreNemiciPerZona`

Contiene i numeri e le mosse dei nove nemici comuni. Ogni nemico ha una mossa gratuita e una più forte che costa 6 reagenti. Nelle zone più profonde i nemici hanno più vita e colpiscono più forte. I valori sono provvisori e li sistemerò nel bilanciamento.

Nei primi tre piani di ogni zona incontro tutti e tre i nemici della zona, uno per principio, ma in un ordine diverso a ogni partita. Ho scelto così per avere partite più varie: in ogni zona devo usare tutti e tre gli elementi. Per farlo, all'inizio della partita il generatore mescola la lista dei tre principi di ogni zona con `Collections.shuffle` e la salva in una `EnumMap`, una mappa che ha come chiavi i valori di un enum. Per ora anche il quarto piano di ogni zona ha un nemico comune, scelto a caso. Lì arriveranno i guardiani.

Il generatore riceve il seme dal costruttore e crea da lì il suo `Random`. Il seme risolve due problemi. Nei test posso usare un seme fisso e il risultato si ripete uguale. Quando carico una partita, ricreo il generatore con il seme salvato: le zone vengono mescolate nello stesso ordine di prima e non ritrovo un nemico che avevo già sconfitto.

### `StatoPartita` (enum)

Dice se la partita è in corso, vinta o persa.

### `Partita`

Tiene insieme la partita intera: l'alchimista, il piano attuale e lo scontro in corso. La grafica parlerà solo con questa classe.

`passaAlPianoSuccessivo()` porta l'alchimista al piano dopo. Prima controlla che lo scontro sia vinto e che la partita non sia finita, poi toglie all'alchimista gli effetti rimasti dallo scontro (una Corazza non deve passare al piano dopo), gli restituisce il 30% della vita massima e metà dei reagenti massimi, e chiede al generatore il nemico del nuovo piano. Senza questo recupero il gioco diventerebbe frustrante dopo pochi piani. All'inizio restituivo tutti i reagenti, ma così non c'era mai motivo di risparmiarli: ora chi spreca in uno scontro lo paga anche in quello dopo.

`getStato()` restituisce PERSA se l'alchimista è sconfitto e VINTA se ha battuto il nemico dell'ultimo piano. Per ora l'ultimo piano è il 12. Quando aggiungerò la Camera del Sigillo, la vittoria arriverà al piano 13.

La partita riceve l'alchimista e il generatore dal costruttore, invece di crearli lei. Così dipende da un'interfaccia e non da una classe concreta (principio di inversione delle dipendenze). Per il caricamento le passo un alchimista ricreato dai dati salvati.

Un secondo costruttore riceve anche il piano: lo uso per riprendere una partita salvata dall'inizio dello scontro di quel piano. `getSeme()` chiede il seme al generatore, così la persistenza lo può salvare senza sapere quale generatore usa la partita.

## Package `persistenza`

Come salvo la partita lo spiego nella pagina [Persistenza](Persistenza). Qui scrivo solo le responsabilità.

### `GestoreSalvataggi` (interfaccia)

Salva, carica e cancella la partita con tre metodi: `salva(partita)`, `carica()` e `cancella()`. La grafica conosce solo questa interfaccia: l'unico punto che sceglie il file JSON è `Main`. Per salvare in un altro modo, per esempio su un server per una versione web, scrivo un'altra implementazione e la grafica resta com'è.

### `DatiPartita` (record)

La fotocopia della partita che finisce nel file: nome, vita e reagenti dell'alchimista, piano e seme. Un `record` è una classe di soli dati che Java scrive per me (costruttore, metodi per leggere i campi, `equals`). L'ho usato perché `DatiPartita` non ha regole. Sa copiare i dati da una partita (`da`) e ricreare la partita dai dati (`creaPartita`). È visibile solo dentro il package `persistenza`: il resto del programma non sa che esiste.

### `SalvataggioJson`

Implementa `GestoreSalvataggi` con un file JSON, scritto e letto con la libreria Gson. Il file sta nella cartella dell'utente, in `.apprendista-alchimista/salvataggio.json`, così funziona su Windows, macOS e Linux. Nei test passo al costruttore un file in una cartella temporanea.

## Package `grafica`

### `Main`

Avvia l'applicazione JavaFX: apre la finestra, crea il gestore dei salvataggi (`SalvataggioJson`) e lo passa al `Navigatore`, poi gli chiede di mostrare il menu. Per salvare in un altro modo cambierei solo questa riga.

### `Navigatore`

Decide quale schermata mostrare. Le schermate non si conoscono tra loro: ognuna riceve dal navigatore cosa fare quando il giocatore vuole andare avanti (un `Runnable`, oppure un `Consumer<String>` per il menu, che passa il nome scelto). Per esempio, quando si preme "Continua" dopo uno scontro, il navigatore guarda lo stato della partita: se è finita mostra la schermata finale, altrimenti fa scendere l'alchimista al piano dopo e mostra la schermata tra i piani.

Decide anche quando salvare: quando inizia una partita nuova e ogni volta che l'alchimista scende di piano. Appena uno scontro chiude la partita (vinta o persa) cancella il salvataggio, già prima che il giocatore prema "Continua": così chiudere la finestra dopo una sconfitta non permette di riprovare il piano. Se il file non si riesce a scrivere, mostra un avviso e il gioco va avanti.

La finestra resta sempre la stessa e cambia solo il contenuto (`scena.setRoot(...)`). Per aggiungere una schermata nuova, come il negozio, basta scriverla e collegarla qui, senza toccare le altre.

### `SchermataMenu`

Il titolo del gioco, una casella per il nome dell'alchimista e i pulsanti "Nuova partita" ed "Esci". Se c'è una partita salvata compare anche "Continua", con il nome dell'alchimista e il piano (per esempio "Continua: Mira, piano 5"). "Nuova partita" sostituisce il salvataggio precedente. La casella non accetta più di 10 caratteri; se resta vuota il nome diventa "Alchimista".

### `SchermataTraPiani`

Compare dopo ogni scontro vinto. Dice quale piano è stato superato, quanta vita e quanti reagenti ha recuperato l'alchimista e se sta entrando in una nuova zona. Il recupero lo calcola `Partita`: il navigatore legge la vita prima e dopo il passaggio di piano e passa la differenza alla schermata.

### `SchermataFinale`

Dice se la partita è vinta o persa, e in caso di sconfitta il piano e la zona raggiunti. Il pulsante "Torna al menu" permette di iniziare una nuova partita.

### `Aspetto`

Raccoglie in un posto solo come la grafica mostra le cose del modello: i nomi e i colori degli elementi, i nomi delle zone e gli stili usati da più schermate. Prima colori e nomi erano dentro `SchermataCombattimento`; quando sono serviti anche altrove li ho spostati qui invece di copiarli.

### `SchermataCombattimento`

Mostra uno scontro: il nemico in alto, l'alchimista in basso, il registro dei messaggi a destra e un pulsante per ogni mossa. Nel testo del pulsante c'è il costo della mossa in reagenti, tra parentesi (per esempio "Fiala Ardente (4)"), e le mosse che non si possono pagare restano disattivate (lo controllo con `haReagenti`). Per le mosse con un limite il pulsante mostra anche gli usi rimasti, per esempio "Distillazione [1 su 2]", e si spegne quando sono finiti (lo chiede a `puoUsare` del combattimento). I pulsanti stanno in un `FlowPane`, un contenitore che li manda a capo da solo quando non ci stanno in una riga. Accanto a vita e reagenti compaiono gli effetti attivi con i turni che restano, per esempio "Veleno (2)".

Quando si preme un pulsante, la schermata passa la mossa a `turnoAlchimista` e poi rilegge tutto dal modello: vita, reagenti, registro e se l'elemento del nemico è stato scoperto. Finché non è scoperto il riquadro del nemico ha solo un bordo tratteggiato; dopo prende il colore del suo elemento (Zolfo giallo, Mercurio grigio, Sale azzurrino), e una piccola legenda spiega i colori.

Quando lo scontro finisce, al posto dei pulsanti delle mosse compare un riepilogo: l'esito, le statistiche dello scontro e il pulsante "Continua". La schermata riceve dal navigatore due azioni: una parte appena lo scontro finisce (serve a cancellare il salvataggio se la partita è finita), l'altra quando si preme "Continua".

La schermata non contiene regole del gioco. Per fare una versione web o mobile basterebbe riscrivere questa classe, lasciando il modello com'è.

## Test

`ElementoTest` controlla il ciclo dei vantaggi, gli svantaggi, lo scontro tra elementi uguali e il comportamento di Neutro.

`CombattenteTest` controlla danni, cure, sconfitta, reagenti, il moltiplicatore dei colpi (anche l'arrotondamento: metà di 5 fa 3) e il metodo `usa`, compreso il caso in cui i reagenti non bastano. Una classe astratta non si può creare, quindi i test usano `CombattenteDiProva`, una piccola sottoclasse che esiste solo nella cartella dei test.

`AlchimistaTest` controlla lo stato iniziale, lo strumento di partenza, le mosse iniziali, che la lista delle mosse non si possa modificare da fuori e che il nome venga rifiutato se è vuoto o più lungo di 10 caratteri. Controlla anche il costruttore per i salvataggi: ricrea vita e reagenti e rifiuta valori impossibili.

`AzioniTest` controlla cosa fanno attacco base, colpo e Distillazione, e che non si possano creare strumenti o colpi con valori non validi.

`EffettiTest` controlla ogni effetto da solo: quanto toglie il Veleno e quando sparisce, che la Corazza dimezzi i colpi ma non il Veleno, che il Catalizzatore valga per un solo colpo e si perda se non viene usato, che la Pietrificazione blocchi solo fino a fine turno, che lo stesso effetto non si sommi e che la lista degli effetti non si possa modificare da fuori. Controlla anche `ColpoConEffetto` (ogni bersaglio riceve un effetto nuovo, nessun effetto su un bersaglio già sconfitto) e `Preparazione` (l'effetto va a chi la usa).

`NemicoComuneTest` controlla che il nemico usi tutte le sue mosse quando ha i reagenti, solo quelle gratuite quando non li ha, che con lo stesso seme faccia le stesse scelte e che non si possa creare un nemico senza mosse gratuite.

`CombattimentoTest` controlla un turno completo con i messaggi del registro, le statistiche dello scontro, il vantaggio di elemento durante lo scontro, la vittoria (il nemico sconfitto non contrattacca), la sconfitta, che a fine turno solo il nemico recuperi reagenti, il limite di 2 Distillazioni per scontro (e che riparta da zero nello scontro dopo), quando si scopre l'elemento del nemico (dopo la sua prima mossa, mai se muore prima di muovere) e i casi vietati: giocare dopo la fine, usare una mossa sconosciuta o una mossa che non si può pagare. Controlla anche gli effetti dentro lo scontro: il nemico pietrificato salta il turno, il Veleno conta tra i danni inflitti e può vincere lo scontro, se l'alchimista cade il Veleno non agisce più, la Corazza protegge già dalla risposta del nemico e il Catalizzatore raddoppia il colpo del turno dopo. Il nemico di prova ha una sola mossa, così so sempre cosa farà.

`ZonaTest` controlla a quale zona appartengono i piani, la posizione dentro la zona e che i piani fuori dalle zone vengano rifiutati.

`GeneratoreNemiciPerZonaTest` prova 20 semi diversi e controlla ogni volta che nei primi tre piani di ogni zona escano tutti e tre i principi. Controlla anche che ogni volta nasca un nemico nuovo con la vita piena, che i nemici della Fornace abbiano più vita di quelli delle Cantine e che due generatori con lo stesso seme propongano gli stessi nemici in tutti i piani.

`PartitaTest` usa `GeneratoreDiProva`, una piccola classe che crea sempre lo stesso nemico: in un test cade al primo colpo, in un altro è imbattibile. Prima i generatori di prova erano lambda; da quando l'interfaccia ha anche `getSeme()` una lambda non basta più. Il test controlla l'inizio della partita, la ripresa da un piano qualsiasi (e il rifiuto dei piani che non esistono), che non si possa scendere senza aver vinto, il recupero di vita e reagenti (anche che la vita non superi il massimo), che scendendo di piano si perdano gli effetti, la vittoria all'ultimo piano e la sconfitta.

`SalvataggioJsonTest` scrive in una cartella temporanea che JUnit crea e cancella da solo (`@TempDir`). Controlla che una partita salvata si ricarichi uguale, con lo stesso nemico al suo piano, che un nuovo salvataggio sostituisca il vecchio, che la cartella venga creata se manca e che dopo `cancella()` non resti niente da caricare (cancellare quando non c'è nessun salvataggio non dà errore). Controlla anche i casi senza salvataggio valido: file assente, vuoto, rovinato, senza nome o con valori impossibili.
