package edu.edext.presentacion;

import com.toedter.calendar.JDateChooser;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario AltaUsuario en sus tres modos: alta (constructor sin
 * argumentos), modificar (1) y consulta (2). Los JOptionPane, el selector de
 * imagenes del SO se reemplazan con Mockito para que no se
 * abra ninguna ventana.
 */
public class AltaUsuarioTest {

    private static final File DIR = new File("target/test-alta-usuario");

    private MockedStatic<JOptionPane> dialogos;

    @BeforeEach
    public void setUp() {
        dialogos = mockStatic(JOptionPane.class);
    }

    @AfterEach
    public void tearDown() {
        dialogos.close();
    }

    // ************************************************************ helpers

    @SuppressWarnings("unchecked")
    private static <T> T campo(Object form, String nombre) throws Exception {
        Field f = form.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return (T) f.get(form);
    }

    private static void click(Object form, String boton) throws Exception {
        ((JButton) campo(form, boton)).doClick();
    }

    /** Espera a que el EDT ejecute lo encolado con invokeLater por el formulario. */
    private static void esperarEDT() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }

    private static AltaUsuario nuevo() throws Exception {
        AltaUsuario form = new AltaUsuario();
        esperarEDT();
        return form;
    }

    private static AltaUsuario nuevo(int modo) throws Exception {
        AltaUsuario form = new AltaUsuario(modo);
        esperarEDT();
        return form;
    }

    private static String unico(String prefijo) {
        return prefijo + UUID.randomUUID().toString().substring(0, 8);
    }

    private static IControlador ic() {
        return Fabrica.getInstance().getIControlador();
    }

    private static Date fecha(int anio, int mes, int dia) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(anio, mes - 1, dia);
        return c.getTime();
    }

    private static File imagen(String nombre) throws Exception {
        DIR.mkdirs();
        File f = new File(DIR, nombre);
        ImageIO.write(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB), "png", f);
        return f;
    }

    private static File archivoSinExtension() throws Exception {
        DIR.mkdirs();
        File f = new File(DIR, "imagensinextension");
        ImageIO.write(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB), "png", f);
        return f;
    }

    private static void completar(AltaUsuario form, String nick, String pass, String nombre, String apellido,
            String email, String nacimiento) throws Exception {
        ((JTextField) campo(form, "txtNickname")).setText(nick);
        ((JTextField) campo(form, "txtPassword")).setText(pass);
        ((JTextField) campo(form, "txtNombre")).setText(nombre);
        ((JTextField) campo(form, "txtApellido")).setText(apellido);
        ((JTextField) campo(form, "txtEmail")).setText(email);
        ((JFormattedTextField) campo(form, "txtNacimiento")).setText(nacimiento);
    }

    /** Verifica que se mostro una sola vez el mensaje y limpia las invocaciones registradas. */
    private void mensaje(Component form, String texto, int tipo) {
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains(texto), anyString(), eq(tipo)));
        dialogos.clearInvocations();
    }

    private void sinMensajes() {
        dialogos.verifyNoInteractions();
    }

    private static MockedStatic<Fabrica> fabricaCon(IControlador ic) {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        MockedStatic<Fabrica> f = mockStatic(Fabrica.class);
        f.when(Fabrica::getInstance).thenReturn(fabrica);
        return f;
    }

    /**
     * Mock de Utils.Imagen: openSO (FileDialog nativo) devuelve el archivo indicado y el resto es real.
     * No se usa img.when(Utils.Imagen::openSO): al stubbear se ejecuta el metodo real y abre el dialogo.
     */
    private static MockedStatic<Utils.Imagen> imagenElegida(AtomicReference<File> archivo) {
        return mockStatic(Utils.Imagen.class, inv -> "openSO".equals(inv.getMethod().getName())
                ? archivo.get() : inv.callRealMethod());
    }

    private static MockedStatic<Utils.Imagen> imagenElegida(File archivo) {
        return imagenElegida(new AtomicReference<>(archivo));
    }

    // ************************************************************ constructores

    @Test
    public void testConstructorAlta() throws Exception {
        AltaUsuario form = nuevo();
        assertFalse(((JPanel) campo(form, "jPInstituto")).isVisible());
        assertTrue(((JPanel) campo(form, "jPBotones")).isVisible());
        assertFalse(((JButton) campo(form, "btnGuardarCambios")).isVisible());
        assertFalse(((JButton) campo(form, "btnGuardarCambios1")).isVisible());
        assertFalse(((JButton) campo(form, "btnQuitarInstituto")).isEnabled());
        assertNotNull(campo(form, "imagenSeleccionada"));
        assertNotNull(((JLabel) campo(form, "lblImagen")).getIcon(), "Se carga la imagen generica en el EDT");
        assertTrue(((JTextField) campo(form, "txtNickname")).isEditable());
        sinMensajes();
    }

    @Test
    public void testConstructorModificar() throws Exception {
        AltaUsuario form = nuevo(1);
        assertFalse(((JTextField) campo(form, "txtNickname")).isEditable());
        assertFalse(((JTextField) campo(form, "txtPassword")).isEditable());
        assertFalse(((JTextField) campo(form, "txtEmail")).isEditable());
        assertTrue(((JTextField) campo(form, "txtNombre")).isEditable());
        assertFalse(((JButton) campo(form, "btnAceptar")).isVisible());
        assertFalse(((JCheckBox) campo(form, "chbDocente")).isVisible());
        assertEquals("Cancelar", ((JButton) campo(form, "btnCancelar")).getText());
        assertNull(campo(form, "imagenSeleccionada"));
    }

    @Test
    public void testConstructorConsulta() throws Exception {
        AltaUsuario form = nuevo(2);
        assertFalse(((JTextField) campo(form, "txtNombre")).isEditable());
        assertFalse(((JTextField) campo(form, "txtApellido")).isEditable());
        assertFalse(((JFormattedTextField) campo(form, "txtNacimiento")).isEditable());
        assertFalse(((JButton) campo(form, "jBNacimiento")).isVisible());
        assertFalse(((JDateChooser) campo(form, "dchFechaDeNacimiento")).isEnabled());
        assertFalse(((JButton) campo(form, "btnAgregarImagen")).isVisible());
        assertEquals("Volver", ((JButton) campo(form, "btnCancelar")).getText());
        assertEquals("Volver", ((JButton) campo(form, "btnCancelar1")).getText());
    }

    // ************************************************************ eventos simples

    @Test
    public void testCheckDocenteCambiaTamanio() throws Exception {
        AltaUsuario form = nuevo();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        JCheckBox chb = campo(form, "chbDocente");

        chb.doClick();
        assertTrue(chb.isSelected());
        assertTrue(((JPanel) campo(form, "jPInstituto")).isVisible());
        assertTrue(((JPanel) campo(form, "jPBotones1")).isVisible());
        assertFalse(((JPanel) campo(form, "jPBotones")).isVisible());
        assertEquals(445, ventana.getHeight(), "El formulario agranda la ventana que lo contiene");

        chb.doClick();
        assertFalse(chb.isSelected());
        assertFalse(((JPanel) campo(form, "jPInstituto")).isVisible());
        assertTrue(((JPanel) campo(form, "jPBotones")).isVisible());
        assertEquals(345, ventana.getHeight());
    }

    @Test
    public void testCancelarCierraLaVentana() throws Exception {
        AltaUsuario form = nuevo();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        click(form, "btnCancelar");
        assertTrue(ventana.isClosed());

        AltaUsuario form2 = nuevo();
        JInternalFrame ventana2 = new JInternalFrame();
        ventana2.add(form2);
        click(form2, "btnCancelar1");
        assertTrue(ventana2.isClosed());
    }

    @Test
    public void testCancelarSinVentanaNoFalla() throws Exception {
        // El panel no esta dentro de un JInternalFrame: cancelar no hace nada
        AltaUsuario form = nuevo();
        assertDoesNotThrow(() -> click(form, "btnCancelar"));
        AltaUsuario consulta = nuevo(2);
        assertDoesNotThrow(() -> click(consulta, "btnCancelar1"));
        sinMensajes();
    }

    @Test
    public void testArrastrarFormulario() throws Exception {
        AltaUsuario form = nuevo();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false));
        form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
        assertNotNull(ventana.getLocation());
    }

    @Test
    public void testBotonCalendarioAbreElSelector() throws Exception {
        AltaUsuario form = nuevo();
        JButton calendario = ((JDateChooser) campo(form, "dchFechaDeNacimiento")).getCalendarButton();
        // Se reemplaza la accion real (abre un popup) por una bandera
        for (ActionListener l : calendario.getActionListeners()) calendario.removeActionListener(l);
        AtomicBoolean abierto = new AtomicBoolean(false);
        calendario.addActionListener(e -> abierto.set(true));

        click(form, "jBNacimiento");
        assertTrue(abierto.get());
    }

    @Test
    public void testFechaDeNacimiento() throws Exception {
        AltaUsuario form = nuevo();
        JFormattedTextField txt = campo(form, "txtNacimiento");
        JDateChooser dch = campo(form, "dchFechaDeNacimiento");

        // Enter con fecha valida: se pasa al calendario
        txt.setText("10/10/2000");
        txt.postActionEvent();
        assertEquals(fecha(2000, 10, 10), dch.getDate());

        // Enter con el campo vacio: no cambia el calendario
        txt.setText("");
        txt.postActionEvent();
        assertEquals(fecha(2000, 10, 10), dch.getDate());

        // Perdida de foco con fecha valida y con campo vacio
        txt.setText("05/03/1999");
        perderFoco(txt);
        assertEquals(fecha(1999, 3, 5), dch.getDate());
        txt.setText("");
        perderFoco(txt);
        assertEquals(fecha(1999, 3, 5), dch.getDate());

        // Elegir en el calendario actualiza el campo de texto; borrar la fecha no lo cambia
        dch.setDate(fecha(1980, 1, 2));
        assertEquals("02/01/1980", txt.getText());
        dch.setDate(null);
        assertEquals("02/01/1980", txt.getText());
    }

    private static void perderFoco(Component c) {
        int llamados = 0;
        for (FocusListener l : c.getFocusListeners()) {
            if (l.getClass().getName().startsWith(AltaUsuario.class.getName() + "$")) {
                l.focusLost(new FocusEvent(c, FocusEvent.FOCUS_LOST));
                llamados++;
            }
        }
        assertEquals(1, llamados);
    }

    @Test
    public void testAgregarYQuitarInstitutos() throws Exception {
        AltaUsuario form = nuevo();
        JComboBox<String> cb = campo(form, "cbInstitutos");
        JList<String> lis = campo(form, "lisInstitutos");
        JButton quitar = campo(form, "btnQuitarInstituto");
        DefaultListModel<String> model = (DefaultListModel<String>) lis.getModel();

        // Sin institutos en el combo no se agrega nada
        cb.removeAllItems();
        click(form, "btnAgregarInstituto");
        assertEquals(0, model.getSize());

        cb.addItem("Instituto A");
        cb.addItem("Instituto B");
        cb.setSelectedItem("Instituto A");
        click(form, "btnAgregarInstituto");
        click(form, "btnAgregarInstituto"); // duplicado: no se repite
        cb.setSelectedItem("Instituto B");
        click(form, "btnAgregarInstituto");
        assertEquals(2, model.getSize());

        // Quitar sin seleccion no hace nada
        lis.clearSelection();
        assertFalse(quitar.isEnabled());
        quitar.setEnabled(true);
        quitar.doClick();
        assertEquals(2, model.getSize());

        // Seleccionar habilita el boton y quitar elimina el elemento
        lis.setSelectedIndex(0);
        assertTrue(quitar.isEnabled());
        quitar.doClick();
        assertEquals(1, model.getSize());
        assertEquals("Instituto B", model.getElementAt(0));
        assertFalse(quitar.isEnabled(), "Al quitar el seleccionado se deshabilita el boton");
    }

    // ************************************************************ imagenes

    @Test
    public void testAgregarImagenSeleccionadaYCancelada() throws Exception {
        AltaUsuario form = nuevo();
        JLabel lbl = campo(form, "lblImagen");
        File foto = imagen("foto-agregar.png");
        AtomicReference<File> elegida = new AtomicReference<>(null);
        try (MockedStatic<Utils.Imagen> img = imagenElegida(elegida)) {
            // Cancelado: no cambia la imagen seleccionada
            File antes = campo(form, "imagenSeleccionada");
            Icon iconoAntes = lbl.getIcon();
            click(form, "btnAgregarImagen");
            assertSame(antes, campo(form, "imagenSeleccionada"));
            assertSame(iconoAntes, lbl.getIcon());

            // Elegida: se guarda y se muestra
            elegida.set(foto);
            click(form, "btnAgregarImagen");
            img.verify(Utils.Imagen::openSO, times(2));
            assertEquals(foto, campo(form, "imagenSeleccionada"));
            assertNotSame(iconoAntes, lbl.getIcon());
        }
    }

    @Test
    public void testLimpiarCampos() throws Exception {
        AltaUsuario form = nuevo();
        JComboBox<String> cb = campo(form, "cbInstitutos");
        JLabel lbl = campo(form, "lblImagen");

        // Con institutos en el combo vuelve al primero
        cb.removeAllItems();
        cb.addItem("Uno");
        cb.addItem("Dos");
        cb.setSelectedIndex(1);
        completar(form, "nick", "pass", "Nombre", "Apellido", "a@b.com", "01/01/2000");
        ((JCheckBox) campo(form, "chbDocente")).doClick();
        assertTrue(((JPanel) campo(form, "jPInstituto")).isVisible());
        ((DefaultListModel<String>) ((JList<String>) campo(form, "lisInstitutos")).getModel()).addElement("Uno");
        form.limpiarCampos();
        assertEquals(0, cb.getSelectedIndex());
        assertEquals("", ((JTextField) campo(form, "txtNickname")).getText());
        assertFalse(((JCheckBox) campo(form, "chbDocente")).isSelected());
        assertEquals(0, ((JList<String>) campo(form, "lisInstitutos")).getModel().getSize());
        assertNotNull(campo(form, "imagenSeleccionada"));
        assertFalse(((JPanel) campo(form, "jPInstituto")).isVisible(), "Se ocultan los institutos");
        assertTrue(((JPanel) campo(form, "jPBotones")).isVisible());

        // Sin institutos y con la etiqueta de la imagen ya dimensionada (> 160)
        cb.removeAllItems();
        lbl.setSize(200, 180);
        form.limpiarCampos();
        assertEquals(-1, cb.getSelectedIndex());
        assertNotNull(lbl.getIcon());
    }

    // ************************************************************ cargarFormulario

    @Test
    public void testCargarFormularioDocenteEnConsulta() throws Exception {
        AltaUsuario form = nuevo(2);
        DtUsuario dt = new DtUsuario("docnick", "x", "doc@mail.com", "Ana", "Perez", "",
                fecha(1990, 5, 15), List.of("Inst 1", "Inst 2"), TipoUsuario.DOCENTE);
        form.cargarFormulario(dt);

        assertEquals("docnick", ((JTextField) campo(form, "txtNickname")).getText());
        assertEquals("********", ((JTextField) campo(form, "txtPassword")).getText());
        assertEquals("doc@mail.com", ((JTextField) campo(form, "txtEmail")).getText());
        assertEquals("Ana", ((JTextField) campo(form, "txtNombre")).getText());
        assertEquals("Perez", ((JTextField) campo(form, "txtApellido")).getText());
        assertEquals("15/05/1990", ((JFormattedTextField) campo(form, "txtNacimiento")).getText());
        assertTrue(((JCheckBox) campo(form, "chbDocente")).isVisible());
        assertTrue(((JCheckBox) campo(form, "chbDocente")).isSelected());
        assertFalse(((JComboBox<?>) campo(form, "cbInstitutos")).isVisible());
        assertFalse(((JButton) campo(form, "btnQuitarInstituto")).isVisible());
        assertFalse(((JButton) campo(form, "btnAgregarInstituto")).isVisible());
        assertEquals(2, ((JList<?>) campo(form, "lisInstitutos")).getModel().getSize());
        assertEquals("usr.png", ((File) campo(form, "imagenSeleccionada")).getName(), "Imagen vacia: usa la generica");
    }

    @Test
    public void testCargarFormularioDocenteSinInstitutosEnModificar() throws Exception {
        AltaUsuario form = nuevo(1);
        DtUsuario dt = new DtUsuario("docnick2", "x", "doc2@mail.com", "Ana", "Perez", null,
                fecha(1990, 5, 15), null, TipoUsuario.DOCENTE);
        form.cargarFormulario(dt);
        assertTrue(((JComboBox<?>) campo(form, "cbInstitutos")).isVisible());
        assertTrue(((JButton) campo(form, "btnAgregarInstituto")).isVisible());
        assertEquals(0, ((JList<?>) campo(form, "lisInstitutos")).getModel().getSize());
        assertTrue(((JPanel) campo(form, "jPInstituto")).isVisible());
        assertEquals("usr.png", ((File) campo(form, "imagenSeleccionada")).getName(), "Imagen nula: usa la generica");
    }

    @Test
    public void testCargarFormularioEstudianteConImagen() throws Exception {
        AltaUsuario form = nuevo(1);
        DtUsuario dt = new DtUsuario("estnick", "x", "est@mail.com", "Luis", "Gomez", "estnick.jpg",
                fecha(2001, 1, 31), null, TipoUsuario.ESTUDIANTE);
        form.cargarFormulario(dt);
        assertFalse(((JCheckBox) campo(form, "chbDocente")).isVisible());
        assertFalse(((JPanel) campo(form, "jPInstituto")).isVisible());
        assertEquals("estnick.jpg", ((File) campo(form, "imagenSeleccionada")).getName());
        assertNotNull(((JLabel) campo(form, "lblImagen")).getIcon());
    }

    // ************************************************************ alta (aceptar)

    @Test
    public void testValidacionesDelAlta() throws Exception {
        AltaUsuario form = nuevo();
        String ok = "15/05/1990";

        completar(form, "  ", "pass", "Nombre", "Apellido", "a@b.com", ok);
        click(form, "btnAceptar");
        mensaje(form, "Nickname vacio", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", " ", "Nombre", "Apellido", "a@b.com", ok);
        click(form, "btnAceptar1");
        mensaje(form, "Contraseña vacia", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "pass", "", "Apellido", "a@b.com", ok);
        click(form, "btnAceptar");
        mensaje(form, "Debe ingresar nombre", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "pass", "Nombre", "  ", "a@b.com", ok);
        click(form, "btnAceptar");
        mensaje(form, "Debe ingresar apellido", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "pass", "Nombre", "Apellido", "correo-invalido", ok);
        click(form, "btnAceptar");
        mensaje(form, "El email ingresado no es válido.", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "pass", "Nombre", "Apellido", "a@b.com", "");
        click(form, "btnAceptar");
        mensaje(form, "Debe seleccionar una fecha de nacimiento.", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "pass", "Nombre", "Apellido", "a@b.com", "01/01/2999");
        click(form, "btnAceptar");
        mensaje(form, "La fecha tiene que ser anterior a la fecha actual", JOptionPane.ERROR_MESSAGE);

        // Docente sin institutos en la lista
        completar(form, "nick", "pass", "Nombre", "Apellido", "a@b.com", ok);
        ((JCheckBox) campo(form, "chbDocente")).doClick();
        click(form, "btnAceptar1");
        mensaje(form, "Debe seleccionar al menos un instituto.", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    public void testAltaEstudianteYDuplicados() throws Exception {
        AltaUsuario form = nuevo();
        String nick = unico("est");
        String email = nick + "@test.com";

        completar(form, nick, "secreta", "juan", "perez", email, "15/05/1990");
        click(form, "btnAceptar");
        mensaje(form, "Usuario creado correctamente.", JOptionPane.INFORMATION_MESSAGE);
        assertTrue(ic().existeUsuario(nick));
        assertEquals("", ((JTextField) campo(form, "txtNickname")).getText(), "Se limpian los campos");
        assertTrue(new File("target/test-imagenes", nick + ".png").exists(), "Se copia la imagen generica");
        DtUsuario creado = ic().listarUsuarios().stream().filter(u -> nick.equals(u.getNickname())).findFirst().orElseThrow();
        assertEquals(TipoUsuario.ESTUDIANTE, creado.getTipoUsuario());
        assertEquals(nick + ".png", creado.getImagen());

        // Mismo nickname
        completar(form, nick, "secreta", "juan", "perez", unico("otro") + "@test.com", "15/05/1990");
        click(form, "btnAceptar");
        mensaje(form, "Ya existe un usuario con el nickname '" + nick + "'.", JOptionPane.ERROR_MESSAGE);

        // Mismo email
        completar(form, unico("est"), "secreta", "juan", "perez", email, "15/05/1990");
        click(form, "btnAceptar");
        mensaje(form, "Ya existe un usuario con el email '" + email + "'.", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    public void testAltaDocenteConImagenElegida() throws Exception {
        String instituto = unico("INST-");
        ic().crearInstituto(instituto);
        AltaUsuario form = nuevo();
        JComboBox<String> cb = campo(form, "cbInstitutos");
        cb.setSelectedItem(instituto);
        assertEquals(instituto, cb.getSelectedItem());
        ((JCheckBox) campo(form, "chbDocente")).doClick();
        click(form, "btnAgregarInstituto");

        String nick = unico("doc");
        File foto = imagen("foto-docente.png");
        try (MockedStatic<Utils.Imagen> img = imagenElegida(foto)) {
            click(form, "btnAgregarImagen");
        }
        completar(form, nick, "secreta", "Maria", "Lopez", nick + "@test.com", "20/12/1985");
        click(form, "btnAceptar1");
        mensaje(form, "Usuario creado correctamente.", JOptionPane.INFORMATION_MESSAGE);

        DtUsuario creado = ic().listarUsuarios().stream().filter(u -> nick.equals(u.getNickname())).findFirst().orElseThrow();
        assertEquals(TipoUsuario.DOCENTE, creado.getTipoUsuario());
        assertEquals(List.of(instituto), creado.getInstitutos());
        assertEquals(nick + ".png", creado.getImagen());
        assertTrue(new File("target/test-imagenes", nick + ".png").exists());
        assertEquals(0, cb.getSelectedIndex(), "limpiarCampos vuelve al primer instituto");
    }

    @Test
    public void testErroresDelControladorEnElAlta() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenThrow(new RuntimeException("sin conexion"));
        when(ic.existeUsuario(anyString())).thenThrow(new RuntimeException("fallo consulta")).thenReturn(false);
        doThrow(new RuntimeException("fallo al crear")).when(ic).crearUsuario(any(DtUsuario.class));

        String nick = unico("nickerr");
        try (MockedStatic<Fabrica> f = fabricaCon(ic)) {
            AltaUsuario form = nuevo();
            mensaje(form, "Error al cargar los institutos: sin conexion", JOptionPane.ERROR_MESSAGE);

            completar(form, nick, "secreta", "Juan", "Perez", nick + "@test.com", "15/05/1990");
            click(form, "btnAceptar");
            mensaje(form, "fallo consulta", JOptionPane.ERROR_MESSAGE);

            click(form, "btnAceptar");
            mensaje(form, "fallo al crear", JOptionPane.ERROR_MESSAGE);
            verify(ic).crearUsuario(any(DtUsuario.class));
            assertEquals(nick, ((JTextField) campo(form, "txtNickname")).getText(), "Si falla no se limpian los campos");
            assertFalse(new File("target/test-imagenes", nick + ".png").exists(), "Si falla el alta no se copia la imagen");
        }
    }

    // ************************************************************ modificar (guardarCambios)

    @Test
    public void testValidacionesDeGuardarCambios() throws Exception {
        AltaUsuario form = nuevo(1);
        String ok = "15/05/1990";

        completar(form, "nick", "", " ", "Apellido", "a@b.com", ok);
        click(form, "btnGuardarCambios");
        mensaje(form, "Debe ingresar nombre", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "", "Nombre", "", "a@b.com", ok);
        click(form, "btnGuardarCambios1");
        mensaje(form, "Debe ingresar apellido", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "", "Nombre", "Apellido", "a@b", ok);
        click(form, "btnGuardarCambios");
        mensaje(form, "El email ingresado no es válido.", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "", "Nombre", "Apellido", "a@b.com", "");
        click(form, "btnGuardarCambios");
        mensaje(form, "Debe seleccionar una fecha de nacimiento.", JOptionPane.ERROR_MESSAGE);

        completar(form, "nick", "", "Nombre", "Apellido", "a@b.com", "01/01/2999");
        click(form, "btnGuardarCambios");
        mensaje(form, "La fecha tiene que ser anterior a la fecha actual", JOptionPane.ERROR_MESSAGE);

        // Docente con la lista de institutos vacia
        completar(form, "nick", "", "Nombre", "Apellido", "a@b.com", ok);
        ((JCheckBox) campo(form, "chbDocente")).setSelected(true);
        click(form, "btnGuardarCambios");
        mensaje(form, "El docente debe tener al menos un instituto.", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    public void testGuardarCambiosEstudiante() throws Exception {
        String nick = unico("mod");
        String email = nick + "@test.com";
        ic().crearUsuario(new DtUsuario(nick, "secreta", email, "Pedro", "Rios", null,
                fecha(1995, 7, 1), null, TipoUsuario.ESTUDIANTE));

        // Sin imagen seleccionada (formulario recien creado): usa la imagen generica
        AltaUsuario form = nuevo(1);
        assertNull(campo(form, "imagenSeleccionada"));
        completar(form, nick, "", "Pedro", "Rios", email, "01/07/1995");
        click(form, "btnGuardarCambios");
        mensaje(form, "Usuario modificado correctamente.", JOptionPane.INFORMATION_MESSAGE);
        assertEquals("usr.png", buscar(nick).getImagen());

        // Con la imagen cargada por cargarFormulario: se guarda con el nickname
        form.cargarFormulario(buscar(nick));
        ((JTextField) campo(form, "txtNombre")).setText("PedroModificado");
        click(form, "btnGuardarCambios1");
        mensaje(form, "Usuario modificado correctamente.", JOptionPane.INFORMATION_MESSAGE);
        DtUsuario modificado = buscar(nick);
        assertEquals("PedroModificado", modificado.getNombre());
        assertEquals(nick + ".png", modificado.getImagen());

        // Usuario inexistente: el controlador lanza excepcion, se muestra y no se copia la imagen
        String inexistente = unico("nadie");
        ((JTextField) campo(form, "txtNickname")).setText(inexistente);
        click(form, "btnGuardarCambios");
        mensaje(form, "El usuario '" + inexistente + "' no existe.", JOptionPane.ERROR_MESSAGE);
        assertFalse(new File("target/test-imagenes", inexistente + ".png").exists(), "Si falla la modificacion no queda una imagen huerfana");
    }

    @Test
    public void testGuardarCambiosDocente() throws Exception {
        String inst1 = unico("INST-");
        String inst2 = unico("INST-");
        ic().crearInstituto(inst1);
        ic().crearInstituto(inst2);
        String nick = unico("dmod");
        ic().crearUsuario(new DtUsuario(nick, "secreta", nick + "@test.com", "Laura", "Diaz", null,
                fecha(1980, 2, 2), List.of(inst1), TipoUsuario.DOCENTE));

        AltaUsuario form = nuevo(1);
        form.cargarFormulario(buscar(nick));
        JComboBox<String> cb = campo(form, "cbInstitutos");
        cb.setSelectedItem(inst2);
        click(form, "btnAgregarInstituto");

        // Imagen elegida sin extension: se guarda como .png
        File sinExt = archivoSinExtension();
        try (MockedStatic<Utils.Imagen> img = imagenElegida(sinExt)) {
            click(form, "btnAgregarImagen");
            click(form, "btnGuardarCambios");
            img.verify(() -> Utils.Imagen.save(sinExt.getAbsolutePath(), nick + ".png"));
        }
        mensaje(form, "Usuario modificado correctamente.", JOptionPane.INFORMATION_MESSAGE);
        DtUsuario modificado = buscar(nick);
        assertEquals(nick + ".png", modificado.getImagen());
        assertTrue(new File("target/test-imagenes", nick + ".png").exists());
        assertEquals(2, modificado.getInstitutos().size());
        assertTrue(modificado.getInstitutos().containsAll(List.of(inst1, inst2)));
    }

    private static DtUsuario buscar(String nick) throws Exception {
        return ic().listarUsuarios().stream().filter(u -> nick.equals(u.getNickname())).findFirst().orElseThrow();
    }

    /** Una imagen elegida sin extension se guarda como .png, igual que al modificar. */
    @Test
    public void testAltaConImagenSinExtensionSeGuardaComoPng() throws Exception {
        AltaUsuario form = nuevo();
        String nick = unico("sinext");
        File sinExt = archivoSinExtension();
        try (MockedStatic<Utils.Imagen> img = imagenElegida(sinExt)) {
            click(form, "btnAgregarImagen");
        }
        completar(form, nick, "secreta", "Juan", "Perez", nick + "@test.com", "15/05/1990");
        click(form, "btnAceptar");

        mensaje(form, "Usuario creado correctamente.", JOptionPane.INFORMATION_MESSAGE);
        assertEquals(nick + ".png", buscar(nick).getImagen());
        assertTrue(new File("target/test-imagenes", nick + ".png").exists(), "La imagen se copia con la extension .png");
    }
}
