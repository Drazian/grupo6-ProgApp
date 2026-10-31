package edu.edext.tools;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de jTable (envoltorio de JTable con DefaultTableModel).
 */
public class jTableTest {

    private static JTable tabla(int filas) {
        return new JTable(new DefaultTableModel(new Object[]{"A", "B"}, filas));
    }

    @Test
    public void testConstantesYColumnas() {
        JTable t = tabla(0);
        jTable helper = new jTable(t);
        assertEquals(SwingConstants.CENTER, helper.ALIGN_CENTER);
        assertEquals(SwingConstants.RIGHT, helper.ALIGN_RIGHT);
        assertEquals(SwingConstants.LEFT, helper.ALIGN_LEFT);

        helper.sizeColumn(0, 120);
        assertEquals(120, t.getColumnModel().getColumn(0).getPreferredWidth());

        helper.sizeColumn(1, 80, helper.ALIGN_RIGHT);
        assertEquals(80, t.getColumnModel().getColumn(1).getPreferredWidth());
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) t.getColumnModel().getColumn(1).getCellRenderer();
        assertEquals(SwingConstants.RIGHT, r.getHorizontalAlignment());

        helper.alignment(0, helper.ALIGN_CENTER);
        r = (DefaultTableCellRenderer) t.getColumnModel().getColumn(0).getCellRenderer();
        assertEquals(SwingConstants.CENTER, r.getHorizontalAlignment());

        assertSame(t.getModel(), helper.getModel());
    }

    @Test
    public void testAddRowLimpiaFilasPreviasSoloLaPrimeraVez() {
        // La tabla arranca con 3 filas vacias: el primer addRow las borra
        JTable t = tabla(3);
        jTable helper = new jTable(t);
        helper.addRow(new Object[]{"a", 1});
        assertEquals(1, t.getRowCount());
        helper.addRow(new Object[]{"b", 2});
        assertEquals(2, t.getRowCount());

        // assignData: si quedan menos filas que las originales se completan con vacias
        helper.assignData();
        assertEquals(3, t.getRowCount());
        assertEquals("a", t.getValueAt(0, 0));
        assertNull(t.getValueAt(2, 0));
    }

    @Test
    public void testAddRowEnTablaVaciaYAssignDataSinRelleno() {
        JTable t = tabla(0);
        jTable helper = new jTable(t);
        helper.addRow(new Object[]{"a", 1});
        helper.assignData();
        assertEquals(1, t.getRowCount());
    }

    @Test
    public void testAddRowNullLimpia() {
        JTable t = tabla(2);
        jTable helper = new jTable(t);
        helper.addRow(null);
        assertEquals(0, t.getRowCount());
        // Ya esta vacia: clear no cambia nada
        helper.clear();
        assertEquals(0, t.getRowCount());
        // Luego de clear el siguiente addRow vuelve a empezar de cero
        helper.addRow(new Object[]{"x", 9});
        assertEquals(1, t.getRowCount());
    }

    @Test
    public void testClearSinModeloPrevio() {
        JTable t = tabla(4);
        jTable helper = new jTable(t);
        helper.clear();
        assertEquals(0, t.getRowCount());
    }

    @Test
    public void testAssignDataSinModeloNoHaceNada() {
        JTable t = tabla(2);
        jTable helper = new jTable(t);
        helper.assignData();
        assertEquals(2, t.getRowCount());
    }
}
