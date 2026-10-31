package edu.edext.datatypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DtEdicionTest {

    private final LocalDate hoy = LocalDate.now();

    @Test
    public void testNombresDeDocentesTienenPrioridadSobreLaLista() {
        DtUsuario doc = new DtUsuario("doc", null, null, "Ana", "Perez", null, null, null, TipoUsuario.DOCENTE);
        DtEdicion e = new DtEdicion("E", 3, hoy, hoy, hoy, null, List.of(doc), Set.of("otro"));
        assertEquals(Set.of("otro"), e.getNameDocentes());
        assertEquals(List.of(doc), e.getDocentes());
        assertEquals(List.of("otro"), e.getDocentes_toArray());
        assertNull(e.getImagen());
    }

    @Test
    public void testDocentesSinNicknameUsanNombreYApellido() {
        DtUsuario conNick = new DtUsuario("doc", null, null, "Ana", "Perez", null, null, null, null);
        DtUsuario sinNick = new DtUsuario(null, null, null, "Juan", "Gomez", null, null, null, null);
        DtEdicion e = new DtEdicion("E", 3, hoy, hoy, hoy, null, List.of(conNick, sinNick), "e.png");
        assertEquals(Set.of("doc", "Juan Gomez"), e.getNameDocentes());
        assertEquals("e.png", e.getImagen());
    }

    @Test
    public void testSinDocentes() {
        DtEdicion e = new DtEdicion("E", null, hoy, hoy, hoy, "e.png");
        assertTrue(e.getDocentes().isEmpty());
        assertTrue(e.getNameDocentes().isEmpty());
        assertNull(e.getCupo());
        assertNull(e.getCurso());
    }
}
