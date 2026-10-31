package edu.edext.presentacion;

import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
 * Tests del formulario ModificarUsuario: lista de usuarios de la base H2 de test
 * y carga del usuario seleccionado en el AltaUsuario embebido.
 */
public class ModificarUsuarioTest {

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

    private static void esperarEDT() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }

    @Test
    public void testListarYSeleccionarUsuario() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        String nick = "MOD-" + UUID.randomUUID().toString().substring(0, 8);
        ic.crearUsuario(new DtUsuario(nick, "secreta", nick + "@test.com", "Carla", "Suarez", null,
                new Date(0), null, TipoUsuario.ESTUDIANTE));

        ModificarUsuario form = new ModificarUsuario();
        esperarEDT();
        JList<String> lis = campo(form, "lisUsuario");
        JPanel panel = campo(form, "panModificar");
        List<DtUsuario> usuarios = campo(form, "usuarios");
        int indice = -1;
        for (int i = 0; i < usuarios.size(); i++)
            if (nick.equals(usuarios.get(i).getNickname())) indice = i;
        assertTrue(indice >= 0, "El usuario creado aparece en la lista");
        assertEquals("Carla Suarez", lis.getModel().getElementAt(indice));
        AltaUsuario inicial = campo(form, "formularioUsuario");
        assertSame(inicial, panel.getComponent(0));

        // Mientras la seleccion se esta ajustando no se hace nada
        lis.setValueIsAdjusting(true);
        lis.setSelectedIndex(indice);
        esperarEDT();
        assertSame(inicial, campo(form, "formularioUsuario"));

        // Al terminar de ajustar se reemplaza el formulario y se carga el usuario en el EDT
        lis.setValueIsAdjusting(false);
        esperarEDT();
        AltaUsuario cargado = campo(form, "formularioUsuario");
        assertNotSame(inicial, cargado);
        assertEquals(1, panel.getComponentCount());
        assertSame(cargado, panel.getComponent(0));
        assertEquals(nick, ((JTextField) campo(cargado, "txtNickname")).getText());
        assertEquals("Carla", ((JTextField) campo(cargado, "txtNombre")).getText());

        // Sin seleccion (indice -1) no se hace nada
        lis.clearSelection();
        esperarEDT();
        assertSame(cargado, campo(form, "formularioUsuario"));
        dialogos.verifyNoInteractions();
    }

    @Test
    public void testErrorAlListarUsuarios() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarUsuarios()).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            ModificarUsuario form = new ModificarUsuario();
            esperarEDT();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Error al actualizar la tabla: sin conexion"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
            assertEquals(0, ((JList<?>) campo(form, "lisUsuario")).getModel().getSize());
        }
    }
}
