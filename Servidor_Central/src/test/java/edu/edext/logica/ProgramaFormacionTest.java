package edu.edext.logica;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProgramaFormacionTest {

    private final LocalDate hoy = LocalDate.now();

    private ProgramaFormacion programa(String nombre) {
        return new ProgramaFormacion(nombre, "d", hoy, hoy, hoy, new ArrayList<>(), null, "p.png");
    }

    @Test
    public void testConstructorConDocente() {
        Docente doc = new Docente("doc", "p", "d@x", "n", "a", null, null, null);
        List<Categoria> categorias = List.of(new Categoria("Social"));
        ProgramaFormacion p = new ProgramaFormacion("P", "d", hoy, hoy, hoy, new ArrayList<>(), categorias, "p.png", doc);
        assertSame(doc, p.getDocente());
        assertSame(categorias, p.getCategorias());
        assertNull(programa("Q").getDocente(), "Sin docente por defecto");
    }

    @Test
    public void testAgregarCursoSinRepetir() {
        ProgramaFormacion p = programa("P");
        Curso c = new Curso("C", "d", "1", 1, 1, "u", null, null, null, null, null);
        p.agregarCurso(c);
        p.agregarCurso(c);
        assertEquals(List.of(c), p.getCursos());
    }

    @Test
    public void testEqualsYHashCodePorNombre() {
        ProgramaFormacion a = programa("A");
        assertTrue(a.equals(a));
        assertTrue(a.equals(programa("A")));
        assertEquals(a.hashCode(), programa("A").hashCode());
        assertFalse(a.equals(programa("B")));
        assertFalse(a.equals(null));
        assertFalse(a.equals("A"));
    }
}
