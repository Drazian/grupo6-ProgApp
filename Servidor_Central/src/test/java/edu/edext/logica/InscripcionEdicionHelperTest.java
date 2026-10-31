package edu.edext.logica;

import edu.edext.datatypes.DtInscripcionEdicion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static edu.edext.logica.DatosUnicos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Inscripciones a ediciones: casos reales contra la base (nombres unicos) y errores de
 * persistencia con un EntityManagerFactory simulado.
 */
public class InscripcionEdicionHelperTest {

    private IControlador ic;

    // Simulados
    private EntityManagerFactory emfMock;
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    public void setUp() {
        ic = Fabrica.getInstance().getIControlador();
        emfMock = mock(EntityManagerFactory.class);
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        when(emfMock.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static TypedQuery query(List<?> resultado, Object unico) {
        TypedQuery q = mock(TypedQuery.class, RETURNS_SELF);
        when(q.getResultList()).thenReturn(resultado);
        when(q.getSingleResult()).thenReturn(unico);
        return q;
    }

    private static Map<String, Integer> mapa(Object... claveValor) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < claveValor.length; i += 2) m.put((String) claveValor[i], (Integer) claveValor[i + 1]);
        return m;
    }

    private static DtInscripcionEdicion de(List<DtInscripcionEdicion> lista, String estudiante) {
        return lista.stream().filter(i -> i.getEstudiante().getNickname().equals(estudiante)).findFirst().orElseThrow();
    }

    private static Edicion edicionSimulada(String nombre, Curso curso) {
        return new Edicion(nombre, 5, LocalDate.now(), LocalDate.now(), LocalDate.now(), curso, null, null);
    }

    private static Estudiante estudianteSimulado() {
        return new Estudiante("est", "p", "e@x", "n", "a", new Date(), "i.png");
    }

    //********************************* Base real *******************************//

    @Test
    public void testInscribirYCancelarDirecto() throws Exception {
        EntityManagerFactory emf = emf();
        String curso = curso(ic, instituto(ic));
        String vigente = edicionVigente(ic, curso, 10);
        String terminada = edicion(ic, curso, 10, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), null);
        String est = estudiante(ic);

        InscripcionEdicionHelper h = new InscripcionEdicionHelper(emf, "");
        assertEquals(1, h.inscribirEstudiante(nombre("NoEst"), vigente));
        assertEquals(2, h.inscribirEstudiante(est, nombre("NoEd")));
        assertEquals(4, h.inscribirEstudiante(est, terminada));
        assertEquals(0, h.inscribirEstudiante(est, vigente));
        assertEquals(3, h.inscribirEstudiante(est, vigente));
        assertEquals(List.of(est), new InscripcionEdicionHelper(emf, vigente).getEstudiantesInscriptos());
        assertFalse(new InscripcionEdicionHelper(emf, vigente).getEstudiantesCandidatos().contains(est));
        assertEquals(0, h.cancelarInscripcion(est, vigente));
        assertEquals(-1, h.cancelarInscripcion(est, vigente));
        h.kill(); // el EntityManager ya esta cerrado
        h.kill(); // y luego es nulo
    }

    @Test
    public void testEvaluarInscripciones() throws Exception {
        String curso = curso(ic, instituto(ic));
        String ed = edicionVigente(ic, curso, 1);
        String e1 = estudiante(ic), e2 = estudiante(ic), noInscripto = estudiante(ic);
        assertEquals(0, ic.setInscripcion(e1, ed));
        assertEquals(0, ic.setInscripcion(e2, ed));

        Exception noExiste = assertThrows(Exception.class, () -> ic.evaluarInscripciones(nombre("NoEd"), mapa(e1, 2)));
        assertTrue(noExiste.getMessage().startsWith("Ocurrió un error"));
        Exception nulo = assertThrows(Exception.class, () -> ic.evaluarInscripciones(ed, mapa(e1, null)));
        assertTrue(nulo.getMessage().startsWith("Estado inválido"));
        Exception inscripto = assertThrows(Exception.class, () -> ic.evaluarInscripciones(ed, mapa(e1, 1)));
        assertTrue(inscripto.getMessage().startsWith("Estado inválido"));
        Exception cupo = assertThrows(Exception.class, () -> ic.evaluarInscripciones(ed, mapa(e1, 2, e2, 2)));
        assertTrue(cupo.getMessage().startsWith("Se superó el cupo"));
        assertEquals(1, de(ic.getInscripcionesCompletas(ed), e1).getValorEstado(), "Se deshace la evaluacion");

        // Un aceptado (entra en el cupo), un rechazado y uno que no estaba inscripto (se ignora)
        ic.evaluarInscripciones(ed, mapa(e1, 2, e2, 3, noInscripto, 2));
        List<DtInscripcionEdicion> lista = ic.getInscripcionesCompletas(ed);
        assertEquals(2, lista.size());
        assertEquals("Aceptado", de(lista, e1).getEstado());
        assertEquals("Rechazado", de(lista, e2).getEstado());
        assertEquals(curso, de(lista, e1).getEdicion().getCurso().getNombre());

        // Sin cupo (nulo) y cupo 0: no se controla el cupo
        for (Integer sinLimite : new Integer[]{null, 0}) {
            String ed2 = edicionVigente(ic, curso, sinLimite);
            assertEquals(0, ic.setInscripcion(e1, ed2));
            assertEquals(0, ic.setInscripcion(e2, ed2));
            ic.evaluarInscripciones(ed2, mapa(e1, 2, e2, 2));
            assertEquals(2, ic.getInscripcionesCompletas(ed2).stream().filter(i -> i.getValorEstado() == 2).count());
        }
    }

    @Test
    public void testPrioridadPorRechazosPrevios() throws Exception {
        EntityManagerFactory emf = emf();
        String curso = curso(ic, instituto(ic));
        String pasada = edicion(ic, curso, 10, LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(3), null);
        String vigente = edicionVigente(ic, curso, 10);
        String rechazado = estudiante(ic), nuevo = estudiante(ic);

        // Rechazo en una edicion ya finalizada del mismo curso (no se puede hacer por la API: la edicion termino)
        EntityManager db = emf.createEntityManager();
        db.getTransaction().begin();
        db.persist(new InscripcionEdicion(db.find(Estudiante.class, rechazado), db.find(Edicion.class, pasada),
                LocalDate.now().minusMonths(4), InscripcionEdicionHelper.RECHAZADO));
        db.getTransaction().commit();
        db.close();

        assertEquals(0, ic.setInscripcion(rechazado, vigente));
        assertEquals(0, ic.setInscripcion(nuevo, vigente));
        List<DtInscripcionEdicion> lista = ic.getInscripcionesCompletas(vigente);
        assertEquals(0.5, de(lista, rechazado).getPrioridad());
        assertEquals(0.0, de(lista, nuevo).getPrioridad());
        assertEquals("Inscripto", de(lista, nuevo).getEstado());
    }

    @Test
    public void testInscripcionesDeEstudianteConImagenes() throws Exception {
        String inst = instituto(ic);
        String est = estudiante(ic);
        String[][] casos = {{null, null}, {"", ""}, {"c.png", "e.png"}};
        String[] ediciones = new String[casos.length];
        for (int i = 0; i < casos.length; i++) {
            String curso = curso(ic, inst, set(), set(), casos[i][0]);
            ediciones[i] = edicion(ic, curso, 10, LocalDate.now(), LocalDate.now().plusYears(1), casos[i][1]);
            assertEquals(0, ic.setInscripcion(est, ediciones[i]));
        }
        List<DtInscripcionEdicion> lista = ic.getInscripcionesDeEstudiante(est);
        assertEquals(3, lista.size());
        for (int i = 0; i < casos.length; i++) {
            String nombreEd = ediciones[i];
            DtInscripcionEdicion insc = lista.stream().filter(x -> x.getEdicion().getNombre().equals(nombreEd)).findFirst().orElseThrow();
            boolean conImagen = i == 2;
            assertEquals(conImagen ? "c.png" : "usr.png", insc.getEdicion().getCurso().getImagen());
            assertEquals(conImagen ? "e.png" : "usr.png", insc.getEdicion().getImagen());
            assertEquals(est, insc.getEstudiante().getNickname());
        }
        assertTrue(ic.getInscripcionesDeEstudiante(nombre("NoEst")).isEmpty());
    }

    @Test
    public void testInscripcionesCompletasConImagenes() throws Exception {
        // Igual que getInscripcionesDeEstudiante: imagenes reales del curso y la edicion, o la generica si no tienen
        String inst = instituto(ic);
        String est = estudiante(ic);
        String[][] casos = {{null, null}, {"", ""}, {"c.png", "e.png"}};
        for (int i = 0; i < casos.length; i++) {
            String curso = curso(ic, inst, set(), set(), casos[i][0]);
            String ed = edicion(ic, curso, 10, LocalDate.now(), LocalDate.now().plusYears(1), casos[i][1]);
            assertEquals(0, ic.setInscripcion(est, ed));
            List<DtInscripcionEdicion> lista = ic.getInscripcionesCompletas(ed);
            assertEquals(1, lista.size());
            boolean conImagen = i == 2;
            assertEquals(conImagen ? "c.png" : "usr.png", lista.get(0).getEdicion().getCurso().getImagen());
            assertEquals(conImagen ? "e.png" : "usr.png", lista.get(0).getEdicion().getImagen());
            assertEquals(curso, lista.get(0).getEdicion().getCurso().getNombre());
        }
    }

    //******************************** Simulados *********************************//

    @Test
    public void testConsultasConErrorDevuelvenListasVacias() {
        when(em.createQuery(anyString(), any(Class.class))).thenThrow(new IllegalStateException("falla"));
        InscripcionEdicionHelper h = new InscripcionEdicionHelper(emfMock, "ed");
        assertTrue(h.getEstudiantesInscriptos().isEmpty());
        assertTrue(h.getEstudiantesCandidatos().isEmpty());
        assertTrue(h.getInscripcionesCompletas().isEmpty());
        assertTrue(h.getInscripcionesDeEstudiante().isEmpty());
    }

    @Test
    public void testInscribirEdicionSinFechaFin() {
        Estudiante est = estudianteSimulado();
        when(em.find(Estudiante.class, "est")).thenReturn(est);
        when(em.find(Edicion.class, "ed")).thenReturn(new Edicion());
        InscripcionEdicionHelper h = new InscripcionEdicionHelper(emfMock, "");
        assertEquals(0, h.inscribirEstudiante("est", "ed"));
        verify(em).persist(any(InscripcionEdicion.class));
        verify(tx).commit();
    }

    @Test
    public void testInscribirYCancelarConError() {
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException("falla"));
        for (boolean activa : new boolean[]{true, false}) {
            clearInvocations(tx);
            when(tx.isActive()).thenReturn(activa);
            InscripcionEdicionHelper h = new InscripcionEdicionHelper(emfMock, "");
            assertEquals(-1, h.inscribirEstudiante("est", "ed"));
            assertEquals(-1, h.cancelarInscripcion("est", "ed"));
            verify(tx, times(activa ? 2 : 0)).rollback();
        }
    }

    @Test
    public void testPrioridadNulaOConError() {
        Curso curso = new Curso("c", "d", "1", 1, 1, "u", new Date(), null, null, null, null);
        InscripcionEdicion insc = new InscripcionEdicion(estudianteSimulado(), edicionSimulada("ed", curso), LocalDate.now(), 1);
        doReturn(query(List.of(insc), null)).when(em).createQuery(anyString(), eq(InscripcionEdicion.class));

        doReturn(query(List.of(), null)).when(em).createQuery(anyString(), eq(Long.class));
        assertEquals(0.0, new InscripcionEdicionHelper(emfMock, "ed").getInscripcionesCompletas().get(0).getPrioridad());

        doThrow(new IllegalStateException("falla")).when(em).createQuery(anyString(), eq(Long.class));
        assertEquals(0.0, new InscripcionEdicionHelper(emfMock, "ed").getInscripcionesCompletas().get(0).getPrioridad());
    }

    @Test
    public void testEvaluarSinTransaccionActiva() {
        when(tx.isActive()).thenReturn(false);
        InscripcionEdicionHelper h = new InscripcionEdicionHelper(emfMock, "ed");

        // Edicion inexistente: error generico
        Exception e1 = assertThrows(Exception.class, () -> h.evaluarInscripciones(mapa("est", 2)));
        assertTrue(e1.getMessage().startsWith("Ocurrió un error"));

        // Estado invalido
        when(em.find(Edicion.class, "ed")).thenReturn(edicionSimulada("ed", null));
        Exception e2 = assertThrows(Exception.class, () -> h.evaluarInscripciones(mapa("est", 7)));
        assertTrue(e2.getMessage().startsWith("Estado inválido"));
        verify(tx, never()).rollback();
    }

    @Test
    public void testInscripcionesDeEstudianteEdicionSinCurso() {
        InscripcionEdicion insc = new InscripcionEdicion(estudianteSimulado(), edicionSimulada("ed", null), LocalDate.now(), 1);
        doReturn(query(List.of(insc), null)).when(em).createQuery(anyString(), eq(InscripcionEdicion.class));
        // Una edicion sin curso no se puede convertir: se registra el error y devuelve lo que pudo armar
        assertTrue(new InscripcionEdicionHelper(emfMock, "est").getInscripcionesDeEstudiante().isEmpty());
        verify(em, never()).close(); // el EntityManager simulado no esta abierto
    }

    @Test
    public void testInscripcionesDeEstudianteHelperYaLiberado() {
        InscripcionEdicionHelper h = new InscripcionEdicionHelper(emfMock, "est");
        TypedQuery<?> q = query(List.of(), null);
        when(em.isOpen()).thenReturn(true);
        // Si el helper se libera (kill) mientras consulta, al terminar ya no hay EntityManager que cerrar
        when(q.getResultList()).thenAnswer(i -> { h.kill(); return List.of(); });
        doReturn(q).when(em).createQuery(anyString(), eq(InscripcionEdicion.class));
        assertTrue(h.getInscripcionesDeEstudiante().isEmpty());
        verify(em, times(1)).close();
    }

    @Test
    public void testKill() throws Exception {
        InscripcionEdicionHelper h = new InscripcionEdicionHelper(emfMock, "x");
        h.kill(); // sin EntityManager
        Field db = InscripcionEdicionHelper.class.getDeclaredField("db");
        db.setAccessible(true);
        db.set(h, em);
        when(em.isOpen()).thenReturn(true);
        h.kill(); // abierto: se cierra
        verify(em).close();
        assertNull(db.get(h));
        db.set(h, em);
        when(em.isOpen()).thenReturn(false);
        h.kill(); // cerrado: no se vuelve a cerrar
        verify(em, times(1)).close();
    }
}
