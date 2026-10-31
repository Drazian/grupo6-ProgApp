package edu.edext.logica;

import edu.edext.datatypes.DtSessionUsuario;
import edu.edext.datatypes.TipoUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static edu.edext.logica.DatosUnicos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Validacion de credenciales (login por nickname o email). */
public class UsuarioHelperTest {

    @Test
    public void testValidarContraLaBase() throws Exception {
        IControlador ic = ic();
        String est = estudiante(ic);
        String doc = docente(ic);
        UsuarioHelper h = new UsuarioHelper(emf());

        DtSessionUsuario s = h.validaNick(est, PASSWORD);
        assertEquals(est, s.getNickname());
        assertEquals(est + "@test.uy", s.getEmail());
        assertEquals("img.png", s.getImagen());
        assertEquals(TipoUsuario.ESTUDIANTE, s.getTipoUsuario());

        DtSessionUsuario sd = h.validarEmail(doc + "@test.uy", PASSWORD);
        assertEquals(doc, sd.getNickname());
        assertEquals(TipoUsuario.DOCENTE, sd.getTipoUsuario());

        assertNull(h.validaNick(est, "incorrecta"));
        assertNull(h.validaNick(nombre("noexiste"), PASSWORD));
        assertNull(h.validarEmail(est + "@test.uy", "incorrecta"));
        assertNull(h.validarEmail(nombre("noexiste") + "@x.uy", PASSWORD));
        h.kill(); // ya no tiene EntityManager
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorDePersistenciaDevuelveNulo() {
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        EntityManager em = mock(EntityManager.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(any(Class.class), any())).thenThrow(new IllegalStateException("falla"));
        when(em.createQuery(anyString(), any(Class.class))).thenThrow(new IllegalStateException("falla"));

        UsuarioHelper h = new UsuarioHelper(emf);
        assertNull(h.validaNick("u", "p"));
        assertNull(h.validarEmail("u@x", "p"));
        verify(em, never()).close(); // el EntityManager simulado no esta abierto
    }

    @Test
    public void testKillCierraElEntityManagerAbierto() throws Exception {
        EntityManager em = mock(EntityManager.class);
        when(em.isOpen()).thenReturn(true);
        UsuarioHelper h = new UsuarioHelper(mock(EntityManagerFactory.class));
        Field db = UsuarioHelper.class.getDeclaredField("db");
        db.setAccessible(true);
        db.set(h, em);
        h.kill();
        verify(em).close();
        assertNull(db.get(h));
    }
}
