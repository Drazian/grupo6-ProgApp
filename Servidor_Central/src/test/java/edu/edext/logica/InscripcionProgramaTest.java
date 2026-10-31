package edu.edext.logica;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InscripcionProgramaTest {

    private final Estudiante est1 = new Estudiante("e1", "p", "e1@x", "n", "a", null, null);
    private final Estudiante est2 = new Estudiante("e2", "p", "e2@x", "n", "a", null, null);
    private final ProgramaFormacion p1 = new ProgramaFormacion("p1", null, null, null, null, null, null, null);
    private final ProgramaFormacion p2 = new ProgramaFormacion("p2", null, null, null, null, null, null, null);
    private final LocalDate hoy = LocalDate.now();

    @Test
    public void testGettersYSetters() {
        InscripcionPrograma i = new InscripcionPrograma(est1, p1, hoy, 1);
        assertSame(est1, i.getEstudiante());
        assertSame(p1, i.getProgramaFormacion());
        assertEquals(hoy, i.getFechaInscripcion());
        assertEquals(1, i.getEstado());

        InscripcionPrograma vacia = new InscripcionPrograma();
        vacia.setEstudiante(est2);
        vacia.setProgramaFormacion(p2);
        vacia.setFechaInscripcion(hoy.minusDays(1));
        vacia.setEstado(2);
        assertSame(est2, vacia.getEstudiante());
        assertSame(p2, vacia.getProgramaFormacion());
        assertEquals(hoy.minusDays(1), vacia.getFechaInscripcion());
        assertEquals(2, vacia.getEstado());
    }

    @Test
    public void testEqualsYHashCodePorEstudianteYPrograma() {
        InscripcionPrograma a = new InscripcionPrograma(est1, p1, hoy, 1);
        assertTrue(a.equals(a));
        assertTrue(a.equals(new InscripcionPrograma(est1, p1, hoy.minusDays(3), 2)));
        assertEquals(a.hashCode(), new InscripcionPrograma(est1, p1, hoy, 3).hashCode());
        assertFalse(a.equals(new InscripcionPrograma(est2, p1, hoy, 1)));
        assertFalse(a.equals(new InscripcionPrograma(est1, p2, hoy, 1)));
        assertFalse(a.equals(null));
        assertFalse(a.equals("x"));
    }
}
