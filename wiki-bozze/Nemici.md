# Nemici

Nelle rovine vivono gli esperimenti falliti del maestro: animali trasformati e creature nate dai suoi lavori. Ogni zona ha tre nemici comuni, uno per principio, e ognuno si trova in un posto dove avrebbe senso incontrarlo.

Il nome non dice l'elemento. Lo deve capire chi gioca guardando il nemico: la grafica mostra l'elemento con il suo aspetto.

## Cantine (piani 1-3)

I magazzini bui e umidi sotto il laboratorio.

| Nemico | Elemento | Perché si trova qui |
|---|---|---|
| Ombra di Fuliggine | Zolfo | Nelle cantine il maestro teneva il carbone per la fornace. L'ombra è nata dalla polvere di carbone e dal fumo delle canne fumarie. |
| Verme Cieco | Mercurio | Vive nella terra umida, al buio, e ha perso gli occhi. |
| Ragno Bianco | Sale | Vive tra i barili dei cibi conservati sotto sale ed è coperto di cristalli. |

## Laboratorio (piani 5-7)

Le stanze degli esperimenti.

| Nemico | Elemento | Perché si trova qui |
|---|---|---|
| Cane a Due Teste | Zolfo | Il cane del maestro, usato per un esperimento finito male. |
| Omuncolo | Mercurio | Il piccolo uomo artificiale nato in un'ampolla. Gli scritti attribuiti a Paracelso, da cui vengono anche i tre principi, parlano dell'omuncolo. |
| Manichino di Gesso | Sale | Il modello su cui il maestro studiava il corpo umano, animato per errore. Il gesso è un sale (solfato di calcio). |

## Fornace (piani 9-11)

Le profondità, vicino alla Camera del Sigillo.

| Nemico | Elemento | Perché si trova qui |
|---|---|---|
| Divoratore di Cenere | Zolfo | Si nutre di quello che resta nella fornace. |
| Apprendista Corrotto | Mercurio | Un altro allievo del maestro, impazzito per i vapori di mercurio respirati vicino alla fornace. |
| Mummia di Sale | Sale | Il maestro conservava sotto sale i corpi degli esperimenti più vecchi, vicino alla porta sigillata. |

Nel codice sono tutti oggetti della classe `NemicoComune`: cambiano nome, elemento, vita, reagenti e mosse. Il quarto piano di ogni zona ha invece un guardiano.

Nei primi tre piani di una zona incontro tutti e tre i nemici, ma l'ordine cambia a ogni partita.

## Numeri e mosse

Ogni nemico ha 10 reagenti, una mossa gratuita e una mossa forte che costa 6 reagenti. Tutte e due le mosse sono del suo elemento. I numeri sono provvisori.

| Nemico | Vita | Mossa gratuita (danno) | Mossa forte (danno) |
|---|---|---|---|
| Ombra di Fuliggine | 28 | Artiglio di Fumo (6) | Nube di Fuliggine (9) |
| Verme Cieco | 34 | Morso (5) | Stretta (9) |
| Ragno Bianco | 30 | Morso (5) | Puntura (10) |
| Cane a Due Teste | 45 | Morso (8) | Doppio Morso (14) |
| Omuncolo | 40 | Graffio (7) | Vapore di Mercurio (13) |
| Manichino di Gesso | 50 | Pugno (7) | Schianto (12) |
| Divoratore di Cenere | 60 | Morso Rovente (11) | Fiammata (17) |
| Apprendista Corrotto | 55 | Fiala Rubata (10) | Mercurio Bollente (17) |
| Mummia di Sale | 65 | Colpo di Bende (10) | Sale negli Occhi (16) |
