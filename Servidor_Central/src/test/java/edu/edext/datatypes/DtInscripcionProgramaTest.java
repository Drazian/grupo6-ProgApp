package edu.edext.datatypes;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DtInscripcionProgramaTest {

    @Test
    public void testGetters() {
        LocalDate hoy = LocalDate.now();
        DtUsuario est = new DtUsuario("est", null, null, null, null, null, null, null, TipoUsuario.ESTUDIANTE);
        DtPrograma prog = new DtPrograma("P", hoy, hoy, hoy);
        DtInscripcionPrograma i = new DtInscripcionPrograma(est, prog, hoy, 1);

        assertSame(est, i.getEstudiante());
        assertSame(prog, i.getPrograma());
        assertEquals(hoy, i.getFechaInscripcion());
        assertEquals(1, i.getValorEstado());
        assertEquals("Inscripto", i.getEstado());
        assertEquals("No Inscripto", new DtInscripcionPrograma(est, prog, hoy, 0).getEstado());
        assertEquals("Rechazado", new DtInscripcionPrograma(est, prog, hoy, 3).getEstado());
        assertEquals(0, i.NOINSCRIPTO);
        assertEquals(1, i.INSCRIPTO);
        assertEquals(2, i.ACEPTADO);
        assertEquals(3, i.RECHAZADO);
    }
}
