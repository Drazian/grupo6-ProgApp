package edu.edext.datatypes;

import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DtUsuarioTest {

    private static DtUsuario usuario(String nick) {
        return new DtUsuario(nick, "p", nick + "@x", "n", "a", "i.png", new Date(), null, TipoUsuario.ESTUDIANTE);
    }

    @Test
    public void testSeguidosYSeguidores() {
        DtUsuario sinListas = usuario("a");
        assertTrue(sinListas.getSeguidos().isEmpty());
        assertTrue(sinListas.getSeguidores().isEmpty());

        DtUsuario conListas = new DtUsuario("b", null, null, null, null, null, null, List.of("i"), TipoUsuario.DOCENTE,
                List.of("x"), List.of("y", "z"));
        assertEquals(List.of("x"), conListas.getSeguidos());
        assertEquals(List.of("y", "z"), conListas.getSeguidores());
        assertEquals(List.of("i"), conListas.getInstitutos());
        assertEquals(TipoUsuario.DOCENTE, conListas.getTipoUsuario());
    }

    @Test
    public void testToStringEqualsYHashCodePorNickname() {
        DtUsuario a = usuario("a");
        assertEquals("a", a.toString());
        assertTrue(a.equals(a));
        assertTrue(a.equals(new DtUsuario("a", null, null, null, null, null, null, null, null)));
        assertEquals(a.hashCode(), usuario("a").hashCode());
        assertFalse(a.equals(usuario("b")));
        assertFalse(a.equals(null));
        assertFalse(a.equals("a"));
    }
}
