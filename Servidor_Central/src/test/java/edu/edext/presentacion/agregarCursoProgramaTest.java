package edu.edext.presentacion;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
 * Tests del formulario agregarCursoPrograma contra la base H2 de los tests.
 * Los JOptionPane se reemplazan con Mockito para que no se abra ninguna ventana.
 */
public class agregarCursoProgramaTest {

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

    private static String unico(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static int fila(JTable tbl, String nombre) {
        for (int i = 0; i < tbl.getRowCount(); i++)
            if (nombre.equals(tbl.getValueAt(i, 0))) return i;
        return -1;
    }

    /** Instituto, categoria, un curso sin previas/categorias, otro con ambas y un programa vacio. */
    private record Datos(String instituto, String categoria, String cursoBase, String cursoCompleto, String programa) {}

    private static Datos crearDatos() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        String inst = unico("INST"), cat = unico("CAT"), base = unico("CURSO"), completo = unico("CURSO"), prog = unico("PROG");
        ic.crearInstituto(inst);
        ic.crearCategoria(cat);
        ic.altaCurso(new DtCurso(base, "Curso base", "2 semanas", 10, 3, "http://base", new Date(),
                new DtInstituto(inst), new HashSet<>(), new HashSet<>(), "usr.png"), inst);
        ic.altaCurso(new DtCurso(completo, "Curso completo", "1 meses", 20, 6, "http://completo", new Date(),
                new DtInstituto(inst), new HashSet<>(Set.of(base)), new HashSet<>(Set.of(cat)), "usr.png"), inst);
        assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(prog, "Programa de prueba", LocalDate.now(),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), new HashSet<String>(), (String) null)));
        return new Datos(inst, cat, base, completo, prog);
    }

    private static MockedStatic<Fabrica> fabricaCon(IControlador ic) {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        MockedStatic<Fabrica> f = mockStatic(Fabrica.class);
        f.when(Fabrica::getInstance).thenReturn(fabrica);
        return f;
    }

    @Test
    public void testCargaInicialDeProgramasYCursos() throws Exception {
        Datos d = crearDatos();
        agregarCursoPrograma form = new agregarCursoPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTable tbl = campo(form, "tbl");

        boolean hayPrograma = false;
        for (int i = 0; i < cb.getItemCount(); i++) hayPrograma |= d.programa().equals(cb.getItemAt(i));
        assertTrue(hayPrograma, "El programa creado aparece en el combo");

        int base = fila(tbl, d.cursoBase()), completo = fila(tbl, d.cursoCompleto());
        assertTrue(base >= 0 && completo >= 0, "Los cursos creados aparecen en la tabla");
        assertEquals("---", tbl.getValueAt(base, 7), "Sin previas se muestra ---");
        assertEquals("---", tbl.getValueAt(base, 8), "Sin categorias se muestra ---");
        assertEquals(d.cursoBase(), tbl.getValueAt(completo, 7));
        assertEquals(d.categoria(), tbl.getValueAt(completo, 8));
        assertEquals(20, tbl.getValueAt(completo, 3));
        assertEquals("http://completo", tbl.getValueAt(completo, 6));
        // "Registro" es una columna de texto: la fecha se muestra como dd/MM/yyyy
        Date registro = null;
        for (DtCurso c : Fabrica.getInstance().getIControlador().listarCursos())
            if (c.getNombre().equals(d.cursoCompleto())) registro = c.getFechaRegistro();
        assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(registro), tbl.getValueAt(completo, 5));

        // Modelo de la tabla: tipos de columna y no editable
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        assertEquals(String.class, model.getColumnClass(0));
        assertEquals(Integer.class, model.getColumnClass(3));
        assertFalse(model.isCellEditable(0, 0));

        // Cambiar de programa recarga la tabla y limpia el curso seleccionado
        JTextField txtCurso = campo(form, "txtCurso");
        txtCurso.setText("algo");
        cb.setSelectedItem(d.programa());
        assertEquals("", txtCurso.getText());
        assertTrue(fila(tbl, d.cursoCompleto()) >= 0);
    }

    @Test
    public void testClicEnTabla() throws Exception {
        agregarCursoPrograma form = new agregarCursoPrograma();
        JTable tbl = campo(form, "tbl");
        JTextField txtCurso = campo(form, "txtCurso");
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"CursoSeleccionado"});
        model.addRow(new Object[]{null});
        MouseEvent clic = new MouseEvent(tbl, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false);

        // Sin fila seleccionada: no cambia nada
        tbl.clearSelection();
        txtCurso.setText("previo");
        tbl.dispatchEvent(clic);
        assertEquals("previo", txtCurso.getText());

        // Fila con nombre nulo: no cambia nada
        tbl.setRowSelectionInterval(1, 1);
        tbl.dispatchEvent(clic);
        assertEquals("previo", txtCurso.getText());

        // Fila con nombre: lo copia al campo
        tbl.setRowSelectionInterval(0, 0);
        tbl.dispatchEvent(clic);
        assertEquals("CursoSeleccionado", txtCurso.getText());
    }

    @Test
    public void testAgregarValidaciones() throws Exception {
        agregarCursoPrograma form = new agregarCursoPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTextField txtCurso = campo(form, "txtCurso");
        JButton btnAgregar = campo(form, "btnAgregar");

        // Sin programas en el combo
        cb.removeAllItems();
        btnAgregar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("No hay programas de formación disponibles."), eq("Advertencia"), eq(JOptionPane.WARNING_MESSAGE)), times(1));

        // Con programas pero ninguno seleccionado
        cb.addItem("Programa X");
        cb.setSelectedIndex(-1);
        btnAgregar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("No hay programas de formación disponibles."), eq("Advertencia"), eq(JOptionPane.WARNING_MESSAGE)), times(2));

        // Programa seleccionado pero sin curso
        cb.setSelectedIndex(0);
        txtCurso.setText("   ");
        btnAgregar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Debe seleccionar un curso y un Programa de Formacion validos"), eq("Advertencia"), eq(JOptionPane.WARNING_MESSAGE)), times(1));

        // Curso elegido pero programa con nombre vacio
        cb.removeAllItems();
        cb.addItem("");
        txtCurso.setText("Curso X");
        btnAgregar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Debe seleccionar un curso y un Programa de Formacion validos"), eq("Advertencia"), eq(JOptionPane.WARNING_MESSAGE)), times(2));
    }

    @Test
    public void testAgregarCursoOkYRepetido() throws Exception {
        Datos d = crearDatos();
        agregarCursoPrograma form = new agregarCursoPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTextField txtCurso = campo(form, "txtCurso");
        JButton btnAgregar = campo(form, "btnAgregar");

        cb.setSelectedItem(d.programa());
        txtCurso.setText(d.cursoCompleto());
        btnAgregar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form),
                eq("Curso '" + d.cursoCompleto() + "' agregado con éxito al programa de formacion '" + d.programa() + "'."),
                eq("Éxito"), eq(JOptionPane.INFORMATION_MESSAGE)));
        IControlador ic = Fabrica.getInstance().getIControlador();
        assertTrue(ic.listarCursosPorPrograma(d.programa()).stream().anyMatch(c -> c.getNombre().equals(d.cursoCompleto())));

        // Agregar el mismo curso de nuevo: el controlador lo rechaza y se muestra el error
        btnAgregar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form),
                eq("Error al agregar curso a programa de formacion: El curso '" + d.cursoCompleto() + "' ya pertenece al programa '" + d.programa() + "'."),
                eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testClicEnEtiquetaPrograma() throws Exception {
        agregarCursoPrograma form = new agregarCursoPrograma();
        form.setSize(700, 400);
        JLabel lblPrograma = campo(form, "lblPrograma");
        // El clic no hace nada: ya no imprime las dimensiones por consola (depuracion)
        PrintStream original = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salida, true));
        try {
            lblPrograma.dispatchEvent(new MouseEvent(lblPrograma, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false));
        } finally {
            System.setOut(original);
        }
        assertEquals("", salida.toString());
        assertEquals(700, form.getWidth());
        dialogos.verifyNoInteractions();
    }

    @Test
    public void testCursosConListasNulas() throws Exception {
        // Un IControlador que devuelve cursos sin listas de previas/categorias (null)
        DtCurso curso = mock(DtCurso.class);
        when(curso.getNombre()).thenReturn("CursoSinListas");
        when(curso.getPrevias_toArray()).thenReturn(null);
        when(curso.getCategorias_toArray()).thenReturn(null);
        IControlador ic = mock(IControlador.class);
        when(ic.listarProgramas()).thenReturn(List.of());
        when(ic.listarCursos()).thenReturn(List.of(curso));
        try (MockedStatic<Fabrica> f = fabricaCon(ic)) {
            agregarCursoPrograma form = new agregarCursoPrograma();
            JTable tbl = campo(form, "tbl");
            JComboBox<String> cb = campo(form, "cbPrograma");
            assertEquals(0, cb.getItemCount());
            assertEquals(1, tbl.getRowCount());
            assertEquals("CursoSinListas", tbl.getValueAt(0, 0));
            assertEquals("---", tbl.getValueAt(0, 7));
            assertEquals("---", tbl.getValueAt(0, 8));
        }
    }

    @Test
    public void testErroresDelControlador() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarProgramas()).thenThrow(new RuntimeException("sin programas"));
        when(ic.listarCursos()).thenThrow(new RuntimeException("sin cursos"));
        doThrow(new RuntimeException("sin conexion")).when(ic).agregarProgramaCurso(anyString(), anyString());
        try (MockedStatic<Fabrica> f = fabricaCon(ic)) {
            agregarCursoPrograma form = new agregarCursoPrograma();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al obtener lista de Programas de Formacion: sin programas"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al obtener Cursos: sin cursos"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));

            // El combo conserva los items de diseño: al agregar, falla el controlador
            JTextField txtCurso = campo(form, "txtCurso");
            txtCurso.setText("Curso X");
            ((JButton) campo(form, "btnAgregar")).doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al agregar curso a programa de formacion: sin conexion"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }
}
