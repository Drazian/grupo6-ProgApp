package edu.edext.tools;

import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de cronometroHelper. Se usa un intervalo muy largo para que el Timer
 * real nunca dispare durante el test; la accion del Timer se invoca a mano.
 */
public class cronometroHelperTest {

    private static final int LARGO = 600_000;
    private cronometroHelper crono;

    @AfterEach
    public void tearDown() {
        if (crono != null) crono.kill();
    }

    private static Timer timer(cronometroHelper c) throws Exception {
        Field f = cronometroHelper.class.getDeclaredField("cronoTimer");
        f.setAccessible(true);
        return (Timer) f.get(c);
    }

    @Test
    public void testAccionDelTimerAlternaVisibilidad() throws Exception {
        JPanel panel = new JPanel();
        crono = new cronometroHelper(LARGO, panel, new JLabel(), new JButton(), new JList<String>());
        ActionListener accion = timer(crono).getActionListeners()[0];
        panel.setVisible(true);
        accion.actionPerformed(null);
        assertFalse(panel.isVisible());
        accion.actionPerformed(null);
        assertTrue(panel.isVisible());
    }

    @Test
    public void testAlarmaConMensajes() throws Exception {
        JPanel panel = new JPanel();
        JLabel mensaje = new JLabel();
        crono = new cronometroHelper(LARGO, panel, mensaje);
        Timer t = timer(crono);

        assertTrue(crono.isEmpty());
        crono.alarm(true, "A");
        assertTrue(t.isRunning());
        assertTrue(panel.isVisible());
        assertEquals("A", mensaje.getText());

        // Repetir una alerta no la duplica ni cambia el mensaje
        mensaje.setText("otro");
        crono.alarm(true, "A");
        assertEquals("otro", mensaje.getText());
        assertEquals(1, crono.size());

        crono.alarm(true, "B");
        assertEquals("B", mensaje.getText());
        assertArrayEquals(new String[]{"A", "B"}, crono.getLista());
        assertTrue(crono.contains("B"));
        assertEquals("A", crono.getFirst());
        assertEquals("B", crono.getLast());

        // Quitar la ultima muestra la anterior
        crono.alarm(false, "B");
        assertEquals("A", mensaje.getText());
        assertFalse(crono.contains("B"));

        // Quitar una alerta que no existe no cambia la lista
        crono.alarm(false, "X");
        assertEquals(1, crono.size());
        assertTrue(t.isRunning());

        // Quitar la ultima detiene el cronometro y oculta el componente
        crono.alarm(false, "A");
        assertTrue(crono.isEmpty());
        assertFalse(t.isRunning());
        assertEquals("", mensaje.getText());
        assertFalse(panel.isVisible());

        // Con la lista vacia tambien se detiene
        crono.alarm(false, "Z");
        assertFalse(t.isRunning());
        assertFalse(panel.isVisible());
    }

    @Test
    public void testAlarmSinParametrosAlterna() throws Exception {
        JLabel mensaje = new JLabel();
        crono = new cronometroHelper(LARGO, new JPanel(), mensaje, new JButton());
        Timer t = timer(crono);
        crono.alarm();
        assertTrue(t.isRunning());
        assertTrue(crono.contains(""));
        crono.alarm();
        assertFalse(t.isRunning());
        assertTrue(crono.isEmpty());
    }

    @Test
    public void testSinEtiquetaDeMensaje() throws Exception {
        JPanel panel = new JPanel();
        crono = new cronometroHelper(LARGO, panel, new JButton());
        Timer t = timer(crono);
        crono.alarm(true, "A");
        crono.alarm(true, "B");
        assertTrue(t.isRunning());

        // Quitar una alerta cuando quedan otras: no hay etiqueta que actualizar y el cronometro sigue
        assertDoesNotThrow(() -> crono.alarm(false, "B"));
        assertArrayEquals(new String[]{"A"}, crono.getLista());
        assertTrue(t.isRunning());
        assertTrue(panel.isVisible());

        crono.alarm(false, "A");
        assertFalse(t.isRunning());
        assertFalse(panel.isVisible());
    }

    @Test
    public void testDeshabilitadoMataElCronometro() throws Exception {
        JPanel panel = new JPanel();
        crono = new cronometroHelper(LARGO, panel, new JLabel());
        Timer t = timer(crono);
        crono.alarm(true, "A");
        assertTrue(t.isRunning());

        crono.enabled(false);
        crono.alarm(true, "B");
        assertTrue(crono.isEmpty());
        assertFalse(t.isRunning());
        assertFalse(panel.isVisible());

        // kill deja el cronometro deshabilitado
        crono.enabled(true);
        crono.kill();
        crono.alarm(true, "C");
        assertTrue(crono.isEmpty());
    }

    @Test
    public void testArrayNull() {
        crono = new cronometroHelper(LARGO, new JPanel(), new JLabel());
        assertNull(crono.ArrayNull());
    }
}
