package edu.edext.presentacion;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario ConsultaEdicion contra la base H2 de los tests.
 */
public class ConsultaEdicionTest {

    private static final String ID = UUID.randomUUID().toString().substring(0, 8);
    private static final String INSTITUTO = "INST-CE-" + ID;
    private static final String INSTITUTO_VACIO = "INST-CE-VACIO-" + ID;
    private static final String CURSO = "CUR-CE-" + ID;
    /** Edicion con cupo y sin imagen. */
    private static final String EDICION = "ED-CE-" + ID;
    /** Edicion de los datos de prueba con cupo -1 ("Sin cupo") e imagen. */
    private static final String EDICION_SIN_CUPO = "Flor del Ceibo - 2010";
    private static final String EDICION_INEXISTENTE = "ED-CE-NO-EXISTE-" + ID;
    private static String docente;

    private MockedStatic<JOptionPane> dialogos;

    @BeforeAll
    public static void cargarDatos() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        ic.cargarDatosDePrueba();
        ic.crearInstituto(INSTITUTO);
        ic.crearInstituto(INSTITUTO_VACIO);
        ic.altaCurso(new DtCurso(CURSO, "Curso de prueba", "1 mes", 10, 2, "http://edext.test",
                new Date(), new DtInstituto(INSTITUTO), null), INSTITUTO);
        docente = ic.listarDocentes().get(0);
        ic.altaEdicionCurso(CURSO, new DtEdicion(EDICION, 25, LocalDate.of(2030, 3, 1),
                LocalDate.of(2030, 7, 1), LocalDate.of(2030, 1, 15), new DtCurso(CURSO, null), Set.of(docente)));
        DtEdicion sinCupo = ic.getEdicion(EDICION_SIN_CUPO);
        assertNotNull(sinCupo, "Los datos de prueba incluyen " + EDICION_SIN_CUPO);
        assertEquals(-1, sinCupo.getCupo());
        assertNotNull(sinCupo.getImagen());
    }

    @BeforeEach
    public void setUp() {
        dialogos = mockStatic(JOptionPane.class);
    }

    @AfterEach
    public void tearDown() {
        dialogos.close();
    }

    @SuppressWarnings("unchecked")
    private static <T> T campo(Object form, String nombre) throws Exception {
        Field f = form.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return (T) f.get(form);
    }

    private static List<String> elementos(JList<String> lista) {
        ListModel<String> m = lista.getModel();
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < m.getSize(); i++) ret.add(m.getElementAt(i));
        return ret;
    }

    @Test
    public void testConstructorSinInstitutos() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            ConsultaEdicion form = new ConsultaEdicion();
            JComboBox<?> combo = campo(form, "jCInstituto");
            assertEquals(1, combo.getItemCount(), "Queda el modelo inicial del editor");
            // Modelo inicial de la lista de cursos (generado por el editor)
            JList<String> cursos = campo(form, "jLCursos");
            assertEquals(1, cursos.getModel().getSize());
            assertEquals(" ", cursos.getModel().getElementAt(0));
            assertFalse(((JPanel) campo(form, "jPCursos")).isVisible());
        }
    }

    @Test
    public void testListaSinUsoDelEditor() throws Exception {
        ConsultaEdicion form = new ConsultaEdicion();
        JList<String> lista = campo(form, "jList1");
        assertEquals(5, lista.getModel().getSize());
        assertEquals("Item 1", lista.getModel().getElementAt(0));
    }

    @Test
    public void testNavegacionYDatosDeEdicion() throws Exception {
        ConsultaEdicion form = new ConsultaEdicion();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        @SuppressWarnings("unchecked")
        JComboBox<String> combo = campo(form, "jCInstituto");
        JList<String> cursos = campo(form, "jLCursos");
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> docentes = campo(form, "jLdocentes");
        JPanel pCursos = campo(form, "jPCursos");
        JPanel pEdiciones = campo(form, "jPEdiciones");
        JPanel pData = campo(form, "jPData");
        JTextField nombre = campo(form, "jTnombre");
        JTextField cupo = campo(form, "jTcupo");
        JTextField curso = campo(form, "jTcurso");
        JTextField inicio = campo(form, "jTFechaInicio");
        JTextField fin = campo(form, "jTFechaFin");
        JTextField publicacion = campo(form, "jTffechaPublicacion");

        assertTrue(pCursos.isVisible());

        // Instituto sin cursos
        combo.setSelectedItem(INSTITUTO_VACIO);
        assertEquals(0, cursos.getModel().getSize());
        assertFalse(pEdiciones.isVisible());
        assertFalse(pData.isVisible());

        // Instituto con un curso
        combo.setSelectedItem(INSTITUTO);
        assertEquals(List.of(CURSO), elementos(cursos));
        assertEquals(335, ventana.getWidth(), "El formulario se achica al cambiar de instituto");

        // Seleccion de curso en curso (valueIsAdjusting): no carga ediciones
        cursos.getSelectionModel().setValueIsAdjusting(true);
        cursos.setSelectedIndex(0);
        assertFalse(pEdiciones.isVisible());
        cursos.getSelectionModel().setValueIsAdjusting(false);
        assertTrue(pEdiciones.isVisible());
        assertEquals(List.of(EDICION), elementos(ediciones));

        // Seleccion de la edicion: muestra los datos y agranda el formulario
        ediciones.setSelectedIndex(0);
        assertTrue(pData.isVisible());
        assertEquals(795, ventana.getWidth());
        assertEquals(EDICION, nombre.getText());
        assertEquals("25", cupo.getText());
        assertEquals(CURSO, curso.getText());
        assertEquals("01/03/2030", inicio.getText());
        assertEquals("01/07/2030", fin.getText());
        assertEquals("15/01/2030", publicacion.getText());
        assertEquals(List.of(docente), elementos(docentes));

        // Deseleccionar la edicion oculta los datos y achica el formulario
        ediciones.clearSelection();
        assertFalse(pData.isVisible());
        assertEquals(335, ventana.getWidth());

        // Edicion sin cupo (-1) y con imagen; luego una inexistente (no cambia los datos)
        ediciones.setListData(new String[]{EDICION_SIN_CUPO, EDICION_INEXISTENTE});
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salida, true));
        try {
            ediciones.setSelectedIndex(0);
        } finally {
            System.setOut(salidaOriginal);
        }
        assertFalse(salida.toString().contains("Entre con"), "No queda el System.out de depuracion al cargar la imagen");
        assertTrue(pData.isVisible());
        assertEquals(EDICION_SIN_CUPO, nombre.getText());
        assertEquals("Sin cupo", cupo.getText());
        ediciones.setSelectedIndex(1);
        assertEquals(EDICION_SIN_CUPO, nombre.getText(), "Una edicion inexistente no pisa los datos");

        // Seleccion de edicion en curso (valueIsAdjusting): se ignora
        ediciones.getSelectionModel().setValueIsAdjusting(true);
        ediciones.setSelectedIndex(0);
        ediciones.getSelectionModel().setValueIsAdjusting(false);

        // Deseleccionar el curso oculta las ediciones y los datos
        cursos.clearSelection();
        assertFalse(pEdiciones.isVisible());
        assertFalse(pData.isVisible());
    }

    @Test
    public void testEventosDeComponente() throws Exception {
        ConsultaEdicion form = new ConsultaEdicion();
        JPanel pData = campo(form, "jPData");
        JList<String> ediciones = campo(form, "jLEdiciones");
        ediciones.setListData(new String[]{"a"});
        form.dispatchEvent(new ComponentEvent(form, ComponentEvent.COMPONENT_HIDDEN));
        pData.dispatchEvent(new ComponentEvent(pData, ComponentEvent.COMPONENT_SHOWN));
        pData.dispatchEvent(new ComponentEvent(pData, ComponentEvent.COMPONENT_HIDDEN));
        assertEquals(1, ediciones.getModel().getSize());
    }

    @Test
    public void testCargarEdicionExistente() throws Exception {
        ConsultaEdicion form = new ConsultaEdicion();
        form.cargarEdicion(EDICION);
        assertEquals(EDICION, ((JTextField) campo(form, "jTnombre")).getText());
        assertEquals("25", ((JTextField) campo(form, "jTcupo")).getText());
        assertEquals(CURSO, ((JTextField) campo(form, "jTcurso")).getText());
        assertTrue(((JPanel) campo(form, "jPData")).isVisible());
        assertFalse(((JPanel) campo(form, "jPCursos")).isVisible());
        assertFalse(((JPanel) campo(form, "jPEdiciones")).isVisible());
        dialogos.verifyNoInteractions();
    }

    @Test
    public void testCargarEdicionInexistente() throws Exception {
        ConsultaEdicion form = new ConsultaEdicion();
        form.cargarEdicion(EDICION_INEXISTENTE);
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("No se encontró la edición: " + EDICION_INEXISTENTE),
                eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        assertFalse(((JPanel) campo(form, "jPData")).isVisible());
    }

    @Test
    public void testCargarEdicionSinCupo() throws Exception {
        // Cupo -1 de los datos de prueba: igual que al navegar (loadData), se muestra "Sin cupo"
        ConsultaEdicion form = new ConsultaEdicion();
        form.cargarEdicion(EDICION_SIN_CUPO);
        assertEquals(EDICION_SIN_CUPO, ((JTextField) campo(form, "jTnombre")).getText());
        assertEquals("Sin cupo", ((JTextField) campo(form, "jTcupo")).getText());
        assertTrue(((JPanel) campo(form, "jPData")).isVisible());

        // Cupo nulo (alta sin cupo) y cupo 0: tampoco limitan las inscripciones => "Sin cupo"
        IControlador ic = mock(IControlador.class);
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        for (Integer cupo : new Integer[]{null, 0}) {
            when(ic.getEdicion("ED-MOCK")).thenReturn(new DtEdicion("ED-MOCK", cupo, LocalDate.of(2030, 3, 1),
                    LocalDate.of(2030, 7, 1), LocalDate.of(2030, 1, 15), new DtCurso(CURSO, null), Set.of(docente)));
            try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
                f.when(Fabrica::getInstance).thenReturn(fabrica);
                ConsultaEdicion otro = new ConsultaEdicion();
                otro.cargarEdicion("ED-MOCK");
                assertEquals("ED-MOCK", ((JTextField) campo(otro, "jTnombre")).getText());
                assertEquals("Sin cupo", ((JTextField) campo(otro, "jTcupo")).getText(), "Cupo " + cupo);
            }
        }
        dialogos.verifyNoInteractions();
    }

    @Test
    public void testCargarEdicionSinPantalla() throws Exception {
        // Si el propio aviso no se puede mostrar, el error sube al llamador: no se intenta un segundo dialogo
        ConsultaEdicion form = new ConsultaEdicion();
        dialogos.when(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()))
                .thenThrow(new HeadlessException("sin pantalla"));
        assertThrows(HeadlessException.class, () -> form.cargarEdicion(EDICION_INEXISTENTE));
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()), times(1));
        assertFalse(((JPanel) campo(form, "jPData")).isVisible());
    }
}
