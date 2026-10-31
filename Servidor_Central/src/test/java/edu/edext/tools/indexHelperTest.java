package edu.edext.tools;

import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de indexHelper sobre un JDesktopPane real que nunca se muestra en pantalla
 * (los JInternalFrame son componentes livianos, no abren ventanas del sistema).
 */
public class indexHelperTest {

    /** Paneles de prueba: cada clase distinta cuenta como un formulario distinto. */
    public static class PanelA extends JPanel {}
    public static class PanelB extends JPanel {}
    public static class FrameA extends JInternalFrame {
        public FrameA() { super("Titulo propio", true, true, true, true); }
    }
    public static class FrameB extends JInternalFrame {}
    public static class PanelQueFalla extends JPanel {
        @Override
        public Dimension getPreferredSize() { throw new IllegalStateException("falla a proposito"); }
    }

    private JDesktopPane escritorio;
    private indexHelper helper;

    @BeforeEach
    public void setUp() {
        escritorio = new JDesktopPane();
        helper = new indexHelper(escritorio);
    }

    private JInternalFrame frameCon(Class<?> clasePanel) {
        for (JInternalFrame f : escritorio.getAllFrames())
            if (f.getContentPane().getComponentCount() > 0 && f.getContentPane().getComponent(0).getClass().equals(clasePanel))
                return f;
        return null;
    }

    private int cantidad(Class<?> clasePanel) {
        int n = 0;
        for (JInternalFrame f : escritorio.getAllFrames())
            if (f.getContentPane().getComponentCount() > 0 && f.getContentPane().getComponent(0).getClass().equals(clasePanel))
                n++;
        return n;
    }

    @Test
    public void testCargarPanelUnicoNoSeDuplica() {
        PanelA panel = new PanelA();
        helper.cargarPanel("Uno", panel, 300, 200);
        JInternalFrame frame = frameCon(PanelA.class);
        assertNotNull(frame);
        assertEquals("Uno", frame.getTitle());
        assertTrue(frame.isClosable());
        assertTrue(frame.isVisible());
        assertSame(panel, frame.getContentPane().getComponent(0));

        // Otro panel de la misma clase: no se agrega uno nuevo
        helper.cargarPanel("Dos", new PanelA(), 300, 200);
        assertEquals(1, cantidad(PanelA.class));
        assertEquals("Uno", frame.getTitle());

        // Cerrar el frame lo quita del escritorio
        frame.dispose();
        assertEquals(0, cantidad(PanelA.class));
        assertEquals(0, frame.getContentPane().getComponentCount());
    }

    @Test
    public void testSobrecargasDeCargarPanel() {
        helper.cargarPanel(new PanelA());
        assertEquals(1, escritorio.getAllFrames().length);
        assertNull(frameCon(PanelA.class).getTitle());

        helper.cargarPanel("B", new PanelB());
        helper.cargarPanel(new PanelB(), true);
        assertEquals(1, cantidad(PanelB.class));

        // Sin unico: se agregan aunque ya exista uno igual
        helper.cargarPanel(new PanelB(), false);
        helper.cargarPanel("B2", new PanelB(), false);
        helper.cargarPanel("B3", new PanelB(), false, true);
        helper.cargarPanel("B4", new PanelB(), false, true, 100, 100);
        helper.cargarPanel("B5", new PanelB(), false, true, false, true, true, true);
        assertEquals(6, cantidad(PanelB.class));
    }

    @Test
    public void testModoArrastreOutline() {
        assertEquals(JDesktopPane.LIVE_DRAG_MODE, escritorio.getDragMode());
        helper.cargarPanel("A", new PanelA(), true, false);
        assertEquals(JDesktopPane.OUTLINE_DRAG_MODE, escritorio.getDragMode());

        JDesktopPane otro = new JDesktopPane();
        new indexHelper(otro).cargarPanel(null, new FrameA(), false, false);
        assertEquals(JDesktopPane.OUTLINE_DRAG_MODE, otro.getDragMode());
    }

    @Test
    public void testCargarJInternalFrame() {
        FrameA conTitulo = new FrameA();
        helper.cargarPanel("Nuevo titulo", conTitulo);
        assertEquals("Nuevo titulo", conTitulo.getTitle());
        assertSame(escritorio, conTitulo.getParent());
        assertTrue(conTitulo.isVisible());
        assertTrue(conTitulo.isResizable());
        assertTrue(conTitulo.isIconifiable());

        FrameA sinTitulo = new FrameA();
        helper.cargarPanel(sinTitulo, false);
        assertEquals("Titulo propio", sinTitulo.getTitle());

        // Cerrar lo quita del escritorio
        conTitulo.dispose();
        assertNotSame(escritorio, conTitulo.getParent());
        assertEquals(1, escritorio.getAllFrames().length);
    }

    @Test
    public void testJInternalFrameUnicoNoSeDuplica() throws Exception {
        FrameA primero = new FrameA();
        helper.cargarPanel("A", primero);
        // Un JPanel queda dentro de un JInternalFrame comun, que no cuenta como FrameA
        helper.cargarPanel(new PanelA());
        primero.setIcon(true);

        // Otro frame de la misma clase: no se agrega y se restaura el que ya estaba
        FrameA segundo = new FrameA();
        helper.cargarPanel("A otra vez", segundo);
        assertNull(segundo.getParent());
        assertFalse(primero.isIcon());
        assertEquals("A", primero.getTitle());
        assertEquals(2, escritorio.getAllFrames().length);

        // Un frame de otra clase si se agrega
        helper.cargarPanel("B", new FrameB());
        assertEquals(3, escritorio.getAllFrames().length);

        // Sin unico se agrega aunque ya exista uno igual
        helper.cargarPanel(new FrameA(), false);
        assertEquals(4, escritorio.getAllFrames().length);
    }

    @Test
    public void testComponenteQueNoEsPanelNiFrameNoSeAgrega() {
        // Un frame con el contenido vacio y otro con un panel de otra clase en el escritorio
        escritorio.add(new JInternalFrame());
        helper.cargarPanel("B", new PanelB());
        helper.cargarPanel("Etiqueta", new JLabel("x"));
        helper.cargarPanel("Etiqueta", new JLabel("x"), false);
        assertEquals(2, escritorio.getAllFrames().length);
    }

    @Test
    public void testPanelMinimizadoSeRestaura() throws Exception {
        helper.cargarPanel("A", new PanelA());
        JInternalFrame frame = frameCon(PanelA.class);
        frame.setIcon(true);
        assertTrue(frame.isIcon());

        helper.cargarPanel("A", new PanelA());
        assertFalse(frame.isIcon());
        assertEquals(1, cantidad(PanelA.class));
    }

    @Test
    public void testPanelMinimizadoQueNoSePuedeRestaurar() throws Exception {
        helper.cargarPanel("A", new PanelA());
        JInternalFrame frame = frameCon(PanelA.class);
        frame.setIcon(true);
        frame.addVetoableChangeListener(evt -> {
            if (JInternalFrame.IS_ICON_PROPERTY.equals(evt.getPropertyName()))
                throw new PropertyVetoException("no se restaura", evt);
        });

        helper.cargarPanel("A", new PanelA());
        assertTrue(frame.isIcon(), "El veto se ignora y el frame sigue minimizado");
        assertEquals(1, cantidad(PanelA.class));
    }

    @Test
    public void testErrorAlCargarPanelSeIgnora() {
        assertDoesNotThrow(() -> helper.cargarPanel("Falla", new PanelQueFalla()));
        assertEquals(0, escritorio.getComponentCount());
    }

    @Test
    public void testScrollYLimites() {
        helper.assignScroll();
        // Un componente que no es JInternalFrame no recibe el listener de movimiento
        escritorio.add(new JLabel("etiqueta"));

        helper.cargarPanel("A", new PanelA());
        helper.cargarPanel("B", new PanelB());
        JInternalFrame a = frameCon(PanelA.class), b = frameCon(PanelB.class);
        a.setBounds(0, 0, 400, 300);
        b.setBounds(10, 10, 50, 50);

        ComponentListener movimiento = null;
        for (ComponentListener l : a.getComponentListeners())
            if (l.getClass().getName().startsWith(indexHelper.class.getName())) movimiento = l;
        assertNotNull(movimiento, "assignScroll agrega un listener a cada JInternalFrame");

        // Con el scroll inactivo no cambia el tamano preferido
        escritorio.setPreferredSize(new Dimension(1, 1));
        movimiento.componentMoved(new ComponentEvent(a, ComponentEvent.COMPONENT_MOVED));
        assertEquals(new Dimension(1, 1), escritorio.getPreferredSize());

        helper.setScroll(true);
        assertTrue(helper.isScrollActive());
        movimiento.componentResized(new ComponentEvent(a, ComponentEvent.COMPONENT_RESIZED));
        assertEquals(new Dimension(400, 300), escritorio.getPreferredSize());

        // Un frame que queda mas a la derecha/abajo agranda el area
        b.setBounds(500, 600, 50, 50);
        movimiento.componentMoved(new ComponentEvent(a, ComponentEvent.COMPONENT_MOVED));
        assertEquals(new Dimension(550, 650), escritorio.getPreferredSize());

        helper.setScroll(false);
        assertFalse(helper.isScrollActive());

        // Limite minimo al arrastrar
        DesktopManager dm = escritorio.getDesktopManager();
        assertFalse(helper.isLimiteMin());
        dm.dragFrame(a, -20, -30);
        assertEquals(-20, a.getX());
        assertEquals(-30, a.getY());

        helper.setLimiteMin(true);
        assertTrue(helper.isLimiteMin());
        dm.dragFrame(a, -20, -30);
        assertEquals(0, a.getX());
        assertEquals(0, a.getY());
        dm.dragFrame(a, 15, 25);
        assertEquals(15, a.getX());
        assertEquals(25, a.getY());
    }

    @Test
    public void testSetScrollFalseRestauraElTamanoOriginal() {
        escritorio.setPreferredSize(new Dimension(640, 480));
        helper.cargarPanel("A", new PanelA());
        escritorio.setPreferredSize(new Dimension(10, 10));
        helper.setScroll(false);
        assertEquals(new Dimension(640, 480), escritorio.getPreferredSize());
    }

    @Test
    public void testCargarDatosDePrueba() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.cargarDatosDePrueba()).thenReturn(7).thenThrow(new Exception("ya cargados"));
        Fabrica f = mock(Fabrica.class);
        when(f.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> fabrica = mockStatic(Fabrica.class)) {
            fabrica.when(Fabrica::getInstance).thenReturn(f);
            assertEquals(7, helper.cargarDatosdePrueba());
            assertEquals(-1, helper.cargarDatosdePrueba());
        }
    }
}
