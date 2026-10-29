package it.unicam.cs.mpgc.rpg130759.modello;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ElementoTest {

    @Test
    void ogniPrincipioPrevaleSulSuccessivoDelCiclo() {
        assertEquals(2.0, Elemento.ZOLFO.moltiplicatoreContro(Elemento.MERCURIO));
        assertEquals(2.0, Elemento.MERCURIO.moltiplicatoreContro(Elemento.SALE));
        assertEquals(2.0, Elemento.SALE.moltiplicatoreContro(Elemento.ZOLFO));
    }

    @Test
    void colpireUnPrincipioCheTiBatteDimezzaIlDanno() {
        assertEquals(0.5, Elemento.MERCURIO.moltiplicatoreContro(Elemento.ZOLFO));
        assertEquals(0.5, Elemento.SALE.moltiplicatoreContro(Elemento.MERCURIO));
        assertEquals(0.5, Elemento.ZOLFO.moltiplicatoreContro(Elemento.SALE));
    }

    @Test
    void stessoElementoNonCambiaIlDanno() {
        for (Elemento elemento : Elemento.values()) {
            assertEquals(1.0, elemento.moltiplicatoreContro(elemento));
        }
    }

    @Test
    void neutroNonCambiaMaiIlDanno() {
        for (Elemento elemento : Elemento.values()) {
            assertEquals(1.0, Elemento.NEUTRO.moltiplicatoreContro(elemento));
            assertEquals(1.0, elemento.moltiplicatoreContro(Elemento.NEUTRO));
        }
    }
}
