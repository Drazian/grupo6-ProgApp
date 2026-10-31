package edu.edext.tools;

import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de ArbolHelper con el controlador simulado (mockStatic de Fabrica).
 */
public class ArbolHelperTest {

    private MockedStatic<Fabrica> fabrica;
    private IControlador ic;

    @BeforeEach
    public void setUp() {
        ic = mock(IControlador.class);
        Fabrica f = mock(Fabrica.class);
        when(f.getIControlador()).thenReturn(ic);
        fabrica = mockStatic(Fabrica.class);
        fabrica.when(Fabrica::getInstance).thenReturn(f);
    }

    @AfterEach
    public void tearDown() {
        fabrica.close();
    }

    /** INCO sin cursos; IMERL con un curso sin ediciones y otro con una edicion vacia y otra con un inscripto. */
    private void cargarDatos() throws Exception {
        when(ic.listarInstitutos()).thenReturn(List.of(new DtInstituto("INCO"), new DtInstituto("IMERL")));
        when(ic.listarCursosPorInstituto("INCO")).thenReturn(new ArrayList<>());
        when(ic.listarCursosPorInstituto("IMERL")).thenReturn(List.of("Calculo", "Algebra"));
        when(ic.listaEdicionPorCurso("Calculo")).thenReturn(new ArrayList<>());
        when(ic.listaEdicionPorCurso("Algebra")).thenReturn(List.of("Alg-2024", "Alg-2025"));
        when(ic.getEstudiantesInscriptosEdicion("Alg-2024")).thenReturn(new ArrayList<>());
        when(ic.getEstudiantesInscriptosEdicion("Alg-2025")).thenReturn(List.of("ana"));
    }

    private static DefaultMutableTreeNode hijo(DefaultMutableTreeNode nodo, int i) {
        return (DefaultMutableTreeNode) nodo.getChildAt(i);
    }

    @Test
    public void testGetModelCompleto() throws Exception {
        cargarDatos();
        DefaultTreeModel model = new ArbolHelper().getModel();
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) model.getRoot();

        assertEquals("Plataforma Educativa edEXT", raiz.getUserObject());
        assertEquals(2, raiz.getChildCount());
        DefaultMutableTreeNode inco = hijo(raiz, 0), imerl = hijo(raiz, 1);
        assertEquals("INCO", inco.getUserObject());
        assertEquals("vacio", hijo(inco, 0).getUserObject());

        DefaultMutableTreeNode calculo = hijo(imerl, 0), algebra = hijo(imerl, 1);
        assertEquals("Calculo", calculo.getUserObject());
        assertEquals("vacio", hijo(calculo, 0).getUserObject());
        assertEquals("Algebra", algebra.getUserObject());
        assertEquals("vacio", hijo(hijo(algebra, 0), 0).getUserObject());
        assertEquals("Alg-2025", hijo(algebra, 1).getUserObject());
        assertEquals("ana", hijo(hijo(algebra, 1), 0).getUserObject());
    }

    @Test
    public void testGetModelSinInstitutos() throws Exception {
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) new ArbolHelper().getModel().getRoot();
        assertEquals(1, raiz.getChildCount());
        assertEquals("vacio", hijo(raiz, 0).getUserObject());
    }

    @Test
    public void testGetModelConError() throws Exception {
        when(ic.listarInstitutos()).thenThrow(new Exception("sin conexion"));
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) new ArbolHelper().getModel().getRoot();
        assertEquals("Error al crear el Arbol", raiz.getUserObject());
        assertEquals(0, raiz.getChildCount());
    }

    @Test
    public void testCreateArbol() throws Exception {
        cargarDatos();
        JTree arbol = new JTree();
        assertTrue(new ArbolHelper().createArbol(arbol));

        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) arbol.getModel().getRoot();
        assertEquals(2, raiz.getChildCount());
        // INCO no tiene cursos: en este modelo no se agrega el nodo "vacio"
        assertEquals(0, hijo(raiz, 0).getChildCount());
        assertEquals("ana", hijo(hijo(hijo(hijo(raiz, 1), 1), 1), 0).getUserObject());
        // Todas las filas quedan expandidas
        assertTrue(arbol.getRowCount() > 1);
        for (int i = 0; i < arbol.getRowCount() - 1; i++) {
            if (!arbol.getModel().isLeaf(arbol.getPathForRow(i).getLastPathComponent()))
                assertTrue(arbol.isExpanded(i));
        }
    }

    @Test
    public void testCreateArbolConError() throws Exception {
        when(ic.listarInstitutos()).thenThrow(new Exception("sin conexion"));
        JTree arbol = new JTree();
        Object modeloOriginal = arbol.getModel();
        assertFalse(new ArbolHelper().createArbol(arbol));
        assertSame(modeloOriginal, arbol.getModel());
    }
}
