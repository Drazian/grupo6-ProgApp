package edu.edext.logica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InstitutoTest {

    @Test
    public void testNombre() {
        Instituto i = new Instituto();
        assertNull(i.getNombre());
        i.setNombre("INCO");
        assertEquals("INCO", i.getNombre());
        assertEquals("IMERL", new Instituto("IMERL").getNombre());
    }
}
