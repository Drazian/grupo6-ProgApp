package edu.edext.logica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InscripcionProgramaIDTest {

    @Test
    public void testGettersYSetters() {
        InscripcionProgramaID id = new InscripcionProgramaID();
        id.setEstudiante("est");
        id.setPrograma("p1");
        assertEquals("est", id.getEstudiante());
        assertEquals("p1", id.getPrograma());
        id.setProgramaFormacion("p2");
        assertEquals("p2", id.getProgramaFormacion());
        assertEquals("p2", id.getPrograma());
    }

    @Test
    public void testEqualsYHashCode() {
        InscripcionProgramaID a = new InscripcionProgramaID("est", "p");
        assertTrue(a.equals(a));
        assertTrue(a.equals(new InscripcionProgramaID("est", "p")));
        assertEquals(a.hashCode(), new InscripcionProgramaID("est", "p").hashCode());
        assertFalse(a.equals(new InscripcionProgramaID("otro", "p")));
        assertFalse(a.equals(new InscripcionProgramaID("est", "otro")));
        assertFalse(a.equals(null));
        assertFalse(a.equals("est"));
    }
}
