package edu.edext.presentacion;

import edu.edext.logica.Fabrica;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.util.UUID;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del panel Arbol: arma el arbol de institutos/cursos/ediciones desde la base H2 de test.
 */
public class ArbolTest {

    @SuppressWarnings("unchecked")
    private static <T> T campo(Object form, String nombre) throws Exception {
        Field f = form.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        return (T) f.get(form);
    }

    private static boolean contiene(TreeModel model, Object nodo, String texto) {
        if (texto.equals(nodo.toString())) return true;
        for (int i = 0; i < model.getChildCount(nodo); i++)
            if (contiene(model, model.getChild(nodo, i), texto)) return true;
        return false;
    }

    @Test
    public void testCargaYRecargaAlTomarFoco() throws Exception {
        String inst1 = "ARB-" + UUID.randomUUID().toString().substring(0, 8);
        Fabrica.getInstance().getIControlador().crearInstituto(inst1);

        Arbol arbol = new Arbol();
        JTree tree = campo(arbol, "jTree");
        TreeModel model = tree.getModel();
        assertEquals("Plataforma Educativa edEXT", model.getRoot().toString());
        assertTrue(contiene(model, model.getRoot(), inst1));
        assertTrue(tree.getRowCount() > 1, "Las filas se expanden");
        assertTrue(tree.isExpanded(0));

        // Un instituto nuevo aparece recien al recargar cuando el arbol toma el foco
        String inst2 = "ARB-" + UUID.randomUUID().toString().substring(0, 8);
        Fabrica.getInstance().getIControlador().crearInstituto(inst2);
        assertFalse(contiene(tree.getModel(), tree.getModel().getRoot(), inst2));

        int llamados = 0;
        for (FocusListener l : tree.getFocusListeners()) {
            if (l.getClass().getName().startsWith(Arbol.class.getName() + "$")) {
                l.focusGained(new FocusEvent(tree, FocusEvent.FOCUS_GAINED));
                llamados++;
            }
        }
        assertEquals(1, llamados);
        assertNotSame(model, tree.getModel());
        assertTrue(contiene(tree.getModel(), tree.getModel().getRoot(), inst2));
    }
}
