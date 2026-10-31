package edu.edext.presentacion;

import com.toedter.calendar.JDateChooser;
import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import edu.edext.tools.cfg;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario CrearProgramaForm contra la base H2 de los tests.
 * JOptionPane y el FileDialog de seleccion de imagen se reemplazan con Mockito
 * para que no se abra ninguna ventana.
 */
public class CrearProgramaFormTest {

    private static final String PREFIJO = CrearProgramaForm.class.getName();

    private MockedStatic<JOptionPane> dialogos;

    @BeforeAll
    public static void cargarDatos() throws Exception {
        Fabrica.getInstance().getIControlador().cargarDatosDePrueba();
    }

    @BeforeEach
    public void setUp() {
        dialogos = mockStatic(JOptionPane.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        procesarEventos();
        dialogos.close();
    }

    // ----------------------------------------------------------------- helpers

    @SuppressWarnings("unchecked")
    private static <T> T campo(Object form, String nombre) throws Exception {
        Field f = form.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return (T) f.get(form);
    }

    /** Espera a que el EDT procese los eventos encolados (p.ej. COMPONENT_SHOWN/HIDDEN de setVisible). */
    private static void procesarEventos() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }

    /** Ejecuta los FocusListener del formulario (sin los de Swing) como si el componente perdiera el foco. */
    private static void perderFoco(Component c) {
        FocusEvent evt = new FocusEvent(c, FocusEvent.FOCUS_LOST);
        for (FocusListener l : c.getFocusListeners())
            if (l.getClass().getName().startsWith(PREFIJO)) l.focusLost(evt);
    }

    /** Ejecuta los CaretListener del formulario (fuera del EDT Swing no mueve el caret al cambiar el texto). */
    private static void moverCaret(JTextComponent c) {
        for (CaretListener l : c.getCaretListeners())
            if (l.getClass().getName().startsWith(PREFIJO)) l.caretUpdate(null);
    }

    private static List<String> elementos(JList<String> lista) {
        List<String> ret = new ArrayList<>();
        ListModel<String> model = lista.getModel();
        for (int i = 0; i < model.getSize(); i++) ret.add(model.getElementAt(i));
        return ret;
    }

    private static String nombreUnico() {
        return "PROG-TEST-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static boolean existePrograma(String nombre) throws Exception {
        for (DtPrograma p : Fabrica.getInstance().getIControlador().listarProgramas())
            if (p.getNombre().equals(nombre)) return true;
        return false;
    }

    private static File crearImagenPng() throws Exception {
        File dir = new File("target/test-crearprograma").getAbsoluteFile();
        dir.mkdirs();
        File png = new File(dir, "img-" + UUID.randomUUID().toString().substring(0, 8) + ".png");
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), "png", png);
        return png;
    }

    private void confirmarCon(int respuesta) {
        dialogos.when(() -> JOptionPane.showOptionDialog(any(), any(), anyString(), anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(respuesta);
    }

    /** Elige una imagen con el boton "Agregar Imagen" (el FileDialog nativo se reemplaza con Mockito). */
    private static void elegirImagen(CrearProgramaForm form, File archivo) throws Exception {
        try (MockedConstruction<FileDialog> fd = mockConstruction(FileDialog.class, (m, ctx) -> {
            when(m.getDirectory()).thenReturn(archivo.getParent());
            when(m.getFile()).thenReturn(archivo.getName());
        })) {
            ((JButton) campo(form, "btnAgregarImagen")).doClick();
        }
        assertEquals(archivo, campo(form, "imagen"));
    }

    /** Pulsa "Crear" con Fabrica devolviendo el IControlador dado y espera los eventos pendientes. */
    private static void crearCon(CrearProgramaForm form, IControlador ic) throws Exception {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            ((JButton) campo(form, "jBCreate")).doClick();
        }
        procesarEventos();
    }

    private static CrearProgramaForm formEnVentana() {
        CrearProgramaForm form = new CrearProgramaForm();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        return form;
    }

    /** Completa nombre y fechas validas usando los campos de texto (camino del teclado). */
    private static void completarDatos(CrearProgramaForm form, String nombre) throws Exception {
        JTextField jTnombre = campo(form, "jTnombre");
        JFormattedTextField jFfechaInicio = campo(form, "jFfechaInicio");
        JFormattedTextField jFfechaFin = campo(form, "jFfechaFin");
        JFormattedTextField jFfechaPublic = campo(form, "jFfechaPublic");
        jTnombre.setText(nombre);
        perderFoco(jTnombre);
        jFfechaInicio.setText("01/02/2027");
        perderFoco(jFfechaInicio);
        jFfechaFin.setText("01/03/2027");
        perderFoco(jFfechaFin);
        jFfechaPublic.setText("15/12/2026");
        perderFoco(jFfechaPublic);
    }

    // ------------------------------------------------------------------- tests

    @Test
    public void testListaInicialConProgramasExistentes() throws Exception {
        CrearProgramaForm form = new CrearProgramaForm();
        JList<String> jLista = campo(form, "jLista");
        List<String> esperados = new ArrayList<>();
        for (DtPrograma p : Fabrica.getInstance().getIControlador().listarProgramas()) esperados.add(p.getNombre());
        assertFalse(esperados.isEmpty(), "Los datos de prueba incluyen programas");
        assertTrue(elementos(jLista).containsAll(esperados));
        // La lista es solo de lectura: no se puede seleccionar
        jLista.setSelectedIndex(0);
        assertEquals(-1, jLista.getSelectedIndex());
    }

    @Test
    public void testNuevoYCancelarSinCambios() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JInternalFrame ventana = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, form);
        JButton jBNew = campo(form, "jBNew");
        JButton jBCreate = campo(form, "jBCreate");
        JPanel jPNuevo = campo(form, "jPNuevo");
        assertFalse(jBCreate.isEnabled(), "'Crear' arranca deshabilitado");

        jBNew.doClick();
        assertFalse(jBNew.isEnabled());
        assertFalse(jBCreate.isEnabled(), "La primera vez, con todos los campos vacios, no se puede crear");
        assertEquals(670, ventana.getWidth(), "El formulario se agranda");

        // Aunque se intente, con el boton deshabilitado no se pide confirmacion ni se envian datos nulos
        jBCreate.doClick();
        dialogos.verifyNoInteractions();

        // Sin cambios cancela sin pedir confirmacion
        ((JButton) campo(form, "jBCancel")).doClick();
        procesarEventos();
        assertFalse(jPNuevo.isVisible());
        assertTrue(jBNew.isEnabled(), "Al ocultar el panel se habilita 'Nuevo Programa'");
        assertEquals(185, ventana.getWidth(), "El formulario se achica");
        dialogos.verifyNoInteractions();

        // Volver a abrir: el panel se muestra y se deshabilita el boton
        jBNew.doClick();
        procesarEventos();
        assertTrue(jPNuevo.isVisible());
        assertFalse(jBNew.isEnabled());
    }

    @Test
    public void testValidacionesYCreacionDePrograma() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JTextField jTnombre = campo(form, "jTnombre");
        JFormattedTextField jFfechaInicio = campo(form, "jFfechaInicio");
        JFormattedTextField jFfechaFin = campo(form, "jFfechaFin");
        JFormattedTextField jFfechaPublic = campo(form, "jFfechaPublic");
        JDateChooser jDfechaInicio = campo(form, "jDfechaInicio");
        JDateChooser jDfechaPublic = campo(form, "jDfechaPublic");
        JTextArea jTAdescripcion = campo(form, "jTAdescripcion");
        JButton jBCreate = campo(form, "jBCreate");
        JLabel jLErrorMsg = campo(form, "jLErrorMsg");
        JPanel jPNuevo = campo(form, "jPNuevo");
        JList<String> jLista = campo(form, "jLista");
        ((JButton) campo(form, "jBNew")).doClick();

        // Descripcion antes del nombre: no se puede crear (nombre nulo)
        jTAdescripcion.setText("Programa creado por los tests");
        perderFoco(jTAdescripcion);
        assertFalse(jBCreate.isEnabled());

        // Nombre vacio
        jTnombre.setText("");
        perderFoco(jTnombre);
        assertEquals("Nombre faltante", jLErrorMsg.getText());

        // Nombre de un programa que ya existe
        String existente = Fabrica.getInstance().getIControlador().listarProgramas().get(0).getNombre();
        jTnombre.setText(existente);
        perderFoco(jTnombre);
        assertEquals("Nombre Existente", jLErrorMsg.getText());

        // Nombre nuevo: se limpian los errores; falta la fecha de inicio
        String nombre = nombreUnico();
        jTnombre.setText(nombre);
        perderFoco(jTnombre);
        assertEquals("", jLErrorMsg.getText());
        assertFalse(jBCreate.isEnabled());

        // Fecha de inicio invalida y luego valida (se copia al calendario)
        jFfechaInicio.setText("99/99/2027");
        perderFoco(jFfechaInicio);
        assertEquals("Fecha de Inicio", jLErrorMsg.getText());
        jFfechaInicio.setText("10/03/2027");
        perderFoco(jFfechaInicio);
        assertEquals("", jLErrorMsg.getText());
        assertEquals(LocalDate.of(2027, 3, 10), Utils.Cast.valueOf(jDfechaInicio.getDate()));
        assertFalse(jBCreate.isEnabled(), "Falta la fecha de finalizacion");

        // Fecha de fin anterior a la de inicio: fechas invertidas
        jFfechaFin.setText("01/03/2027");
        perderFoco(jFfechaFin);
        assertEquals("Fechas Invertidas", jLErrorMsg.getText());
        assertFalse(jBCreate.isEnabled(), "Falta la fecha de publicacion");

        // Corregir el inicio desde el calendario: deja de estar invertido
        jDfechaInicio.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 2, 1)));
        assertEquals("01/02/2027", jFfechaInicio.getText());
        assertEquals("", jLErrorMsg.getText());

        // Fecha de publicacion desde el calendario: ya se puede crear
        jDfechaPublic.setDate(Utils.Cast.valueOf(LocalDate.of(2026, 12, 1)));
        assertEquals("01/12/2026", jFfechaPublic.getText());
        assertTrue(jBCreate.isEnabled());

        // Mientras se escribe una fecha invalida (caret) hay error y no se puede crear
        jFfechaPublic.setText("31/02/2026");
        moverCaret(jFfechaPublic);
        assertEquals("Fecha de Publicacion", jLErrorMsg.getText());
        assertFalse(jBCreate.isEnabled(), "Todos los datos cargados pero con errores pendientes");
        perderFoco(jFfechaPublic);
        assertEquals("Fecha de Publicacion", jLErrorMsg.getText());
        jFfechaPublic.setText("15/12/2026");
        moverCaret(jFfechaPublic);
        assertEquals("", jLErrorMsg.getText());
        perderFoco(jFfechaPublic);
        assertEquals(LocalDate.of(2026, 12, 15), Utils.Cast.valueOf(jDfechaPublic.getDate()));

        jFfechaInicio.setText("32/01/2027");
        moverCaret(jFfechaInicio);
        assertEquals("Fecha de Inicio", jLErrorMsg.getText());
        jFfechaInicio.setText("01/02/2027");
        moverCaret(jFfechaInicio);
        assertEquals("", jLErrorMsg.getText());

        jFfechaFin.setText("00/03/2027");
        moverCaret(jFfechaFin);
        assertEquals("Fecha de Finalizacion", jLErrorMsg.getText());
        perderFoco(jFfechaFin);
        jFfechaFin.setText("01/03/2027");
        moverCaret(jFfechaFin);
        assertEquals("", jLErrorMsg.getText());
        assertTrue(jBCreate.isEnabled());

        // El usuario no confirma: no se crea nada
        confirmarCon(JOptionPane.NO_OPTION);
        jBCreate.doClick();
        dialogos.verify(() -> JOptionPane.showOptionDialog(eq(form), eq("Confirme creación de Programa de Edición"),
                anyString(), eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.WARNING_MESSAGE), isNull(), isNull(), isNull()));
        assertTrue(jPNuevo.isVisible());
        assertFalse(existePrograma(nombre));

        // Confirma: se crea, se cierra el panel y aparece en la lista
        confirmarCon(JOptionPane.YES_OPTION);
        jBCreate.doClick();
        procesarEventos();
        assertTrue(existePrograma(nombre));
        assertFalse(jPNuevo.isVisible());
        assertTrue(elementos(jLista).contains(nombre));
        assertEquals("", jTnombre.getText(), "Al cerrar el panel se limpian los campos");
        assertFalse(jBCreate.isEnabled());
    }

    @Test
    public void testCrearSinDescripcionConImagen() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JLabel lblImagen = campo(form, "lblImagen");
        JButton btnAgregarImagen = campo(form, "btnAgregarImagen");
        JButton jBCreate = campo(form, "jBCreate");
        ((JButton) campo(form, "jBNew")).doClick();
        File png = crearImagenPng();

        // Dialogo cancelado: no hay imagen
        try (MockedConstruction<FileDialog> fd = mockConstruction(FileDialog.class)) {
            btnAgregarImagen.doClick();
            assertEquals(1, fd.constructed().size());
            verify(fd.constructed().get(0)).setVisible(true);
        }
        assertNull(lblImagen.getIcon());
        assertNull(campo(form, "imagen"));

        // Se elige una imagen
        try (MockedConstruction<FileDialog> fd = mockConstruction(FileDialog.class, (m, ctx) -> {
            when(m.getDirectory()).thenReturn(png.getParent());
            when(m.getFile()).thenReturn(png.getName());
        })) {
            btnAgregarImagen.doClick();
        }
        assertNotNull(lblImagen.getIcon());
        assertEquals(png, campo(form, "imagen"));

        String nombre = nombreUnico();
        completarDatos(form, nombre);
        assertTrue(jBCreate.isEnabled());

        confirmarCon(JOptionPane.YES_OPTION);
        jBCreate.doClick();
        procesarEventos();
        dialogos.verify(() -> JOptionPane.showOptionDialog(eq(form), eq("La descripción se encuentra vacía,  ¿Desea continuar?"),
                anyString(), anyInt(), anyInt(), any(), any(), any()));
        assertTrue(existePrograma(nombre));
        DtPrograma creado = Fabrica.getInstance().getIControlador().buscarPrograma(nombre);
        assertEquals("Programa." + nombre + ".png", creado.getImagen());
        assertTrue(new File("target/test-imagenes", "Programa." + nombre + ".png").exists(), "La imagen se copia a la carpeta de imagenes");
    }

    @Test
    public void testErrorDelControladorAlCrear() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JPanel jPNuevo = campo(form, "jPNuevo");
        JList<String> jLista = campo(form, "jLista");
        ((JButton) campo(form, "jBNew")).doClick();
        elegirImagen(form, crearImagenPng());
        String nombre = nombreUnico();
        completarDatos(form, nombre);
        confirmarCon(JOptionPane.YES_OPTION);

        IControlador ic = mock(IControlador.class);
        when(ic.setCrearProgramaFormacion(any(DtPrograma.class))).thenThrow(new RuntimeException("sin conexion"));
        crearCon(form, ic);
        // Se informa el error real (no "Nombre Existente")
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al crear el Programa de Formacion : sin conexion"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), eq("Nombre Existente")), never());
        assertTrue(jPNuevo.isVisible(), "Ante el error el panel queda abierto");
        assertFalse(elementos(jLista).contains(nombre));
        assertFalse(new File(cfg.pathImagen(), "Programa." + nombre + ".png").exists(), "Sin programa no se copia la imagen");
    }

    @Test
    public void testControladorRechazaLaCreacion() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JPanel jPNuevo = campo(form, "jPNuevo");
        JTextField jTnombre = campo(form, "jTnombre");
        JList<String> jLista = campo(form, "jLista");
        ((JButton) campo(form, "jBNew")).doClick();
        File png = crearImagenPng();
        elegirImagen(form, png);
        String nombre = nombreUnico();
        completarDatos(form, nombre);
        confirmarCon(JOptionPane.YES_OPTION);

        // setCrearProgramaFormacion devuelve false (p.ej. otro usuario creo el mismo nombre)
        IControlador ic = mock(IControlador.class);
        when(ic.setCrearProgramaFormacion(any(DtPrograma.class))).thenReturn(false);
        crearCon(form, ic);
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form),
                eq("No se pudo crear el Programa de Formacion '" + nombre + "'. Verifique que el nombre no exista y que las fechas sean correctas."),
                eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), eq("Nombre Existente")), never());
        // El formulario no se cierra y conserva los datos; la imagen no se copia (no queda un archivo huerfano)
        assertTrue(jPNuevo.isVisible());
        assertEquals(nombre, jTnombre.getText());
        assertEquals(nombre, campo(form, "nombre"));
        assertEquals(png, campo(form, "imagen"));
        assertTrue(((JButton) campo(form, "jBCreate")).isEnabled(), "Se puede reintentar");
        assertFalse(elementos(jLista).contains(nombre));
        assertFalse(new File(cfg.pathImagen(), "Programa." + nombre + ".png").exists());
    }

    @Test
    public void testImagenSinExtensionSeGuardaComoPng() throws Exception {
        CrearProgramaForm form = formEnVentana();
        ((JButton) campo(form, "jBNew")).doClick();
        File dir = new File("target/test-crearprograma.dir").getAbsoluteFile();
        dir.mkdirs();
        File sinExtension = new File(dir, "img-" + UUID.randomUUID().toString().substring(0, 8));
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), "png", sinExtension);
        elegirImagen(form, sinExtension);
        String nombre = nombreUnico();
        completarDatos(form, nombre);
        confirmarCon(JOptionPane.YES_OPTION);
        ((JButton) campo(form, "jBCreate")).doClick();
        procesarEventos();
        // El nombre guardado en la base y el archivo copiado coinciden
        assertEquals("Programa." + nombre + ".png", Fabrica.getInstance().getIControlador().buscarPrograma(nombre).getImagen());
        assertTrue(new File(cfg.pathImagen(), "Programa." + nombre + ".png").isFile());
    }

    @Test
    public void testFalloAlGuardarLaImagenNoEsErrorDeLaCreacion() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JPanel jPNuevo = campo(form, "jPNuevo");
        ((JButton) campo(form, "jBNew")).doClick();
        File png = crearImagenPng();
        String nombre = nombreUnico();
        // openSO devuelve la imagen y save(String, String) falla; el resto de Utils.Imagen es real
        try (MockedStatic<Utils.Imagen> imagenes = mockStatic(Utils.Imagen.class, inv -> {
                switch (inv.getMethod().getName()) {
                    case "openSO": return png;
                    case "save": throw new IllegalStateException("disco lleno");
                    default: return inv.callRealMethod();
                }
            })) {
            ((JButton) campo(form, "btnAgregarImagen")).doClick();
            completarDatos(form, nombre);
            confirmarCon(JOptionPane.YES_OPTION);
            ((JButton) campo(form, "jBCreate")).doClick();
        }
        procesarEventos();
        assertTrue(existePrograma(nombre));
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El programa se creo, pero no se pudo guardar la imagen : disco lleno"),
                eq("Advertencia"), eq(JOptionPane.WARNING_MESSAGE)));
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), eq("Error"), anyInt()), never());
        assertFalse(jPNuevo.isVisible(), "El programa se creo: el panel se cierra");
    }

    @Test
    public void testCerrarElPanelLimpiaImagenYCalendarios() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JButton jBNew = campo(form, "jBNew");
        JButton jBCancel = campo(form, "jBCancel");
        JPanel jPNuevo = campo(form, "jPNuevo");
        JLabel lblImagen = campo(form, "lblImagen");
        JLabel jLErrorMsg = campo(form, "jLErrorMsg");
        JDateChooser jDfechaInicio = campo(form, "jDfechaInicio");
        JDateChooser jDfechaFin = campo(form, "jDfechaFin");
        JDateChooser jDfechaPublic = campo(form, "jDfechaPublic");
        jBNew.doClick();

        // Solo se eligio una imagen: cancelar pide confirmacion porque se perderia
        elegirImagen(form, crearImagenPng());
        assertNotNull(lblImagen.getIcon());
        confirmarCon(JOptionPane.NO_OPTION);
        jBCancel.doClick();
        dialogos.verify(() -> JOptionPane.showOptionDialog(eq(form), eq("Si cancela perderá los datos no guardados"),
                anyString(), anyInt(), anyInt(), any(), any(), any()));
        assertTrue(jPNuevo.isVisible());

        // Fechas desde los calendarios; se cancela aceptando perder los datos
        jDfechaInicio.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 2, 1)));
        jDfechaFin.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 3, 1)));
        jDfechaPublic.setDate(Utils.Cast.valueOf(LocalDate.of(2026, 12, 1)));
        confirmarCon(JOptionPane.YES_OPTION);
        jBCancel.doClick();
        procesarEventos();
        assertFalse(jPNuevo.isVisible());
        assertNull(campo(form, "imagen"));
        assertNull(lblImagen.getIcon());
        assertNull(jDfechaInicio.getDate());
        assertNull(jDfechaFin.getDate());
        assertNull(jDfechaPublic.getDate());

        // Nuevo programa: un inicio posterior al fin del programa anterior no da "Fechas Invertidas"
        jBNew.doClick();
        procesarEventos();
        jDfechaInicio.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 6, 1)));
        assertEquals("", jLErrorMsg.getText());
        assertFalse(((JButton) campo(form, "jBCreate")).isEnabled());

        // Sin datos (la imagen y las fechas anteriores se limpiaron) cancelar no pide confirmacion
        jDfechaInicio.setDate(null);
        assertNull(campo(form, "fechaInicio"));
        jBCancel.doClick();
        procesarEventos();
        assertFalse(jPNuevo.isVisible());
        dialogos.verify(() -> JOptionPane.showOptionDialog(eq(form), eq("Si cancela perderá los datos no guardados"),
                anyString(), anyInt(), anyInt(), any(), any(), any()), times(2));
    }

    @Test
    public void testCancelarConCambios() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JPanel jPNuevo = campo(form, "jPNuevo");
        JPanel jPError = campo(form, "jPError");
        JButton jBCancel = campo(form, "jBCancel");
        JTextArea jTAdescripcion = campo(form, "jTAdescripcion");
        ((JButton) campo(form, "jBNew")).doClick();
        completarDatos(form, nombreUnico());
        jTAdescripcion.setText("algo");
        perderFoco(jTAdescripcion);

        // No acepta perder los datos: el panel sigue abierto y se muestra el panel de error
        confirmarCon(JOptionPane.NO_OPTION);
        jBCancel.doClick();
        dialogos.verify(() -> JOptionPane.showOptionDialog(eq(form), eq("Si cancela perderá los datos no guardados"),
                anyString(), anyInt(), anyInt(), any(), any(), any()));
        assertTrue(jPNuevo.isVisible());
        assertTrue(jPError.isVisible());

        // Acepta: se cierra el panel
        confirmarCon(JOptionPane.YES_OPTION);
        jBCancel.doClick();
        procesarEventos();
        assertFalse(jPNuevo.isVisible());
        assertNull(campo(form, "nombre"));
        assertNull(campo(form, "fechaInicio"));
    }

    @Test
    public void testEventosConPanelOculto() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JPanel jPNuevo = campo(form, "jPNuevo");
        JFormattedTextField jFfechaInicio = campo(form, "jFfechaInicio");
        JFormattedTextField jFfechaFin = campo(form, "jFfechaFin");
        JFormattedTextField jFfechaPublic = campo(form, "jFfechaPublic");
        JDateChooser jDfechaInicio = campo(form, "jDfechaInicio");
        JDateChooser jDfechaFin = campo(form, "jDfechaFin");
        JDateChooser jDfechaPublic = campo(form, "jDfechaPublic");

        // Panel visible y calendarios sin fecha: no cambia nada
        jDfechaInicio.firePropertyChange("prueba", false, true);
        jDfechaFin.firePropertyChange("prueba", false, true);
        jDfechaPublic.firePropertyChange("prueba", false, true);
        assertNull(campo(form, "fechaInicio"));
        assertNull(campo(form, "fechaFin"));
        assertNull(campo(form, "fechaRegistro"));
        assertEquals("--/--/----", jFfechaInicio.getText());

        jPNuevo.setVisible(false);
        procesarEventos();
        jFfechaInicio.setText("01/02/2027");
        jFfechaFin.setText("01/03/2027");
        jFfechaPublic.setText("01/01/2027");
        perderFoco(jFfechaInicio);
        perderFoco(jFfechaFin);
        perderFoco(jFfechaPublic);
        moverCaret(jFfechaInicio);
        moverCaret(jFfechaFin);
        moverCaret(jFfechaPublic);
        jDfechaInicio.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 2, 1)));
        jDfechaFin.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 3, 1)));
        jDfechaPublic.setDate(Utils.Cast.valueOf(LocalDate.of(2027, 1, 1)));

        // Con el panel oculto los eventos de fecha se ignoran
        assertNull(campo(form, "fechaInicio"));
        assertNull(campo(form, "fechaFin"));
        assertNull(campo(form, "fechaRegistro"));
    }

    @Test
    public void testBotonesDeCalendario() throws Exception {
        CrearProgramaForm form = new CrearProgramaForm();
        String[][] pares = {{"jBfechaInicio", "jDfechaInicio"}, {"jBfechaFin", "jDfechaFin"}, {"jBfechaPublic", "jDfechaPublic"}};
        for (String[] par : pares) {
            JDateChooser chooser = campo(form, par[1]);
            JButton calendario = chooser.getCalendarButton();
            // El popup real del JDateChooser necesita la ventana en pantalla: se reemplaza por un contador
            for (ActionListener l : calendario.getActionListeners()) calendario.removeActionListener(l);
            AtomicInteger clics = new AtomicInteger();
            calendario.addActionListener(e -> clics.incrementAndGet());
            ((JButton) campo(form, par[0])).doClick();
            assertEquals(1, clics.get(), par[0] + " abre el calendario de " + par[1]);
        }
    }

    @Test
    public void testArrastrarFormularioYLista() throws Exception {
        CrearProgramaForm form = formEnVentana();
        JInternalFrame ventana = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, form);
        JList<String> jLista = campo(form, "jLista");
        for (Component c : new Component[]{form, jLista}) {
            ventana.setLocation(-1000, -1000);
            c.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false));
            c.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
            assertNotEquals(-1000, ventana.getX(), "Arrastrar " + c.getClass().getSimpleName() + " mueve la ventana");
        }
    }
}
