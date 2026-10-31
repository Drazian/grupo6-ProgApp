package edu.edext.tools;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de ConsultaEdicionHelper con el controlador simulado (mockStatic de Fabrica).
 */
public class ConsultaEdicionHelperTest {

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
    public void testInstitutosYRefresh() throws Exception {
        when(ic.listarInstitutos()).thenReturn(List.of(new DtInstituto("INCO")))
                                   .thenReturn(List.of(new DtInstituto("INCO"), new DtInstituto("IMERL")));
        ConsultaEdicionHelper helper = new ConsultaEdicionHelper();
        assertArrayEquals(new String[]{"INCO"}, helper.getListInstitutos());
        helper.refreshInstituto();
        assertArrayEquals(new String[]{"INCO", "IMERL"}, helper.getListInstitutos());
        // Antes de consultar una edicion la imagen tiene el valor inicial
        assertEquals("hola", helper.getImagen());
    }

    @Test
    public void testInstitutosConErrorYVacios() throws Exception {
        when(ic.listarInstitutos()).thenThrow(new Exception("sin conexion"));
        ConsultaEdicionHelper conError = new ConsultaEdicionHelper();
        assertEquals(0, conError.getListInstitutos().length);

        reset(ic);
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        assertEquals(0, new ConsultaEdicionHelper().getListInstitutos().length);
    }

    @Test
    public void testCursosEdicionesEInscriptos() throws Exception {
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        when(ic.listarCursosPorInstituto("INCO")).thenReturn(List.of("Prog", "BD"));
        LocalDate hoy = LocalDate.now();
        when(ic.listarEdicionPorCurso("Prog")).thenReturn(List.of(
                new DtEdicion("Prog-2025", 10, hoy, hoy, hoy),
                new DtEdicion("Prog-2026", 20, hoy, hoy, hoy)));
        when(ic.listarEdicionPorCurso("BD")).thenReturn(new ArrayList<>());
        when(ic.getEstudiantesInscriptosEdicion("Prog-2025")).thenReturn(List.of("ana"));

        ConsultaEdicionHelper helper = new ConsultaEdicionHelper();
        assertArrayEquals(new String[]{"Prog", "BD"}, helper.getListCursos("INCO"));
        assertArrayEquals(new String[]{"Prog-2025", "Prog-2026"}, helper.getListaEdiciones("Prog"));
        assertEquals(0, helper.getListaEdiciones("BD").length);
        assertArrayEquals(new String[]{"ana"}, helper.getListaInscriptos("Prog-2025"));
    }

    @Test
    public void testCursosEdicionesEInscriptosConError() throws Exception {
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        when(ic.listarCursosPorInstituto(anyString())).thenThrow(new Exception("x"));
        when(ic.listarEdicionPorCurso(anyString())).thenThrow(new Exception("x"));
        when(ic.getEstudiantesInscriptosEdicion(anyString())).thenThrow(new Exception("x"));

        ConsultaEdicionHelper helper = new ConsultaEdicionHelper();
        assertEquals(0, helper.getListCursos("INCO").length);
        assertEquals(0, helper.getListaEdiciones("Prog").length);
        assertEquals(0, helper.getListaInscriptos("Prog-2025").length);
    }

    @Test
    public void testGetDataEncontrada() throws Exception {
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        LocalDate ini = LocalDate.of(2025, 3, 1), fin = LocalDate.of(2025, 7, 1), pub = LocalDate.of(2025, 2, 1);
        DtEdicion edicion = new DtEdicion("Prog-2025", 30, ini, fin, pub, new DtCurso("Prog", "c.png"),
                new ArrayList<DtUsuario>(), Set.of("docente1"), "e.png");
        when(ic.getEdicion("Prog-2025")).thenReturn(edicion);

        ConsultaEdicionHelper helper = new ConsultaEdicionHelper();
        assertTrue(helper.getData("Prog-2025"));
        assertFalse(helper.isEmpty());
        assertEquals("Prog-2025", helper.getNombre());
        assertEquals(30, helper.getCupo());
        assertEquals(ini, helper.getFechaInicio());
        assertEquals(fin, helper.getFechaFin());
        assertEquals(pub, helper.getFechaPublicacion());
        assertEquals("Prog", helper.getCurso());
        assertEquals("e.png", helper.getImagen());
        assertEquals(List.of("docente1"), helper.getDocentes());
    }

    @Test
    public void testGetDataNoEncontradaLimpiaLosDatos() throws Exception {
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        when(ic.getEdicion(anyString())).thenThrow(new Exception("no existe"));

        ConsultaEdicionHelper helper = new ConsultaEdicionHelper();
        assertFalse(helper.getData("nada"));
        assertTrue(helper.isEmpty());
        assertEquals("", helper.getNombre());
        assertEquals(0, helper.getCupo());
        assertEquals("", helper.getCurso());
        assertEquals("", helper.getImagen());
        assertNull(helper.getFechaInicio());
        assertNull(helper.getFechaFin());
        assertNull(helper.getFechaPublicacion());
        assertTrue(helper.getDocentes().isEmpty());
    }
}
