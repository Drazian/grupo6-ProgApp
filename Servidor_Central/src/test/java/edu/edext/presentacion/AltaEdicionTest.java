package edu.edext.presentacion;

import com.toedter.calendar.JDateChooser;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import edu.edext.tools.cfg;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario AltaEdicion contra la base H2 de los tests. Los dialogos
 * (JOptionPane y el FileDialog de Utils.Imagen.openSO) se reemplazan con Mockito
 * y el calendario emergente del JDateChooser no se abre.
 */
public class AltaEdicionTest {

    private static final String ID = UUID.randomUUID().toString().substring(0, 8);
    private static final String INSTITUTO = "INST-AE-" + ID;
    private static final String CURSO = "CUR-AE-" + ID;
    private static File imagenPrueba;

    private MockedStatic<JOptionPane> dialogos;

    @BeforeAll
    public static void cargarDatos() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        ic.cargarDatosDePrueba();
        ic.crearInstituto(INSTITUTO);
        ic.altaCurso(new DtCurso(CURSO, "Curso de prueba", "1 mes", 10, 2, "http://edext.test",
                new Date(), new DtInstituto(INSTITUTO), null), INSTITUTO);
        File carpeta = new File("target/test-imagenes-origen");
        carpeta.mkdirs();
        imagenPrueba = new File(carpeta, "edicion-" + ID + ".png");
        ImageIO.write(new BufferedImage(40, 20, BufferedImage.TYPE_INT_RGB), "png", imagenPrueba);
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

    private static String nombreUnico() {
        return "ED-AE-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static Fabrica fabricaCon(IControlador ic) {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        return fabrica;
    }

    /** Mock de Utils.Imagen: openSO (FileDialog nativo) devuelve el archivo indicado, el resto es real. */
    private static MockedStatic<Utils.Imagen> imagenSeleccionada(AtomicReference<File> archivo) {
        return mockStatic(Utils.Imagen.class, inv -> "openSO".equals(inv.getMethod().getName())
                ? archivo.get() : inv.callRealMethod());
    }

    /** Ejecuta solo los FocusListener propios del formulario (sin depender del foco real). */
    private static void perderFoco(JFormattedTextField campo) {
        FocusEvent evento = new FocusEvent(campo, FocusEvent.FOCUS_LOST);
        for (FocusListener l : campo.getFocusListeners())
            if (l.getClass().getName().startsWith(AltaEdicion.class.getName() + "$")) l.focusLost(evento);
    }

    /** Completa el formulario con datos validos (cupo vacio). */
    private static void completar(AltaEdicion form, String nombre) throws Exception {
        @SuppressWarnings("unchecked")
        JComboBox<String> instituto = campo(form, "seleccionInstituto");
        @SuppressWarnings("unchecked")
        JComboBox<String> curso = campo(form, "seleccionCurso");
        instituto.setSelectedItem(INSTITUTO);
        curso.setSelectedItem(CURSO);
        ((JTextField) campo(form, "textoNombreEdicion")).setText(nombre);
        ((JFormattedTextField) campo(form, "textoFechaInicio")).setText("01/12/2026");
        ((JFormattedTextField) campo(form, "textoFechaFin")).setText("01/03/2027");
        ((JList<?>) campo(form, "listaDocentes")).setSelectedIndex(0);
    }

    private void verificarMensaje(AltaEdicion form, String mensaje, String titulo, int tipo) {
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq(mensaje), eq(titulo), eq(tipo)));
    }

    @Test
    public void testCargaInicial() throws Exception {
        AltaEdicion form = new AltaEdicion();
        IControlador ic = Fabrica.getInstance().getIControlador();
        JComboBox<?> instituto = campo(form, "seleccionInstituto");
        JList<?> docentes = campo(form, "listaDocentes");
        assertEquals("Alta de Edición de Curso", form.getTitle());
        assertEquals("Seleccione un Instituto...", instituto.getItemAt(0));
        assertEquals(ic.listarInstitutos().size() + 1, instituto.getItemCount());
        assertEquals(ic.listarDocentes().size(), docentes.getModel().getSize());
        assertTrue(docentes.getModel().getSize() > 0);
    }

    @Test
    public void testErrorAlCargarDatos() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = fabricaCon(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            AltaEdicion form = new AltaEdicion();
            verificarMensaje(form, "Error al cargar datos: sin conexion", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Test
    public void testSeleccionarInstituto() throws Exception {
        AltaEdicion form = new AltaEdicion();
        @SuppressWarnings("unchecked")
        JComboBox<String> instituto = campo(form, "seleccionInstituto");
        JComboBox<?> curso = campo(form, "seleccionCurso");
        instituto.setSelectedItem(INSTITUTO);
        assertEquals(2, curso.getItemCount());
        assertEquals("Seleccione un Curso...", curso.getItemAt(0));
        assertEquals(CURSO, curso.getItemAt(1));
        instituto.setSelectedIndex(0);
        assertEquals(0, curso.getItemCount());
    }

    @Test
    public void testErrorAlListarCursos() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenReturn(List.of(new DtInstituto("INST-MOCK")));
        when(ic.listarDocentes()).thenReturn(List.of("docente-mock"));
        when(ic.listarCursosPorInstituto("INST-MOCK")).thenThrow(new RuntimeException("sin cursos"));
        Fabrica fabrica = fabricaCon(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            AltaEdicion form = new AltaEdicion();
            ((JComboBox<?>) campo(form, "seleccionInstituto")).setSelectedIndex(1);
            verificarMensaje(form, "sin cursos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Test
    public void testValidaciones() throws Exception {
        AltaEdicion form = new AltaEdicion();
        JButton alta = campo(form, "buttonDarAlta");
        @SuppressWarnings("unchecked")
        JComboBox<String> instituto = campo(form, "seleccionInstituto");
        @SuppressWarnings("unchecked")
        JComboBox<String> curso = campo(form, "seleccionCurso");
        JTextField nombre = campo(form, "textoNombreEdicion");
        JFormattedTextField inicio = campo(form, "textoFechaInicio");
        JFormattedTextField fin = campo(form, "textoFechaFin");
        JTextField cupos = campo(form, "textoCupos");

        alta.doClick();
        verificarMensaje(form, "Debe seleccionar un curso.", "Error", JOptionPane.ERROR_MESSAGE);

        instituto.setSelectedItem(INSTITUTO);
        curso.setSelectedItem(CURSO);
        nombre.setText("   ");
        alta.doClick();
        verificarMensaje(form, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);

        nombre.setText(nombreUnico());
        alta.doClick();
        verificarMensaje(form, "Las fechas deben tener el formato dd/MM/yyyy", "Error de Formato", JOptionPane.ERROR_MESSAGE);

        inicio.setText("10/03/2027");
        fin.setText("01/03/2027");
        alta.doClick();
        verificarMensaje(form, "La fecha de inicio no puede ser posterior a la fecha de fin.", "Error", JOptionPane.ERROR_MESSAGE);

        inicio.setText("01/12/2026");
        // El filtro de soloNumeros descarta el "-": el negativo solo puede llegar sin el filtro del campo
        cupos.setText("-5");
        assertEquals("5", cupos.getText());
        cupos.setDocument(new javax.swing.text.PlainDocument());
        cupos.setText("-5");
        alta.doClick();
        verificarMensaje(form, "El cupo no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);

        cupos.setText("10");
        alta.doClick();
        verificarMensaje(form, "Debe seleccionar al menos un docente.", "Error", JOptionPane.ERROR_MESSAGE);
        assertFalse(form.isClosed());
    }

    @Test
    public void testCupoNoNumerico() throws Exception {
        // soloNumeros filtra tambien lo pegado: solo quedan los digitos
        JTextField filtrado = campo(new AltaEdicion(), "textoCupos");
        filtrado.setText("12abc");
        assertEquals("12", filtrado.getText());
        filtrado.setText("1.5");
        assertEquals("15", filtrado.getText());
        filtrado.setText("abc");
        assertEquals("", filtrado.getText());

        // Un numero fuera de rango pasa el filtro y se rechaza al dar de alta
        IControlador ic = Fabrica.getInstance().getIControlador();
        for (String valor : new String[]{"99999999999"}) {
            String nombre = nombreUnico();
            AltaEdicion form = new AltaEdicion();
            completar(form, nombre);
            ((JTextField) campo(form, "textoCupos")).setText(valor);
            ((JButton) campo(form, "buttonDarAlta")).doClick();
            verificarMensaje(form, "El cupo debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            assertFalse(form.isClosed(), "Cupo '" + valor + "': el formulario sigue abierto");
            assertNull(ic.getEdicion(nombre), "Cupo '" + valor + "': no se crea la edicion");
        }
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), contains("For input string"), anyString(), anyInt()), never());
    }

    @Test
    public void testAltaSinImagenYDuplicada() throws Exception {
        String nombre = nombreUnico();
        AltaEdicion form = new AltaEdicion();
        completar(form, nombre);
        ((JButton) campo(form, "buttonDarAlta")).doClick();
        verificarMensaje(form, "Edición registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        assertTrue(form.isClosed(), "El formulario se cierra despues del alta");

        DtEdicion creada = Fabrica.getInstance().getIControlador().getEdicion(nombre);
        assertNotNull(creada);
        assertNull(creada.getCupo());
        assertEquals(cfg.IMAGE_USER, creada.getImagen());
        assertEquals(LocalDate.of(2026, 12, 1), creada.getFechaInicio());
        assertEquals(LocalDate.of(2027, 3, 1), creada.getFechaFin());
        assertEquals(CURSO, creada.getCurso().getNombre());

        // Mismo nombre: el controlador rechaza el alta
        AltaEdicion otro = new AltaEdicion();
        completar(otro, nombre);
        ((JButton) campo(otro, "buttonDarAlta")).doClick();
        verificarMensaje(otro, "Ya existe una Edición de Curso con el nombre: " + nombre, "Error", JOptionPane.ERROR_MESSAGE);
        assertFalse(otro.isClosed());
    }

    @Test
    public void testAltaConImagenYCupo() throws Exception {
        String nombre = nombreUnico();
        AltaEdicion form = new AltaEdicion();
        JLabel lblImagen = campo(form, "lblImagen");
        JButton agregarImagen = campo(form, "btnAgregarImagen");
        AtomicReference<File> archivo = new AtomicReference<>();
        try (MockedStatic<Utils.Imagen> img = imagenSeleccionada(archivo)) {
            // Seleccion cancelada: no cambia nada
            agregarImagen.doClick();
            assertNull(campo(form, "imagen"));
            assertNull(lblImagen.getIcon());

            archivo.set(imagenPrueba);
            agregarImagen.doClick();
            assertEquals(imagenPrueba, campo(form, "imagen"));
            assertNotNull(lblImagen.getIcon());
            img.verify(Utils.Imagen::openSO, times(2));
        }

        completar(form, nombre);
        ((JTextField) campo(form, "textoCupos")).setText(" 20 ");
        ((JButton) campo(form, "buttonDarAlta")).doClick();
        verificarMensaje(form, "Edición registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        DtEdicion creada = Fabrica.getInstance().getIControlador().getEdicion(nombre);
        assertEquals(20, creada.getCupo());
        assertEquals("Edicion." + nombre + ".png", creada.getImagen());
        assertTrue(new File(cfg.fullpathImagen() + "Edicion." + nombre + ".png").isFile(),
                "La imagen se copia a la carpeta de imagenes de los tests");
    }

    @Test
    public void testCancelar() throws Exception {
        AltaEdicion form = new AltaEdicion();
        @SuppressWarnings("unchecked")
        JComboBox<String> curso = campo(form, "seleccionCurso");
        curso.addItem("algo");
        ((JButton) campo(form, "buttonCancelar")).doClick();
        assertTrue(form.isClosed());
    }

    @Test
    public void testFechas() throws Exception {
        AltaEdicion form = new AltaEdicion();
        JFormattedTextField inicio = campo(form, "textoFechaInicio");
        JFormattedTextField fin = campo(form, "textoFechaFin");
        JDateChooser calInicio = campo(form, "jDfechaInicio");
        JDateChooser calFin = campo(form, "jDfechaFin");

        // Campos vacios: no se toca el calendario
        perderFoco(inicio);
        inicio.postActionEvent();
        perderFoco(fin);
        fin.postActionEvent();
        assertNull(calInicio.getDate());
        assertNull(calFin.getDate());

        // Fecha escrita + perder foco
        inicio.setText("15/11/2026");
        perderFoco(inicio);
        assertEquals(Utils.Cast.valudf("15/11/2026"), calInicio.getDate());
        fin.setText("20/12/2026");
        perderFoco(fin);
        assertEquals(Utils.Cast.valudf("20/12/2026"), calFin.getDate());

        // Fecha escrita + Enter
        inicio.setText("16/11/2026");
        inicio.postActionEvent();
        assertEquals(Utils.Cast.valudf("16/11/2026"), calInicio.getDate());
        fin.setText("21/12/2026");
        fin.postActionEvent();
        assertEquals(Utils.Cast.valudf("21/12/2026"), calFin.getDate());

        // Fecha elegida en el calendario: se copia al campo de texto
        calInicio.setDate(Utils.Cast.valudf("05/11/2026"));
        assertEquals("05/11/2026", inicio.getText());
        calFin.setDate(Utils.Cast.valudf("06/12/2026"));
        assertEquals("06/12/2026", fin.getText());
        // Calendario sin fecha: el texto queda como estaba
        calInicio.setDate(null);
        assertEquals("05/11/2026", inicio.getText());
        calFin.setDate(null);
        assertEquals("06/12/2026", fin.getText());
    }

    @Test
    public void testBotonesDeCalendario() throws Exception {
        AltaEdicion form = new AltaEdicion();
        List<String> clics = new ArrayList<>();
        for (String[] par : new String[][]{{"jDfechaInicio", "inicio"}, {"jDfechaFin", "fin"}}) {
            JButton calendario = ((JDateChooser) campo(form, par[0])).getCalendarButton();
            // El JDateChooser abre un popup con el calendario: se quita para que no aparezca ninguna ventana
            for (ActionListener l : calendario.getActionListeners())
                if (l instanceof JDateChooser) calendario.removeActionListener(l);
            calendario.addActionListener(e -> clics.add(par[1]));
        }
        ((JButton) campo(form, "jBfechaInicio")).doClick();
        ((JButton) campo(form, "jBfechaFin")).doClick();
        assertEquals(List.of("inicio", "fin"), clics);
    }
}
