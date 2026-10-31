package edu.edext.tools;

import com.sun.net.httpserver.HttpServer;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.tinylog.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de Utils y sus clases internas (GUI, Validate, Fecha, Cast, Crypt, Imagen, Disk, OS, Mouse).
 *
 * Esta clase corre en su propia JVM: antes de cargar cfg se apunta user.home a
 * target/utils-test/home y la carpeta de imagenes a target/utils-test, asi ningun
 * archivo se escribe fuera de target. Los dialogos (JFileChooser, FileDialog), los
 * procesos externos (powershell/zenity) y el puntero del mouse se simulan con Mockito:
 * nunca se abre una ventana.
 */
public class UtilsTest {

    private static File base;
    private static String homeOriginal;

    @BeforeAll
    public static void preparar() throws IOException {
        base = new File(cfgTest.target(), "utils-test");
        cfgTest.borrar(base);
        assertTrue(base.mkdirs());
        homeOriginal = System.getProperty("user.home");
        System.setProperty("user.home", new File(base, "home").getAbsolutePath());
        nuevaCarpetaImagenes();
    }

    @AfterAll
    public static void restaurar() {
        System.setProperty("user.home", homeOriginal);
    }

    /** Apunta cfg.pathImagen() a una carpeta nueva (todavia sin crear) dentro de target/utils-test. */
    private static File nuevaCarpetaImagenes() {
        File carpeta = new File(base, "img-" + UUID.randomUUID());
        cfg.propiedades().setProperty(cfg.IMAGEN_RUTA, carpeta.getAbsolutePath());
        return carpeta;
    }

    private static File archivo(String nombre, String contenido) throws IOException {
        File f = new File(base, "src-" + UUID.randomUUID() + File.separator + nombre);
        Files.createDirectories(f.getParentFile().toPath());
        Files.writeString(f.toPath(), contenido, StandardCharsets.UTF_8);
        return f;
    }

    /** Ejecuta con otro os.name (lo usa Utils.Disk) y lo restaura. */
    private static <T> T conSO(String nombre, Callable<T> accion) throws Exception {
        String original = System.getProperty("os.name");
        System.setProperty("os.name", nombre);
        try {
            return accion.call();
        } finally {
            System.setProperty("os.name", original);
        }
    }

    private static Object invocarPrivado(Class<?> clase, String metodo, Class<?>[] tipos, Object... args) throws Exception {
        Method m = clase.getDeclaredMethod(metodo, tipos);
        m.setAccessible(true);
        return m.invoke(null, args);
    }

    // ************************************************************************
    //  Utils, records y constructores de las clases internas
    // ************************************************************************

    @Test
    public void testConstantesRecordsYConstructores() {
        assertNull(Utils.ArrayNull());
        assertEquals(0, Utils.PATHFILE);
        assertEquals(1, Utils.NAMEFILE);
        assertEquals(2, Utils.EXTFILE);
        assertEquals(0, Utils.SAVEINTERNAL);
        assertEquals(1, Utils.SAVEEXTERNAL);

        Utils.CfgDB c = new Utils.CfgDB("clave", "valor");
        assertEquals("clave", c.clave());
        assertEquals("valor", c.valor());
        assertEquals(new Utils.CfgDB("clave", "valor"), c);
        Utils.dbData d = new Utils.dbData("k", "v");
        assertEquals("k", d.clave());
        assertEquals("v", d.valor());

        Utils u = new Utils();
        assertNotNull(u.new GUI());
        assertNotNull(u.new Validate());
        assertNotNull(u.new Fecha());
        assertNotNull(u.new Cast());
        assertNotNull(u.new Crypt());
        assertNotNull(u.new Imagen());
        assertNotNull(u.new Disk());
        assertNotNull(u.new OS());
        assertNotNull(new Utils.Mouse());
    }

    // ************************************************************************
    //  GUI
    // ************************************************************************

    private static JTable tabla() {
        return new JTable(new DefaultTableModel(new Object[]{"A", "B"}, 0));
    }

    @Test
    public void testGuiColumnas() {
        JTable t = tabla();
        Utils.GUI.sizeColumn(t, 0, 111);
        assertEquals(111, t.getColumnModel().getColumn(0).getPreferredWidth());

        Utils.GUI.sizeColumn(t, 1, 77, Utils.ALIGN_RIGHT);
        assertEquals(77, t.getColumnModel().getColumn(1).getPreferredWidth());
        assertEquals(Utils.ALIGN_RIGHT, ((DefaultTableCellRenderer) t.getColumnModel().getColumn(1).getCellRenderer()).getHorizontalAlignment());

        Utils.GUI.alignment(t, 0, Utils.ALIGN_CENTER);
        assertEquals(Utils.ALIGN_CENTER, ((DefaultTableCellRenderer) t.getColumnModel().getColumn(0).getCellRenderer()).getHorizontalAlignment());

        // Con tabla nula no hacen nada
        assertDoesNotThrow(() -> {
            Utils.GUI.sizeColumn(null, 0, 1);
            Utils.GUI.sizeColumn(null, 0, 1, Utils.ALIGN_LEFT);
            Utils.GUI.alignment(null, 0, Utils.ALIGN_LEFT);
        });
    }

    @Test
    public void testGuiModeloYFilas() {
        JTable t = tabla();
        assertSame(t.getModel(), Utils.GUI.getModel(t));
        assertNull(Utils.GUI.getModel(null));

        Utils.GUI.addRow(t, new Object[]{"a", "b"});
        Utils.GUI.addRow(t, new Object[]{"c", "d"});
        assertEquals(2, t.getRowCount());
        assertEquals("c", t.getValueAt(1, 0));

        DefaultTableModel m = Utils.GUI.clear(t);
        assertSame(t.getModel(), m);
        assertEquals(0, t.getRowCount());

        assertDoesNotThrow(() -> Utils.GUI.addRow(null, new Object[]{"x"}));
        assertNull(Utils.GUI.clear(null));

        // Una tabla cuyo modelo es nulo
        JTable sinModelo = mock(JTable.class);
        when(sinModelo.getModel()).thenReturn(null);
        Utils.GUI.addRow(sinModelo, new Object[]{"x"});
        verify(sinModelo, never()).setModel(any());
        assertNull(Utils.GUI.clear(sinModelo));
    }

    @Test
    public void testGuiDeshabilitarListaYCheck() {
        JList<String> lista = new JList<>(new String[]{"a", "b", "c"});
        Utils.GUI.setDisable(lista);
        lista.setSelectedIndex(1);
        lista.addSelectionInterval(0, 2);
        assertTrue(lista.isSelectionEmpty());

        JCheckBox check = new JCheckBox();
        Utils.GUI.setDisable(check);
        check.getModel().setPressed(true);
        assertFalse(check.getModel().isPressed());
    }

    private static boolean consumido(Component c, char tecla) {
        KeyListener[] ls = c.getKeyListeners();
        KeyEvent e = new KeyEvent(c, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, tecla);
        ls[ls.length - 1].keyTyped(e);
        return e.isConsumed();
    }

    @Test
    public void testGuiSoloNumeros() {
        JLabel sinEspacios = new JLabel();
        Utils.GUI.soloNumeros(sinEspacios);
        assertFalse(consumido(sinEspacios, '7'));
        assertTrue(consumido(sinEspacios, ' '));
        assertTrue(consumido(sinEspacios, 'a'));

        JLabel conEspacios = new JLabel();
        Utils.GUI.soloNumeros(conEspacios, true);
        assertFalse(consumido(conEspacios, '7'));
        assertFalse(consumido(conEspacios, ' '));
        assertTrue(consumido(conEspacios, 'a'));
    }

    @Test
    public void testGuiSoloLetras() {
        JLabel sinEspacios = new JLabel();
        Utils.GUI.soloLetras(sinEspacios);
        assertFalse(consumido(sinEspacios, 'a'));
        assertTrue(consumido(sinEspacios, ' '));
        assertTrue(consumido(sinEspacios, '7'));

        JLabel conEspacios = new JLabel();
        Utils.GUI.soloLetras(conEspacios, true);
        assertFalse(consumido(conEspacios, 'a'));
        assertFalse(consumido(conEspacios, ' '));
        assertTrue(consumido(conEspacios, '7'));
    }

    @Test
    public void testGuiFormatoFecha() {
        JFormattedTextField conNull = mock(JFormattedTextField.class);
        when(conNull.getText()).thenReturn(null);
        assertTrue(Utils.GUI.isEmpty(conNull));

        JFormattedTextField campo = new JFormattedTextField();
        campo.setText("   ");
        assertTrue(Utils.GUI.isEmpty(campo));

        // Con la mascara aplicada el campo vacio se muestra como --/--/----
        Utils.GUI.assignFormato(campo);
        assertInstanceOf(javax.swing.text.MaskFormatter.class, campo.getFormatter());
        assertEquals("--/--/----", campo.getText());
        assertTrue(Utils.GUI.isEmpty(campo));

        JFormattedTextField conFecha = new JFormattedTextField();
        conFecha.setText("12/10/2025");
        assertFalse(Utils.GUI.isEmpty(conFecha));

        // Si no se puede aplicar la mascara el error se registra y no se propaga
        JFormattedTextField falla = mock(JFormattedTextField.class);
        doAnswer(inv -> { throw new ParseException("mascara invalida", 0); }).when(falla).setFormatterFactory(any());
        assertDoesNotThrow(() -> Utils.GUI.assignFormato(falla, "##"));
    }

    @Test
    public void testGuiContenedor() throws Exception {
        JInternalFrame frame = new JInternalFrame();
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        assertSame(frame, Utils.GUI.getContenedor(frame));
        assertSame(frame, Utils.GUI.getContenedor(panel));
        assertNull(Utils.GUI.getContenedor(new JPanel()));

        Utils.GUI.reSizePadre(panel, 300, 200);
        assertEquals(new Dimension(300, 200), frame.getSize());
        assertDoesNotThrow(() -> Utils.GUI.reSizePadre(new JPanel(), 10, 10));

        // repaint(Component) es privado y no se usa en ningun lado
        JPanel p = spy(new JPanel());
        invocarPrivado(Utils.GUI.class, "repaint", new Class<?>[]{Component.class}, p);
        verify(p).revalidate();
        verify(p).repaint();
    }

    // ************************************************************************
    //  Validate / Fecha / Cast
    // ************************************************************************

    @Test
    public void testValidate() {
        assertTrue(Utils.Validate.isFecha("28/02/2025"));
        assertFalse(Utils.Validate.isFecha("31/02/2025"));
        assertFalse(Utils.Validate.isFecha("no es fecha"));

        assertTrue(Utils.Validate.isEmpty((String) null));
        assertTrue(Utils.Validate.isEmpty("  "));
        assertFalse(Utils.Validate.isEmpty("x"));

        assertTrue(Utils.Validate.isEmpty((List<?>) null));
        assertTrue(Utils.Validate.isEmpty(new ArrayList<>()));
        assertFalse(Utils.Validate.isEmpty(List.of(1)));

        assertTrue(Utils.Validate.isEmpty((Set<?>) null));
        assertTrue(Utils.Validate.isEmpty(new HashSet<>()));
        assertFalse(Utils.Validate.isEmpty(Set.of(1)));
    }

    @Test
    public void testValidateEmail() {
        assertTrue(Utils.Validate.isEmail("ana@correo.com"));
        assertTrue(Utils.Validate.isEmail("  ana@correo.com.uy  "));
        assertFalse(Utils.Validate.isEmail(null));
        assertFalse(Utils.Validate.isEmail("   "));
        assertFalse(Utils.Validate.isEmail("a@b@correo.com"), "Dos arrobas");
        assertFalse(Utils.Validate.isEmail("@correo.com"), "Sin usuario");
        assertFalse(Utils.Validate.isEmail("ana@.com"), "Punto pegado a la arroba");
        assertFalse(Utils.Validate.isEmail("ana@correo.c"), "Dominio de un caracter");
    }

    @Test
    public void testFecha() {
        Date fecha = new GregorianCalendar(2025, Calendar.MARCH, 9).getTime();
        LocalDate local = LocalDate.of(2025, 3, 9);
        assertEquals("09/03/2025", Utils.Fecha.assignFormato(fecha));
        assertEquals("09/03/2025", Utils.Fecha.assignFormato(local));
        assertEquals("2025-03", Utils.Fecha.assignFormato(fecha, "yyyy-MM"));
        assertEquals("2025-03", Utils.Fecha.assignFormato(local, "yyyy-MM"));
        assertNull(Utils.Fecha.assignFormato((Date) null, "yyyy"));
        assertNull(Utils.Fecha.assignFormato((LocalDate) null, "yyyy"));

        assertTrue(Utils.Fecha.validate("01/01/2024"));
        assertFalse(Utils.Fecha.validate("1/1/24"));

        Date despues = new GregorianCalendar(2025, Calendar.MARCH, 10).getTime();
        assertTrue(Utils.Fecha.isCoherente(fecha, despues));
        assertTrue(Utils.Fecha.isCoherente(fecha, fecha));
        assertFalse(Utils.Fecha.isCoherente(despues, fecha));
        assertFalse(Utils.Fecha.isCoherente(null, fecha));
        assertFalse(Utils.Fecha.isCoherente(fecha, null));

        assertTrue(Utils.Fecha.isCoherente(local, local.plusDays(1)));
        assertFalse(Utils.Fecha.isCoherente(local.plusDays(1), local));
        assertFalse(Utils.Fecha.isCoherente((LocalDate) null, local));
        assertFalse(Utils.Fecha.isCoherente(local, (LocalDate) null));
    }

    @Test
    public void testCast() {
        LocalDate local = LocalDate.of(2024, 12, 31);
        Date fecha = Utils.Cast.valueOf(local);
        assertEquals(local, Utils.Cast.valueOf(fecha));
        assertNull(Utils.Cast.valueOf((Date) null));
        assertNull(Utils.Cast.valueOf((LocalDate) null));

        assertEquals(local, Utils.Cast.valueOf("31/12/2024"));
        assertNull(Utils.Cast.valueOf("2024-12-31"));
        assertEquals(fecha, Utils.Cast.valudf("31/12/2024"));
        assertNull(Utils.Cast.valudf("cualquier cosa"));

        assertEquals(42, Utils.Cast.valuOf("42"));
        assertNull(Utils.Cast.valuOf("cuarenta"));
        assertEquals(42, Utils.Cast.valueof("42"));
        assertEquals(Integer.MIN_VALUE, Utils.Cast.valueof("cuarenta"));
        assertEquals("42", Utils.Cast.valueOf(Integer.valueOf(42)));
        assertNull(Utils.Cast.valueOf((Integer) null));
    }

    // ************************************************************************
    //  Crypt
    // ************************************************************************

    @Test
    public void testCryptBCrypt() {
        String hash = Utils.Crypt.encodeBCrypt("secreto", 4);
        assertTrue(Utils.Crypt.isBCrypt(hash));
        assertTrue(Utils.Crypt.compareBCrypt("secreto", hash));
        assertFalse(Utils.Crypt.compareBCrypt("otro", hash));

        String hash7 = Utils.Crypt.hashPassword("clave");
        assertTrue(hash7.startsWith("$2a$07$"));
        assertTrue(Utils.Crypt.compareBCrypt("clave", Utils.Crypt.encodeBCrypt("clave")));

        assertFalse(Utils.Crypt.isBCrypt(null));
        assertFalse(Utils.Crypt.isBCrypt("texto plano"));
    }

    @Test
    public void testCryptVerifyPassword() {
        String hash = Utils.Crypt.encodeBCrypt("secreto", 4);
        assertTrue(Utils.Crypt.verifyPassword("secreto", hash));
        assertFalse(Utils.Crypt.verifyPassword("otro", hash));
        assertFalse(Utils.Crypt.verifyPassword(null, hash));
        assertFalse(Utils.Crypt.verifyPassword("secreto", null));

        // Parece BCrypt pero tiene una cantidad de rondas invalida: BCrypt lanza excepcion
        String falso = "$2a$99$" + "a".repeat(53);
        assertTrue(Utils.Crypt.isBCrypt(falso));
        assertFalse(Utils.Crypt.verifyPassword("secreto", falso));

        // Contrasenas guardadas con AES (iteracion anterior)
        String aes = Utils.Crypt.cifrarAES("secreto");
        assertEquals("secreto", Utils.Crypt.descifrarAES(aes));
        assertTrue(Utils.Crypt.verifyPassword("secreto", aes));
        assertFalse(Utils.Crypt.verifyPassword("otro", aes));

        // Texto plano: no es AES, se compara directo
        assertTrue(Utils.Crypt.verifyPassword("hola", "hola"));
        assertFalse(Utils.Crypt.verifyPassword("hola", "chau"));
    }

    @Test
    public void testCryptErroresAES() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> Utils.Crypt.descifrarAES("aG9sYQ=="));
        assertEquals("Error al descifrar", e.getMessage());

        try (MockedStatic<Cipher> cipher = mockStatic(Cipher.class)) {
            cipher.when(() -> Cipher.getInstance(anyString())).thenThrow(new NoSuchAlgorithmException("sin AES"));
            RuntimeException e2 = assertThrows(RuntimeException.class, () -> Utils.Crypt.cifrarAES("x"));
            assertEquals("Error al cifrar", e2.getMessage());
            assertInstanceOf(NoSuchAlgorithmException.class, e2.getCause());
        }
    }

    // ************************************************************************
    //  Imagen
    // ************************************************************************

    private static ImageIcon icono(int ancho, int alto) {
        return new ImageIcon(new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB));
    }

    private static ImageIcon iconoFalso(int ancho, int alto) {
        ImageIcon i = mock(ImageIcon.class);
        when(i.getIconWidth()).thenReturn(ancho);
        when(i.getIconHeight()).thenReturn(alto);
        return i;
    }

    @Test
    public void testImagenVaciaYRelacionDeAspecto() {
        assertTrue(Utils.Imagen.isEmpty(null));
        assertTrue(Utils.Imagen.isEmpty(iconoFalso(0, 10)));
        assertTrue(Utils.Imagen.isEmpty(iconoFalso(10, 0)));
        assertFalse(Utils.Imagen.isEmpty(icono(4, 2)));

        assertEquals(2.0f, Utils.Imagen.getAspectRatio(icono(40, 20)));
        assertEquals(0f, Utils.Imagen.getAspectRatio(null));
        assertEquals(128, Utils.Imagen.SIZEIMG);
    }

    @Test
    public void testImagenResize() {
        ImageIcon original = icono(40, 20);

        ImageIcon cuadrada = Utils.Imagen.resize(0, 0, original);
        assertEquals(128, cuadrada.getIconWidth());
        assertEquals(128, cuadrada.getIconHeight());

        ImageIcon porAlto = Utils.Imagen.resize(0, 10, original);
        assertEquals(20, porAlto.getIconWidth());
        assertEquals(10, porAlto.getIconHeight());

        ImageIcon porAncho = Utils.Imagen.resize(10, 0, original);
        assertEquals(10, porAncho.getIconWidth());
        assertEquals(5, porAncho.getIconHeight());

        ImageIcon fija = Utils.Imagen.resize(30, 30, original);
        assertEquals(30, fija.getIconWidth());
        assertEquals(30, fija.getIconHeight());

        // Imagen nula: el error se registra (debug habilitado) y devuelve null
        assertNull(Utils.Imagen.resize(10, 10, null));
    }

    @Test
    public void testImagenCargaDesdeJarYDisco() throws IOException {
        ImageIcon jar = Utils.Imagen.loadJVM("usr.png");
        assertNotNull(jar);
        assertTrue(jar.getIconWidth() > 0);
        assertNull(Utils.Imagen.loadJVM("no-existe.png"));

        File png = new File(base, "disco-" + UUID.randomUUID() + ".png");
        try (InputStream in = Utils.class.getResourceAsStream("/Images/usr.png")) {
            Files.copy(in, png.toPath());
        }
        ImageIcon disco = Utils.Imagen.loadSO(png.getPath());
        assertEquals(jar.getIconWidth(), disco.getIconWidth());
        assertNull(Utils.Imagen.loadSO(null));

        assertNull(Utils.Disk.toImageIcon(null));
        assertEquals(png.getAbsolutePath(), Utils.Disk.toImageIcon(png).getDescription());
    }

    @Test
    public void testImagenErroresConLoggerDeshabilitado() {
        // Con el nivel de log deshabilitado no se registra nada pero el resultado es el mismo
        try (MockedStatic<Logger> log = mockStatic(Logger.class)) {
            assertNull(Utils.Imagen.resize(10, 10, null));
            assertNull(Utils.Imagen.loadJVM("no-existe.png"));
            assertNull(Utils.Imagen.loadSO(null));
            log.verify(Logger::isDebugEnabled);
            log.verify(Logger::isErrorEnabled, times(2));
        }
    }

    @Test
    public void testImagenLoadWeb() throws IOException {
        byte[] png;
        try (InputStream in = Utils.class.getResourceAsStream("/Images/usr.png")) {
            png = in.readAllBytes();
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        server.createContext("/usr.png", ex -> {
            ex.sendResponseHeaders(200, png.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(png); }
        });
        server.createContext("/nada.png", ex -> {
            ex.sendResponseHeaders(404, -1);
            ex.close();
        });
        byte[] texto = "esto no es una imagen".getBytes(StandardCharsets.UTF_8);
        server.createContext("/texto.png", ex -> {
            ex.sendResponseHeaders(200, texto.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(texto); }
        });
        server.start();
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort();
            ImageIcon web = Utils.Imagen.loadWeb(url + "/usr.png");
            assertNotNull(web);
            assertTrue(web.getIconWidth() > 0);
            assertNull(Utils.Imagen.loadWeb(url + "/nada.png"), "404 -> IOException");
            assertNull(Utils.Imagen.loadWeb(url + "/texto.png"), "El contenido no es una imagen");
        } finally {
            server.stop(0);
        }
        assertNull(Utils.Imagen.loadWeb("http://url con espacios/x.png"), "URISyntaxException");
        assertNull(Utils.Imagen.loadWeb("usr.png"), "Direccion relativa (sin protocolo)");

        // Otros protocolos no son direcciones web: se registra el error y devuelve null
        File local = new File(base, "web-" + UUID.randomUUID() + ".png");
        Files.write(local.toPath(), png);
        assertNull(Utils.Imagen.loadWeb(local.toURI().toString()), "file:");
        String enJar = Logger.class.getResource("Logger.class").toString();
        assertTrue(enJar.startsWith("jar:"), enJar);
        assertNull(Utils.Imagen.loadWeb(enJar), "jar:");
    }

    @Test
    public void testImagenToStream() throws IOException {
        File carpeta = nuevaCarpetaImagenes();
        Files.createDirectories(carpeta.toPath());
        Files.writeString(new File(carpeta, "dato.txt").toPath(), "hola");
        try (InputStream in = Utils.Imagen.toStream("dato.txt")) {
            assertEquals("hola", new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
        assertThrows(IOException.class, () -> Utils.Imagen.toStream("no-existe.txt"));
    }

    @Test
    public void testImagenOpenJVM() {
        File elegido = new File(base, "elegida.png");
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) -> {
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                when(m.getSelectedFile()).thenReturn(elegido); })) {
            assertSame(elegido, Utils.Imagen.openJVM());
            verify(mc.constructed().get(0)).setFileFilter(any(FileNameExtensionFilter.class));
        }
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) ->
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.CANCEL_OPTION))) {
            assertNull(Utils.Imagen.openJVM());
        }
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) -> {
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                when(m.getSelectedFile()).thenThrow(new IllegalStateException("falla")); })) {
            assertNull(Utils.Imagen.openJVM());
        }
    }

    @Test
    public void testImagenOpenSOWindows() {
        File carpeta = new File(base, "dialogo");
        try (MockedConstruction<FileDialog> mc = mockConstruction(FileDialog.class, (m, ctx) -> {
                when(m.getDirectory()).thenReturn(carpeta.getPath());
                when(m.getFile()).thenReturn("foto.png"); });
             MockedStatic<Utils.OS> os = mockStatic(Utils.OS.class, CALLS_REAL_METHODS)) {
            os.when(Utils.OS::isWin).thenReturn(true);
            assertEquals(new File(carpeta, "foto.png"), Utils.Imagen.openSO());
            FileDialog d = mc.constructed().get(0);
            verify(d).setFile("*.jpg;*.jpeg;*.png");
            verify(d).setVisible(true);
            verify(d, never()).setFilenameFilter(any());
        }
    }

    @Test
    public void testImagenOpenSOOtrosSistemas() {
        try (MockedStatic<Utils.OS> os = mockStatic(Utils.OS.class, CALLS_REAL_METHODS)) {
            os.when(Utils.OS::isWin).thenReturn(false);

            // Se cancela el dialogo (sin directorio)
            try (MockedConstruction<FileDialog> mc = mockConstruction(FileDialog.class)) {
                assertNull(Utils.Imagen.openSO());
                FileDialog d = mc.constructed().get(0);
                verify(d, never()).setFile(anyString());
                ArgumentCaptor<FilenameFilter> filtro = ArgumentCaptor.forClass(FilenameFilter.class);
                verify(d).setFilenameFilter(filtro.capture());
                FilenameFilter f = filtro.getValue();
                assertTrue(f.accept(null, "FOTO.JPG"));
                assertTrue(f.accept(null, "foto.jpeg"));
                assertTrue(f.accept(null, "foto.Png"));
                assertFalse(f.accept(null, "foto.gif"));
            }
            // Directorio sin archivo
            try (MockedConstruction<FileDialog> mc = mockConstruction(FileDialog.class, (m, ctx) ->
                    when(m.getDirectory()).thenReturn(base.getPath()))) {
                assertNull(Utils.Imagen.openSO());
            }
            // Error al leer la seleccion
            try (MockedConstruction<FileDialog> mc = mockConstruction(FileDialog.class, (m, ctx) ->
                    when(m.getDirectory()).thenThrow(new IllegalStateException("falla")))) {
                assertNull(Utils.Imagen.openSO());
            }
        }
    }

    @Test
    public void testImagenSaveDesdeRecursosInternos() throws IOException {
        File carpeta = nuevaCarpetaImagenes();
        // Imagen por defecto contenida en el jar
        Utils.Imagen.extractImage("usr.png");
        File usr = new File(carpeta, "usr.png");
        assertTrue(usr.exists());
        try (InputStream in = Utils.class.getResourceAsStream("/Images/usr.png")) {
            assertArrayEquals(in.readAllBytes(), Files.readAllBytes(usr.toPath()));
        }
        // Recurso que no existe: no se crea nada
        Utils.Imagen.extractImage("no-existe.png");
        assertFalse(new File(carpeta, "no-existe.png").exists());

        // Una ruta externa que termina en usr.png usa la imagen por defecto del jar
        Utils.Imagen.save(base.getPath() + File.separator + "lo-que-sea" + File.separator + cfg.IMAGE_USER, "perfil");
        assertTrue(new File(carpeta, "perfil.png").exists());
    }

    @Test
    public void testImagenSaveDesdeArchivo() throws IOException {
        File carpeta = nuevaCarpetaImagenes();
        File origen = archivo("foto.jpg", "contenido de la foto");

        Utils.Imagen.save(origen.getPath(), "copia");
        assertEquals("contenido de la foto", Files.readString(new File(carpeta, "copia.jpg").toPath()));

        // Si el nombre ya trae la extension no se duplica
        Utils.Imagen.save(origen.getPath(), "copia2.jpg");
        assertTrue(new File(carpeta, "copia2.jpg").exists());
        assertFalse(new File(carpeta, "copia2.jpg.jpg").exists());

        // Origen inexistente: el error se registra y no se crea nada
        Utils.Imagen.save(new File(base, "no-existe.jpg").getPath(), "nada");
        assertFalse(new File(carpeta, "nada.jpg").exists());

        // Ruta terminada en separador: se copia la carpeta (vacia) sin extension
        File dir = origen.getParentFile();
        Utils.Imagen.save(dir.getPath() + File.separator, "carpeta");
        assertTrue(new File(carpeta, "carpeta").isDirectory());
    }

    @Test
    public void testImagenSaveArchivoSinExtension() throws IOException {
        File carpeta = nuevaCarpetaImagenes();
        // Archivo sin extension dentro de una carpeta con punto: se guarda como .png
        File conPunto = new File(base, "juan.perez-" + UUID.randomUUID());
        assertTrue(conPunto.mkdirs());
        File sinExt = new File(conPunto, "foto");
        Files.writeString(sinExt.toPath(), "sin extension");

        Utils.Imagen.save(sinExt.getPath(), "Curso.Prueba");
        assertEquals("sin extension", Files.readString(new File(carpeta, "Curso.Prueba.png").toPath()));
    }

    @Test
    public void testImagenSaveDesdeStream() throws IOException {
        File carpeta = nuevaCarpetaImagenes();
        assertFalse(carpeta.exists());
        assertTrue(Utils.Imagen.save(new ByteArrayInputStream(new byte[]{1, 2, 3}), "s1.png", "image/png"));
        assertArrayEquals(new byte[]{1, 2, 3}, Files.readAllBytes(new File(carpeta, "s1.png").toPath()));

        // Carpeta ya existente y stream vacio: no se copio nada
        assertFalse(Utils.Imagen.save(new ByteArrayInputStream(new byte[0]), "vacio.png", "image/png"));

        InputStream roto = new InputStream() {
            @Override
            public int read() throws IOException { throw new IOException("stream roto"); }
        };
        assertFalse(Utils.Imagen.save(roto, "roto.png", "image/png"));
    }

    // ************************************************************************
    //  Disk
    // ************************************************************************

    @Test
    public void testDiskRutas() throws Exception {
        assertTrue(Utils.Disk.isExist(base.getPath()));
        assertFalse(Utils.Disk.isExist(new File(base, "no-existe").getPath()));

        String sep = File.separator;
        assertArrayEquals(new String[]{"dir" + sep + "sub" + sep, "archivo", ".txt"},
                Utils.Disk.splitPath("dir" + sep + "sub" + sep + "archivo.txt"));
        assertArrayEquals(new String[]{"dir" + sep, "archivo", ""}, Utils.Disk.splitPath("dir" + sep + "archivo"));
        assertArrayEquals(new String[]{"", "archivo", ""}, Utils.Disk.splitPath("archivo"));
        assertArrayEquals(new String[]{"", "foto", ".png"}, Utils.Disk.splitPath("foto.png"));
        // El punto de una carpeta no es la extension del archivo
        assertArrayEquals(new String[]{"juan.perez" + sep, "foto", ""}, Utils.Disk.splitPath("juan.perez" + sep + "foto"));
        assertArrayEquals(new String[]{"dir/", "foto", ".jpg"}, Utils.Disk.splitPath("dir/foto.jpg"));

        File nueva = new File(base, "carpeta-" + UUID.randomUUID() + File.separator + "sub");
        Utils.Disk.createFolder(nueva.getPath());
        assertTrue(nueva.isDirectory());
        Utils.Disk.createFolder(nueva.getPath());
        assertTrue(nueva.isDirectory());

        // getPaqueteName es privado y no se usa en ningun lado
        assertEquals("edu.edext.tools", invocarPrivado(Utils.Disk.class, "getPaqueteName", new Class<?>[]{}));
    }

    @Test
    public void testDiskOpenJVM() {
        File elegido = new File(base, "elegido.txt");
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) -> {
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                when(m.getSelectedFile()).thenReturn(elegido); })) {
            assertSame(elegido, Utils.Disk.openJVM());
        }
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) ->
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.CANCEL_OPTION))) {
            assertNull(Utils.Disk.openJVM());
        }
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) -> {
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                when(m.getSelectedFile()).thenThrow(new IllegalStateException("falla")); })) {
            assertNull(Utils.Disk.openJVM());
        }
    }

    @Test
    public void testDiskOpenPathJVM() {
        File carpeta = new File(base, "elegida");
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) -> {
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                when(m.getSelectedFile()).thenReturn(carpeta); })) {
            assertEquals(carpeta.getAbsolutePath(), Utils.Disk.openPathJVM());
            JFileChooser c = mc.constructed().get(0);
            verify(c).setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            verify(c).setAcceptAllFileFilterUsed(false);
        }
        try (MockedConstruction<JFileChooser> mc = mockConstruction(JFileChooser.class, (m, ctx) ->
                when(m.showOpenDialog(any())).thenReturn(JFileChooser.CANCEL_OPTION))) {
            assertEquals("", Utils.Disk.openPathJVM());
        }
    }

    /** Dialogo simulado que devuelve dir/archivo. */
    private static MockedConstruction<FileDialog> dialogo(String dir, String archivo) {
        return mockConstruction(FileDialog.class, (m, ctx) -> {
            when(m.getDirectory()).thenReturn(dir);
            when(m.getFile()).thenReturn(archivo);
        });
    }

    @Test
    public void testDiskOpenFileOSWindows() throws Exception {
        String dir = base.getPath();
        conSO("Windows 11", () -> {
            // Sin filtro se muestran todos los archivos
            try (MockedConstruction<FileDialog> mc = dialogo(dir, "a.txt")) {
                assertEquals(new File(dir, "a.txt"), Utils.Disk.openFileOS());
                FileDialog d = mc.constructed().get(0);
                verify(d).setFile("*.*");
                verify(d).setVisible(true);
            }
            try (MockedConstruction<FileDialog> mc = dialogo(dir, "a.txt")) {
                Utils.Disk.openFileOS((String) null);
                verify(mc.constructed().get(0)).setFile("*.*");
            }
            try (MockedConstruction<FileDialog> mc = dialogo(dir, "a.txt")) {
                Utils.Disk.openFileOS("*.*", "Titulo");
                verify(mc.constructed().get(0)).setFile("*.*");
            }
            try (MockedConstruction<FileDialog> mc = dialogo(dir, "a.pdf")) {
                Utils.Disk.openFileOS("documento.pdf");
                verify(mc.constructed().get(0)).setFile("*.pdf");
            }
            try (MockedConstruction<FileDialog> mc = dialogo(dir, "a.pdf")) {
                Utils.Disk.openFileOS("pdf", "Titulo");
                verify(mc.constructed().get(0)).setFile("*.*");
            }
            return null;
        });
    }

    @Test
    public void testDiskOpenFileOSOtrosSistemas() throws Exception {
        String dir = base.getPath();
        conSO("Linux", () -> {
            try (MockedConstruction<FileDialog> mc = dialogo(dir, "a.txt")) {
                assertEquals(new File(dir, "a.txt"), Utils.Disk.openFileOS(".txt", "Titulo"));
                FileDialog d = mc.constructed().get(0);
                verify(d, never()).setFile(anyString());
                ArgumentCaptor<FilenameFilter> filtro = ArgumentCaptor.forClass(FilenameFilter.class);
                verify(d).setFilenameFilter(filtro.capture());
                assertTrue(filtro.getValue().accept(null, "NOTAS.TXT"));
                assertFalse(filtro.getValue().accept(null, "notas.pdf"));
            }
            try (MockedConstruction<FileDialog> mc = dialogo(null, null)) {
                assertNull(Utils.Disk.openFileOS("*.*", "Titulo"));
                verify(mc.constructed().get(0), never()).setFilenameFilter(any());
            }
            try (MockedConstruction<FileDialog> mc = dialogo(dir, null)) {
                assertNull(Utils.Disk.openFileOS("txt", "Titulo"));
                verify(mc.constructed().get(0), never()).setFilenameFilter(any());
            }
            try (MockedConstruction<FileDialog> mc = mockConstruction(FileDialog.class, (m, ctx) ->
                    when(m.getDirectory()).thenThrow(new IllegalStateException("falla")))) {
                assertNull(Utils.Disk.openFileOS("txt", "Titulo"));
            }
            return null;
        });
    }

    /** ProcessBuilder simulado: guarda el comando y devuelve un proceso cuya salida es la indicada. */
    private static MockedConstruction<ProcessBuilder> proceso(List<List<String>> comandos, String salida, boolean falla) {
        return mockConstruction(ProcessBuilder.class, (m, ctx) -> {
            comandos.add(List.of((String[]) ctx.arguments().get(0)));
            if (falla) {
                when(m.start()).thenThrow(new IOException("no se pudo ejecutar"));
            } else {
                Process p = mock(Process.class);
                when(p.getInputStream()).thenReturn(new ByteArrayInputStream(salida.getBytes(StandardCharsets.UTF_8)));
                when(m.start()).thenReturn(p);
            }
        });
    }

    @Test
    public void testDiskOpenPathOSWindows() throws Exception {
        conSO("Windows 10", () -> {
            List<List<String>> comandos = new ArrayList<>();
            try (MockedConstruction<ProcessBuilder> pb = proceso(comandos, "C:\\Elegida\r\n", false)) {
                assertEquals("C:\\Elegida", Utils.Disk.openPathOS(""));
            }
            assertEquals("powershell", comandos.get(0).get(0));
            assertTrue(comandos.get(0).get(3).contains("'Selecciona una carpeta'"), "Titulo vacio usa el titulo por defecto");

            // Se cancela el dialogo: powershell no imprime nada
            try (MockedConstruction<ProcessBuilder> pb = proceso(comandos, "", false)) {
                assertEquals("", Utils.Disk.openPathOS());
            }
            // No se pudo ejecutar powershell
            try (MockedConstruction<ProcessBuilder> pb = proceso(comandos, "", true)) {
                assertEquals("", Utils.Disk.openPathOS("Titulo"));
            }
            assertTrue(comandos.get(2).get(3).contains("'Titulo'"));
            return null;
        });
    }

    @Test
    public void testDiskOpenPathOSOtrosSistemas() throws Exception {
        conSO("Linux", () -> {
            List<List<String>> comandos = new ArrayList<>();
            try (MockedConstruction<ProcessBuilder> pb = proceso(comandos, "/home/ana/fotos\n", false)) {
                assertEquals("/home/ana/fotos", Utils.Disk.openPathOS("Elegir"));
            }
            assertEquals(List.of("zenity", "--file-selection", "--directory", "--title=Elegir"), comandos.get(0));

            try (MockedConstruction<ProcessBuilder> pb = proceso(comandos, "", true)) {
                assertEquals("", Utils.Disk.openPathOS("Elegir"));
            }
            return null;
        });
    }

    @Test
    public void testDiskSave() throws IOException {
        File origen = archivo("foto-" + UUID.randomUUID() + ".jpg", "bytes");
        String nombre = Utils.Disk.splitPath(origen.getAbsolutePath())[Utils.NAMEFILE];
        File porDefecto = new File(System.getProperty("user.home"), ".edEXT" + File.separator + "imagenes");

        Utils.Disk.save(origen);
        assertEquals("bytes", Files.readString(new File(porDefecto, nombre + ".jpg").toPath()));

        Utils.Disk.save(origen, "renombrada");
        assertTrue(new File(porDefecto, "renombrada.jpg").exists());

        File destino = new File(base, "destino-" + UUID.randomUUID());
        Utils.Disk.save(origen, destino.getPath(), "");
        assertTrue(new File(destino, nombre + ".jpg").exists());

        // Origen inexistente: el error se registra
        Utils.Disk.save(new File(base, "no-existe.jpg"), destino.getPath(), "x");
        assertFalse(new File(destino, "x.jpg").exists());
    }

    @Test
    public void testDiskLoadCFG() throws IOException {
        File f = archivo("prueba.cfg", "\n   \n# comentario : x\n// otro : y\nsin separador\nclave1 : valor1\n  clave2:valor:con:dos  \n");
        Utils.CfgDB[] r = Utils.Disk.loadCFG(f.getPath());
        assertArrayEquals(new Utils.CfgDB[]{new Utils.CfgDB("clave1", "valor1"), new Utils.CfgDB("clave2", "valor:con:dos")}, r);

        assertEquals(0, Utils.Disk.loadCFG(new File(base, "no-existe.cfg").getPath()).length);
    }

    @Test
    public void testDiskSaveCfgPath() throws IOException {
        Path archivo = Path.of(cfg.PATH_FILE);
        assertTrue(archivo.startsWith(base.toPath()), "El edEXT.cfg de los tests queda dentro de target");
        Files.deleteIfExists(archivo);

        Utils.Disk.saveCfgPath("#", " configuracion", false);
        Utils.Disk.saveCfgPath("\n", "imagen : /fotos");
        assertEquals("# configuracion\nimagen : /fotos", Files.readString(archivo));
        assertArrayEquals(new Utils.CfgDB[]{new Utils.CfgDB("imagen", "/fotos")}, Utils.Disk.loadCFG(cfg.PATH_FILE));

        Utils.Disk.saveCfgPath("clave", "valor", false);
        assertEquals("clave : valor", Files.readString(archivo));

        // Al agregar claves seguidas cada una queda en su propia linea
        Utils.Disk.saveCfgPath("otra", "valor2");
        Utils.Disk.saveCfgPath("imagen", "/fotos", true);
        assertEquals("clave : valor\notra : valor2\nimagen : /fotos", Files.readString(archivo));
        assertArrayEquals(new Utils.CfgDB[]{new Utils.CfgDB("clave", "valor"), new Utils.CfgDB("otra", "valor2"),
                new Utils.CfgDB("imagen", "/fotos")}, Utils.Disk.loadCFG(cfg.PATH_FILE));

        // Despues de un comentario la clave tambien empieza en una linea nueva
        Utils.Disk.saveCfgPath("#", " configuracion", false);
        Utils.Disk.saveCfgPath("imagen", "/fotos");
        assertEquals("# configuracion\nimagen : /fotos", Files.readString(archivo));

        // Si no se puede escribir (el archivo es una carpeta) el error se registra
        Files.delete(archivo);
        Files.createDirectory(archivo);
        try {
            assertDoesNotThrow(() -> Utils.Disk.saveCfgPath("clave", "valor"));
            assertTrue(Files.isDirectory(archivo));
        } finally {
            Files.delete(archivo);
        }
    }

    // ************************************************************************
    //  OS
    // ************************************************************************

    @Test
    public void testOS() throws Exception {
        assertEquals("edu.edext.tools", Utils.OS.getPaqueteName(this));
        assertEquals(System.getProperty("user.home"), Utils.OS.getHomePath());
        assertEquals(System.getenv("APPDATA"), Utils.OS.getAppDataPath());
        assertEquals(System.getProperty("os.name"), Utils.OS.getName());
        assertEquals(System.getProperty("user.dir"), Utils.OS.getWorkPath());
        assertEquals(File.separator, Utils.OS.getSeparador());
        assertEquals("~", Utils.OS.SymHome);

        conSO("Windows 11", () -> {
            assertTrue(Utils.OS.isWin());
            assertFalse(Utils.OS.isLinux());
            assertFalse(Utils.OS.isMac());
            assertFalse(Utils.OS.isUnix());
            assertFalse(Utils.OS.isBSD());
            return null;
        });
        conSO("Linux", () -> { assertTrue(Utils.OS.isLinux()); assertFalse(Utils.OS.isWin()); return null; });
        conSO("Mac OS X", () -> { assertTrue(Utils.OS.isMac()); return null; });
        conSO("Unix", () -> { assertTrue(Utils.OS.isUnix()); return null; });
        conSO("FreeBSD", () -> { assertTrue(Utils.OS.isBSD()); return null; });
    }

    // ************************************************************************
    //  Mouse
    // ************************************************************************

    private static MouseEvent evento(Object origen, Component componente, int x, int y) {
        MouseEvent e = mock(MouseEvent.class);
        when(e.getSource()).thenReturn(origen);
        when(e.getComponent()).thenReturn(componente);
        when(e.getX()).thenReturn(x);
        when(e.getY()).thenReturn(y);
        return e;
    }

    @Test
    public void testMouseArrastrarVentanas() {
        Utils.Mouse mouse = new Utils.Mouse();
        mouse.MousePressed(evento(null, null, 5, 7));

        PointerInfo puntero = mock(PointerInfo.class);
        when(puntero.getLocation()).thenReturn(new Point(100, 200));
        try (MockedStatic<MouseInfo> mi = mockStatic(MouseInfo.class)) {
            mi.when(MouseInfo::getPointerInfo).thenReturn(puntero);

            // El evento viene de la ventana
            JFrame ventana = mock(JFrame.class);
            mouse.MouseDragged(evento(ventana, null, 0, 0));
            verify(ventana).setLocation(95, 193);

            // El evento viene de un componente dentro de la ventana
            JFrame padre = mock(JFrame.class);
            JPanel hijo = new JPanel() {
                @Override
                public Container getParent() { return padre; }
            };
            mouse.MouseDragged(evento(hijo, hijo, 0, 0));
            verify(padre).setLocation(95, 193);

            // Arrastrar el JInternalFrame que contiene al componente
            JInternalFrame interno = new JInternalFrame();
            JPanel contenido = new JPanel();
            interno.getContentPane().add(contenido);
            mouse.MouseContenedorDragged(evento(contenido, contenido, 0, 0));
            assertEquals(new Point(95, 193), interno.getLocation());
        }
    }
}
