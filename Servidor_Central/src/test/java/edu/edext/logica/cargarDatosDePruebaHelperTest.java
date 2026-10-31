package edu.edext.logica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Carga de datos de prueba. En H2 esta clase arranca con la base vacia (JVM propia) y prueba
 * la carga inicial; contra una base con datos solo verifica que la carga sea idempotente.
 */
public class cargarDatosDePruebaHelperTest {

    private EntityManager em;
    private EntityTransaction tx;

    private EntityManagerFactory emfSimulado(boolean transaccionActiva) {
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(tx.isActive()).thenReturn(transaccionActiva);
        return emf;
    }

    private static boolean yaCargados(EntityManagerFactory emf) {
        EntityManager db = emf.createEntityManager();
        try {
            return db.find(Instituto.class, "INCO") != null
                    || db.find(Categoria.class, "Social") != null
                    || db.find(Usuario.class, "eleven11") != null
                    || db.find(Curso.class, "Talleres plenarios") != null
                    || db.find(Edicion.class, "Flor del Ceibo - 2010") != null
                    || db.find(ProgramaFormacion.class, "EFI Ingeniería Mecánica") != null;
        } finally {
            db.close();
        }
    }

    @Test
    public void testCargaInicialEIdempotente() throws Exception {
        EntityManagerFactory emf = DatosUnicos.emf();
        boolean habiaDatos = yaCargados(emf);

        cargarDatosDePruebaHelper h = new cargarDatosDePruebaHelper(emf);
        assertEquals(habiaDatos ? 1 : 0, h.cargar(), "0 si cargo los datos, 1 si ya estaban");
        h.kill(); // el EntityManager ya quedo cerrado
        h.kill(); // y ahora es nulo

        assertTrue(yaCargados(emf));
        IControlador ic = DatosUnicos.ic();
        assertTrue(ic.existeUsuario("eleven11"));
        assertEquals("IMPII", ic.obtenerInstitutoPorCurso("Dalavuelta"));

        cargarDatosDePruebaHelper otra = new cargarDatosDePruebaHelper(emf);
        assertEquals(1, otra.cargar(), "La segunda carga no hace nada");
    }

    @Test
    public void testKillConEntityManagerAbierto() {
        EntityManagerFactory emf = emfSimulado(true);
        when(em.isOpen()).thenReturn(true);
        new cargarDatosDePruebaHelper(emf).kill();
        verify(em).close();
    }

    @Test
    public void testDetectaCadaMarcaDeDatosCargados() {
        // Cada una de las marcas por separado alcanza para considerar los datos ya cargados
        @SuppressWarnings("unchecked")
        Consumer<EntityManager>[] marcas = new Consumer[]{
            (Consumer<EntityManager>) e -> when(e.find(Instituto.class, "INCO")).thenReturn(new Instituto("INCO")),
            (Consumer<EntityManager>) e -> when(e.find(Categoria.class, "Social")).thenReturn(new Categoria("Social")),
            (Consumer<EntityManager>) e -> when(e.find(Usuario.class, "eleven11")).thenReturn(new Estudiante()),
            (Consumer<EntityManager>) e -> when(e.find(Curso.class, "Talleres plenarios")).thenReturn(new Curso()),
            (Consumer<EntityManager>) e -> when(e.find(Edicion.class, "Flor del Ceibo - 2010")).thenReturn(new Edicion()),
            (Consumer<EntityManager>) e -> when(e.find(ProgramaFormacion.class, "EFI Ingeniería Mecánica")).thenReturn(new ProgramaFormacion())
        };
        for (Consumer<EntityManager> marca : marcas) {
            EntityManagerFactory emf = emfSimulado(true);
            marca.accept(em);
            assertEquals(1, new cargarDatosDePruebaHelper(emf).cargar());
            verify(tx).rollback();
            verify(em, never()).persist(any());
            verify(em).close();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErroresDuranteLaCarga() {
        // Error sin mensaje, con transaccion activa
        EntityManagerFactory emf = emfSimulado(true);
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException());
        assertEquals(-1, new cargarDatosDePruebaHelper(emf).cargar());
        verify(tx).rollback();

        // Error con otro mensaje, sin transaccion activa
        emf = emfSimulado(false);
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException("otro error"));
        assertEquals(-1, new cargarDatosDePruebaHelper(emf).cargar());
        verify(tx, never()).rollback();
        verify(em).close();
    }
}
