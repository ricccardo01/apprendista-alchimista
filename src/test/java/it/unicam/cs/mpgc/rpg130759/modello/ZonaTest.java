package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ZonaTest {

    @Test
    void ogniPianoAppartieneAllaSuaZona() {
        assertEquals(Zona.CANTINE, Zona.delPiano(1));
        assertEquals(Zona.CANTINE, Zona.delPiano(4));
        assertEquals(Zona.LABORATORIO, Zona.delPiano(5));
        assertEquals(Zona.LABORATORIO, Zona.delPiano(8));
        assertEquals(Zona.FORNACE, Zona.delPiano(9));
        assertEquals(Zona.FORNACE, Zona.delPiano(12));
    }

    @Test
    void laPosizioneRiparteDaZeroInOgniZona() {
        assertEquals(0, Zona.posizioneNellaZona(1));
        assertEquals(3, Zona.posizioneNellaZona(4));
        assertEquals(0, Zona.posizioneNellaZona(5));
        assertEquals(3, Zona.posizioneNellaZona(12));
    }

    @Test
    void unPianoFuoriDalleZoneVieneRifiutato() {
        assertEquals(12, Zona.ULTIMO_PIANO);
        assertThrows(IllegalArgumentException.class, () -> Zona.delPiano(0));
        assertThrows(IllegalArgumentException.class, () -> Zona.delPiano(13));
    }
}
