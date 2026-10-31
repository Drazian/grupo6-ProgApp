package edu.edext.presentacion;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario ConsultaPrograma contra la base H2 de los tests.
 * Los JOptionPane se reemplazan con Mockito para que no se abra ninguna ventana.
 */
public class ConsultaProgramaTest {

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

    private static void cargarCursos(ConsultaPrograma form) throws Exception {
        Method m = ConsultaPrograma.class.getDeclaredMethod("cargarCursos");
        m.setAccessible(true);
        m.invoke(form);
    }

    private static String unico(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static int fila(JTable tbl, String nombre) {
        for (int i = 0; i < tbl.getRowCount(); i++)
            if (nombre.equals(tbl.getValueAt(i, 0))) return i;
        return -1;
    }

    /**
     * programaCompleto: dos cursos (uno con previa y categoria) e imagen;
     * programaVacio: sin cursos ni imagen; programaImagenVacia: sin cursos e imagen "".
     */
    private record Datos(String categoria, String cursoBase, String cursoCompleto,
                         String programaCompleto, String programaVacio, String programaImagenVacia) {}

    private static Datos crearDatos() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        String inst = unico("INST"), cat = unico("CAT"), base = unico("CURSO"), completo = unico("CURSO");
        String progCompleto = unico("PROG"), progVacio = unico("PROG"), progImagenVacia = unico("PROG");
        ic.crearInstituto(inst);
        ic.crearCategoria(cat);
        ic.altaCurso(new DtCurso(base, "Curso base", "2 semanas", 10, 3, "http://base", new Date(),
                new DtInstituto(inst), new HashSet<>(), new HashSet<>(), "usr.png"), inst);
        ic.altaCurso(new DtCurso(completo, "Curso completo", "1 meses", 20, 6, "http://completo", new Date(),
                new DtInstituto(inst), new HashSet<>(Set.of(base)), new HashSet<>(Set.of(cat)), "usr.png"), inst);
        assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(progCompleto, "Programa completo", LocalDate.of(2024, 12, 1),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), new HashSet<>(Set.of(base, completo)), "Programa.test.png")));
        assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(progVacio, "Programa vacio", LocalDate.of(2024, 12, 2),
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 11, 30), new HashSet<String>(), (String) null)));
        assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(progImagenVacia, "Programa imagen vacia", LocalDate.of(2024, 12, 3),
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 10, 31), new HashSet<String>(), "")));
        return new Datos(cat, base, completo, progCompleto, progVacio, progImagenVacia);
    }

    private static MockedStatic<Fabrica> fabricaCon(IControlador ic) {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        MockedStatic<Fabrica> f = mockStatic(Fabrica.class);
        f.when(Fabrica::getInstance).thenReturn(fabrica);
        return f;
    }

    @Test
    public void testSeleccionarProgramas() throws Exception {
        Datos d = crearDatos();
        ConsultaPrograma form = new ConsultaPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTextField txtNombre = campo(form, "txtNombre");
        JTextField txtRegistro = campo(form, "txtRegistro");
        JTextField txtInicio = campo(form, "txtInicio");
        JTextField txtFin = campo(form, "txtFin");
        JTextArea txaDescripcion = campo(form, "txaDescripcion");
        JTextArea txtCategorias = campo(form, "txtCategorias");
        JTable tblCursos = campo(form, "tblCursos");
        JLabel lblImagen = campo(form, "lblImagen");

        boolean hayPrograma = false;
        for (int i = 0; i < cb.getItemCount(); i++) hayPrograma |= d.programaCompleto().equals(cb.getItemAt(i));
        assertTrue(hayPrograma, "Los programas creados aparecen en el combo");

        // Programa con cursos (categorias) e imagen; la etiqueta de imagen todavia no tiene tamaño
        assertEquals(0, lblImagen.getWidth());
        cb.setSelectedItem(d.programaCompleto());
        assertEquals(d.programaCompleto(), txtNombre.getText());
        assertEquals("Programa completo", txaDescripcion.getText());
        assertEquals("01/12/2024", txtRegistro.getText());
        assertEquals("01/01/2025", txtInicio.getText());
        assertEquals("31/12/2025", txtFin.getText());
        assertEquals(d.categoria(), txtCategorias.getText());
        assertEquals(2, tblCursos.getRowCount());
        assertTrue(fila(tblCursos, d.cursoBase()) >= 0);
        int completo = fila(tblCursos, d.cursoCompleto());
        assertTrue(completo >= 0);
        // Cada fila tiene exactamente las 6 columnas de la tabla, con el registro como texto dd/MM/yyyy
        DtCurso esperado = null;
        for (DtCurso c : Fabrica.getInstance().getIControlador().listarCursosPorPrograma(d.programaCompleto()))
            if (c.getNombre().equals(d.cursoCompleto())) esperado = c;
        DefaultTableModel modelCursos = (DefaultTableModel) tblCursos.getModel();
        assertEquals(6, modelCursos.getColumnCount());
        assertEquals(List.of(d.cursoCompleto(), "Curso completo", "1 meses", 20, 6,
                        new SimpleDateFormat("dd/MM/yyyy").format(esperado.getFechaRegistro())),
                List.copyOf(modelCursos.getDataVector().get(completo)));
        assertInstanceOf(String.class, tblCursos.getValueAt(completo, 5));
        assertNotNull(lblImagen.getIcon());

        // Programa sin cursos ni imagen, con la etiqueta de imagen ya dimensionada
        lblImagen.setSize(150, 140);
        cb.setSelectedItem(d.programaVacio());
        assertEquals(d.programaVacio(), txtNombre.getText());
        assertEquals("Sin categorías", txtCategorias.getText());
        assertEquals(0, tblCursos.getRowCount());
        assertNotNull(lblImagen.getIcon());

        // Programa con imagen vacia: se usa la imagen por defecto
        cb.setSelectedItem(d.programaImagenVacia());
        assertEquals(d.programaImagenVacia(), txtNombre.getText());
        assertNotNull(lblImagen.getIcon());

        // Modelo de la tabla: tipos de columna y no editable
        DefaultTableModel model = (DefaultTableModel) tblCursos.getModel();
        assertEquals(String.class, model.getColumnClass(0));
        assertEquals(Integer.class, model.getColumnClass(4));
        assertFalse(model.isCellEditable(0, 0));

        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), anyString(), anyString(), anyInt()), never());
    }

    @Test
    public void testCamposDeSoloLecturaYTablaDeInscriptos() throws Exception {
        ConsultaPrograma form = new ConsultaPrograma();
        JTextArea txtCategorias = campo(form, "txtCategorias");
        JTable tblInscriptos = campo(form, "tblInscriptos");

        // Las categorias se muestran pero no se editan, como el resto de los campos
        assertFalse(txtCategorias.isEditable());
        for (String nombre : new String[]{"txtNombre", "txtRegistro", "txtInicio", "txtFin"})
            assertFalse(((JTextField) campo(form, nombre)).isEditable(), nombre);
        assertFalse(((JTextArea) campo(form, "txaDescripcion")).isEditable());

        // Inscriptos: columnas con sentido (sin los "Title n" de diseño), vacia y de solo lectura
        assertEquals(3, tblInscriptos.getColumnCount());
        assertEquals("Estudiante", tblInscriptos.getColumnName(0));
        assertEquals("Fecha de Inscripcion", tblInscriptos.getColumnName(1));
        assertEquals("Estado", tblInscriptos.getColumnName(2));
        assertEquals(0, tblInscriptos.getRowCount());
        ((DefaultTableModel) tblInscriptos.getModel()).addRow(new Object[]{"est", "01/01/2025", "Inscripto"});
        assertFalse(tblInscriptos.getModel().isCellEditable(0, 0));
    }

    @Test
    public void testProgramaConFechasNulas() throws Exception {
        // Un programa sin fechas: se muestran vacias, sin error
        DtPrograma programa = new DtPrograma("ProgramaSinFechas", "Sin fechas", null, null, null);
        IControlador ic = mock(IControlador.class);
        when(ic.listarProgramas()).thenReturn(List.of());
        when(ic.buscarPrograma("ProgramaSinFechas")).thenReturn(programa);
        when(ic.listarCursosPorPrograma("ProgramaSinFechas")).thenReturn(List.of());
        try (MockedStatic<Fabrica> f = fabricaCon(ic)) {
            ConsultaPrograma form = new ConsultaPrograma();
            JTextField txtRegistro = campo(form, "txtRegistro");
            JTextField txtInicio = campo(form, "txtInicio");
            JTextField txtFin = campo(form, "txtFin");
            txtRegistro.setText("viejo");
            txtInicio.setText("viejo");
            txtFin.setText("viejo");

            form.cargarPrograma("ProgramaSinFechas");
            assertEquals("ProgramaSinFechas", ((JTextField) campo(form, "txtNombre")).getText());
            assertEquals("", txtRegistro.getText());
            assertEquals("", txtInicio.getText());
            assertEquals("", txtFin.getText());
            dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()), never());
        }
    }

    @Test
    public void testComboVacioOSinSeleccion() throws Exception {
        crearDatos();
        ConsultaPrograma form = new ConsultaPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTextField txtNombre = campo(form, "txtNombre");
        JTable tblCursos = campo(form, "tblCursos");

        // Sin programas: ni la seleccion ni la carga de cursos hacen nada
        txtNombre.setText("previo");
        cb.removeAllItems();
        cargarCursos(form);
        assertEquals("previo", txtNombre.getText());

        // Con un programa inexistente: buscarPrograma falla y se informa el error
        cb.addItem("Programa inexistente");
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), startsWith("Error al obtener los datos: "), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));

        // Con programas pero ninguno seleccionado: no se carga nada
        int filas = tblCursos.getRowCount();
        cb.setSelectedIndex(-1);
        cargarCursos(form);
        assertEquals("previo", txtNombre.getText());
        assertEquals(filas, tblCursos.getRowCount());

        // Programa inexistente seleccionado: listarCursosPorPrograma falla
        cb.setSelectedIndex(0);
        cargarCursos(form);
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), startsWith("Error al obtener Cursos: "), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testCargarProgramaPorNombre() throws Exception {
        Datos d = crearDatos();
        ConsultaPrograma form = new ConsultaPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTextField txtNombre = campo(form, "txtNombre");
        JTextField txtRegistro = campo(form, "txtRegistro");
        JTextField txtInicio = campo(form, "txtInicio");
        JTextField txtFin = campo(form, "txtFin");
        JTable tblCursos = campo(form, "tblCursos");
        JTextArea txtCategorias = campo(form, "txtCategorias");
        JLabel lblImagen = campo(form, "lblImagen");
        lblImagen.setIcon(null);

        // Mismo formato dd/MM/yyyy que al elegir el programa en el combo, y se cargan categorias e imagen
        form.cargarPrograma(d.programaCompleto());
        assertEquals(1, cb.getItemCount());
        assertEquals(d.programaCompleto(), cb.getSelectedItem());
        assertEquals(d.programaCompleto(), txtNombre.getText());
        assertEquals("01/12/2024", txtRegistro.getText());
        assertEquals("01/01/2025", txtInicio.getText());
        assertEquals("31/12/2025", txtFin.getText());
        assertEquals(2, tblCursos.getRowCount());
        assertEquals(d.categoria(), txtCategorias.getText());
        assertNotNull(lblImagen.getIcon());

        // Programa inexistente: el controlador lanza excepcion
        form.cargarPrograma("Programa inexistente");
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar el programa: No existe ningún programa con el nombre: Programa inexistente"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testProgramaYCursosSinListas() throws Exception {
        // Un IControlador que devuelve un programa sin categorias (null) y un curso sin fecha de registro
        DtPrograma programa = mock(DtPrograma.class);
        when(programa.getNombre()).thenReturn("ProgramaMock");
        when(programa.getDescripcion()).thenReturn("Descripcion mock");
        when(programa.getCategorias()).thenReturn(null);
        DtCurso curso = mock(DtCurso.class);
        when(curso.getNombre()).thenReturn("CursoSinListas");
        IControlador ic = mock(IControlador.class);
        when(ic.listarProgramas()).thenReturn(List.of(programa));
        when(ic.buscarPrograma("ProgramaMock")).thenReturn(programa);
        when(ic.listarCursosPorPrograma("ProgramaMock")).thenReturn(List.of(curso));
        try (MockedStatic<Fabrica> f = fabricaCon(ic)) {
            ConsultaPrograma form = new ConsultaPrograma();
            JTextArea txtCategorias = campo(form, "txtCategorias");
            JTextField txtNombre = campo(form, "txtNombre");
            JTable tblCursos = campo(form, "tblCursos");
            assertEquals("ProgramaMock", txtNombre.getText());
            assertEquals("Sin categorías", txtCategorias.getText());
            assertEquals(1, tblCursos.getRowCount());
            assertEquals("CursoSinListas", tblCursos.getValueAt(0, 0));
            assertNull(tblCursos.getValueAt(0, 5), "Sin fecha de registro la celda queda vacia");

            // buscarPrograma devuelve null: se informa que no se encontro
            form.cargarPrograma("Nulo");
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("No se encontró el programa: Nulo"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }

    @Test
    public void testErroresDelControlador() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarProgramas()).thenThrow(new RuntimeException("sin programas"));
        when(ic.listarCursosPorPrograma(anyString())).thenThrow(new RuntimeException("sin cursos"));
        when(ic.buscarPrograma(anyString())).thenThrow(new RuntimeException("sin conexion"));
        try (MockedStatic<Fabrica> f = fabricaCon(ic)) {
            ConsultaPrograma form = new ConsultaPrograma();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al obtener lista de Programas de Formacion: sin programas"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
            // El combo conserva los items de diseño, por eso se intentan cargar los cursos
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al obtener Cursos: sin cursos"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));

            JComboBox<String> cb = campo(form, "cbPrograma");
            cb.setSelectedIndex(1);
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al obtener los datos: sin conexion"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));

            form.cargarPrograma("Cualquiera");
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar el programa: sin conexion"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }
}
