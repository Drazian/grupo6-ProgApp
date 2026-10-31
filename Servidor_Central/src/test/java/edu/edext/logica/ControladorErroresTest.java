package edu.edext.logica;

import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Errores de persistencia y datos que la base real nunca devuelve (colecciones nulas, etc.).
 *
 * Antes de cargar Controlador se reemplaza Persistence.createEntityManagerFactory por un mock:
 * el EntityManagerFactory estatico de Controlador queda siendo un mock y cada test decide que
 * EntityManager entrega. Esta clase corre en su propia JVM (surefire reuseForks=false).
 */
public class ControladorErroresTest {

    private static EntityManagerFactory emf;
    private static IControlador ic;

    private EntityTransaction tx;
    private final LocalDate hoy = LocalDate.now();
    private final Instituto instituto = new Instituto("Inst");

    @BeforeAll
    public static void initControlador() throws Exception {
        emf = mock(EntityManagerFactory.class);
        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {
            persistence.when(() -> Persistence.createEntityManagerFactory(anyString(), anyMap())).thenReturn(emf);
            ic = new Controlador();
        }
        Field f = Controlador.class.getDeclaredField("emf");
        f.setAccessible(true);
        assertSame(emf, f.get(null), "Controlador debe usar el EntityManagerFactory simulado");
    }

    @BeforeEach
    public void setUp() {
        reset(emf);
    }

    private interface Operacion {
        void ejecutar() throws Exception;
    }

    /** EntityManager simulado con una transaccion que reporta el estado indicado. */
    private EntityManager em(boolean transaccionActiva) {
        EntityManager em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        when(em.getTransaction()).thenReturn(tx);
        when(tx.isActive()).thenReturn(transaccionActiva);
        when(emf.createEntityManager()).thenReturn(em);
        return em;
    }

    @SuppressWarnings("unchecked")
    private EntityManager emQueFalla(boolean transaccionActiva) {
        EntityManager em = em(transaccionActiva);
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException("falla find"));
        when(em.createQuery(anyString(), any(Class.class))).thenThrow(new IllegalStateException("falla query"));
        return em;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static TypedQuery query(List<?> resultado) {
        TypedQuery q = mock(TypedQuery.class, RETURNS_SELF);
        when(q.getResultList()).thenReturn(resultado);
        when(q.getResultStream()).thenAnswer(i -> resultado.stream());
        return q;
    }

    private Curso cursoSinColecciones(String nombre) {
        return new Curso(nombre, "d", "1", 1, 1, "u", new Date(), instituto, null, null, "c.png");
    }

    private Curso cursoSinInstituto(String nombre) {
        return new Curso(nombre, "d", "1", 1, 1, "u", new Date(), null, null, null, "c.png");
    }

    private ProgramaFormacion programa(String nombre, List<Curso> cursos) {
        return new ProgramaFormacion(nombre, "d", hoy, hoy, hoy, cursos, null, "p.png");
    }

    private Map<String, Operacion> operacionesConCatch() {
        DtUsuario u = new DtUsuario("u", "p", "u@x", "n", "a", null, null, null, TipoUsuario.ESTUDIANTE);
        DtCurso c = new DtCurso("c", "d", "1", 1, 1, "u", new Date(), new DtInstituto("i"), "img");
        DtEdicion e = new DtEdicion("e", 1, hoy, hoy, hoy);
        Map<String, Operacion> ops = new LinkedHashMap<>();
        ops.put("crearInstituto", () -> ic.crearInstituto("i"));
        ops.put("eliminarInstituto", () -> ic.eliminarInstituto("i"));
        ops.put("listarInstitutos", () -> ic.listarInstitutos());
        ops.put("crearCategoria", () -> ic.crearCategoria("c"));
        ops.put("listarCategorias", () -> ic.listarCategorias());
        ops.put("modificarUsuario", () -> ic.modificarUsuario(u));
        ops.put("crearUsuario", () -> ic.crearUsuario(u));
        ops.put("altaCurso", () -> ic.altaCurso(c, "i"));
        ops.put("listarCursos", () -> ic.listarCursos());
        ops.put("altaEdicionCurso", () -> ic.altaEdicionCurso("c", e));
        ops.put("agregarProgramaCurso", () -> ic.agregarProgramaCurso("p", "c"));
        ops.put("listarProgramas", () -> ic.listarProgramas());
        ops.put("listarCursosPorPrograma", () -> ic.listarCursosPorPrograma("p"));
        ops.put("buscarPrograma", () -> ic.buscarPrograma("p"));
        ops.put("modificarUsuarioWeb", () -> ic.modificarUsuario(u, null, null));
        ops.put("seguirUsuario", () -> ic.seguirUsuario("a", "b"));
        ops.put("dejarDeSeguirUsuario", () -> ic.dejarDeSeguirUsuario("a", "b"));
        ops.put("setInscripcionPrograma", () -> ic.setInscripcionPrograma("a", "p"));
        ops.put("agregarDocenteAEdicion", () -> ic.agregarDocenteAEdicion("e", "d"));
        ops.put("quitarDocenteDeEdicion", () -> ic.quitarDocenteDeEdicion("e", "d"));
        return ops;
    }

    @Test
    public void testErrorDePersistenciaConTransaccionActivaHaceRollback() {
        for (Map.Entry<String, Operacion> op : operacionesConCatch().entrySet()) {
            reset(emf);
            EntityManager em = emQueFalla(true);
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> op.getValue().ejecutar(), op.getKey());
            assertTrue(ex.getMessage().startsWith("falla"), op.getKey());
            verify(tx, description(op.getKey())).rollback();
            verify(em, description(op.getKey())).close();
        }
    }

    @Test
    public void testErrorDePersistenciaSinTransaccionActivaNoHaceRollback() {
        for (Map.Entry<String, Operacion> op : operacionesConCatch().entrySet()) {
            reset(emf);
            EntityManager em = emQueFalla(false);
            assertThrows(IllegalStateException.class, () -> op.getValue().ejecutar(), op.getKey());
            verify(tx, never().description(op.getKey())).rollback();
            verify(em, description(op.getKey())).close();
        }
    }

    @Test
    public void testHelpersSinConexionDevuelvenValoresPorDefecto() throws Exception {
        when(emf.createEntityManager()).thenThrow(new IllegalStateException("sin conexion"));

        assertNull(ic.getEdicion("e"));
        assertTrue(ic.listarEdicionPorCurso("c").isEmpty());
        assertTrue(ic.listaEdicionPorCurso("c").isEmpty());
        assertEquals(-1, ic.setInscripcion("a", "e"));
        assertEquals(-1, ic.delInscripcion("a", "e"));
        assertTrue(ic.getEstudiantesInscriptosEdicion("e").isEmpty());
        assertTrue(ic.getEstudiantesCandidatosEdicion("e").isEmpty());
        assertFalse(ic.setCrearProgramaFormacion(new DtPrograma("p", hoy, hoy, hoy)));
    }

    @Test
    public void testCargarDatosDePruebaConError() throws Exception {
        try (MockedConstruction<cargarDatosDePruebaHelper> helpers = mockConstruction(cargarDatosDePruebaHelper.class,
                (mock, ctx) -> when(mock.cargar()).thenThrow(new IllegalStateException("falla carga")))) {
            assertEquals(-1, ic.cargarDatosDePrueba());
            verify(helpers.constructed().get(0)).kill();
        }
    }

    @Test
    public void testModificarUsuarioEmailEncontradoEsElMismoUsuario() throws Exception {
        // Un motor que compara emails sin distinguir espacios/acentos puede devolver al mismo usuario
        for (boolean web : new boolean[]{false, true}) {
            reset(emf);
            EntityManager em = em(true);
            Estudiante u = new Estudiante("nick", "hash", "viejo@x.uy", "n", "a", new Date(), "i.png");
            when(em.find(Usuario.class, "nick")).thenReturn(u);
            doReturn(query(List.of(u))).when(em).createQuery(anyString(), eq(Usuario.class));
            DtUsuario dt = new DtUsuario("nick", null, "nuevo@x.uy", "n", "a", null, new Date(), null, TipoUsuario.ESTUDIANTE);
            if (web) ic.modificarUsuario(dt, null, null);
            else ic.modificarUsuario(dt);
            assertEquals("nuevo@x.uy", u.getEmail());
            verify(tx).commit();
        }
    }

    @Test
    public void testListarCursosPorUsuarioConColeccionesNulas() throws Exception {
        EntityManager em = em(false);
        when(em.find(Usuario.class, "doc")).thenReturn(new Docente("doc", "p", "d@x", "n", "a", new Date(), "i", null));
        doReturn(query(List.of(cursoSinColecciones("c")))).when(em).createQuery(anyString(), eq(Curso.class));

        List<DtCurso> r = ic.listarCursosPorUsuario("doc");
        assertEquals(1, r.size());
        assertTrue(r.get(0).getNamePrevias().isEmpty());
        assertTrue(r.get(0).getNameCategorias().isEmpty());
    }

    @Test
    public void testObtenerDatosCursoConColeccionesNulas() throws Exception {
        EntityManager em = em(false);
        Curso previa = cursoSinColecciones("previa");
        Curso c = new Curso("c", "d", "1", 1, 1, "u", new Date(), instituto, null, List.of(previa), "c.png");
        Edicion ed = new Edicion("ed", 1, hoy, hoy, hoy, c, null, "e.png");
        when(em.find(Curso.class, "c")).thenReturn(c);
        when(em.find(Curso.class, "c2")).thenReturn(cursoSinColecciones("c2"));
        doReturn(query(List.of(ed))).when(em).createQuery(anyString(), eq(Edicion.class));
        doReturn(query(List.of(programa("pg", null)))).when(em).createQuery(anyString(), eq(ProgramaFormacion.class));

        DtConsultaCurso r = ic.obtenerDatosCurso("c");
        assertTrue(r.getNameCategorias().isEmpty());
        assertEquals(java.util.Set.of("previa"), r.getNamePrevias());
        assertTrue(r.getPrevias().get(0).getNamePrevias().isEmpty());
        assertTrue(r.getEdiciones().get(0).getDocentes().isEmpty());
        assertTrue(r.getProgramas().get(0).getNameCursos().isEmpty());

        DtConsultaCurso r2 = ic.obtenerDatosCurso("c2");
        assertTrue(r2.getNamePrevias().isEmpty());
    }

    @Test
    public void testListarCursosYEdicionesConColeccionesNulas() throws Exception {
        EntityManager em = em(false);
        Curso c = cursoSinColecciones("c");
        doReturn(query(List.of(c))).when(em).createQuery(anyString(), eq(Curso.class));
        doReturn(query(List.of(new Edicion("ed", 1, hoy, hoy, hoy, c, null, "e.png")))).when(em).createQuery(anyString(), eq(Edicion.class));
        when(em.find(Usuario.class, "est")).thenReturn(new Estudiante("est", "p", "e@x", "n", "a", new Date(), "i"));

        List<DtCurso> cursos = ic.listarCursos();
        assertTrue(cursos.get(0).getNamePrevias().isEmpty());
        assertTrue(cursos.get(0).getNameCategorias().isEmpty());

        List<DtEdicion> ediciones = ic.listarEdicionesPorUsuario("est");
        assertTrue(ediciones.get(0).getNameDocentes().isEmpty());
        assertTrue(ediciones.get(0).getCurso().getNamePrevias().isEmpty());
    }

    @Test
    public void testProgramasConColeccionesNulas() throws Exception {
        EntityManager em = em(false);
        ProgramaFormacion sinCursos = programa("sinCursos", null);
        ProgramaFormacion conCurso = programa("conCurso", List.of(cursoSinInstituto("c")));
        ProgramaFormacion conCursoSinColecciones = programa("conCursoSinColecciones", List.of(cursoSinColecciones("c2")));
        doReturn(query(List.of(sinCursos, conCurso))).when(em).createQuery(anyString(), eq(ProgramaFormacion.class));
        when(em.find(Usuario.class, "est")).thenReturn(new Estudiante("est", "p", "e@x", "n", "a", new Date(), "i"));
        when(em.find(ProgramaFormacion.class, "sinCursos")).thenReturn(sinCursos);
        when(em.find(ProgramaFormacion.class, "conCurso")).thenReturn(conCurso);
        when(em.find(ProgramaFormacion.class, "conCursoSinColecciones")).thenReturn(conCursoSinColecciones);

        List<DtPrograma> todos = ic.listarProgramas();
        assertTrue(todos.get(0).getCursos().isEmpty());
        assertNull(todos.get(1).getCursos().get(0).getInstituto());
        assertTrue(todos.get(1).getCategorias().isEmpty());

        List<DtPrograma> delEstudiante = ic.listarProgramasPorUsuario("est");
        assertTrue(delEstudiante.get(0).getCursos().isEmpty());
        assertNull(delEstudiante.get(1).getCursos().get(0).getInstituto());

        assertTrue(ic.buscarPrograma("sinCursos").getCursos().isEmpty());
        DtCurso dc = ic.buscarPrograma("conCurso").getCursos().get(0);
        assertNull(dc.getInstituto());
        assertTrue(dc.getNamePrevias().isEmpty());
        assertTrue(dc.getNameCategorias().isEmpty());

        DtCurso dc2 = ic.listarCursosPorPrograma("conCursoSinColecciones").get(0);
        assertTrue(dc2.getNamePrevias().isEmpty());
        assertTrue(dc2.getNameCategorias().isEmpty());
    }

    @Test
    public void testSeguidosYSeguidoresNulos() throws Exception {
        EntityManager em = em(false);
        Usuario u = mock(Usuario.class);
        when(u.getSeguidos()).thenReturn(null);
        when(u.getSeguidores()).thenReturn(null);
        when(em.find(Usuario.class, "u")).thenReturn(u);

        assertTrue(ic.listarSeguidos("u").isEmpty());
        assertTrue(ic.listarSeguidores("u").isEmpty());
    }
}
