package edu.edext.logica;

import edu.edext.datatypes.DtInscripcionPrograma;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * listarInscriptosPorPrograma: inscriptos de un programa de formacion (lo usa ConsultaPrograma).
 */
public class ControladorInscriptosProgramaTest {

    private final IControlador ic = DatosUnicos.ic();

    @Test
    public void testProgramaInexistente() {
        Exception e = assertThrows(Exception.class, () -> ic.listarInscriptosPorPrograma("no-existe-" + DatosUnicos.uid()));
        assertTrue(e.getMessage().contains("No existe el programa"));
    }

    @Test
    public void testProgramaSinInscriptos() throws Exception {
        String programa = DatosUnicos.programa(ic, new HashSet<>(), null);
        assertTrue(ic.listarInscriptosPorPrograma(programa).isEmpty());
    }

    @Test
    public void testProgramaConInscriptos() throws Exception {
        String programa = DatosUnicos.programa(ic, new HashSet<>(), null);
        String ana = DatosUnicos.estudiante(ic);
        String beto = DatosUnicos.estudiante(ic);
        assertEquals(0, ic.setInscripcionPrograma(ana, programa));
        assertEquals(0, ic.setInscripcionPrograma(beto, programa));

        List<DtInscripcionPrograma> inscriptos = ic.listarInscriptosPorPrograma(programa);
        assertEquals(2, inscriptos.size());
        for (DtInscripcionPrograma ins : inscriptos) {
            assertTrue(List.of(ana, beto).contains(ins.getEstudiante().getNickname()));
            assertNull(ins.getEstudiante().getPassword(), "No se expone la contraseña");
            assertEquals(LocalDate.now(), ins.getFechaInscripcion());
            assertEquals("Inscripto", ins.getEstado());
            assertEquals(programa, ins.getPrograma().getNombre());
        }
    }
}
