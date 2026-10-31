package edu.edext.datatypes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DtConsultaCursoTest {

    private final Date fecha = new Date();

    @Test
    public void testConstructorConListas() {
        LocalDate hoy = LocalDate.now();
        List<DtEdicion> ediciones = List.of(new DtEdicion("E1", 1, hoy, hoy, hoy));
        List<DtPrograma> programas = List.of(new DtPrograma("P1", hoy, hoy, hoy));
        List<DtCategoria> categorias = List.of(new DtCategoria("Cat"));
        List<DtCurso> previas = List.of(new DtCurso("Previa", null));
        DtConsultaCurso c = new DtConsultaCurso("C", "desc", "1 mes", 20, 4, "http://c", fecha,
                ediciones, programas, categorias, previas, "c.png");

        assertEquals("C", c.getNombre());
        assertEquals("desc", c.getDescripcion());
        assertEquals("1 mes", c.getDuracion());
        assertEquals(20, c.getCantidadHoras());
        assertEquals(4, c.getCreditos());
        assertEquals("http://c", c.getUrl());
        assertEquals(fecha, c.getFechaRegistro());
        assertEquals("c.png", c.getImagen());
        assertSame(ediciones, c.getEdiciones());
        assertSame(programas, c.getProgramas());
        assertSame(categorias, c.getCategorias());
        assertSame(previas, c.getPrevias());
        assertEquals(List.of("E1"), c.getEdiciones_toArray());
        assertEquals(List.of("P1"), c.getProgramas_toArray());
        assertEquals(List.of("Cat"), c.getCategorias_toArray());
        assertEquals(List.of("Previa"), c.getPrevias_toArray());
    }

    @Test
    public void testConstructorConListasNulas() {
        DtConsultaCurso c = new DtConsultaCurso("C", "d", "1", 1, 1, "u", fecha,
                (List<DtEdicion>) null, (List<DtPrograma>) null, (List<DtCategoria>) null, (List<DtCurso>) null, null);
        assertTrue(c.getNameEdiciones().isEmpty());
        assertTrue(c.getNameProgramas().isEmpty());
        assertTrue(c.getNameCategorias().isEmpty());
        assertTrue(c.getNamePrevias().isEmpty());
    }

    @Test
    public void testConstructorConNombres() {
        DtConsultaCurso c = new DtConsultaCurso("C", "d", "1", 1, 1, "u", fecha,
                Set.of("E"), Set.of("P"), Set.of("Cat"), Set.of("Previa"), "c.png");
        assertEquals(Set.of("E"), c.getNameEdiciones());
        assertEquals(Set.of("P"), c.getNameProgramas());
        assertEquals(Set.of("Cat"), c.getNameCategorias());
        assertEquals(Set.of("Previa"), c.getNamePrevias());
        assertNull(c.getEdiciones());
        assertNull(c.getPrevias());
    }
}
