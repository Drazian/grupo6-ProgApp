package edu.edext.logica;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EdicionTest {

    private static Edicion edicion(String nombre) {
        LocalDate hoy = LocalDate.now();
        return new Edicion(nombre, 10, hoy, hoy, hoy.plusMonths(1), new Curso(), List.of(), "e.png");
    }

    @Test
    public void testGettersYSetters() {
        LocalDate pub = LocalDate.of(2026, 1, 1), ini = LocalDate.of(2026, 2, 1), fin = LocalDate.of(2026, 3, 1);
        Curso curso = new Curso();
        Edicion e = new Edicion();
        e.setNombre("E");
        e.setCupo(5);
        e.setFechaPublicacion(pub);
        e.setFechaInicio(ini);
        e.setFechaFin(fin);
        e.setCurso(curso);
        e.setDocentes(List.of());
        e.setImagen("e.png");
        assertEquals("E", e.getNombre());
        assertEquals(5, e.getCupo());
        assertEquals(pub, e.getFechaPublicacion());
        assertEquals(ini, e.getFechaInicio());
        assertEquals(fin, e.getFechaFin());
        assertSame(curso, e.getCurso());
        assertTrue(e.getDocentes().isEmpty());
        assertEquals("e.png", e.getImagen());
    }

    @Test
    public void testEqualsYHashCodePorNombre() {
        Edicion a = edicion("A");
        assertTrue(a.equals(a));
        assertTrue(a.equals(edicion("A")));
        assertEquals(a.hashCode(), edicion("A").hashCode());
        assertFalse(a.equals(edicion("B")));
        assertFalse(a.equals(null));
        assertFalse(a.equals("A"));
    }
}
