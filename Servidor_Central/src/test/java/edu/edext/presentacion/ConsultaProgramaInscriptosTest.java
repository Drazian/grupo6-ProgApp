package edu.edext.presentacion;

import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * La pestaña "Inscriptos" de ConsultaPrograma muestra los estudiantes inscriptos al programa elegido.
 */
public class ConsultaProgramaInscriptosTest {

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

    private static String uid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void testInscriptosDelProgramaSeleccionado() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        String conInscriptos = "ProgIns-" + uid(), sinInscriptos = "ProgVacio-" + uid(), nick = "insc" + uid();
        for (String p : new String[]{conInscriptos, sinInscriptos})
            assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(p, "desc", LocalDate.now(), LocalDate.now(),
                    LocalDate.now().plusMonths(3), null, new HashSet<>(), "prog.png")));
        ic.crearUsuario(new edu.edext.datatypes.DtUsuario(nick, "clave123", nick + "@test.com", "Ana", "Paz", null,
                new java.util.Date(0), null, edu.edext.datatypes.TipoUsuario.ESTUDIANTE));
        assertEquals(0, ic.setInscripcionPrograma(nick, conInscriptos));

        ConsultaPrograma form = new ConsultaPrograma();
        JComboBox<String> cb = campo(form, "cbPrograma");
        JTable tabla = campo(form, "tblInscriptos");

        cb.setSelectedItem(conInscriptos);
        assertEquals(1, tabla.getRowCount());
        assertEquals(nick + " (Ana Paz)", tabla.getValueAt(0, 0));
        assertEquals(Utils.Fecha.assignFormato(LocalDate.now()), tabla.getValueAt(0, 1));
        assertEquals("Inscripto", tabla.getValueAt(0, 2));
        assertFalse(tabla.isCellEditable(0, 0));

        // Al cambiar de programa la tabla se reemplaza, no se acumula
        cb.setSelectedItem(sinInscriptos);
        assertEquals(0, tabla.getRowCount());
    }
}
