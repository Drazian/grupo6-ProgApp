package edu.edext.presentacion;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del formulario de presentacion InscripcionEdicion (no confundir con la
 * entidad edu.edext.logica.InscripcionEdicion). Usa la base H2 de los tests con
 * un instituto, curso y edicion propios (nombres unicos) mas los datos de prueba.
 */
public class InscripcionEdicionFormTest {

    private static final String ID = UUID.randomUUID().toString().substring(0, 8);
    private static final String INSTITUTO = "INST-IE-" + ID;
    private static final String INSTITUTO_VACIO = "INST-IE-VACIO-" + ID;
    private static final String CURSO = "CUR-IE-" + ID;
    private static final String EDICION = "ED-IE-" + ID;

    private MockedStatic<JOptionPane> dialogos;

    @BeforeAll
    public static void cargarDatos() throws Exception {
        IControlador ic = Fabrica.getInstance().getIControlador();
        ic.cargarDatosDePrueba();
        ic.crearInstituto(INSTITUTO);
        ic.crearInstituto(INSTITUTO_VACIO);
        ic.altaCurso(new DtCurso(CURSO, "Curso de prueba", "1 mes", 10, 2, "http://edext.test",
                new Date(), new DtInstituto(INSTITUTO), null), INSTITUTO);
        String docente = ic.listarDocentes().get(0);
        ic.altaEdicionCurso(CURSO, new DtEdicion(EDICION, 30, LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(90), LocalDate.now(), new DtCurso(CURSO, null), Set.of(docente)));
        List<String> candidatos = ic.getEstudiantesCandidatosEdicion(EDICION);
        assertTrue(candidatos.size() >= 4, "Los datos de prueba traen estudiantes");
        assertEquals(0, ic.setInscripcion(candidatos.get(0), EDICION));
        assertEquals(0, ic.setInscripcion(candidatos.get(1), EDICION));
    }

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

    private static void asignar(Object form, String nombre, Object valor) throws Exception {
        Field f = form.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        f.set(form, valor);
    }

    private static List<String> elementos(JList<String> lista) {
        ListModel<String> m = lista.getModel();
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < m.getSize(); i++) ret.add(m.getElementAt(i));
        return ret;
    }

    private static void soltarMouse(JList<?> lista) {
        lista.dispatchEvent(new MouseEvent(lista, MouseEvent.MOUSE_RELEASED, 0, 0, 1, 1, 1, false));
    }

    private static Fabrica fabricaCon(IControlador ic) {
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        return fabrica;
    }

    @Test
    public void testConstructorSinInstitutos() throws Exception {
        IControlador ic = mock(IControlador.class);
        when(ic.listarInstitutos()).thenReturn(new ArrayList<>());
        Fabrica fabrica = fabricaCon(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            InscripcionEdicion form = new InscripcionEdicion();
            JComboBox<?> combo = campo(form, "jCInstituto");
            assertEquals(0, combo.getItemCount());
            assertFalse(((JPanel) campo(form, "jPCursos")).isVisible());
        }
    }

    @Test
    public void testNavegacionInstitutoCursoEdicion() throws Exception {
        InscripcionEdicion form = new InscripcionEdicion();
        @SuppressWarnings("unchecked")
        JComboBox<String> combo = campo(form, "jCInstituto");
        JList<String> cursos = campo(form, "jLCursos");
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> inscriptos = campo(form, "jLInscriptos");
        JList<String> candidatos = campo(form, "jLCandidatos");
        JPanel pCursos = campo(form, "jPCursos");
        JPanel pEdiciones = campo(form, "jPEdiciones");
        JPanel pCandidatos = campo(form, "jPECandidatos");
        JPanel pEstudiantes = campo(form, "jPEstudiantes");
        JButton mostrarCandidatos = campo(form, "jBshowCandidatos");
        JButton conmutar = campo(form, "jBEst_Cand");

        // El constructor carga los institutos y selecciona el primero
        assertTrue(combo.getItemCount() >= 2);
        assertTrue(pCursos.isVisible());

        // Instituto sin cursos: la lista de cursos queda vacia y se ocultan las ediciones
        combo.setSelectedItem(INSTITUTO_VACIO);
        assertEquals(0, cursos.getModel().getSize());
        assertFalse(pEdiciones.isVisible());
        assertFalse(pEstudiantes.isVisible());

        // Instituto con curso
        combo.setSelectedItem(INSTITUTO);
        assertEquals(List.of(CURSO), elementos(cursos));

        // Seleccion "en curso" (valueIsAdjusting): se ignora
        cursos.getSelectionModel().setValueIsAdjusting(true);
        cursos.setSelectedIndex(0);
        assertFalse(pEdiciones.isVisible());
        // Al terminar la seleccion se cargan las ediciones
        cursos.getSelectionModel().setValueIsAdjusting(false);
        assertTrue(pEdiciones.isVisible());
        assertEquals(List.of(EDICION), elementos(ediciones));

        // Deseleccionar el curso oculta las ediciones
        cursos.clearSelection();
        assertFalse(pEdiciones.isVisible());
        cursos.setSelectedIndex(0);
        assertTrue(pEdiciones.isVisible());

        // Seleccionar la edicion carga los inscriptos (candidatos todavia ocultos)
        ediciones.setSelectedIndex(0);
        assertEquals(2, inscriptos.getModel().getSize());
        assertTrue(pEstudiantes.isVisible());
        assertTrue(mostrarCandidatos.isVisible());
        assertFalse(conmutar.isVisible());

        // Click sobre la lista de ediciones (manejador vacio)
        ediciones.dispatchEvent(new MouseEvent(ediciones, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false));

        // Mostrar candidatos
        mostrarCandidatos.doClick();
        assertTrue(pCandidatos.isVisible());
        assertTrue(conmutar.isVisible());
        assertTrue(candidatos.getModel().getSize() >= 2);
        assertFalse(elementos(candidatos).contains(inscriptos.getModel().getElementAt(0)));

        // Cambio de edicion "en curso" (valueIsAdjusting) con candidatos visibles: no se toca nada
        inscriptos.setListData(new String[]{"sin recargar"});
        ediciones.getSelectionModel().setValueIsAdjusting(true);
        ediciones.clearSelection();
        assertTrue(mostrarCandidatos.isVisible(), "Con valueIsAdjusting no se oculta el boton de candidatos");
        assertTrue(pCandidatos.isVisible(), "Con valueIsAdjusting no se ocultan los candidatos");
        assertTrue(conmutar.isVisible(), "Con valueIsAdjusting no se oculta el boton de conmutar");
        ediciones.setSelectedIndex(0);
        assertEquals(List.of("sin recargar"), elementos(inscriptos), "Con valueIsAdjusting no se recargan los estudiantes");
        assertTrue(mostrarCandidatos.isVisible());
        // Evento final de la seleccion: recien ahora se recargan los inscriptos y se ocultan los candidatos y el boton
        ediciones.getSelectionModel().setValueIsAdjusting(false);
        assertEquals(2, inscriptos.getModel().getSize());
        assertTrue(mostrarCandidatos.isVisible());
        assertFalse(pCandidatos.isVisible());
        assertFalse(conmutar.isVisible());

        // Sin edicion seleccionada se oculta el boton de candidatos
        ediciones.clearSelection();
        assertFalse(mostrarCandidatos.isVisible());
    }

    @Test
    public void testInscribirYDesinscribirEstudiante() throws Exception {
        InscripcionEdicion form = new InscripcionEdicion();
        @SuppressWarnings("unchecked")
        JComboBox<String> combo = campo(form, "jCInstituto");
        JList<String> cursos = campo(form, "jLCursos");
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> inscriptos = campo(form, "jLInscriptos");
        JList<String> candidatos = campo(form, "jLCandidatos");
        JButton conmutar = campo(form, "jBEst_Cand");
        IControlador ic = Fabrica.getInstance().getIControlador();

        combo.setSelectedItem(INSTITUTO);
        cursos.setSelectedIndex(0);
        ediciones.setSelectedIndex(0);
        ((JButton) campo(form, "jBshowCandidatos")).doClick();
        int totalInscriptos = inscriptos.getModel().getSize();
        int totalCandidatos = candidatos.getModel().getSize();
        String estudiante = candidatos.getModel().getElementAt(0);

        // "<<": inscribe al candidato seleccionado
        candidatos.setSelectedIndex(0);
        conmutar.setText("<<");
        conmutar.doClick();
        assertTrue(elementos(inscriptos).contains(estudiante));
        assertFalse(elementos(candidatos).contains(estudiante));
        assertEquals(totalInscriptos + 1, inscriptos.getModel().getSize());
        assertEquals(totalCandidatos - 1, candidatos.getModel().getSize());
        assertTrue(ic.getEstudiantesInscriptosEdicion(EDICION).contains(estudiante));

        // ">>": cancela la inscripcion del inscripto seleccionado
        inscriptos.setSelectedValue(estudiante, false);
        conmutar.setText(">>");
        conmutar.doClick();
        assertFalse(elementos(inscriptos).contains(estudiante));
        assertTrue(elementos(candidatos).contains(estudiante));
        assertEquals(totalInscriptos, inscriptos.getModel().getSize());
        assertFalse(ic.getEstudiantesInscriptosEdicion(EDICION).contains(estudiante));

        // Cualquier otro texto en el boton no hace nada
        List<String> antes = elementos(inscriptos);
        conmutar.setText("X");
        conmutar.doClick();
        assertEquals(antes, elementos(inscriptos));
    }

    @Test
    public void testOperacionConErrorDelControlador() throws Exception {
        InscripcionEdicion form = new InscripcionEdicion();
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> inscriptos = campo(form, "jLInscriptos");
        JList<String> candidatos = campo(form, "jLCandidatos");
        JButton conmutar = campo(form, "jBEst_Cand");
        ediciones.setListData(new String[]{"EDICION-INEXISTENTE-" + ID});
        ediciones.setSelectedIndex(0);
        inscriptos.setListData(new String[]{"ins1", "ins2"});
        candidatos.setListData(new String[]{"cand1", "cand2"});

        IControlador ic = mock(IControlador.class);
        when(ic.setInscripcion(anyString(), anyString())).thenThrow(new RuntimeException("sin conexion"));
        when(ic.delInscripcion(anyString(), anyString())).thenThrow(new RuntimeException("sin conexion"));
        Fabrica fabrica = fabricaCon(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            candidatos.setSelectedIndex(0);
            conmutar.setText("<<");
            conmutar.doClick();
            inscriptos.setSelectedIndex(0);
            conmutar.setText(">>");
            conmutar.doClick();
        }
        // El error se registra en el log, se avisa al usuario y las listas no cambian
        assertEquals(List.of("ins1", "ins2"), elementos(inscriptos));
        assertEquals(List.of("cand1", "cand2"), elementos(candidatos));
        verify(ic).setInscripcion("cand1", "EDICION-INEXISTENTE-" + ID);
        verify(ic).delInscripcion("ins1", "EDICION-INEXISTENTE-" + ID);
        dialogos.verify(() -> JOptionPane.showMessageDialog(form,
                "No se pudo inscribir al estudiante cand1 en la edición EDICION-INEXISTENTE-" + ID + ".",
                "Error", JOptionPane.ERROR_MESSAGE));
        dialogos.verify(() -> JOptionPane.showMessageDialog(form,
                "No se pudo cancelar la inscripción de ins1 a la edición EDICION-INEXISTENTE-" + ID + ".",
                "Error", JOptionPane.ERROR_MESSAGE));
    }

    @Test
    public void testOperacionSegunCodigoDelControlador() throws Exception {
        String edicion = "ED-MOCK-" + ID;
        InscripcionEdicion form = new InscripcionEdicion();
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> inscriptos = campo(form, "jLInscriptos");
        JList<String> candidatos = campo(form, "jLCandidatos");
        JButton conmutar = campo(form, "jBEst_Cand");
        ediciones.setListData(new String[]{edicion});
        ediciones.setSelectedIndex(0);
        inscriptos.setListData(new String[]{"ins1", "ins2"});
        candidatos.setListData(new String[]{"cand1", "cand2"});

        // Codigos de error de setInscripcion (logica.InscripcionEdicionHelper) y el mensaje que ve el usuario
        Map<Integer, String> errores = new LinkedHashMap<>();
        errores.put(1, "No se encontró el estudiante cand1.");
        errores.put(2, "No se encontró la edición " + edicion + ".");
        errores.put(3, "El estudiante cand1 ya está inscripto en la edición " + edicion + ".");
        errores.put(4, "La edición " + edicion + " ya finalizó, no admite inscripciones.");
        errores.put(-1, "No se pudo inscribir al estudiante cand1 en la edición " + edicion + ".");

        IControlador ic = mock(IControlador.class);
        Fabrica fabrica = fabricaCon(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            for (Map.Entry<Integer, String> error : errores.entrySet()) {
                when(ic.setInscripcion("cand1", edicion)).thenReturn(error.getKey());
                candidatos.setSelectedIndex(0);
                conmutar.setText("<<");
                conmutar.doClick();
                dialogos.verify(() -> JOptionPane.showMessageDialog(form, error.getValue(), "Error", JOptionPane.ERROR_MESSAGE));
                assertEquals(List.of("ins1", "ins2"), elementos(inscriptos), "Codigo " + error.getKey() + ": no se inscribe");
                assertEquals(List.of("cand1", "cand2"), elementos(candidatos), "Codigo " + error.getKey() + ": no se inscribe");
            }

            // Baja rechazada por la base (-1, p. ej. la inscripcion no existe): no se mueve el inscripto
            when(ic.delInscripcion("ins1", edicion)).thenReturn(-1);
            inscriptos.setSelectedIndex(0);
            conmutar.setText(">>");
            conmutar.doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(form,
                    "No se pudo cancelar la inscripción de ins1 a la edición " + edicion + ".", "Error", JOptionPane.ERROR_MESSAGE));
            assertEquals(List.of("ins1", "ins2"), elementos(inscriptos));
            assertEquals(List.of("cand1", "cand2"), elementos(candidatos));

            // Codigo 0 (exito): recien ahora se actualizan las listas
            when(ic.setInscripcion("cand1", edicion)).thenReturn(0);
            candidatos.setSelectedIndex(0);
            conmutar.setText("<<");
            conmutar.doClick();
            assertEquals(List.of("ins1", "ins2", "cand1"), elementos(inscriptos));
            assertEquals(List.of("cand2"), elementos(candidatos));
            when(ic.delInscripcion("ins1", edicion)).thenReturn(0);
            inscriptos.setSelectedIndex(0);
            conmutar.setText(">>");
            conmutar.doClick();
            assertEquals(List.of("ins2", "cand1"), elementos(inscriptos));
            assertEquals(List.of("cand2", "ins1"), elementos(candidatos));
        }
        // Un solo mensaje por cada error, ninguno por los exitos
        dialogos.verify(() -> JOptionPane.showMessageDialog(any(), any(), anyString(), anyInt()), times(errores.size() + 1));
    }

    @Test
    public void testOperacionSinSeleccion() throws Exception {
        InscripcionEdicion form = new InscripcionEdicion();
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> inscriptos = campo(form, "jLInscriptos");
        JList<String> candidatos = campo(form, "jLCandidatos");
        JButton conmutar = campo(form, "jBEst_Cand");
        ediciones.setListData(new String[]{EDICION});
        ediciones.setSelectedIndex(0);
        inscriptos.setListData(new String[]{"ins1"});
        candidatos.setListData(new String[]{"cand1"});

        IControlador ic = mock(IControlador.class);
        Fabrica fabrica = fabricaCon(ic);
        try (MockedStatic<Fabrica> f = mockStatic(Fabrica.class)) {
            f.when(Fabrica::getInstance).thenReturn(fabrica);
            // Sin candidato seleccionado: se avisa y no se llama al controlador ni se agrega null
            conmutar.setText("<<");
            conmutar.doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(form, "Debe seleccionar el candidato a inscribir.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE));
            // Sin inscripto seleccionado
            conmutar.setText(">>");
            conmutar.doClick();
            dialogos.verify(() -> JOptionPane.showMessageDialog(form, "Debe seleccionar el inscripto a dar de baja.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE));
        }
        verifyNoInteractions(ic);
        assertEquals(List.of("ins1"), elementos(inscriptos));
        assertEquals(List.of("cand1"), elementos(candidatos));
    }

    @Test
    public void testOperacionRechazadaPorLaBase() throws Exception {
        InscripcionEdicion form = new InscripcionEdicion();
        @SuppressWarnings("unchecked")
        JComboBox<String> combo = campo(form, "jCInstituto");
        JList<String> cursos = campo(form, "jLCursos");
        JList<String> ediciones = campo(form, "jLEdiciones");
        JList<String> inscriptos = campo(form, "jLInscriptos");
        JList<String> candidatos = campo(form, "jLCandidatos");
        JButton conmutar = campo(form, "jBEst_Cand");
        IControlador ic = Fabrica.getInstance().getIControlador();

        combo.setSelectedItem(INSTITUTO);
        cursos.setSelectedIndex(0);
        ediciones.setSelectedIndex(0);
        ((JButton) campo(form, "jBshowCandidatos")).doClick();
        String inscripto = inscriptos.getModel().getElementAt(0);
        String candidato = candidatos.getModel().getElementAt(0);
        List<String> inscriptosEnBase = ic.getEstudiantesInscriptosEdicion(EDICION);

        // Listas desactualizadas: el "candidato" ya esta inscripto (codigo 3)
        candidatos.setListData(new String[]{inscripto});
        candidatos.setSelectedIndex(0);
        conmutar.setText("<<");
        conmutar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(form,
                "El estudiante " + inscripto + " ya está inscripto en la edición " + EDICION + ".", "Error", JOptionPane.ERROR_MESSAGE));
        assertEquals(1, elementos(inscriptos).stream().filter(inscripto::equals).count(), "No se duplica el inscripto");
        assertEquals(List.of(inscripto), elementos(candidatos));

        // Baja de un estudiante que no esta inscripto: el controlador devuelve -1
        inscriptos.setListData(new String[]{candidato});
        inscriptos.setSelectedIndex(0);
        conmutar.setText(">>");
        conmutar.doClick();
        dialogos.verify(() -> JOptionPane.showMessageDialog(form,
                "No se pudo cancelar la inscripción de " + candidato + " a la edición " + EDICION + ".", "Error", JOptionPane.ERROR_MESSAGE));
        assertEquals(List.of(candidato), elementos(inscriptos));
        assertEquals(List.of(inscripto), elementos(candidatos));
        assertEquals(inscriptosEnBase, ic.getEstudiantesInscriptosEdicion(EDICION), "La base no cambia");
    }

    @Test
    public void testConmutarBotonSegunSeleccion() throws Exception {
        InscripcionEdicion form = new InscripcionEdicion();
        JList<String> ins = campo(form, "jLInscriptos");
        JList<String> cand = campo(form, "jLCandidatos");
        JButton boton = campo(form, "jBEst_Cand");
        JPanel pCandidatos = campo(form, "jPECandidatos");
        ins.setListData(new String[]{"a", "b"});
        cand.setListData(new String[]{"x", "y"});

        // 1. Inscripto seleccionado, sin candidato, distinto al anterior: ">>" (candidatos ocultos => boton oculto)
        pCandidatos.setVisible(false);
        ins.setSelectedIndex(0);
        soltarMouse(ins);
        assertEquals(">>", boton.getText());
        assertEquals("a", campo(form, "oldInscriptoSelect"));
        assertFalse(boton.isVisible());

        // 2. Candidato seleccionado, sin inscripto, distinto al anterior: "<<"
        ins.clearSelection();
        cand.setSelectedIndex(0);
        soltarMouse(cand);
        assertEquals("<<", boton.getText());
        assertTrue(boton.isVisible());
        assertEquals("x", campo(form, "oldCandidatoSelect"));

        // 3. Mismo inscripto otra vez (sin candidato): deselecciona y deja el boton en blanco
        cand.clearSelection();
        ins.setSelectedIndex(0);
        soltarMouse(ins);
        assertEquals("", campo(form, "oldInscriptoSelect"));
        assertNull(ins.getSelectedValue());
        assertEquals("  ", boton.getText());

        // 4. Mismo candidato otra vez (sin inscripto)
        cand.setSelectedIndex(0);
        soltarMouse(cand);
        assertEquals("", campo(form, "oldCandidatoSelect"));
        assertNull(cand.getSelectedValue());
        assertEquals("  ", boton.getText());

        // 5. Ambos seleccionados, inscripto nuevo: boton "X" y se recuerda el estado anterior
        ins.setSelectedIndex(1);
        cand.setSelectedIndex(1);
        soltarMouse(ins);
        assertEquals("X", boton.getText());
        assertEquals("  ", campo(form, "estadoCruce"));
        assertEquals("b", campo(form, "oldInscriptoSelect"));

        // 6. Ambos seleccionados, mismo inscripto y boton ya en "X": vuelve a "<<" y deselecciona el inscripto
        soltarMouse(ins);
        assertEquals("<<", boton.getText());
        assertEquals("", campo(form, "oldInscriptoSelect"));
        assertNull(ins.getSelectedValue());

        // 7. Ambos seleccionados, candidato nuevo
        ins.setSelectedIndex(1);
        soltarMouse(cand);
        assertEquals("X", boton.getText());
        assertEquals("<<", campo(form, "estadoCruce"));
        assertEquals("y", campo(form, "oldCandidatoSelect"));

        // 8. Ambos seleccionados, mismo candidato: vuelve a ">>" y deselecciona el candidato
        soltarMouse(cand);
        assertEquals(">>", boton.getText());
        assertEquals("", campo(form, "oldCandidatoSelect"));
        assertNull(cand.getSelectedValue());

        // 9. Sin inscripto (vacio == anterior) con candidato seleccionado: "<<" (candidatos visibles => boton visible)
        pCandidatos.setVisible(true);
        ins.clearSelection();
        cand.setSelectedIndex(1);
        soltarMouse(ins);
        assertEquals("<<", boton.getText());
        assertTrue(boton.isVisible());

        // 10. Sin candidato (vacio == anterior) con inscripto seleccionado: ">>"
        cand.clearSelection();
        ins.setSelectedIndex(1);
        soltarMouse(cand);
        assertEquals(">>", boton.getText());

        // 11. Sin inscripto, anterior distinto de vacio y con candidato: no cambia el texto
        ins.clearSelection();
        cand.setSelectedIndex(1);
        asignar(form, "oldInscriptoSelect", "zzz");
        boton.setText("sin cambio");
        soltarMouse(ins);
        assertEquals("sin cambio", boton.getText());
        assertEquals("", campo(form, "oldInscriptoSelect"));
    }
}
