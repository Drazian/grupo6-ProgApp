package edu.edext.datatypes;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DtInscripcionEdicionTest {

    @Test
    public void testConstructorSinPrioridad() {
        LocalDate hoy = LocalDate.now();
        DtUsuario est = new DtUsuario("est", null, null, null, null, null, null, null, TipoUsuario.ESTUDIANTE);
        DtEdicion ed = new DtEdicion("ed", 1, hoy, hoy, hoy);
        DtInscripcionEdicion i = new DtInscripcionEdicion(est, ed, hoy, 2);

        assertSame(est, i.getEstudiante());
        assertSame(ed, i.getEdicion());
        assertEquals(hoy, i.getFechaInscripcion());
        assertEquals(2, i.getValorEstado());
        assertEquals("Aceptado", i.getEstado());
        assertEquals(0.0, i.getPrioridad());
        assertEquals(1.5, new DtInscripcionEdicion(est, ed, hoy, 3, 1.5).getPrioridad());
        assertEquals(0, i.NOINSCRIPTO);
        assertEquals(1, i.INSCRIPTO);
        assertEquals(2, i.ACEPTADO);
        assertEquals(3, i.RECHAZADO);
    }
}
