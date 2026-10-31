package edu.edext.logica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InscripcionEdicionIDTest {

    @Test
    public void testGettersYSetters() {
        InscripcionEdicionID id = new InscripcionEdicionID();
        id.setEstudiante("est");
        id.setEdicion("ed");
        assertEquals("est", id.getEstudiante());
        assertEquals("ed", id.getEdicion());
    }

    @Test
    public void testEqualsYHashCode() {
        InscripcionEdicionID a = new InscripcionEdicionID("est", "ed");
        assertTrue(a.equals(a));
        assertTrue(a.equals(new InscripcionEdicionID("est", "ed")));
        assertEquals(a.hashCode(), new InscripcionEdicionID("est", "ed").hashCode());
        assertFalse(a.equals(new InscripcionEdicionID("otro", "ed")));
        assertFalse(a.equals(new InscripcionEdicionID("est", "otra")));
        assertFalse(a.equals(null));
        assertFalse(a.equals("est"));
    }
}
