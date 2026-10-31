package edu.edext.presentacion;

import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.cfg;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario ConsultaCurso. Los JOptionPane se reemplazan con Mockito
 * para que no se abra ninguna ventana durante los tests.
 */
public class ConsultaCursoTest {

    private static final String CURSO_CON_PREVIA = "Seminarios de Resolución de Problemas";
    private static final String PREVIA = "Talleres plenarios";

    private MockedStatic<JOptionPane> dialogos;

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

    private static String nombreUnico(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /** Carga los datos de prueba; si ya estaban cargados el controlador lanza una excepcion que se ignora. */
    private static IControlador controladorConDatos() {
        IControlador ic = Fabrica.getInstance().getIControlador();
        try {
            ic.cargarDatosDePrueba();
        } catch (Exception yaCargados) {
            // los datos ya estaban en la base
        }
        return ic;
    }

    /** Crea el formulario con un controlador dado (Fabrica mockeada solo durante el constructor). */
    private static ConsultaCurso formConControlador(IControlador ic) {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            return new ConsultaCurso();
        }
    }

    private static List<Object> columna(JTable tbl, int col) {
        List<Object> ret = new ArrayList<>();
        for (int i = 0; i < tbl.getRowCount(); i++) ret.add(tbl.getValueAt(i, col));
        return ret;
    }

    @Test
    public void testConsultaCursoConDatosReales() throws Exception {
        IControlador ic = controladorConDatos();
        ConsultaCurso form = new ConsultaCurso();
        JComboBox<String> institutos = campo(form, "institutoSeleccion");
        JComboBox<String> cursos = campo(form, "cursoSeleccion");
        JTextField textoNombre = campo(form, "textoNombre");
        JTable tblPrevias = campo(form, "tblPrevias");

        assertEquals("Seleccione un Instituto...", institutos.getItemAt(0));
        List<String> nombres = new ArrayList<>();
        for (int i = 0; i < institutos.getItemCount(); i++) nombres.add(institutos.getItemAt(i));
        assertTrue(nombres.contains("INCO"), "Se listan los institutos de la base");

        form.cargarCurso(CURSO_CON_PREVIA);

        assertEquals(ic.obtenerInstitutoPorCurso(CURSO_CON_PREVIA), institutos.getSelectedItem());
        assertEquals(CURSO_CON_PREVIA, cursos.getSelectedItem());
        assertEquals(CURSO_CON_PREVIA, textoNombre.getText());
        assertEquals(PREVIA, tblPrevias.getValueAt(0, 0));
        assertEquals(8, tblPrevias.getRowCount(), "La tabla de previas se completa hasta 8 filas");

        // Volver a "Seleccione un Instituto...": se vacian los cursos y los campos
        institutos.setSelectedIndex(0);
        assertEquals(0, cursos.getItemCount());
        assertEquals("", textoNombre.getText());

        dialogos.verifyNoInteractions();
    }

    @Test
    public void testCargarCursoInexistente() throws Exception {
        ConsultaCurso form = new ConsultaCurso();
        form.cargarCurso(nombreUnico("NO-EXISTE"));
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Error al cargar el curso"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testSeleccionarCursoInexistente() throws Exception {
        ConsultaCurso form = new ConsultaCurso();
        JComboBox<String> cursos = campo(form, "cursoSeleccion");
        cursos.removeAllItems();
        cursos.addItem("Seleccione un Curso...");
        cursos.addItem(nombreUnico("NO-EXISTE"));

        cursos.setSelectedIndex(1);

        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El curso no existe."), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testMuestraTodosLosDatosDelCurso() throws Exception {
        String instituto = nombreUnico("INST");
        String cursoCompleto = nombreUnico("CURSO-COMPLETO");
        String cursoSinListas = nombreUnico("CURSO-SIN-LISTAS");
        Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse("15/03/2026");

        // Imagen real dentro de target/test-imagenes
        String imagen = nombreUnico("img") + ".png";
        File archivoImagen = new File(cfg.fullpathImagen() + imagen);
        archivoImagen.getParentFile().mkdirs();
        ImageIO.write(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB), "png", archivoImagen);

        Set<String> categoriasPrevia = new LinkedHashSet<>(List.of("Algebra", "Calculo"));
        DtCurso previaConCategorias = new DtCurso("Previa 1", "desc previa 1", "2 semanas", 10, 2, "www.p1.uy", fecha,
                new DtInstituto(instituto), new LinkedHashSet<String>(), categoriasPrevia, null);
        DtCurso previaSinCategorias = new DtCurso("Previa 2", "desc previa 2", "1 mes", 20, 3, "www.p2.uy", fecha,
                new DtInstituto(instituto), new LinkedHashSet<String>(), new LinkedHashSet<String>(), null);

        Set<String> docentes = new LinkedHashSet<>(List.of("docente1", "docente2"));
        Set<String> sinDocentes = new LinkedHashSet<>();
        LocalDate ini = LocalDate.of(2026, 4, 1), fin = LocalDate.of(2026, 6, 30), pub = LocalDate.of(2026, 3, 1);
        DtEdicion edicionCupoNulo = new DtEdicion("Ed cupo nulo", null, ini, fin, pub, null, docentes);
        DtEdicion edicionCupoCero = new DtEdicion("Ed cupo cero", 0, ini, fin, pub, null, sinDocentes);
        DtEdicion edicionConCupo = new DtEdicion("Ed con cupo", 25, ini, fin, pub, null, sinDocentes);

        List<DtCurso> cursosPrograma = List.of(previaConCategorias);
        DtPrograma programa = new DtPrograma("Programa 1", "desc programa", pub, ini, fin, cursosPrograma);

        DtConsultaCurso completo = new DtConsultaCurso(cursoCompleto, "descripcion completa", "4 semanas", 40, 4, "www.curso.uy", fecha,
                List.of(edicionCupoNulo, edicionCupoCero, edicionConCupo), List.of(programa),
                List.of(new DtCategoria("Algebra"), new DtCategoria("Calculo")), List.of(previaConCategorias, previaSinCategorias), imagen);
        DtConsultaCurso sinListas = new DtConsultaCurso(cursoSinListas, "sin listas", "1 semana", 5, 1, "www.vacio.uy", fecha,
                (Set<String>) null, (Set<String>) null, (Set<String>) null, (Set<String>) null, null);

        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenReturn(List.of(new DtInstituto(instituto)));
        when(ic.listarCursosPorInstituto(instituto)).thenReturn(List.of(cursoCompleto, cursoSinListas));
        when(ic.obtenerDatosCurso(cursoCompleto)).thenReturn(completo);
        when(ic.obtenerDatosCurso(cursoSinListas)).thenReturn(sinListas);
        when(ic.obtenerInstitutoPorCurso(cursoCompleto)).thenReturn(instituto);

        ConsultaCurso form = formConControlador(ic);
        JComboBox<String> institutos = campo(form, "institutoSeleccion");
        JComboBox<String> cursos = campo(form, "cursoSeleccion");
        assertEquals(2, institutos.getItemCount());

        form.cargarCurso(cursoCompleto);

        assertEquals(instituto, institutos.getSelectedItem());
        assertEquals(3, cursos.getItemCount());
        assertEquals(cursoCompleto, ((JTextField) campo(form, "textoNombre")).getText());
        assertEquals("descripcion completa", ((JTextArea) campo(form, "textoDescripcion")).getText());
        assertEquals("4 semanas", ((JTextField) campo(form, "textoDuracion")).getText());
        assertEquals("40", ((JTextField) campo(form, "textoHoras")).getText());
        assertEquals("4", ((JTextField) campo(form, "textoCreditos")).getText());
        assertEquals("www.curso.uy", ((JTextField) campo(form, "textoURL")).getText());
        assertEquals("15/03/2026", ((JTextField) campo(form, "textoFechaAlta")).getText());
        assertNotNull(((JLabel) campo(form, "lblImagen")).getIcon());

        JTable tblPrevias = campo(form, "tblPrevias");
        assertEquals(8, tblPrevias.getRowCount());
        assertEquals("Previa 1", tblPrevias.getValueAt(0, 0));
        assertEquals("Algebra, Calculo", tblPrevias.getValueAt(0, 5));
        assertEquals("Previa 2", tblPrevias.getValueAt(1, 0));
        assertEquals("", tblPrevias.getValueAt(1, 5), "Previa sin categorias");

        JTable tblEdiciones = campo(form, "tblEdiciones");
        assertEquals(8, tblEdiciones.getRowCount());
        assertEquals(List.of("Ed cupo nulo", "Ed cupo cero", "Ed con cupo"), columna(tblEdiciones, 0).subList(0, 3));
        assertEquals("Sin cupo", tblEdiciones.getValueAt(0, 1));
        assertEquals("Sin cupo", tblEdiciones.getValueAt(1, 1));
        assertEquals(25, tblEdiciones.getValueAt(2, 1));
        assertEquals("docente1, docente2", tblEdiciones.getValueAt(0, 5));

        JTable tblProgramas = campo(form, "tblProgramas");
        assertEquals("Programa 1", tblProgramas.getValueAt(0, 0));
        assertEquals("desc programa", tblProgramas.getValueAt(0, 5));

        JTable tblCategorias = campo(form, "tblCategorias");
        assertEquals(List.of("Algebra", "Calculo"), columna(tblCategorias, 0).subList(0, 2));
        assertEquals(8, tblCategorias.getRowCount());

        // Curso sin listas asociadas (null): solo se cargan los datos basicos y las tablas quedan vacias
        cursos.setSelectedItem(cursoSinListas);
        assertEquals(cursoSinListas, ((JTextField) campo(form, "textoNombre")).getText());
        assertEquals(0, tblPrevias.getRowCount());
        assertEquals(0, tblEdiciones.getRowCount());
        assertEquals(0, tblProgramas.getRowCount());
        assertEquals(0, tblCategorias.getRowCount());

        dialogos.verifyNoInteractions();
    }

    @Test
    public void testCursoSinFechaYListasDeUnElemento() throws Exception {
        String instituto = nombreUnico("INST");
        String cursoConFecha = nombreUnico("CURSO-CON-FECHA");
        String cursoSinFecha = nombreUnico("CURSO-SIN-FECHA");
        Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse("02/01/2026");
        LocalDate ini = LocalDate.of(2026, 4, 1), fin = LocalDate.of(2026, 6, 30), pub = LocalDate.of(2026, 3, 1);

        // Colecciones de un solo elemento de un caracter: no deben quedar con el separador final
        DtCurso previa = new DtCurso("P", "desc", "1 mes", 10, 2, "www.p.uy", null,
                new DtInstituto(instituto), new LinkedHashSet<String>(), new LinkedHashSet<>(List.of("a")), null);
        DtEdicion edicion = new DtEdicion("E", 10, ini, fin, pub, null, new LinkedHashSet<>(List.of("d")));

        DtConsultaCurso conFecha = new DtConsultaCurso(cursoConFecha, "con fecha", "1 semana", 5, 1, "www.con.uy", fecha,
                (Set<String>) null, (Set<String>) null, (Set<String>) null, (Set<String>) null, null);
        DtConsultaCurso sinFecha = new DtConsultaCurso(cursoSinFecha, "sin fecha", "2 semanas", 8, 2, "www.sin.uy", null,
                List.of(edicion), null, null, List.of(previa), null);

        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenReturn(List.of(new DtInstituto(instituto)));
        when(ic.listarCursosPorInstituto(instituto)).thenReturn(List.of(cursoConFecha, cursoSinFecha));
        when(ic.obtenerDatosCurso(cursoConFecha)).thenReturn(conFecha);
        when(ic.obtenerDatosCurso(cursoSinFecha)).thenReturn(sinFecha);

        ConsultaCurso form = formConControlador(ic);
        JComboBox<String> institutos = campo(form, "institutoSeleccion");
        JComboBox<String> cursos = campo(form, "cursoSeleccion");
        JTextField textoFechaAlta = campo(form, "textoFechaAlta");
        institutos.setSelectedItem(instituto);
        cursos.setSelectedItem(cursoConFecha);
        assertEquals("02/01/2026", textoFechaAlta.getText());

        // Sin fecha de registro: el campo queda vacio y se cargan el resto de los datos
        cursos.setSelectedItem(cursoSinFecha);
        assertEquals("", textoFechaAlta.getText());
        assertEquals(cursoSinFecha, ((JTextField) campo(form, "textoNombre")).getText());
        assertEquals("www.sin.uy", ((JTextField) campo(form, "textoURL")).getText());
        JTable tblPrevias = campo(form, "tblPrevias");
        assertEquals("P", tblPrevias.getValueAt(0, 0));
        assertNull(tblPrevias.getValueAt(0, 4), "La previa tampoco tiene fecha");
        assertEquals("a", tblPrevias.getValueAt(0, 5));
        JTable tblEdiciones = campo(form, "tblEdiciones");
        assertEquals("E", tblEdiciones.getValueAt(0, 0));
        assertEquals("d", tblEdiciones.getValueAt(0, 5));

        dialogos.verifyNoInteractions();
    }

    @Test
    public void testErroresDelControlador() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenThrow(new RuntimeException("sin conexion"));
        when(ic.listarCursosPorInstituto(anyString())).thenThrow(new RuntimeException("fallo cursos"));

        ConsultaCurso form = formConControlador(ic);
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar institutos: sin conexion"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));

        JComboBox<String> institutos = campo(form, "institutoSeleccion");
        institutos.addItem("Instituto con error");
        institutos.setSelectedIndex(1);
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("fallo cursos"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testModelosYListenersDeTablas() throws Exception {
        ConsultaCurso form = new ConsultaCurso();
        String[] tablas = {"tblPrevias", "tblEdiciones", "tblProgramas", "tblCategorias"};
        for (String nombre : tablas) {
            JTable tbl = campo(form, nombre);
            TableModel model = tbl.getModel();
            assertEquals(String.class, model.getColumnClass(0), nombre);
            for (int c = 0; c < model.getColumnCount(); c++)
                assertFalse(model.isCellEditable(0, c), nombre + " no editable");
            // Los clics en las tablas no hacen nada (handlers vacios)
            tbl.dispatchEvent(new MouseEvent(tbl, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false));
        }
        assertEquals(Integer.class, ((JTable) campo(form, "tblPrevias")).getModel().getColumnClass(2));
        assertEquals(Integer.class, ((JTable) campo(form, "tblEdiciones")).getModel().getColumnClass(1));

        // Enter en la fecha de alta: handler vacio
        JTextField fecha = campo(form, "textoFechaAlta");
        fecha.setText("01/01/2026");
        fecha.postActionEvent();
        assertEquals("01/01/2026", fecha.getText());

        dialogos.verifyNoInteractions();
    }

    @Test
    public void testArrastrarFormulario() throws Exception {
        ConsultaCurso form = new ConsultaCurso();
        // El UI del JInternalFrame necesita una ventana ancestro para procesar el mouse; el JFrame nunca se muestra
        JFrame ventana = new JFrame();
        try {
            JDesktopPane escritorio = new JDesktopPane();
            ventana.setContentPane(escritorio);
            escritorio.add(form);
            form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false));
            form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
            assertNotNull(form.getLocation());
            assertFalse(ventana.isVisible());
        } finally {
            ventana.dispose();
        }
    }
}
