package edu.edext.presentacion;

import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
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
 * Tests del formulario ConsultaUsuario contra la base H2 de los tests (datos de prueba).
 * Los JOptionPane se reemplazan con Mockito para que no se abra ninguna ventana; las ventanas
 * internas que abre el formulario se agregan a un JDesktopPane que nunca se muestra.
 */
public class ConsultaUsuarioTest {

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
    private static <T> T campo(Object obj, String nombre) throws Exception {
        Field f = obj.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return (T) f.get(obj);
    }

    private static void procesarEventos() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }

    private static MouseEvent clic(Component c) {
        return new MouseEvent(c, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false);
    }

    private void sinErrores() {
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()), never());
    }

    /** Selecciona en la lista el primer usuario que cumple la condicion y devuelve su indice. */
    private static int seleccionar(ConsultaUsuario form, Predicate<DtUsuario> condicion) throws Exception {
        JList<String> lis = campo(form, "lisUsuario");
        List<DtUsuario> usuarios = campo(form, "usuarios");
        for (int i = 0; i < usuarios.size(); i++) {
            if (condicion.test(usuarios.get(i))) {
                lis.setSelectedIndex(i);
                return i;
            }
        }
        fail("No hay usuario que cumpla la condicion en los datos de prueba");
        return -1;
    }

    /** Recorre los usuarios hasta que la tabla indicada tenga filas (deja seleccionado ese usuario). */
    private static void seleccionarConFilas(ConsultaUsuario form, String tabla) throws Exception {
        JList<String> lis = campo(form, "lisUsuario");
        JTable tbl = campo(form, tabla);
        List<DtUsuario> usuarios = campo(form, "usuarios");
        for (int i = 0; i < usuarios.size(); i++) {
            if (tabla.equals("tblCursos") && usuarios.get(i).getTipoUsuario() != TipoUsuario.DOCENTE) continue;
            lis.setSelectedIndex(i);
            if (tbl.getRowCount() > 0) return;
        }
        fail("Ningun usuario de los datos de prueba tiene filas en " + tabla);
    }

    private static String nickEnFormulario(ConsultaUsuario form) throws Exception {
        AltaUsuario alta = campo(form, "formularioUsuario");
        return ((JTextField) campo(alta, "txtNickname")).getText();
    }

    /** Arma JDesktopPane > JInternalFrame > formulario, como en la aplicacion. */
    private static JDesktopPane enEscritorio(ConsultaUsuario form) {
        return enEscritorio(form, new JDesktopPane());
    }

    private static JDesktopPane enEscritorio(ConsultaUsuario form, JDesktopPane desktop) {
        JInternalFrame marco = new JInternalFrame();
        marco.add(form);
        desktop.add(marco);
        return desktop;
    }

    private static JInternalFrame ventanaConTitulo(JDesktopPane desktop, String titulo) {
        for (JInternalFrame f : desktop.getAllFrames())
            if (titulo.equals(f.getTitle())) return f;
        return null;
    }

    /** Ejecuta el listener de foco del formulario sobre la lista (ganar o perder el foco). */
    private static void foco(ConsultaUsuario form, boolean ganado) throws Exception {
        JList<String> lis = campo(form, "lisUsuario");
        int llamados = 0;
        for (FocusListener l : lis.getFocusListeners()) {
            if (!l.getClass().getName().startsWith(ConsultaUsuario.class.getName())) continue;
            if (ganado) l.focusGained(new FocusEvent(lis, FocusEvent.FOCUS_GAINED));
            else l.focusLost(new FocusEvent(lis, FocusEvent.FOCUS_LOST));
            llamados++;
        }
        assertEquals(1, llamados);
    }

    private static int indiceDe(ConsultaUsuario form, String nickname) throws Exception {
        List<DtUsuario> usuarios = campo(form, "usuarios");
        for (int i = 0; i < usuarios.size(); i++)
            if (nickname.equals(usuarios.get(i).getNickname())) return i;
        return -1;
    }

    private static String crearEstudiante(String nombre, String apellido) throws Exception {
        String nick = "cu" + UUID.randomUUID().toString().substring(0, 8);
        Fabrica.getInstance().getIControlador().crearUsuario(new DtUsuario(nick, "secreta", nick + "@test.com",
                nombre, apellido, null, new Date(0), null, TipoUsuario.ESTUDIANTE));
        return nick;
    }

    // ------------------------------------------------------------------- tests

    @Test
    public void testListaDeUsuarios() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JList<String> lis = campo(form, "lisUsuario");
        List<DtUsuario> usuarios = campo(form, "usuarios");
        assertFalse(usuarios.isEmpty());
        assertEquals(usuarios.size(), lis.getModel().getSize());
        for (int i = 0; i < usuarios.size(); i++)
            assertEquals(usuarios.get(i).getNombre() + " " + usuarios.get(i).getApellido(), lis.getModel().getElementAt(i));
        assertEquals(-1, lis.getSelectedIndex());
        assertEquals("", nickEnFormulario(form));
        sinErrores();
    }

    @Test
    public void testSeleccionarCadaUsuario() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JList<String> lis = campo(form, "lisUsuario");
        List<DtUsuario> usuarios = campo(form, "usuarios");
        JTabbedPane tabs = campo(form, "tbCursosYProgramas");
        JPanel panCursos = campo(form, "panCursos");
        JTable tblCursos = campo(form, "tblCursos");
        JTable tblEdiciones = campo(form, "tblEdicionDeCursos");
        JTable tblProgramas = campo(form, "tblProgramas");
        IControlador ic = Fabrica.getInstance().getIControlador();

        int cursos = 0, ediciones = 0, programas = 0;
        for (int i = 0; i < usuarios.size(); i++) {
            DtUsuario u = usuarios.get(i);
            lis.setSelectedIndex(i);
            assertEquals(u.getNickname(), nickEnFormulario(form));
            assertEquals(ic.listarEdicionesPorUsuario(u.getNickname()).size(), tblEdiciones.getRowCount());
            assertEquals(ic.listarProgramasPorUsuario(u.getNickname()).size(), tblProgramas.getRowCount());
            ediciones += tblEdiciones.getRowCount();
            programas += tblProgramas.getRowCount();
            if (u.getTipoUsuario() == TipoUsuario.ESTUDIANTE) {
                assertEquals(-1, tabs.indexOfComponent(panCursos), "Un estudiante no tiene pestaña de cursos");
                assertEquals(2, tabs.getTabCount());
                assertEquals(0, tblCursos.getRowCount(), "Un estudiante no conserva los cursos del usuario anterior");
            } else {
                assertEquals(0, tabs.indexOfComponent(panCursos));
                assertEquals(3, tabs.getTabCount());
                assertEquals(ic.listarCursosPorUsuario(u.getNickname()).size(), tblCursos.getRowCount());
                cursos += tblCursos.getRowCount();
            }
        }
        assertTrue(cursos > 0 && ediciones > 0 && programas > 0, "Los datos de prueba llenan las tres tablas");

        // Docente despues de un estudiante: vuelve la pestaña de cursos
        seleccionar(form, u -> u.getTipoUsuario() == TipoUsuario.ESTUDIANTE);
        assertEquals(-1, tabs.indexOfComponent(panCursos));
        seleccionar(form, u -> u.getTipoUsuario() == TipoUsuario.DOCENTE);
        assertEquals(0, tabs.indexOfComponent(panCursos));
        assertEquals("Cursos", tabs.getTitleAt(0));
        sinErrores();
    }

    @Test
    public void testSeleccionAjustandoYSinSeleccion() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JList<String> lis = campo(form, "lisUsuario");
        List<DtUsuario> usuarios = campo(form, "usuarios");
        AltaUsuario inicial = campo(form, "formularioUsuario");

        // Mientras la seleccion se esta ajustando no se carga nada
        lis.setValueIsAdjusting(true);
        lis.setSelectedIndex(0);
        assertSame(inicial, campo(form, "formularioUsuario"));
        lis.setValueIsAdjusting(false);
        assertNotSame(inicial, campo(form, "formularioUsuario"));
        assertEquals(usuarios.get(0).getNickname(), nickEnFormulario(form));
        assertEquals(usuarios.get(0).getNickname(), campo(form, "selected"), "Se recuerda el nickname del seleccionado");

        // Sin seleccion: formulario vacio
        lis.clearSelection();
        assertEquals("", nickEnFormulario(form));
        sinErrores();
    }

    @Test
    public void testFocoSinCambiosNoRecarga() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JList<String> lis = campo(form, "lisUsuario");
        List<DtUsuario> usuarios = campo(form, "usuarios");
        int indice = usuarios.size() - 1;
        lis.setSelectedIndex(indice);
        AltaUsuario detalle = campo(form, "formularioUsuario");

        // Ganar o perder el foco sin cambios en la base: no se reconstruye la lista ni se recarga el usuario
        foco(form, true);
        foco(form, false);
        assertSame(usuarios, campo(form, "usuarios"));
        assertSame(detalle, campo(form, "formularioUsuario"));
        assertEquals(indice, lis.getSelectedIndex());
        assertEquals(usuarios.get(indice).getNickname(), nickEnFormulario(form));

        // Con un usuario nuevo en la base: al ganar el foco se recarga y se mantiene el seleccionado
        String nuevo = crearEstudiante("Nuevo", "Usuario");
        foco(form, true);
        List<DtUsuario> recargados = campo(form, "usuarios");
        assertNotSame(usuarios, recargados);
        assertEquals(usuarios.size() + 1, lis.getModel().getSize());
        assertTrue(indiceDe(form, nuevo) != -1);
        assertEquals(usuarios.get(indice).getNickname(), recargados.get(lis.getSelectedIndex()).getNickname());
        assertEquals(usuarios.get(indice).getNickname(), nickEnFormulario(form));
        sinErrores();
    }

    @Test
    public void testUsuariosConElMismoNombreSeSeleccionanPorNickname() throws Exception {
        String apellido = "Repetido" + UUID.randomUUID().toString().substring(0, 4);
        String nick1 = crearEstudiante("Homonimo", apellido);
        String nick2 = crearEstudiante("Homonimo", apellido);
        ConsultaUsuario form = new ConsultaUsuario();
        JList<String> lis = campo(form, "lisUsuario");
        int i1 = indiceDe(form, nick1), i2 = indiceDe(form, nick2);
        assertEquals(lis.getModel().getElementAt(i1), lis.getModel().getElementAt(i2), "Se muestran con el mismo texto");

        // Se elige el que aparece despues: al reconstruir la lista no debe quedar seleccionado el primero
        String elegido = i1 > i2 ? nick1 : nick2;
        lis.setSelectedIndex(Math.max(i1, i2));
        assertEquals(elegido, nickEnFormulario(form));
        crearEstudiante("Otro", "Usuario");
        foco(form, true);
        assertEquals(indiceDe(form, elegido), lis.getSelectedIndex());
        assertEquals(elegido, nickEnFormulario(form));
        sinErrores();
    }

    @Test
    public void testEstudianteVaciaLaTablaDeCursos() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JTable tblCursos = campo(form, "tblCursos");
        seleccionarConFilas(form, "tblCursos");
        assertTrue(tblCursos.getRowCount() > 0);

        seleccionar(form, u -> u.getTipoUsuario() == TipoUsuario.ESTUDIANTE);
        assertEquals(0, tblCursos.getRowCount(), "No quedan los cursos del docente anterior");
        sinErrores();
    }

    @Test
    public void testClicEnFilasVaciasNoAbreNada() throws Exception {
        // Recien creado el formulario las tablas tienen filas vacias (sin usuario elegido)
        ConsultaUsuario form = new ConsultaUsuario();
        enEscritorio(form);
        try (MockedConstruction<ConsultaCurso> cc = mockConstruction(ConsultaCurso.class);
             MockedConstruction<ConsultaEdicion> ce = mockConstruction(ConsultaEdicion.class);
             MockedConstruction<ConsultaPrograma> cp = mockConstruction(ConsultaPrograma.class)) {
            for (String nombre : new String[]{"tblCursos", "tblEdicionDeCursos", "tblProgramas"}) {
                JTable t = campo(form, nombre);
                assertTrue(t.getRowCount() > 0);
                assertNull(t.getValueAt(0, 0));
                t.setRowSelectionInterval(0, 0);
                assertDoesNotThrow(() -> t.dispatchEvent(clic(t)), nombre);
            }
            assertEquals(0, cc.constructed().size() + ce.constructed().size() + cp.constructed().size());
        }
        sinErrores();
    }

    @Test
    public void testRestauraPestanasFaltantes() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JTabbedPane tabs = campo(form, "tbCursosYProgramas");
        JPanel panCursos = campo(form, "panCursos");
        JPanel panEdiciones = campo(form, "panEdicionesDeCursos");
        JPanel panProgramas = campo(form, "panProgramas");
        tabs.removeAll();

        seleccionar(form, u -> u.getTipoUsuario() == TipoUsuario.DOCENTE);
        assertEquals(0, tabs.indexOfComponent(panCursos));
        assertEquals(1, tabs.indexOfComponent(panEdiciones));
        assertEquals(2, tabs.indexOfComponent(panProgramas));
        assertEquals("Ediciones de Cursos", tabs.getTitleAt(1));
        assertEquals("Programas", tabs.getTitleAt(2));
    }

    @Test
    public void testErrorAlListarUsuarios() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarUsuarios()).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            ConsultaUsuario form = new ConsultaUsuario();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al actualizar la tabla: sin conexion"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
            assertEquals(0, ((JList<?>) campo(form, "lisUsuario")).getModel().getSize());
        }
    }

    @Test
    public void testErrorAlCargarDatosDelUsuario() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        IControlador ic = mock(IControlador.class);
        when(ic.listarEdicionesPorUsuario(anyString())).thenThrow(new Exception("falla"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            seleccionar(form, u -> u.getTipoUsuario() == TipoUsuario.DOCENTE);
        }
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al cargar los datos del usuario: falla"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
        verify(ic).listarCursosPorUsuario(anyString());
        verify(ic, never()).listarProgramasPorUsuario(anyString());
    }

    @Test
    public void testTablasNoEditables() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        for (String nombre : new String[]{"tblCursos", "tblEdicionDeCursos", "tblProgramas"}) {
            TableModel model = ((JTable) campo(form, nombre)).getModel();
            for (int c = 0; c < model.getColumnCount(); c++)
                assertFalse(model.isCellEditable(0, c), nombre + " columna " + c);
        }
    }

    @Test
    public void testClicSinFilaOSinEscritorio() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        seleccionarConFilas(form, "tblCursos");
        seleccionarConFilas(form, "tblEdicionDeCursos");
        seleccionarConFilas(form, "tblProgramas");
        JTable[] tablas = {campo(form, "tblCursos"), campo(form, "tblEdicionDeCursos"), campo(form, "tblProgramas")};
        // Si el ultimo seleccionado no es docente la tabla de cursos queda vacia: se carga una fila conocida
        ((DefaultTableModel) tablas[0].getModel()).setRowCount(0);
        ((DefaultTableModel) tablas[0].getModel()).addRow(new Object[]{Fabrica.getInstance().getIControlador().listarCursos().get(0).getNombre()});

        try (MockedConstruction<ConsultaCurso> cc = mockConstruction(ConsultaCurso.class);
             MockedConstruction<ConsultaEdicion> ce = mockConstruction(ConsultaEdicion.class);
             MockedConstruction<ConsultaPrograma> cp = mockConstruction(ConsultaPrograma.class)) {
            // Sin fila seleccionada no se abre nada
            for (JTable t : tablas) {
                t.clearSelection();
                t.dispatchEvent(clic(t));
            }
            assertEquals(0, cc.constructed().size() + ce.constructed().size() + cp.constructed().size());

            // Con fila pero el formulario no esta en un escritorio: se crea la consulta pero no se muestra
            for (JTable t : tablas) {
                t.setRowSelectionInterval(0, 0);
                t.dispatchEvent(clic(t));
            }
            assertEquals(1, cc.constructed().size());
            assertEquals(1, ce.constructed().size());
            assertEquals(1, cp.constructed().size());
            verify(cc.constructed().get(0)).cargarCurso(tablas[0].getValueAt(0, 0).toString());
            verify(cc.constructed().get(0), never()).setVisible(true);
            verify(ce.constructed().get(0)).cargarEdicion(tablas[1].getValueAt(0, 0).toString());
            verify(cp.constructed().get(0)).cargarPrograma(tablas[2].getValueAt(0, 0).toString());
        }
    }

    @Test
    public void testClicAbreConsultasEnElEscritorio() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JDesktopPane desktop = enEscritorio(form);

        seleccionarConFilas(form, "tblCursos");
        JTable tblCursos = campo(form, "tblCursos");
        tblCursos.setRowSelectionInterval(0, 0);
        tblCursos.dispatchEvent(clic(tblCursos));
        ConsultaCurso consultaCurso = null;
        for (JInternalFrame f : desktop.getAllFrames())
            if (f instanceof ConsultaCurso) consultaCurso = (ConsultaCurso) f;
        assertNotNull(consultaCurso, "Se abre la consulta del curso");
        assertTrue(consultaCurso.isVisible());

        seleccionarConFilas(form, "tblEdicionDeCursos");
        JTable tblEdiciones = campo(form, "tblEdicionDeCursos");
        tblEdiciones.setRowSelectionInterval(0, 0);
        tblEdiciones.dispatchEvent(clic(tblEdiciones));
        JInternalFrame edicion = ventanaConTitulo(desktop, "Consulta Edición");
        assertNotNull(edicion);
        assertTrue(edicion.isVisible());
        assertTrue(edicion.isMaximum());
        assertInstanceOf(ConsultaEdicion.class, edicion.getContentPane().getComponent(0));

        seleccionarConFilas(form, "tblProgramas");
        JTable tblProgramas = campo(form, "tblProgramas");
        tblProgramas.setRowSelectionInterval(0, 0);
        tblProgramas.dispatchEvent(clic(tblProgramas));
        JInternalFrame programa = ventanaConTitulo(desktop, "Consulta Programa");
        assertNotNull(programa);
        assertTrue(programa.isVisible());
        assertTrue(programa.isMaximum());
        assertInstanceOf(ConsultaPrograma.class, programa.getContentPane().getComponent(0));
        sinErrores();
    }

    @Test
    public void testClicConVentanaQueRechazaSeleccion() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        // Un mock de JInternalFrame no se puede agregar a un contenedor AWT real (no tiene tree lock):
        // el escritorio de prueba registra los mocks sin agregarlos
        List<Component> mocksAgregados = new java.util.ArrayList<>();
        JDesktopPane desktop = enEscritorio(form, new JDesktopPane() {
            @Override
            public Component add(Component c) {
                if (mockingDetails(c).isMock()) {
                    mocksAgregados.add(c);
                    return c;
                }
                return super.add(c);
            }
        });
        // Toda ventana nueva del escritorio rechaza cambios de estado (maximizar / seleccionar)
        desktop.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                if (e.getChild() instanceof JInternalFrame f)
                    f.addVetoableChangeListener(evt -> { throw new PropertyVetoException("veto", evt); });
            }
        });

        seleccionarConFilas(form, "tblEdicionDeCursos");
        JTable tblEdiciones = campo(form, "tblEdicionDeCursos");
        tblEdiciones.setRowSelectionInterval(0, 0);
        tblEdiciones.dispatchEvent(clic(tblEdiciones));
        JInternalFrame edicion = ventanaConTitulo(desktop, "Consulta Edición");
        assertNotNull(edicion);
        assertFalse(edicion.isMaximum(), "El veto se ignora y la ventana queda sin maximizar");

        seleccionarConFilas(form, "tblProgramas");
        JTable tblProgramas = campo(form, "tblProgramas");
        tblProgramas.setRowSelectionInterval(0, 0);
        tblProgramas.dispatchEvent(clic(tblProgramas));
        JInternalFrame programa = ventanaConTitulo(desktop, "Consulta Programa");
        assertNotNull(programa);
        assertFalse(programa.isMaximum());

        // ConsultaCurso solo intenta seleccionarse, y JInternalFrame ignora setSelected si no esta en pantalla:
        // se simula el veto con un mock de la ventana
        seleccionarConFilas(form, "tblCursos");
        JTable tblCursos = campo(form, "tblCursos");
        tblCursos.setRowSelectionInterval(0, 0);
        try (MockedConstruction<ConsultaCurso> cc = mockConstruction(ConsultaCurso.class,
                (m, ctx) -> doThrow(new PropertyVetoException("veto", null)).when(m).setSelected(true))) {
            tblCursos.dispatchEvent(clic(tblCursos));
            assertEquals(1, cc.constructed().size());
            assertEquals(List.of(cc.constructed().get(0)), mocksAgregados, "La consulta se agrega al escritorio");
            verify(cc.constructed().get(0)).setVisible(true);
            verify(cc.constructed().get(0)).setSelected(true);
        }
    }

    @Test
    public void testRedimensionarAjustaLasTablas() throws Exception {
        ConsultaUsuario form = new ConsultaUsuario();
        JTabbedPane tabs = campo(form, "tbCursosYProgramas");
        JTable tblProgramas = campo(form, "tblProgramas");
        // Se captura la consola: redimensionar no debe imprimir mensajes de depuracion
        PrintStream consola = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salida, true));
        try {
            form.setSize(800, 700);
            form.dispatchEvent(new ComponentEvent(form, ComponentEvent.COMPONENT_RESIZED));
            procesarEventos();
            assertEquals(800, tabs.getWidth());
            assertEquals(700 - tabs.getY(), tabs.getHeight());
            assertEquals(tabs.getSize(), tblProgramas.getPreferredSize());

            form.setSize(900, 650);
            tabs.dispatchEvent(clic(tabs));
            procesarEventos();
            assertEquals(900, tabs.getWidth());
            assertEquals(tabs.getSize(), ((JTable) campo(form, "tblCursos")).getMinimumSize());
        } finally {
            System.setOut(consola);
        }
        assertFalse(salida.toString().contains("X : "), "Sin System.out de depuracion");
    }
}
