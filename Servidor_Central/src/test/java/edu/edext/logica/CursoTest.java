package edu.edext.logica;

import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CursoTest {

    private static Curso curso(String nombre) {
        return new Curso(nombre, "d", "1 mes", 10, 2, "u", new Date(), new Instituto("I"), null, null, "c.png");
    }

    @Test
    public void testEqualsYHashCodePorNombre() {
        Curso a = curso("A");
        assertTrue(a.equals(a));
        assertTrue(a.equals(curso("A")));
        assertEquals(a.hashCode(), curso("A").hashCode());
        assertFalse(a.equals(curso("B")));
        assertFalse(a.equals(null));
        assertFalse(a.equals("A"));
        assertNull(new Curso().getNombre());
    }
}
