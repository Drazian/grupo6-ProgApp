package edu.edext.presentacion;

import com.toedter.calendar.JDateChooser;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import edu.edext.tools.cfg;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario AltaCurso contra la base H2 de los tests. No se abre ninguna ventana:
 * los JOptionPane se reemplazan con Mockito, el popup del calendario (JDateChooser) por uno que
 * solo cuenta las veces que se pidio mostrarlo y el selector de archivos (Utils.Imagen.openSO,
 * que abre un FileDialog nativo) por un mock que devuelve un archivo creado en target/.
 */
public class AltaCursoTest {

    private static final String CONFIRM_DESCRIPCION = "Desean continuar sin descripcion";
    private static final String CONFIRM_CATEGORIA = "Desea continuar sin asignar categoria";
    private static final String CONFIRM_PREVIAS = "Desea continuar sin asignar previas";

    private MockedStatic<JOptionPane> dialogos;
    /** Respuesta de cada confirmacion (showOptionDialog), por mensaje. Por defecto: Si. */
    private final Map<String, Integer> respuestas = new HashMap<>();
    private int calendarioAbierto;

    @BeforeEach
    public void setUp() {
        dialogos = mockStatic(JOptionPane.class);
        dialogos.when(() -> JOptionPane.showOptionDialog(any(), any(), any(), anyInt(), anyInt(), any(), any(), any()))
                .thenAnswer(inv -> respuestas.getOrDefault(String.valueOf((Object) inv.getArgument(1)), JOptionPane.YES_OPTION));
        respuestas.clear();
        calendarioAbierto = 0;
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

    /** Crea el formulario y cambia el popup del calendario por uno que no se muestra en pantalla. */
    private AltaCurso nuevoForm() throws Exception {
        AltaCurso form = new AltaCurso();
        JDateChooser chooser = campo(form, "jDfechaAlta");
        Field popup = JDateChooser.class.getDeclaredField("popup");
        popup.setAccessible(true);
        popup.set(chooser, new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                calendarioAbierto++;
            }
        });
        return form;
    }

    /** true si el listener es una clase anonima de AltaCurso (y no uno de Swing). */
    private static boolean esDelFormulario(Object listener) {
        return listener.getClass().getName().startsWith(AltaCurso.class.getName() + "$");
    }

    /** Ejecuta solo los FocusListener que agrega el formulario (no los de Swing). */
    private static void perderFoco(Component c) {
        FocusEvent evt = new FocusEvent(c, FocusEvent.FOCUS_LOST);
        int ejecutados = 0;
        for (FocusListener l : c.getFocusListeners())
            if (esDelFormulario(l)) {
                l.focusLost(evt);
                ejecutados++;
            }
        assertEquals(1, ejecutados);
    }

    /**
     * El mismo archivo, pero con getAbsolutePath() devolviendo la ruta relativa al modulo (p.ej. target\fotoSinPunto):
     * asi la ruta que recibe el formulario no tiene ningun punto, sin depender de donde esta el proyecto.
     */
    private static File sinPuntos(File relativo) {
        File ret = new File(relativo.getPath()) {
            @Override
            public String getAbsolutePath() {
                return getPath();
            }
        };
        assertFalse(ret.getAbsolutePath().contains("."));
        return ret;
    }

    private record Datos(String instituto, String categoria, String previa) {}

    private static Datos crearDatos() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        String inst = unico("INST"), cat = unico("CAT"), previa = unico("PREVIA");
        ic.crearInstituto(inst);
        ic.crearCategoria(cat);
        ic.altaCurso(new DtCurso(previa, "Curso previo", "2 semanas", 10, 3, "http://previa", new Date(),
                new DtInstituto(inst), new HashSet<>(), new HashSet<>(), cfg.IMAGE_USER), inst);
        return new Datos(inst, cat, previa);
    }

    private static DtCurso buscarCurso(String nombre) throws Exception {
        for (DtCurso c : Fabrica.getInstance().getIControlador().listarCursos())
            if (c.getNombre().equals(nombre)) return c;
        return null;
    }

    private static void llenarValido(AltaCurso form, String instituto, String nombre) throws Exception {
        ((JComboBox<?>) campo(form, "institutoSeleccion")).setSelectedItem(instituto);
        ((JTextField) campo(form, "textoNombre")).setText(nombre);
        ((JTextField) campo(form, "textoDescripcion")).setText("Descripcion de " + nombre);
        ((JTextField) campo(form, "textoURL")).setText("http://edext/" + nombre);
        ((JTextField) campo(form, "textoDuracion")).setText("3");
        JFormattedTextField fecha = campo(form, "textoFechaAlta");
        fecha.setText("15/03/2024");
        assertEquals("15/03/2024", fecha.getText());
        ((JTextField) campo(form, "textoCantidadHoras")).setText("40");
        ((JTextField) campo(form, "textoCreditos")).setText("8");
    }

    private void verificarAlerta(AltaCurso form, String mensaje) {
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq(mensaje), eq("Advertencia"), eq(JOptionPane.WARNING_MESSAGE)));
    }

    private void verificarConfirmacion(AltaCurso form, String mensaje, int veces) {
        dialogos.verify(() -> JOptionPane.showOptionDialog(eq(form), eq(mensaje), eq("Confirme acción"),
                eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.WARNING_MESSAGE), isNull(), isNull(), isNull()), times(veces));
    }

    @Test
    public void testValidacionesYAltaSinOpcionales() throws Exception {
        Datos d = crearDatos();
        AltaCurso form = nuevoForm();
        String nombre = unico("CURSO");
        JButton guardar = campo(form, "buttonGuardar");
        JTextField textoNombre = campo(form, "textoNombre");
        JTextField textoDescripcion = campo(form, "textoDescripcion");
        JTextField textoURL = campo(form, "textoURL");
        JTextField textoDuracion = campo(form, "textoDuracion");
        JFormattedTextField textoFechaAlta = campo(form, "textoFechaAlta");
        JTextField textoCantidadHoras = campo(form, "textoCantidadHoras");
        JTextField textoCreditos = campo(form, "textoCreditos");
        assertTrue(guardar.isEnabled(), "Hay institutos, se puede guardar");
        ((JComboBox<?>) campo(form, "institutoSeleccion")).setSelectedItem(d.instituto());

        // Sin nombre
        guardar.doClick();
        verificarAlerta(form, "Ingrese nombre del curso");

        // Sin descripcion y el usuario no quiere continuar
        textoNombre.setText(nombre);
        respuestas.put(CONFIRM_DESCRIPCION, JOptionPane.NO_OPTION);
        guardar.doClick();
        verificarConfirmacion(form, CONFIRM_DESCRIPCION, 1);
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), eq("Ingrese link del curso"), anyString(), anyInt()), never());

        // Sin descripcion pero continua: falta la URL
        respuestas.remove(CONFIRM_DESCRIPCION);
        guardar.doClick();
        verificarConfirmacion(form, CONFIRM_DESCRIPCION, 2);
        verificarAlerta(form, "Ingrese link del curso");

        // Falta la duracion
        textoDescripcion.setText("Descripcion");
        textoURL.setText("http://edext/curso");
        guardar.doClick();
        verificarAlerta(form, "Ingrese duracion del curso");

        // Falta la fecha: se abre el calendario
        textoDuracion.setText("3");
        assertEquals("--/--/----", textoFechaAlta.getText());
        guardar.doClick();
        verificarAlerta(form, "Ingrese fecha de alta");
        assertEquals(1, calendarioAbierto);

        // Faltan las horas
        textoFechaAlta.setText("15/03/2024");
        guardar.doClick();
        verificarAlerta(form, "Ingrese cantidad de horas");

        // Faltan los creditos
        textoCantidadHoras.setText("40");
        guardar.doClick();
        verificarAlerta(form, "Ingrese creditos del curso");

        // Sin categorias y el usuario no quiere continuar
        textoCreditos.setText("8");
        respuestas.put(CONFIRM_CATEGORIA, JOptionPane.NO_OPTION);
        guardar.doClick();
        verificarConfirmacion(form, CONFIRM_CATEGORIA, 1);
        verificarConfirmacion(form, CONFIRM_PREVIAS, 0);

        // Sin previas y el usuario no quiere continuar
        respuestas.remove(CONFIRM_CATEGORIA);
        respuestas.put(CONFIRM_PREVIAS, JOptionPane.NO_OPTION);
        guardar.doClick();
        verificarConfirmacion(form, CONFIRM_PREVIAS, 1);
        assertNull(buscarCurso(nombre), "Todavia no se creo el curso");

        // Acepta continuar sin categorias ni previas: se crea el curso con la imagen por defecto
        respuestas.remove(CONFIRM_PREVIAS);
        guardar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El curso '" + nombre + "' fue creado exitosamente."), eq("Éxito"), eq(JOptionPane.INFORMATION_MESSAGE)));
        DtCurso creado = buscarCurso(nombre);
        assertNotNull(creado);
        assertEquals("3 dias", creado.getDuracion());
        assertEquals(40, creado.getCantidadHoras());
        assertEquals(8, creado.getCreditos());
        assertEquals(cfg.IMAGE_USER, creado.getImagen());
        assertTrue(creado.getNameCategorias().isEmpty());
        assertTrue(creado.getNamePrevias().isEmpty());
        // El formulario queda limpio y conserva el instituto
        assertEquals("", textoNombre.getText());
        assertEquals("", textoURL.getText());
        assertEquals(d.instituto(), ((JComboBox<?>) campo(form, "institutoSeleccion")).getSelectedItem());

        // Mismo nombre otra vez: el controlador lo rechaza
        llenarValido(form, d.instituto(), nombre);
        guardar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Ya existe un curso registrado con el nombre '" + nombre + "'."), eq("Error en el Sistema"), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testAltaConCategoriasPreviasEImagen() throws Exception {
        Datos d = crearDatos();
        File carpeta = new File("target/test-alta-curso");
        File carpetaSinExt = new File("target/test-alta-curso.dir");
        carpeta.mkdirs();
        carpetaSinExt.mkdirs();
        File foto = new File(carpeta, "foto.png");
        File fotoSinExtension = new File(carpetaSinExt, "fotoSinExtension");
        File fotoSinPunto = new File("target", "fotoSinPunto");
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), "png", foto);
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), "png", fotoSinExtension);
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), "png", fotoSinPunto);
        File[] seleccion = {null};

        // Solo se reemplaza openSO (abre un FileDialog nativo); el resto de Utils.Imagen es real
        try (MockedStatic<Utils.Imagen> imagenes = mockStatic(Utils.Imagen.class,
                inv -> "openSO".equals(inv.getMethod().getName()) ? seleccion[0] : inv.callRealMethod())) {
            AltaCurso form = nuevoForm();
            JButton btnAgregarImagen = campo(form, "btnAgregarImagen");
            JLabel lblImagen = campo(form, "lblImagen");
            JList<String> listaCategorias = campo(form, "listaCategorias");
            JList<String> listaPrevias = campo(form, "listaPrevias");
            JButton guardar = campo(form, "buttonGuardar");

            // Se cancela la seleccion de imagen
            btnAgregarImagen.doClick();
            assertNull(lblImagen.getIcon());

            // Se elige una imagen con extension
            seleccion[0] = foto;
            btnAgregarImagen.doClick();
            assertNotNull(lblImagen.getIcon());

            String nombre = unico("CURSO");
            llenarValido(form, d.instituto(), nombre);
            ((JComboBox<?>) campo(form, "jCUnitDuracion")).setSelectedItem("semanas");
            listaCategorias.setSelectedValue(d.categoria(), false);
            listaPrevias.setSelectedValue(d.previa(), false);
            guardar.doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El curso '" + nombre + "' fue creado exitosamente."), eq("Éxito"), eq(JOptionPane.INFORMATION_MESSAGE)));
            verificarConfirmacion(form, CONFIRM_CATEGORIA, 0);
            verificarConfirmacion(form, CONFIRM_PREVIAS, 0);
            DtCurso creado = buscarCurso(nombre);
            assertNotNull(creado);
            assertEquals("3 semanas", creado.getDuracion());
            assertEquals("Curso." + nombre + ".png", creado.getImagen());
            assertEquals(java.util.Set.of(d.categoria()), creado.getNameCategorias());
            assertEquals(java.util.Set.of(d.previa()), creado.getNamePrevias());
            assertTrue(new File(cfg.pathImagen(), "Curso." + nombre + ".png").isFile(), "La imagen se copia a la carpeta de imagenes");
            assertNull(lblImagen.getIcon(), "Despues de guardar se limpia la imagen");

            // Imagen sin extension (en una carpeta con punto): el nombre guardado y el archivo usan .png
            seleccion[0] = fotoSinExtension;
            btnAgregarImagen.doClick();
            String nombre2 = unico("CURSO");
            llenarValido(form, d.instituto(), nombre2);
            guardar.doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El curso '" + nombre2 + "' fue creado exitosamente."), eq("Éxito"), eq(JOptionPane.INFORMATION_MESSAGE)));
            assertEquals("Curso." + nombre2 + ".png", buscarCurso(nombre2).getImagen());
            assertTrue(new File(cfg.pathImagen(), "Curso." + nombre2 + ".png").isFile(), "La imagen sin extension se copia como .png");

            // Imagen sin extension y sin ningun punto en toda la ruta: antes lanzaba excepcion
            seleccion[0] = sinPuntos(fotoSinPunto);
            btnAgregarImagen.doClick();
            String nombre3 = unico("CURSO");
            llenarValido(form, d.instituto(), nombre3);
            guardar.doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El curso '" + nombre3 + "' fue creado exitosamente."), eq("Éxito"), eq(JOptionPane.INFORMATION_MESSAGE)));
            assertEquals("Curso." + nombre3 + ".png", buscarCurso(nombre3).getImagen());
            assertTrue(new File(cfg.pathImagen(), "Curso." + nombre3 + ".png").isFile(), "La imagen se copia aunque la ruta no tenga puntos");

            // Nombre de curso terminado en la extension de la imagen: el archivo se llama igual que en la base
            seleccion[0] = foto;
            btnAgregarImagen.doClick();
            String nombre4 = unico("CURSO") + ".png";
            llenarValido(form, d.instituto(), nombre4);
            guardar.doClick();
            assertEquals("Curso." + nombre4 + ".png", buscarCurso(nombre4).getImagen());
            assertTrue(new File(cfg.pathImagen(), "Curso." + nombre4 + ".png").isFile(), "Archivo y base usan el mismo nombre");

            dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), eq("Error en el Sistema"), anyInt()), never());
        }
    }

    @Test
    public void testFalloAlGuardarLaImagenNoEsErrorDelAlta() throws Exception {
        Datos d = crearDatos();
        File carpeta = new File("target/test-alta-curso");
        carpeta.mkdirs();
        File foto = new File(carpeta, "foto-falla.png");
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), "png", foto);

        // openSO devuelve la foto y save(String, String) falla; el resto de Utils.Imagen es real
        try (MockedStatic<Utils.Imagen> imagenes = mockStatic(Utils.Imagen.class, inv -> {
                switch (inv.getMethod().getName()) {
                    case "openSO": return foto;
                    case "save": throw new IllegalStateException("disco lleno");
                    default: return inv.callRealMethod();
                }
            })) {
            AltaCurso form = nuevoForm();
            ((JButton) campo(form, "btnAgregarImagen")).doClick();
            String nombre = unico("CURSO");
            llenarValido(form, d.instituto(), nombre);
            ((JButton) campo(form, "buttonGuardar")).doClick();

            // El curso queda creado: se avisa lo de la imagen y se informa el alta, sin "Error en el Sistema"
            assertEquals("Curso." + nombre + ".png", buscarCurso(nombre).getImagen());
            verificarAlerta(form, "El curso '" + nombre + "' fue creado, pero no se pudo guardar la imagen : disco lleno");
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("El curso '" + nombre + "' fue creado exitosamente."), eq("Éxito"), eq(JOptionPane.INFORMATION_MESSAGE)));
            dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), eq("Error en el Sistema"), anyInt()), never());
            assertNull(((JLabel) campo(form, "lblImagen")).getIcon(), "El formulario queda listo para otro curso");
        }
    }

    @Test
    public void testAltasSucesivasNoAcumulanListeners() throws Exception {
        Datos d = crearDatos();
        AltaCurso form = nuevoForm();
        JTextField textoCantidadHoras = campo(form, "textoCantidadHoras");
        JTextField textoCreditos = campo(form, "textoCreditos");
        JTextField textoDuracion = campo(form, "textoDuracion");
        JFormattedTextField textoFechaAlta = campo(form, "textoFechaAlta");
        int horas = textoCantidadHoras.getKeyListeners().length;
        int creditos = textoCreditos.getKeyListeners().length;
        int duracion = textoDuracion.getKeyListeners().length;
        Object formato = textoFechaAlta.getFormatterFactory();

        for (int i = 0; i < 3; i++) {
            String nombre = unico("CURSO");
            llenarValido(form, d.instituto(), nombre);
            textoFechaAlta.commitEdit();
            assertEquals("15/03/2024", textoFechaAlta.getValue());
            ((JButton) campo(form, "buttonGuardar")).doClick();
            assertNotNull(buscarCurso(nombre));

            // Despues de cada alta los campos siguen con un solo filtro de numeros y el mismo formato de fecha
            assertEquals(horas, textoCantidadHoras.getKeyListeners().length);
            assertEquals(creditos, textoCreditos.getKeyListeners().length);
            assertEquals(duracion, textoDuracion.getKeyListeners().length);
            assertSame(formato, textoFechaAlta.getFormatterFactory());
            // La fecha queda vacia (texto y valor), con la mascara visible
            assertNull(textoFechaAlta.getValue());
            assertEquals("--/--/----", textoFechaAlta.getText());
        }

        // El filtro de numeros sigue funcionando: una letra se descarta
        KeyEvent letra = new KeyEvent(textoCreditos, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, 'a');
        for (KeyListener l : textoCreditos.getKeyListeners()) l.keyTyped(letra);
        assertTrue(letra.isConsumed());
    }

    @Test
    public void testListenersDelFormulario() throws Exception {
        Datos d = crearDatos();
        AltaCurso form = nuevoForm();
        @SuppressWarnings("unchecked")
        JComboBox<String> institutos = campo(form, "institutoSeleccion");
        JButton guardar = campo(form, "buttonGuardar");
        JList<String> listaCategorias = campo(form, "listaCategorias");
        JList<String> listaPrevias = campo(form, "listaPrevias");
        JFormattedTextField textoFechaAlta = campo(form, "textoFechaAlta");
        JDateChooser jDfechaAlta = campo(form, "jDfechaAlta");

        // Al desplegar el combo se recargan los institutos y se mantiene la seleccion
        institutos.setSelectedItem(d.instituto());
        institutos.firePopupMenuWillBecomeVisible();
        assertEquals(d.instituto(), institutos.getSelectedItem());
        assertTrue(guardar.isEnabled());
        institutos.firePopupMenuWillBecomeInvisible();
        institutos.firePopupMenuCanceled();
        institutos.setSelectedIndex(-1);
        assertFalse(guardar.isEnabled(), "Sin instituto seleccionado no se puede guardar");
        institutos.setSelectedItem(d.instituto());
        assertTrue(guardar.isEnabled());

        // Perder el foco sin seleccion recarga la lista; con seleccion la deja como esta
        ListModel<String> modelo = listaCategorias.getModel();
        perderFoco(listaCategorias);
        assertNotSame(modelo, listaCategorias.getModel());
        listaCategorias.setSelectedValue(d.categoria(), false);
        modelo = listaCategorias.getModel();
        perderFoco(listaCategorias);
        assertSame(modelo, listaCategorias.getModel());

        modelo = listaPrevias.getModel();
        perderFoco(listaPrevias);
        assertNotSame(modelo, listaPrevias.getModel());
        listaPrevias.setSelectedValue(d.previa(), false);
        modelo = listaPrevias.getModel();
        perderFoco(listaPrevias);
        assertSame(modelo, listaPrevias.getModel());

        // Elegir una fecha en el calendario la escribe en el campo; borrarla no cambia el campo
        Date fecha = Utils.Cast.valueOf(LocalDate.of(2024, 5, 20));
        jDfechaAlta.setDate(fecha);
        assertEquals("20/05/2024", textoFechaAlta.getText());
        jDfechaAlta.setDate(null);
        assertEquals("20/05/2024", textoFechaAlta.getText());

        // Escribir la fecha y perder el foco (o dar Enter) la pasa al calendario
        textoFechaAlta.setText("10/06/2024");
        perderFoco(textoFechaAlta);
        assertEquals(Utils.Cast.valueOf(LocalDate.of(2024, 6, 10)), jDfechaAlta.getDate());
        textoFechaAlta.setText("11/07/2024");
        textoFechaAlta.postActionEvent();
        assertEquals(Utils.Cast.valueOf(LocalDate.of(2024, 7, 11)), jDfechaAlta.getDate());

        // Campo de fecha vacio: no se toca el calendario
        jDfechaAlta.setDate(null);
        textoFechaAlta.setValue(null);
        assertEquals("--/--/----", textoFechaAlta.getText());
        perderFoco(textoFechaAlta);
        textoFechaAlta.postActionEvent();
        assertNull(jDfechaAlta.getDate());

        // El boton "......" abre el calendario
        ((JButton) campo(form, "jBfechaAlta")).doClick();
        assertEquals(1, calendarioAbierto);

        // Arrastrar la ventana (solo los listeners del formulario: los del look and feel
        // necesitan que el JInternalFrame este dentro de un escritorio visible)
        int ejecutados = 0;
        for (MouseListener l : form.getMouseListeners())
            if (esDelFormulario(l)) {
                l.mousePressed(new MouseEvent(form, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false));
                ejecutados++;
            }
        for (MouseMotionListener l : form.getMouseMotionListeners())
            if (esDelFormulario(l)) {
                l.mouseDragged(new MouseEvent(form, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
                ejecutados++;
            }
        assertEquals(2, ejecutados);
        assertNotNull(form.getLocation());

        // Cancelar cierra el formulario
        ((JButton) campo(form, "buttonCancelar")).doClick();
        assertTrue(form.isClosed());
    }

    @Test
    public void testErroresAlCargarDatos() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenThrow(new RuntimeException("sin institutos"));
        when(ic.listarNombresCursos()).thenThrow(new RuntimeException("sin cursos"));
        when(ic.listarCategorias()).thenThrow(new RuntimeException("sin categorias"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            AltaCurso form = nuevoForm();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar Institutos : sin institutos"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar Previas : sin cursos"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar Categorias : sin categorias"), eq("Error"), eq(JOptionPane.ERROR_MESSAGE)));
            assertFalse(((JButton) campo(form, "buttonGuardar")).isEnabled(), "Sin institutos no se puede guardar");
        }
    }
}
