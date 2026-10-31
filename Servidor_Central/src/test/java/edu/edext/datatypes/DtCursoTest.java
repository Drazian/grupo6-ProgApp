package edu.edext.datatypes;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DtCursoTest {

    @Test
    public void testConstructorConListas() {
        Date fecha = new Date();
        DtInstituto inst = new DtInstituto("INCO");
        List<DtCurso> previas = List.of(new DtCurso("P1", null), new DtCurso("P2", null));
        List<DtCategoria> categorias = List.of(new DtCategoria("Social"));
        DtCurso c = new DtCurso("C", "desc", "2 meses", 40, 8, "http://c", fecha, inst, previas, categorias, "c.png");

        assertEquals("C", c.getNombre());
        assertEquals("desc", c.getDescripcion());
        assertEquals("2 meses", c.getDuracion());
        assertEquals(40, c.getCantidadHoras());
        assertEquals(8, c.getCreditos());
        assertEquals("http://c", c.getUrl());
        assertEquals(fecha, c.getFechaRegistro());
        assertSame(inst, c.getInstituto());
        assertEquals("c.png", c.getImagen());
        assertSame(previas, c.getPrevias());
        assertSame(categorias, c.getCategorias());
        assertEquals(Set.of("P1", "P2"), c.getNamePrevias());
        assertEquals(Set.of("Social"), c.getNameCategorias());
        assertEquals(2, c.getPrevias_toArray().size());
        assertEquals(List.of("Social"), c.getCategorias_toArray());
    }

    @Test
    public void testConstructoresSimples() {
        DtCurso c = new DtCurso("C", "img.png");
        assertTrue(c.getNamePrevias().isEmpty());
        assertTrue(c.getNameCategorias().isEmpty());
        assertNull(c.getPrevias());
        assertNull(c.getCategorias());

        DtCurso conNombres = new DtCurso("C", null, null, 0, 0, null, null, null, Set.of("P"), Set.of("Cat"), null);
        assertEquals(Set.of("P"), conNombres.getNamePrevias());
        assertEquals(Set.of("Cat"), conNombres.getNameCategorias());
    }
}
