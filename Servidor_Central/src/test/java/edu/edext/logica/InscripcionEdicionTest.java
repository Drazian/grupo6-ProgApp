package edu.edext.logica;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InscripcionEdicionTest {

    private final Estudiante est1 = new Estudiante("e1", "p", "e1@x", "n", "a", null, null);
    private final Estudiante est2 = new Estudiante("e2", "p", "e2@x", "n", "a", null, null);
    private final Edicion ed1 = new Edicion("ed1", 1, null, null, null, null, null, null);
    private final Edicion ed2 = new Edicion("ed2", 1, null, null, null, null, null, null);
    private final LocalDate hoy = LocalDate.now();

    @Test
    public void testGettersYSetters() {
        InscripcionEdicion i = new InscripcionEdicion();
        i.setEstudiante(est1);
        i.setEdicion(ed1);
        i.setFechaInscripcion(hoy);
        i.setEstado(2);
        assertSame(est1, i.getEstudiante());
        assertSame(ed1, i.getEdicion());
        assertEquals(hoy, i.getFechaInscripcion());
        assertEquals(2, i.getEstado());
    }

    @Test
    public void testEqualsYHashCodePorEstudianteYEdicion() {
        InscripcionEdicion a = new InscripcionEdicion(est1, ed1, hoy, 1);
        assertTrue(a.equals(a));
        assertTrue(a.equals(new InscripcionEdicion(est1, ed1, hoy.minusDays(1), 3)));
        assertEquals(a.hashCode(), new InscripcionEdicion(est1, ed1, hoy, 2).hashCode());
        assertFalse(a.equals(new InscripcionEdicion(est2, ed1, hoy, 1)));
        assertFalse(a.equals(new InscripcionEdicion(est1, ed2, hoy, 1)));
        assertFalse(a.equals(null));
        assertFalse(a.equals("x"));
    }
}
