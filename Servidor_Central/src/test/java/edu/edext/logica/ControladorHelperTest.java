package edu.edext.logica;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ControladorHelperTest {

    @Test
    public void testKill() {
        EntityManagerFactory emf = mock(EntityManagerFactory.class);
        ControladorHelper h = new ControladorHelper(emf);
        h.kill(); // no abre EntityManager: no hay nada que cerrar
        h.kill();
        verifyNoInteractions(emf);
    }
}
