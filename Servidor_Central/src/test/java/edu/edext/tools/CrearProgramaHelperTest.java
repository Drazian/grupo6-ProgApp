package edu.edext.tools;

import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de CrearProgramaHelper con el controlador simulado (mockStatic de Fabrica).
 */
public class CrearProgramaHelperTest {

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

    private static DtPrograma programa(String nombre) {
        LocalDate hoy = LocalDate.now();
        return new DtPrograma(nombre, "desc", hoy, hoy, hoy.plusDays(10));
    }

    @Test
    public void testListaOrdenadaSinMayusculas() throws Exception {
        when(ic.listarProgramas()).thenReturn(List.of(programa("beta"), programa("Alfa"), programa("gamma")));
        CrearProgramaHelper helper = new CrearProgramaHelper();

        assertArrayEquals(new String[]{"Alfa", "beta", "gamma"}, helper.getList());
        assertEquals(3, helper.getLista().size());
        assertTrue(helper.isExist("ALFA"), "La busqueda no distingue mayusculas");
        assertFalse(helper.isExist("delta"));
        assertNull(helper.ArrayNull());
        ArrayList<String> origen = new ArrayList<>(List.of("x", "y"));
        assertArrayEquals(new String[]{"x", "y"}, helper.getArray(origen));
    }

    @Test
    public void testListaConErrorQuedaVacia() throws Exception {
        when(ic.listarProgramas()).thenThrow(new Exception("sin conexion"));
        CrearProgramaHelper helper = new CrearProgramaHelper();
        assertEquals(0, helper.getList().length);
        assertTrue(helper.getLista().isEmpty());
        assertFalse(helper.isExist("cualquiera"));
    }

    @Test
    public void testListaVacia() throws Exception {
        when(ic.listarProgramas()).thenReturn(new ArrayList<>());
        CrearProgramaHelper helper = new CrearProgramaHelper();
        assertEquals(0, helper.getList().length);
    }

    @Test
    public void testSetData() throws Exception {
        when(ic.listarProgramas()).thenReturn(new ArrayList<>());
        when(ic.setCrearProgramaFormacion(any(DtPrograma.class))).thenReturn(true);
        CrearProgramaHelper helper = new CrearProgramaHelper();
        LocalDate ini = LocalDate.of(2025, 1, 1), fin = LocalDate.of(2025, 12, 31), reg = LocalDate.of(2024, 12, 1);

        assertTrue(helper.setData("Prog", "Descripcion", reg, ini, fin, "img.png"));

        ArgumentCaptor<DtPrograma> captor = ArgumentCaptor.forClass(DtPrograma.class);
        verify(ic).setCrearProgramaFormacion(captor.capture());
        DtPrograma enviado = captor.getValue();
        assertEquals("Prog", enviado.getNombre());
        assertEquals("Descripcion", enviado.getDescripcion());
        assertEquals(reg, enviado.getFechaRegistro());
        assertEquals(ini, enviado.getFechaInicio());
        assertEquals(fin, enviado.getFechaFin());
        assertEquals("img.png", enviado.getImagen());
    }

    @Test
    public void testSetDataPropagaError() throws Exception {
        when(ic.listarProgramas()).thenReturn(new ArrayList<>());
        when(ic.setCrearProgramaFormacion(any(DtPrograma.class))).thenThrow(new Exception("duplicado"));
        CrearProgramaHelper helper = new CrearProgramaHelper();
        LocalDate hoy = LocalDate.now();
        assertThrows(Exception.class, () -> helper.setData("P", "D", hoy, hoy, hoy, null));
    }
}
