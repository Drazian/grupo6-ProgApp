package edu.edext.datatypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DtProgramaTest {

    private final LocalDate reg = LocalDate.of(2026, 1, 1), ini = LocalDate.of(2026, 2, 1), fin = LocalDate.of(2026, 6, 1);
    private final Set<String> nombres = Set.of("C1");
    private final List<DtCurso> cursos = List.of(new DtCurso("C2", null));

    private void fechas(DtPrograma p) {
        assertEquals("P", p.getNombre());
        assertEquals(reg, p.getFechaRegistro());
        assertEquals(ini, p.getFechaInicio());
        assertEquals(fin, p.getFechaFin());
    }

    @Test
    public void testConstructoresSinDescripcion() {
        DtPrograma a = new DtPrograma("P", reg, ini, fin, "a.png");
        fechas(a);
        assertNull(a.getDescripcion());
        assertEquals("a.png", a.getImagen());
        assertTrue(a.getNameCursos().isEmpty());

        DtPrograma b = new DtPrograma("P", reg, ini, fin, nombres);
        fechas(b);
        assertEquals(nombres, b.getNameCursos());
        assertNull(b.getImagen());

        DtPrograma c = new DtPrograma("P", reg, ini, fin, nombres, "c.png");
        assertEquals(nombres, c.getNameCursos());
        assertEquals("c.png", c.getImagen());

        DtPrograma d = new DtPrograma("P", reg, ini, fin, cursos);
        assertSame(cursos, d.getCursos());
        assertEquals(Set.of("C2"), d.getNameCursos());

        DtPrograma e = new DtPrograma("P", reg, ini, fin, cursos, "e.png");
        assertEquals(List.of("C2"), e.getCursos_toArray());
        assertEquals("e.png", e.getImagen());
    }

    @Test
    public void testConstructoresConDescripcion() {
        DtPrograma a = new DtPrograma("P", "desc", reg, ini, fin, "a.png");
        fechas(a);
        assertEquals("desc", a.getDescripcion());
        assertEquals("a.png", a.getImagen());
        assertNull(a.getDocente());

        DtPrograma b = new DtPrograma("P", "desc", reg, ini, fin, nombres);
        assertEquals(nombres, b.getNameCursos());

        DtPrograma c = new DtPrograma("P", "desc", reg, ini, fin, cursos);
        assertEquals(Set.of("C2"), c.getNameCursos());

        DtPrograma d = new DtPrograma("P", "desc", reg, ini, fin, "d.png", "docente");
        assertEquals("docente", d.getDocente());
    }

    @Test
    public void testCategoriasSalenDeLosCursos() {
        DtCurso conCategorias = new DtCurso("C", null, null, 0, 0, null, null, null, Set.of(), Set.of("Social", "Educativo"), null);
        DtCurso sinCategorias = mock(DtCurso.class);
        when(sinCategorias.getNameCategorias()).thenReturn(null);
        DtPrograma p = new DtPrograma("P", "d", reg, ini, fin, List.of(conCategorias, sinCategorias));
        assertEquals(Set.of("Social", "Educativo"), p.getCategorias());
        assertEquals(2, p.getCategorias_toArray().size());

        assertTrue(new DtPrograma("P", reg, ini, fin).getCategorias().isEmpty(), "Sin cursos no hay categorias");
    }
}
