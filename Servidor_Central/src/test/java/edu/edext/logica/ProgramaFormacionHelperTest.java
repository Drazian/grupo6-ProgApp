package edu.edext.logica;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtPrograma;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static edu.edext.logica.DatosUnicos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Alta de programas de formacion: validaciones y persistencia. */
public class ProgramaFormacionHelperTest {

    private final LocalDate hoy = LocalDate.now();

    /** Programa con cursos (para las categorias, que salen de los DtCurso) y nombres de cursos. */
    private DtPrograma dto(String nombre, Set<String> categoriasDeLosCursos, Set<String> cursos, String docente) {
        DtCurso dc = new DtCurso("x", null, null, 0, 0, null, null, null, set(), categoriasDeLosCursos, null);
        return new DtPrograma(nombre, "desc", hoy, hoy, hoy.plusMonths(3), List.of(dc), cursos, "p.png", docente);
    }

    private static boolean crear(EntityManagerFactory emf, DtPrograma dto) {
        ProgramaFormacionHelper h = new ProgramaFormacionHelper(emf, dto);
        try {
            return h.validate() && h.persist();
        } finally {
            h.kill();
        }
    }

    @Test
    public void testValidaciones() throws Exception {
        IControlador ic = ic();
        EntityManagerFactory emf = emf();
        String existente = programa(ic, set(), null);

        assertFalse(new ProgramaFormacionHelper(emf, new DtPrograma(existente, hoy, hoy, hoy)).validate(), "Ya existe");
        assertFalse(new ProgramaFormacionHelper(emf, new DtPrograma(nombre("P"), null, hoy, hoy)).validate(), "Sin fecha de registro");
        assertFalse(new ProgramaFormacionHelper(emf, new DtPrograma(nombre("P"), hoy, null, hoy)).validate(), "Sin fecha de inicio");
        assertFalse(new ProgramaFormacionHelper(emf, new DtPrograma(nombre("P"), hoy, hoy, null)).validate(), "Sin fecha de fin");
        assertFalse(new ProgramaFormacionHelper(emf, new DtPrograma(nombre("P"), hoy, hoy.plusDays(1), hoy)).validate(), "Inicio posterior al fin");
        assertTrue(new ProgramaFormacionHelper(emf, new DtPrograma(nombre("P"), hoy, hoy, hoy)).validate());
    }

    @Test
    public void testPersistirContraLaBase() throws Exception {
        IControlador ic = ic();
        EntityManagerFactory emf = emf();
        String inst = instituto(ic);
        String cat = categoria(ic);
        String curso = curso(ic, inst, set(), set(cat), "c.png");
        String doc = docente(ic, inst);

        String p1 = nombre("P");
        assertTrue(crear(emf, dto(p1, set(cat), set(curso), doc)));
        DtPrograma creado = ic.buscarPrograma(p1);
        assertEquals(set(curso), creado.getNameCursos());
        assertEquals("p.png", creado.getImagen());
        assertTrue(ic.listarProgramasPorUsuario(doc).stream().anyMatch(p -> p.getNombre().equals(p1)), "Queda asociado al docente");

        // Docente en blanco: el programa queda sin docente
        String p2 = nombre("P");
        assertTrue(crear(emf, dto(p2, set(), set(), "   ")));
        assertNotNull(ic.buscarPrograma(p2));

        // Categoria o curso inexistentes: no se crea
        String p3 = nombre("P");
        assertFalse(crear(emf, dto(p3, set(nombre("NoCat")), set(curso), null)));
        String p4 = nombre("P");
        assertFalse(crear(emf, dto(p4, set(cat), set(nombre("NoCurso")), null)));
        assertThrows(Exception.class, () -> ic.buscarPrograma(p3));
        assertThrows(Exception.class, () -> ic.buscarPrograma(p4));
    }

    //******************************** Simulados *********************************//

    @Test
    public void testNoSePuedeIniciarLaTransaccion() {
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        EntityManager em = mock(EntityManager.class);
        EntityTransaction tx = mock(EntityTransaction.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        doThrow(new IllegalStateException("falla")).when(tx).begin();

        ProgramaFormacionHelper h = new ProgramaFormacionHelper(emf, new DtPrograma("p", hoy, hoy, hoy));
        assertFalse(h.persist());
        verify(em, never()).persist(any());
        h.kill(); // EntityManager simulado no abierto: no se cierra
        verify(em, never()).close();
    }

    @Test
    public void testFallaAlPersistirSinTransaccionActiva() {
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        EntityManager em = mock(EntityManager.class);
        EntityTransaction tx = mock(EntityTransaction.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(tx.isActive()).thenReturn(false);
        doThrow(new IllegalStateException("falla")).when(em).persist(any());

        assertFalse(new ProgramaFormacionHelper(emf, new DtPrograma("p", hoy, hoy, hoy)).persist());
        verify(tx, never()).rollback();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExistenciaConError() {
        // Si no se puede consultar la existencia se considera que no existe
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        EntityManager em = mock(EntityManager.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException("falla"));
        assertTrue(new ProgramaFormacionHelper(emf, new DtPrograma("p", hoy, hoy, hoy)).validate());
        verify(em, never()).close(); // EntityManager simulado no abierto

        // Sin EntityManager (la fabrica devuelve null)
        EntityManagerFactory sinEm = mock(EntityManagerFactory.class);
        assertTrue(new ProgramaFormacionHelper(sinEm, new DtPrograma("p", hoy, hoy, hoy)).validate());
    }

    @Test
    public void testKillSinEntityManager() {
        new ProgramaFormacionHelper(mock(EntityManagerFactory.class), null).kill();
    }
}
