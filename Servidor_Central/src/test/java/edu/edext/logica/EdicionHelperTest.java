package edu.edext.logica;

import edu.edext.datatypes.DtEdicion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import static edu.edext.logica.DatosUnicos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Consultas de ediciones. */
public class EdicionHelperTest {

    @Test
    public void testConsultasContraLaBase() throws Exception {
        IControlador ic = ic();
        EntityManagerFactory emf = emf();
        String inst = instituto(ic);
        String curso = curso(ic, inst);
        String doc = docente(ic, inst);
        String ed = edicionVigente(ic, curso, 7, doc);

        EdicionHelper h = new EdicionHelper(emf, ed);
        DtEdicion d = h.getEdicion();
        assertEquals(ed, d.getNombre());
        assertEquals(7, d.getCupo());
        assertEquals(curso, d.getCurso().getNombre());
        assertEquals(inst, d.getCurso().getInstituto().getNombre());
        assertEquals(doc, d.getDocentes().get(0).getNickname());
        assertEquals("img.png", d.getDocentes().get(0).getImagen());
        h.kill(); // el EntityManager quedo abierto para la carga LAZY: se cierra
        h.kill();

        EdicionHelper noExiste = new EdicionHelper(emf, nombre("NoEd"));
        assertNull(noExiste.getEdicion());
        noExiste.kill();

        EdicionHelper porCurso = new EdicionHelper(emf, curso);
        assertEquals(List.of(ed), porCurso.listarEdicionPorCurso());
        assertEquals(ed, porCurso.getListEditPorCurso().get(0).getNombre());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorDePersistencia() {
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        EntityManager em = mock(EntityManager.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException("falla"));
        when(em.createQuery(anyString(), any(Class.class))).thenThrow(new IllegalStateException("falla"));

        EdicionHelper h = new EdicionHelper(emf, "x");
        assertTrue(h.listarEdicionPorCurso().isEmpty());
        assertTrue(h.getListEditPorCurso().isEmpty());
        assertNull(h.getEdicion());
        h.kill(); // EntityManager simulado ya cerrado: no se vuelve a cerrar
        verify(em, times(2)).close(); // solo los dos cierres de las consultas por curso
    }
}
