package edu.edext.presentacion;

import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de la ventana principal (index) de la Estacion de Trabajo. El JFrame se
 * crea pero nunca se hace visible; los formularios se abren dentro de su
 * JDesktopPane. main() se ejecuta sin crear ninguna ventana real.
 */
public class IndexTest {

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

    private static Set<String> titulos(JDesktopPane escritorio) {
        Set<String> ret = new HashSet<>();
        for (JInternalFrame f : escritorio.getAllFrames()) ret.add(f.getTitle());
        return ret;
    }

    @Test
    public void testMenusAbrenFormularios() throws Exception {
        index ventana = new index();
        try {
            assertFalse(ventana.isVisible());
            JDesktopPane escritorio = campo(ventana, "dpIndex");
            String[] menus = {"miInstituto", "miAltaCategoria", "miAltaUsuario", "miConsultaUsuario",
                "miModificarUsuario", "altaCurso", "consultaCurso", "crearEdicion", "jmInscripcionEdicion",
                "jmConsultaEdicion", "jmCrearPrograma", "miAgregarPrograma", "miConsultaPrograma", "jmverArbol"};
            for (String menu : menus) ((JMenuItem) campo(ventana, menu)).doClick();

            assertEquals(menus.length, escritorio.getAllFrames().length, "Cada menu abre un formulario");
            Set<String> abiertos = titulos(escritorio);
            for (String titulo : new String[]{"Institutos", "Categorias", "Nuevo Usuario", "Consulta Usuario",
                "Modificar Usuario", "Agregar curso a programas", "Consultar programas", "Consulta de Edicion de Curso",
                "Inscripcion a Edicion de Curso", "Crear Programa de Formacion", "Arbol", "Alta de Edición de Curso"})
                assertTrue(abiertos.contains(titulo), "Falta el formulario " + titulo + " en " + abiertos);

            // Los formularios son unicos: volver a elegir el menu no abre otro
            ((JMenuItem) campo(ventana, "jmConsultaEdicion")).doClick();
            assertEquals(menus.length, escritorio.getAllFrames().length);
        } finally {
            ventana.dispose();
        }
    }

    @Test
    public void testCargarDatosDePrueba() throws Exception {
        index ventana = new index();
        JMenuItem cargar = campo(ventana, "jmcargarDatosPrueba");
        IControlador ic = mock(IControlador.class);
        when(ic.cargarDatosDePrueba()).thenReturn(0, 1, -1, 7).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        dialogos.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(JOptionPane.NO_OPTION, JOptionPane.YES_OPTION);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);

            // Respuesta "No": no se cargan datos ni se muestra resultado
            cargar.doClick();
            verify(ic, never()).cargarDatosDePrueba();
            dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()), never());

            // Respuesta "Si" con cada resultado posible del controlador
            for (int i = 0; i < 5; i++) cargar.doClick();
        } finally {
            ventana.dispose();
        }
        verify(ic, times(5)).cargarDatosDePrueba();
        dialogos.verify(() -> JOptionPane.showConfirmDialog(eq(ventana), eq("¿Desea cargar los datos de prueba?"),
                eq("Cargar datos de prueba"), eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.QUESTION_MESSAGE)), times(6));
        dialogos.verify(() -> JOptionPane.showMessageDialog(ventana, "Los datos de prueba fueron cargados correctamente.",
                "Datos de prueba", JOptionPane.INFORMATION_MESSAGE));
        dialogos.verify(() -> JOptionPane.showMessageDialog(ventana, "Los datos de prueba ya fueron cargados.",
                "Error al cargar Datos de Prueba", JOptionPane.ERROR_MESSAGE));
        // -1 devuelto por el controlador y excepcion del controlador (el helper la convierte en -1)
        dialogos.verify(() -> JOptionPane.showMessageDialog(ventana, "Error Critico al insertar registros en la Base de Datos",
                "Error al cargar Datos de Prueba", JOptionPane.ERROR_MESSAGE), times(2));
        // Codigo desconocido: mensaje vacio
        dialogos.verify(() -> JOptionPane.showMessageDialog(ventana, "",
                "Error al cargar Datos de Prueba", JOptionPane.ERROR_MESSAGE));
    }

    @Test
    public void testArrastrarVentanaDesdeElEscritorio() throws Exception {
        index ventana = new index();
        try {
            JDesktopPane escritorio = campo(ventana, "dpIndex");
            ventana.setLocation(-5000, -5000);
            escritorio.dispatchEvent(new MouseEvent(escritorio, MouseEvent.MOUSE_PRESSED, 0, 0, 3, 4, 1, false));
            escritorio.dispatchEvent(new MouseEvent(escritorio, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
            assertNotEquals(new Point(-5000, -5000), ventana.getLocation(), "Arrastrar el escritorio mueve la ventana");
            assertFalse(ventana.isVisible());
        } finally {
            ventana.dispose();
        }
    }

    @Test
    public void testMainSinAbrirVentanas() throws Exception {
        LookAndFeelInfo metal = new LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel");
        LookAndFeelInfo nimbus = new LookAndFeelInfo("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Deque<LookAndFeelInfo[]> instalados = new ArrayDeque<>(List.of(
                new LookAndFeelInfo[]{metal, nimbus},   // encuentra Nimbus despues de Metal
                new LookAndFeelInfo[]{metal},           // no hay Nimbus: termina el recorrido
                new LookAndFeelInfo[]{nimbus}));        // setLookAndFeel falla
        List<String> aplicados = new ArrayList<>();
        List<Runnable> tareas = new ArrayList<>();

        // Solo se reemplazan los metodos usados por main; el resto de UIManager/EventQueue es real.
        try (MockedStatic<UIManager> ui = mockStatic(UIManager.class, inv -> switch (inv.getMethod().getName()) {
                    case "getInstalledLookAndFeels" -> instalados.poll();
                    case "setLookAndFeel" -> {
                        if (instalados.isEmpty()) throw new ClassNotFoundException("LookAndFeel no disponible");
                        aplicados.add((String) inv.getArgument(0));
                        yield null;
                    }
                    default -> inv.callRealMethod();
                });
             MockedStatic<EventQueue> eventos = mockStatic(EventQueue.class, inv -> {
                    if ("invokeLater".equals(inv.getMethod().getName())) {
                        tareas.add(inv.getArgument(0));
                        return null;
                    }
                    return inv.callRealMethod();
                })) {
            index.main(new String[0]);
            index.main(new String[0]);
            index.main(new String[0]);
        }
        assertEquals(List.of(nimbus.getClassName()), aplicados);
        assertEquals(3, tareas.size(), "Cada main agenda la creacion de la ventana en el EDT");

        // La tarea agendada crea el index y lo hace visible: se ejecuta con el constructor mockeado
        try (MockedConstruction<index> ventanas = mockConstruction(index.class)) {
            tareas.get(0).run();
            assertEquals(1, ventanas.constructed().size());
            verify(ventanas.constructed().get(0)).setVisible(true);
        }
    }

    @Test
    public void testMainAvisaSiNoSePuedenPublicarLosWebServices() {
        List<Runnable> tareas = new ArrayList<>();
        try (MockedStatic<UIManager> ui = mockStatic(UIManager.class, inv -> "getInstalledLookAndFeels".equals(inv.getMethod().getName())
                    ? new LookAndFeelInfo[0] : inv.callRealMethod());
             MockedStatic<EventQueue> eventos = mockStatic(EventQueue.class, inv -> {
                    if ("invokeLater".equals(inv.getMethod().getName())) {
                        tareas.add(inv.getArgument(0));
                        return null;
                    }
                    return inv.callRealMethod();
                });
             MockedStatic<edu.edext.webservices.ServidorWebServices> ws = mockStatic(edu.edext.webservices.ServidorWebServices.class)) {
            ws.when(edu.edext.webservices.ServidorWebServices::publicar)
                    .thenThrow(new jakarta.xml.ws.WebServiceException("Address already in use: bind"));
            index.main(new String[0]);
        }
        dialogos.verify(() -> JOptionPane.showMessageDialog(isNull(), contains("No se pudieron publicar los Web Services"),
                eq("Web Services"), eq(JOptionPane.WARNING_MESSAGE)));
        dialogos.verify(() -> JOptionPane.showMessageDialog(isNull(), contains("Address already in use"),
                anyString(), anyInt()));
        assertEquals(1, tareas.size(), "La Estacion de Trabajo arranca igual");
    }
}
