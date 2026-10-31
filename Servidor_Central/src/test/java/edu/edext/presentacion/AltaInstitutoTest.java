package edu.edext.presentacion;

import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario AltaInstituto. Los JOptionPane se reemplazan con Mockito
 * para que no se abra ninguna ventana durante los tests.
 */
public class AltaInstitutoTest {

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

    private static String nombreUnico() {
        return "INST-TEST-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static boolean enTabla(JTable tbl, String nombre) {
        for (int i = 0; i < tbl.getRowCount(); i++)
            if (nombre.equals(tbl.getValueAt(i, 0))) return true;
        return false;
    }

    private static boolean existeEnBase(String nombre) throws Exception {
        for (DtInstituto i : Fabrica.getInstance().getIControlador().listarInstitutos())
            if (nombre.equals(i.getNombre())) return true;
        return false;
    }

    /** Selecciona con un clic la fila del instituto: copia el nombre al campo y habilita Eliminar y Actualizar. */
    private static void seleccionar(AltaInstituto form, String nombre) throws Exception {
        JTable tbl = campo(form, "tbl");
        for (int i = 0; i < tbl.getRowCount(); i++) {
            if (nombre.equals(tbl.getValueAt(i, 0))) {
                tbl.setRowSelectionInterval(i, i);
                tbl.dispatchEvent(new MouseEvent(tbl, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false));
                assertTrue(((JButton) campo(form, "btnEliminar")).isEnabled());
                assertTrue(((JButton) campo(form, "btnActualizar")).isEnabled());
                return;
            }
        }
        fail("El instituto " + nombre + " no esta en la tabla");
    }

    private static boolean habilitados(AltaInstituto form) throws Exception {
        boolean eliminar = ((JButton) campo(form, "btnEliminar")).isEnabled();
        assertEquals(eliminar, ((JButton) campo(form, "btnActualizar")).isEnabled(), "Eliminar y Actualizar van juntos");
        return eliminar;
    }

    @Test
    public void testCrearInstitutoNombreVacio() throws Exception {
        AltaInstituto form = new AltaInstituto();
        ((JTextField) campo(form, "txtNombre")).setText("   ");

        ((JButton) campo(form, "btnCrear")).doClick();

        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Debe elegir un nombre"), anyString(), eq(JOptionPane.WARNING_MESSAGE)));
    }

    @Test
    public void testCrearInstitutoOkYDuplicado() throws Exception {
        AltaInstituto form = new AltaInstituto();
        JTextField txtNombre = campo(form, "txtNombre");
        JTable tbl = campo(form, "tbl");
        String nombre = nombreUnico();

        txtNombre.setText("  " + nombre + "  ");
        ((JButton) campo(form, "btnCrear")).doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Instituto '" + nombre + "' creado con éxito."), anyString(), eq(JOptionPane.INFORMATION_MESSAGE)));
        assertEquals("", txtNombre.getText(), "El campo se limpia despues de crear");
        assertTrue(enTabla(tbl, nombre), "El instituto creado aparece en la tabla");
        assertTrue(existeEnBase(nombre));

        // Repetir el mismo nombre: el controlador lanza excepcion y el formulario la muestra
        txtNombre.setText(nombre);
        ((JButton) campo(form, "btnCrear")).doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Error al guardar en la base de datos"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testEliminarNombreVacio() throws Exception {
        String existente = nombreUnico();
        Fabrica.getInstance().getIControlador().crearInstituto(existente);
        AltaInstituto form = new AltaInstituto();
        seleccionar(form, existente);
        ((JTextField) campo(form, "txtNombre")).setText("");

        ((JButton) campo(form, "btnEliminar")).doClick();

        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Debe seleccionar un Instituto"), anyString(), eq(JOptionPane.WARNING_MESSAGE)));
        dialogos.verify(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()), never());
    }

    @Test
    public void testEliminarCanceladoYConfirmado() throws Exception {
        String nombre = nombreUnico();
        Fabrica.getInstance().getIControlador().crearInstituto(nombre);
        AltaInstituto form = new AltaInstituto();
        JTextField txtNombre = campo(form, "txtNombre");
        JTable tbl = campo(form, "tbl");
        assertTrue(enTabla(tbl, nombre));

        // Responde NO: no se elimina nada y la fila sigue seleccionada
        dialogos.when(() -> JOptionPane.showConfirmDialog(eq(form), contains(nombre), anyString(), eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.QUESTION_MESSAGE)))
                .thenReturn(JOptionPane.NO_OPTION);
        seleccionar(form, nombre);
        assertEquals(nombre, txtNombre.getText());
        ((JButton) campo(form, "btnEliminar")).doClick();
        assertTrue(existeEnBase(nombre), "Con NO el instituto sigue en la base");
        assertEquals(nombre, txtNombre.getText());
        assertTrue(habilitados(form));
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()), never());

        // Responde SI: se elimina, se avisa, se refresca la tabla y sin seleccion se deshabilitan los botones
        dialogos.when(() -> JOptionPane.showConfirmDialog(eq(form), contains(nombre), anyString(), eq(JOptionPane.YES_NO_OPTION), eq(JOptionPane.QUESTION_MESSAGE)))
                .thenReturn(JOptionPane.YES_OPTION);
        ((JButton) campo(form, "btnEliminar")).doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Instituto '" + nombre + "' eliminado con éxito."), anyString(), eq(JOptionPane.INFORMATION_MESSAGE)));
        assertFalse(existeEnBase(nombre));
        assertFalse(enTabla(tbl, nombre));
        assertEquals("", txtNombre.getText());
        assertFalse(habilitados(form));
    }

    @Test
    public void testEliminarInexistente() throws Exception {
        String existente = nombreUnico();
        Fabrica.getInstance().getIControlador().crearInstituto(existente);
        AltaInstituto form = new AltaInstituto();
        String nombre = nombreUnico();
        dialogos.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt())).thenReturn(JOptionPane.YES_OPTION);
        // Se selecciona un instituto y despues se escribe a mano otro nombre que no existe
        seleccionar(form, existente);
        ((JTextField) campo(form, "txtNombre")).setText(nombre);

        ((JButton) campo(form, "btnEliminar")).doClick();

        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Error al eliminar Instituto: No se encontró"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
        assertTrue(existeEnBase(existente));
    }

    @Test
    public void testBotonesSeHabilitanSoloConUnaFilaSeleccionada() throws Exception {
        String nombre = nombreUnico();
        Fabrica.getInstance().getIControlador().crearInstituto(nombre);
        AltaInstituto form = new AltaInstituto();
        JTable tbl = campo(form, "tbl");
        JTextField txtNombre = campo(form, "txtNombre");

        // Sin seleccion arrancan deshabilitados y un clic no hace nada
        assertFalse(habilitados(form));
        txtNombre.setText(nombre);
        ((JButton) campo(form, "btnEliminar")).doClick();
        ((JButton) campo(form, "btnActualizar")).doClick();
        dialogos.verifyNoInteractions();
        assertEquals(nombre, txtNombre.getText());

        // Fila con instituto: se habilitan
        seleccionar(form, nombre);

        // Fila vacia: se deshabilitan
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        model.addRow(new Object[]{null});
        tbl.setRowSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
        assertFalse(habilitados(form));

        // Se quita la seleccion: se deshabilitan
        seleccionar(form, nombre);
        tbl.clearSelection();
        assertFalse(habilitados(form));
    }

    @Test
    public void testActualizarRecargaTablaYLimpiaElNombre() throws Exception {
        String existente = nombreUnico();
        Fabrica.getInstance().getIControlador().crearInstituto(existente);
        AltaInstituto form = new AltaInstituto();
        JTextField txtNombre = campo(form, "txtNombre");
        JTable tbl = campo(form, "tbl");
        String nombre = nombreUnico();
        Fabrica.getInstance().getIControlador().crearInstituto(nombre);
        assertFalse(enTabla(tbl, nombre));
        seleccionar(form, existente);
        txtNombre.setText("algo");

        ((JButton) campo(form, "btnActualizar")).doClick();

        assertEquals("", txtNombre.getText());
        assertTrue(enTabla(tbl, nombre), "Actualizar recarga la tabla desde la base");
        assertFalse(habilitados(form), "La tabla recargada queda sin seleccion");
    }

    @Test
    public void testClicEnTabla() throws Exception {
        AltaInstituto form = new AltaInstituto();
        JTable tbl = campo(form, "tbl");
        JTextField txtNombre = campo(form, "txtNombre");
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"InstSeleccionado"});
        model.addRow(new Object[]{null});
        MouseEvent clic = new MouseEvent(tbl, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false);

        // Sin fila seleccionada: no cambia nada
        tbl.clearSelection();
        txtNombre.setText("previo");
        tbl.dispatchEvent(clic);
        assertEquals("previo", txtNombre.getText());

        // Fila con valor nulo: no cambia nada
        tbl.setRowSelectionInterval(1, 1);
        tbl.dispatchEvent(clic);
        assertEquals("previo", txtNombre.getText());

        // Fila con nombre: lo copia al campo
        tbl.setRowSelectionInterval(0, 0);
        tbl.dispatchEvent(clic);
        assertEquals("InstSeleccionado", txtNombre.getText());

        // Modelo de la tabla: tipo de columna y no editable
        assertEquals(String.class, model.getColumnClass(0));
        assertFalse(model.isCellEditable(0, 0));
    }

    @Test
    public void testArrastrarFormulario() throws Exception {
        AltaInstituto form = new AltaInstituto();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false));
        form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
        assertNotNull(ventana.getLocation());
    }

    @Test
    public void testErrorAlCargarTabla() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            AltaInstituto form = new AltaInstituto();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), eq("Error al actualizar la tabla: sin conexion"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }
}
