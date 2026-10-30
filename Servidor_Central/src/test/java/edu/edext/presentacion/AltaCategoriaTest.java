package edu.edext.presentacion;

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
 * Tests del formulario AltaCategoria. Los JOptionPane se reemplazan con Mockito
 * para que no se abra ninguna ventana durante los tests.
 */
public class AltaCategoriaTest {

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
        return "CAT-TEST-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void testCrearCategoriaNombreVacio() throws Exception {
        AltaCategoria form = new AltaCategoria();
        JTextField txtNombre = campo(form, "txtNombre");
        txtNombre.setText("   ");

        ((JButton) campo(form, "btnCrear")).doClick();

        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Debe elegir un nombre"), anyString(), eq(JOptionPane.WARNING_MESSAGE)));
    }

    @Test
    public void testCrearCategoriaOkYDuplicada() throws Exception {
        AltaCategoria form = new AltaCategoria();
        JTextField txtNombre = campo(form, "txtNombre");
        JTable tbl = campo(form, "tbl");
        String nombre = nombreUnico();

        txtNombre.setText(nombre);
        ((JButton) campo(form, "btnCrear")).doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("creado con éxito"), anyString(), eq(JOptionPane.INFORMATION_MESSAGE)));
        assertEquals("", txtNombre.getText(), "El campo se limpia despues de crear");
        boolean enTabla = false;
        for (int i = 0; i < tbl.getRowCount(); i++) enTabla |= nombre.equals(tbl.getValueAt(i, 0));
        assertTrue(enTabla, "La categoria creada aparece en la tabla");

        // Repetir el mismo nombre: el controlador lanza excepcion y el formulario la muestra
        txtNombre.setText(nombre);
        ((JButton) campo(form, "btnCrear")).doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Error al guardar"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
    }

    @Test
    public void testActualizarLimpiaElNombre() throws Exception {
        AltaCategoria form = new AltaCategoria();
        JTextField txtNombre = campo(form, "txtNombre");
        txtNombre.setText("algo");
        ((JButton) campo(form, "btnActualizar")).doClick();
        assertEquals("", txtNombre.getText());
    }

    @Test
    public void testClicEnTabla() throws Exception {
        AltaCategoria form = new AltaCategoria();
        JTable tbl = campo(form, "tbl");
        JTextField txtNombre = campo(form, "txtNombre");
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"CatSeleccionada"});
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
        assertEquals("CatSeleccionada", txtNombre.getText());

        // Modelo de la tabla: tipo de columna y no editable
        assertEquals(String.class, model.getColumnClass(0));
        assertFalse(model.isCellEditable(0, 0));
    }

    @Test
    public void testArrastrarFormulario() throws Exception {
        AltaCategoria form = new AltaCategoria();
        JInternalFrame ventana = new JInternalFrame();
        ventana.add(form);
        form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_PRESSED, 0, 0, 5, 5, 1, false));
        form.dispatchEvent(new MouseEvent(form, MouseEvent.MOUSE_DRAGGED, 0, 0, 10, 10, 1, false));
        assertNotNull(ventana.getLocation());
    }

    @Test
    public void testErrorAlCargarTabla() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarCategorias()).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            AltaCategoria form = new AltaCategoria();
            dialogos.verify(() -> JOptionPane.showMessageDialog(eq(form), contains("Error al actualizar la tabla: sin conexion"), anyString(), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }
}
