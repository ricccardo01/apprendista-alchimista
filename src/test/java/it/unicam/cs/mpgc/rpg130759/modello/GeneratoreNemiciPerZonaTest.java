package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Prova più semi diversi, così l'ordine mescolato cambia da una prova all'altra.
 */
class GeneratoreNemiciPerZonaTest {

    @Test
    void neiPrimiTrePianiDiOgniZonaSiIncontranoTuttiITrePrincipi() {
        for (long seme = 0; seme < 20; seme++) {
            GeneratoreNemiciPerZona generatore = new GeneratoreNemiciPerZona(new Random(seme));
            for (int primoPiano = 1; primoPiano <= Zona.ULTIMO_PIANO; primoPiano += Zona.PIANI_PER_ZONA) {
                Set<Elemento> elementi = new HashSet<>();
                for (int piano = primoPiano; piano < primoPiano + 3; piano++) {
                    elementi.add(generatore.creaNemico(piano).getElemento());
                }
                assertEquals(Set.of(Elemento.ZOLFO, Elemento.MERCURIO, Elemento.SALE), elementi);
            }
        }
    }

    @Test
    void ogniVoltaCreaUnNemicoNuovoConLaVitaPiena() {
        GeneratoreNemiciPerZona generatore = new GeneratoreNemiciPerZona(new Random(1));
        Nemico primo = generatore.creaNemico(1);
        primo.subisciDanno(10);

        Nemico secondo = generatore.creaNemico(1);

        assertNotSame(primo, secondo);
        assertEquals(secondo.getVitaMassima(), secondo.getVita());
    }

    @Test
    void iNemiciDellaFornaceSonoPiuForti() {
        GeneratoreNemiciPerZona generatore = new GeneratoreNemiciPerZona(new Random(7));
        for (int cantina = 1; cantina <= 4; cantina++) {
            for (int fornace = 9; fornace <= 12; fornace++) {
                assertTrue(generatore.creaNemico(fornace).getVitaMassima()
                        > generatore.creaNemico(cantina).getVitaMassima());
            }
        }
    }

    @Test
    void unPianoFuoriDalleZoneVieneRifiutato() {
        GeneratoreNemiciPerZona generatore = new GeneratoreNemiciPerZona(new Random(1));
        assertThrows(IllegalArgumentException.class, () -> generatore.creaNemico(13));
    }
}
