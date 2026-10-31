package edu.edext.tools;

import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de InscripcionEdicionHelper. El controlador se reemplaza con un mock
 * (mockStatic de Fabrica) para controlar las respuestas y simular errores.
 */
public class InscripcionEdicionHelperTest {

    private MockedStatic<Fabrica> fabrica;
    private IControlador ic;

    @BeforeEach
    public void setUp() {
        ic = mock(IControlador.class);
        Fabrica f = mock(Fabrica.class);
        when(f.getIControlador()).thenReturn(ic);
        fabrica = mockStatic(Fabrica.class);
        fabrica.when(Fabrica::getInstance).thenReturn(f);
    }

    @AfterEach
    public void tearDown() {
        fabrica.close();
    }

    @Test
    public void testSetYDelInscripcion() throws Exception {
        when(ic.setInscripcion("est", "ed")).thenReturn(1);
        when(ic.delInscripcion("est", "ed")).thenReturn(2);
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper();
        assertEquals(1, helper.setInscripcion("est", "ed"));
        assertEquals(2, helper.detInscripcion("est", "ed"));
        verify(ic).setInscripcion("est", "ed");
        verify(ic).delInscripcion("est", "ed");
    }

    @Test
    public void testSetInscripcionPropagaError() throws Exception {
        when(ic.setInscripcion(anyString(), anyString())).thenThrow(new Exception("cupo lleno"));
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper();
        Exception e = assertThrows(Exception.class, () -> helper.setInscripcion("a", "b"));
        assertEquals("cupo lleno", e.getMessage());
    }

    @Test
    public void testInscriptosYCandidatos() throws Exception {
        when(ic.getEstudiantesInscriptosEdicion("ed")).thenReturn(List.of("ana", "beto"));
        when(ic.getEstudiantesCandidatosEdicion("ed")).thenReturn(List.of("carla"));
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper();
        assertArrayEquals(new String[]{"ana", "beto"}, helper.getEstudiantesInscriptos("ed"));
        assertArrayEquals(new String[]{"carla"}, helper.getEstudiantesCandidatos("ed"));
    }

    @Test
    public void testInscriptosYCandidatosConError() throws Exception {
        when(ic.getEstudiantesInscriptosEdicion(anyString())).thenThrow(new Exception("sin conexion"));
        when(ic.getEstudiantesCandidatosEdicion(anyString())).thenThrow(new Exception("sin conexion"));
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper();
        assertEquals(0, helper.getEstudiantesInscriptos("ed").length);
        assertEquals(0, helper.getEstudiantesCandidatos("ed").length);
    }
}
