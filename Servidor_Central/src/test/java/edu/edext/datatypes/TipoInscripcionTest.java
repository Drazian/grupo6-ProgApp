package edu.edext.datatypes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TipoInscripcionTest {

    @Test
    public void testValoresYTexto() {
        assertEquals(4, TipoInscripcion.values().length);
        assertEquals("No Inscripto", TipoInscripcion.NO_INSCRIPTO.getString());
        assertEquals("Inscripto", TipoInscripcion.INSCRIPTO.getString());
        assertEquals("Aceptado", TipoInscripcion.ACEPTADO.getString());
        assertEquals("Rechazado", TipoInscripcion.valueOf("RECHAZADO").getString());
    }
}
