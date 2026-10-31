package edu.edext.logica;

import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtSessionUsuario;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static edu.edext.logica.DatosUnicos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Caminos del Controlador contra la base (H2 en los tests) que no cubre ControladorTest.
 * Todo lo que se crea tiene nombre unico; no depende de los datos que ya tenga la base.
 */
public class ControladorCoberturaTest {

    private IControlador ic;

    @BeforeEach
    public void setUp() {
        ic = Fabrica.getInstance().getIControlador();
    }

    private static Set<String> nombres(List<?> lista) {
        Set<String> ret = new HashSet<>();
        for (Object o : lista) {
            if (o instanceof DtCurso c) ret.add(c.getNombre());
            else if (o instanceof DtEdicion e) ret.add(e.getNombre());
            else if (o instanceof DtPrograma p) ret.add(p.getNombre());
            else ret.add(String.valueOf(o));
        }
        return ret;
    }

    private static LocalDate local(Date fecha) {
        return new java.sql.Date(fecha.getTime()).toLocalDate();
    }

    //******************************** Usuarios ********************************//

    @Test
    public void testModificarUsuarioSinCambios() throws Exception {
        String nick = estudiante(ic);
        DtUsuario antes = ic.obtenerUsuario(nick);

        // Todo nulo: no cambia nada
        ic.modificarUsuario(new DtUsuario(nick, null, null, null, null, null, null, null, null));
        // Todo en blanco y el mismo email en mayusculas: no cambia nada
        ic.modificarUsuario(new DtUsuario(nick, "  ", antes.getEmail().toUpperCase(), " ", " ", " ", null, null, TipoUsuario.ESTUDIANTE));
        // La misma contraseña ya hasheada: no se vuelve a hashear
        ic.modificarUsuario(new DtUsuario(nick, antes.getPassword(), null, null, null, null, null, null, null));

        DtUsuario despues = ic.obtenerUsuario(nick);
        assertEquals(antes.getNombre(), despues.getNombre());
        assertEquals(antes.getApellido(), despues.getApellido());
        assertEquals(antes.getImagen(), despues.getImagen());
        assertEquals(antes.getEmail(), despues.getEmail());
        assertEquals(antes.getPassword(), despues.getPassword());
        assertEquals(local(antes.getfNacimiento()), local(despues.getfNacimiento()));
    }

    @Test
    public void testModificarUsuarioTodosLosCampos() throws Exception {
        String nick = estudiante(ic);
        String email = nombre("mail") + "@nuevo.uy";
        Date fecha = java.sql.Date.valueOf(LocalDate.of(1990, 5, 5));

        ic.modificarUsuario(new DtUsuario(nick, "otraClave", email, "Nuevo", "Apellido", "nueva.png", fecha, null, TipoUsuario.ESTUDIANTE));

        DtUsuario u = ic.obtenerUsuario(nick);
        assertEquals("Nuevo", u.getNombre());
        assertEquals("Apellido", u.getApellido());
        assertEquals("nueva.png", u.getImagen());
        assertEquals(email, u.getEmail());
        assertEquals(LocalDate.of(1990, 5, 5), local(u.getfNacimiento()));
        assertNotNull(ic.validarNick(nick, "otraClave"), "La nueva contraseña se guarda hasheada y valida");
        assertNull(ic.validarNick(nick, PASSWORD), "La contraseña anterior ya no sirve");
    }

    @Test
    public void testModificarUsuarioEmailDeOtroUsuario() throws Exception {
        String a = estudiante(ic);
        String b = estudiante(ic);
        Exception e = assertThrows(Exception.class,
                () -> ic.modificarUsuario(new DtUsuario(a, null, b + "@test.uy", null, null, null, null, null, null)));
        assertTrue(e.getMessage().contains("Email"));
        assertEquals(a + "@test.uy", ic.obtenerUsuario(a).getEmail(), "No se modifica el email");
    }

    @Test
    public void testModificarUsuarioInstitutosDocente() throws Exception {
        String i1 = instituto(ic);
        String i2 = instituto(ic);
        String doc = docente(ic, i1);

        // DOCENTE con institutos nulos: no cambia
        ic.modificarUsuario(new DtUsuario(doc, null, null, null, null, null, null, null, TipoUsuario.DOCENTE));
        assertEquals(List.of(i1), ic.obtenerUsuario(doc).getInstitutos());

        // Instituto inexistente: error y no cambia
        assertThrows(Exception.class, () -> ic.modificarUsuario(
                new DtUsuario(doc, null, null, null, null, null, null, lista(nombre("NoExiste")), TipoUsuario.DOCENTE)));
        assertEquals(List.of(i1), ic.obtenerUsuario(doc).getInstitutos());

        // Cambio de instituto
        ic.modificarUsuario(new DtUsuario(doc, null, null, null, null, null, null, lista(i2), TipoUsuario.DOCENTE));
        assertEquals(List.of(i2), ic.obtenerUsuario(doc).getInstitutos());

        // Tipo ESTUDIANTE: los institutos se ignoran
        ic.modificarUsuario(new DtUsuario(doc, null, null, null, null, null, null, lista(i1), TipoUsuario.ESTUDIANTE));
        assertEquals(List.of(i2), ic.obtenerUsuario(doc).getInstitutos());

        // Un estudiante con tipo DOCENTE en el Dt: se ignora (no es Docente)
        String est = estudiante(ic);
        ic.modificarUsuario(new DtUsuario(est, null, null, null, null, null, null, lista(i1), TipoUsuario.DOCENTE));
        assertEquals(TipoUsuario.ESTUDIANTE, ic.obtenerUsuario(est).getTipoUsuario());
    }

    @Test
    public void testCrearUsuarioValidaciones() throws Exception {
        String nick = nombre("val");
        String mail = nick + "@test.uy";
        Date f = fechaNacimiento();
        TipoUsuario t = TipoUsuario.ESTUDIANTE;
        String[][] casos = {
            {null, "n", "a"}, {"  ", "n", "a"},           // contraseña
            {"x", null, "a"}, {"x", "  ", "a"},            // nombre
            {"x", "n", null}, {"x", "n", "  "}             // apellido
        };
        for (String[] c : casos) {
            Exception e = assertThrows(Exception.class,
                    () -> ic.crearUsuario(new DtUsuario(nick, c[0], mail, c[1], c[2], "i.png", f, null, t)));
            assertNotNull(e.getMessage());
        }
        assertFalse(ic.existeUsuario(nick));
        assertFalse(ic.existeEmail(mail));

        // Nombre y apellido se guardan sin espacios y con mayuscula inicial
        ic.crearUsuario(new DtUsuario(nick, "x", mail, "  juan ", " perez", "i.png", f, null, t));
        DtUsuario u = ic.obtenerUsuario(nick);
        assertEquals("Juan", u.getNombre());
        assertEquals("Perez", u.getApellido());
    }

    @Test
    public void testCrearDocenteSinInstitutos() throws Exception {
        String nick = nombre("doc");
        String mail = nick + "@test.uy";
        // Institutos nulos: error con mensaje claro (antes NullPointerException sin mensaje)
        Exception e = assertThrows(Exception.class, () -> ic.crearUsuario(
                new DtUsuario(nick, "x", mail, "n", "a", "i.png", fechaNacimiento(), null, TipoUsuario.DOCENTE)));
        assertEquals("Debe indicar los institutos del docente.", e.getMessage());
        assertFalse(ic.existeUsuario(nick));
        // Igual por la sobrecarga web
        Exception web = assertThrows(Exception.class, () -> ic.crearUsuario(
                new DtUsuario(nick, "x", mail, "n", "a", "i.png", fechaNacimiento(), null, TipoUsuario.DOCENTE), null, null));
        assertEquals("Debe indicar los institutos del docente.", web.getMessage());
        assertFalse(ic.existeUsuario(nick));
        assertFalse(ic.existeEmail(mail));

        // Un estudiante no necesita institutos
        ic.crearUsuario(new DtUsuario(nick, "x", mail, "n", "a", "i.png", fechaNacimiento(), null, TipoUsuario.ESTUDIANTE));
        assertEquals(TipoUsuario.ESTUDIANTE, ic.obtenerUsuario(nick).getTipoUsuario());
    }

    @Test
    public void testObtenerUsuario() throws Exception {
        assertNull(ic.obtenerUsuario(null));
        assertNull(ic.obtenerUsuario(nombre("noexiste")));

        String est = estudiante(ic);
        DtUsuario porNick = ic.obtenerUsuario(est);
        assertEquals(TipoUsuario.ESTUDIANTE, porNick.getTipoUsuario());
        assertNull(porNick.getInstitutos());
        assertEquals(est, ic.obtenerUsuario(est + "@test.uy").getNickname(), "Tambien busca por email");

        String inst = instituto(ic);
        String doc = docente(ic, inst);
        DtUsuario d = ic.obtenerUsuario(doc);
        assertEquals(TipoUsuario.DOCENTE, d.getTipoUsuario());
        assertEquals(List.of(inst), d.getInstitutos());

        DtUsuario enLista = ic.listarUsuarios().stream().filter(x -> x.getNickname().equals(doc)).findFirst().orElseThrow();
        assertEquals(List.of(inst), enLista.getInstitutos());
        assertTrue(ic.listarDocentes().contains(doc));
    }

    @Test
    public void testValidarNickYEmail() throws Exception {
        String est = estudiante(ic);
        String doc = docente(ic);

        DtSessionUsuario s = ic.validarNick(est, PASSWORD);
        assertEquals(est, s.getNickname());
        assertEquals(TipoUsuario.ESTUDIANTE, s.getTipoUsuario());

        DtSessionUsuario sd = ic.validarEmail(doc + "@test.uy", PASSWORD);
        assertEquals(doc, sd.getNickname());
        assertEquals(TipoUsuario.DOCENTE, sd.getTipoUsuario());

        assertNull(ic.validarNick(est, "incorrecta"));
        assertNull(ic.validarEmail(est + "@test.uy", "incorrecta"));
        assertNull(ic.validarNick(nombre("noexiste"), PASSWORD));
        assertNull(ic.validarEmail(nombre("noexiste") + "@x.uy", PASSWORD));
    }

    @Test
    public void testCrearUsuarioWebConImagen() throws Exception {
        String nick = nombre("web");
        DtUsuario dt = new DtUsuario(nick, PASSWORD, nick + "@web.uy", "n", "a", "orig.png", fechaNacimiento(), null, TipoUsuario.ESTUDIANTE);
        ic.crearUsuario(dt, new byte[]{1, 2, 3}, "foto.JPG");
        assertEquals(nick + ".jpg", ic.obtenerUsuario(nick).getImagen());

        Exception e1 = assertThrows(Exception.class, () -> ic.crearUsuario(dt, null, null));
        assertTrue(e1.getMessage().contains("nickname"));
        DtUsuario mismoMail = new DtUsuario(nombre("web"), PASSWORD, nick + "@web.uy", "n", "a", "orig.png", fechaNacimiento(), null, TipoUsuario.ESTUDIANTE);
        Exception e2 = assertThrows(Exception.class, () -> ic.crearUsuario(mismoMail, null, null));
        assertTrue(e2.getMessage().contains("correo"));

        // Sin bytes, bytes vacios o sin nombre de archivo: se conserva la imagen del Dt
        for (Object[] caso : new Object[][]{{null, "x.png"}, {new byte[0], "x.png"}, {new byte[]{1}, null}}) {
            String n = nombre("web");
            ic.crearUsuario(new DtUsuario(n, PASSWORD, n + "@web.uy", "n", "a", "orig.png", fechaNacimiento(), null, TipoUsuario.ESTUDIANTE),
                    (byte[]) caso[0], (String) caso[1]);
            assertEquals("orig.png", ic.obtenerUsuario(n).getImagen());
        }
    }

    @Test
    public void testModificarUsuarioWeb() throws Exception {
        Date fecha = fechaNacimiento();
        assertThrows(Exception.class, () -> ic.modificarUsuario(dtEstudiante(nombre("noexiste")), null, null));

        String est = estudiante(ic);
        DtUsuario orig = ic.obtenerUsuario(est);

        // Sin contraseña, sin email, sin imagen: solo cambian nombre/apellido/fecha
        ic.modificarUsuario(new DtUsuario(est, null, null, "Nom", "Ape", null, fecha, null, TipoUsuario.ESTUDIANTE), null, null);
        DtUsuario u = ic.obtenerUsuario(est);
        assertEquals("Nom", u.getNombre());
        assertEquals("Ape", u.getApellido());
        assertEquals(orig.getImagen(), u.getImagen());
        assertEquals(orig.getPassword(), u.getPassword());
        assertEquals(orig.getEmail(), u.getEmail());

        // Nombre, apellido y fecha nulos o en blanco: se conservan (igual que modificarUsuario(DtUsuario))
        ic.modificarUsuario(new DtUsuario(est, null, null, null, null, null, null, null, TipoUsuario.ESTUDIANTE), null, null);
        ic.modificarUsuario(new DtUsuario(est, null, null, "  ", "  ", null, null, null, TipoUsuario.ESTUDIANTE), null, null);
        u = ic.obtenerUsuario(est);
        assertEquals("Nom", u.getNombre());
        assertEquals("Ape", u.getApellido());
        assertEquals(local(fecha), local(u.getfNacimiento()));

        // Contraseña y email en blanco, bytes vacios: se usa la imagen del Dt
        ic.modificarUsuario(new DtUsuario(est, "   ", "  ", "Nom", "Ape", "nueva.png", fecha, null, TipoUsuario.ESTUDIANTE), new byte[0], "x.png");
        u = ic.obtenerUsuario(est);
        assertEquals("nueva.png", u.getImagen());
        assertEquals(orig.getPassword(), u.getPassword());

        // Mismo email en mayusculas, bytes sin nombre de archivo
        ic.modificarUsuario(new DtUsuario(est, null, orig.getEmail().toUpperCase(), "Nom", "Ape", "otra.png", fecha, null, TipoUsuario.ESTUDIANTE), new byte[]{1}, null);
        u = ic.obtenerUsuario(est);
        assertEquals("otra.png", u.getImagen());
        assertEquals(orig.getEmail(), u.getEmail());

        // Nueva contraseña (se recorta), nuevo email e imagen subida
        String nuevoMail = nombre("m") + "@x.uy";
        ic.modificarUsuario(new DtUsuario(est, " nueva ", nuevoMail, "Nom", "Ape", null, fecha, null, TipoUsuario.ESTUDIANTE), new byte[]{1, 2}, "perfil.png");
        u = ic.obtenerUsuario(est);
        assertEquals(nuevoMail, u.getEmail());
        assertEquals(est + ".png", u.getImagen());
        assertNotNull(ic.validarNick(est, "nueva"));

        // Email de otro usuario
        String otro = estudiante(ic);
        Exception e = assertThrows(Exception.class, () -> ic.modificarUsuario(
                new DtUsuario(est, null, otro + "@test.uy", "Nom", "Ape", null, fecha, null, TipoUsuario.ESTUDIANTE), null, null));
        assertTrue(e.getMessage().contains("Email"));
    }

    @Test
    public void testModificarUsuarioWebDocente() throws Exception {
        String i1 = instituto(ic);
        String i2 = instituto(ic);
        String doc = docente(ic, i1);
        Date fecha = fechaNacimiento();

        // Los institutos inexistentes se ignoran
        ic.modificarUsuario(new DtUsuario(doc, null, null, "N", "A", null, fecha, lista(i2, nombre("NoExiste")), TipoUsuario.DOCENTE), null, null);
        assertEquals(List.of(i2), ic.obtenerUsuario(doc).getInstitutos());

        // Institutos nulos: no cambia
        ic.modificarUsuario(new DtUsuario(doc, null, null, "N", "A", null, fecha, null, TipoUsuario.DOCENTE), null, null);
        assertEquals(List.of(i2), ic.obtenerUsuario(doc).getInstitutos());

        // Tipo ESTUDIANTE: los institutos se ignoran
        ic.modificarUsuario(new DtUsuario(doc, null, null, "N", "A", null, fecha, lista(i1), TipoUsuario.ESTUDIANTE), null, null);
        assertEquals(List.of(i2), ic.obtenerUsuario(doc).getInstitutos());

        // Un estudiante con tipo DOCENTE en el Dt: se ignoran los institutos (antes ClassCastException) y se guarda el resto
        String est = estudiante(ic);
        ic.modificarUsuario(new DtUsuario(est, null, null, "Otro", "Est", null, fecha, lista(i1), TipoUsuario.DOCENTE), null, null);
        DtUsuario u = ic.obtenerUsuario(est);
        assertEquals(TipoUsuario.ESTUDIANTE, u.getTipoUsuario());
        assertNull(u.getInstitutos());
        assertEquals("Otro", u.getNombre());
    }

    //********************************* Cursos *********************************//

    @Test
    public void testAltaCursoConPreviasYCategorias() throws Exception {
        String inst = instituto(ic);
        String cat1 = categoria(ic);
        String cat2 = categoria(ic);
        String previa = curso(ic, inst, set(), set(cat1), "p.png");
        String curso = curso(ic, inst, set(previa), set(cat1, cat2), "c.png");

        assertEquals(List.of(curso), ic.listarCursosPorCategoria(cat2));
        assertEquals(set(previa, curso), new HashSet<>(ic.listarCursosPorInstituto(inst)));
        assertEquals(inst, ic.obtenerInstitutoPorCurso(curso));
        assertTrue(ic.listarNombresCursos().contains(curso));
        DtCurso dc = ic.listarCursos().stream().filter(c -> c.getNombre().equals(curso)).findFirst().orElseThrow();
        assertEquals(set(previa), dc.getNamePrevias());
        assertEquals(set(cat1, cat2), dc.getNameCategorias());

        // Categoria inexistente
        String otro = nombre("Curso");
        Exception e = assertThrows(Exception.class, () -> ic.altaCurso(new DtCurso(otro, "d", "1", 1, 1, "u", new Date(),
                new DtInstituto(inst), set(), set(nombre("NoCat")), "i.png"), inst));
        assertTrue(e.getMessage().contains("categor"));
        assertFalse(ic.listarNombresCursos().contains(otro));
    }

    @Test
    public void testObtenerDatosCursoCompleto() throws Exception {
        String inst = instituto(ic);
        String cat = categoria(ic);
        String previaDePrevia = curso(ic, inst);
        String previa = curso(ic, inst, set(previaDePrevia), set(cat), "p.png");
        String curso = curso(ic, inst, set(previa), set(cat), "c.png");
        String doc = docente(ic, inst);
        String ed = edicionVigente(ic, curso, 10, doc);
        String prog = programa(ic, set(curso), null);

        DtConsultaCurso dc = ic.obtenerDatosCurso(curso);
        assertEquals(curso, dc.getNombre());
        assertEquals(set(previa), dc.getNamePrevias());
        assertEquals(set(cat), dc.getNameCategorias());
        assertEquals(set(ed), dc.getNameEdiciones());
        assertEquals(set(prog), dc.getNameProgramas());
        assertEquals(doc, dc.getEdiciones().get(0).getDocentes().get(0).getNickname());
        assertEquals(set(previaDePrevia), dc.getPrevias().get(0).getNamePrevias());
        assertEquals(set(cat), dc.getPrevias().get(0).getNameCategorias());
        assertEquals(set(curso), dc.getProgramas().get(0).getNameCursos());
    }

    @Test
    public void testListarCursosYEdicionesPorUsuario() throws Exception {
        String inst = instituto(ic);
        String cat = categoria(ic);
        String previa = curso(ic, inst);
        String curso = curso(ic, inst, set(previa), set(cat), "c.png");
        String doc = docente(ic, inst);
        String ed = edicionVigente(ic, curso, 10, doc);
        String est = estudiante(ic);

        List<DtCurso> cursosDoc = ic.listarCursosPorUsuario(doc);
        assertEquals(set(curso), nombres(cursosDoc));
        assertEquals(set(previa), cursosDoc.get(0).getNamePrevias());
        assertEquals(set(cat), cursosDoc.get(0).getNameCategorias());
        assertTrue(ic.listarCursosPorUsuario(est).isEmpty(), "Un estudiante no dicta cursos");

        List<DtEdicion> edDoc = ic.listarEdicionesPorUsuario(doc);
        assertEquals(set(ed), nombres(edDoc));
        assertEquals(set(doc), edDoc.get(0).getNameDocentes());
        assertEquals(set(previa), edDoc.get(0).getCurso().getNamePrevias());

        assertTrue(ic.listarEdicionesPorUsuario(est).isEmpty());
        assertEquals(0, ic.setInscripcion(est, ed));
        assertEquals(set(ed), nombres(ic.listarEdicionesPorUsuario(est)));
    }

    //********************************* Ediciones *******************************//

    @Test
    public void testAltaEdicionValidaciones() throws Exception {
        String curso = curso(ic, instituto(ic));
        LocalDate hoy = LocalDate.now();
        Object[][] invalidas = {
            {null, hoy, 5}, {hoy, null, 5}, {hoy.plusDays(1), hoy, 5}, {hoy, hoy, -1}
        };
        for (Object[] c : invalidas) {
            String n = nombre("Ed");
            assertThrows(Exception.class, () -> ic.altaEdicionCurso(curso,
                    new DtEdicion(n, (Integer) c[2], (LocalDate) c[0], (LocalDate) c[1], hoy)));
            assertNull(ic.getEdicion(n));
        }
        // Docente inexistente (ni por nickname ni por "Nombre Apellido")
        String n = nombre("Ed");
        Exception e = assertThrows(Exception.class, () -> ic.altaEdicionCurso(curso,
                new DtEdicion(n, 5, hoy, hoy, hoy, new DtCurso(curso, null), set(nombre("NoDoc")))));
        assertTrue(e.getMessage().contains("docente"));
        assertNull(ic.getEdicion(n));
    }

    @Test
    public void testAltaEdicionDocentes() throws Exception {
        String inst = instituto(ic);
        String curso = curso(ic, inst);
        String doc = docente(ic, inst);
        DtUsuario d = ic.obtenerUsuario(doc);
        String nombreCompleto = d.getNombre() + " " + d.getApellido();
        LocalDate hoy = LocalDate.now();

        // Mismo docente por nickname y por "Nombre Apellido": queda una sola vez. Cupo e imagen nulos.
        String ed1 = nombre("Ed");
        ic.altaEdicionCurso(curso, new DtEdicion(ed1, null, hoy, hoy, hoy, new DtCurso(curso, null), set(doc, nombreCompleto), (String) null));
        DtEdicion r1 = ic.getEdicion(ed1);
        assertNull(r1.getCupo());
        assertNull(r1.getImagen());
        assertEquals(1, r1.getDocentes().size());
        assertEquals(doc, r1.getDocentes().get(0).getNickname());

        // Docentes como DtUsuario: con nickname, y sin nickname (se resuelve por "Nombre Apellido")
        String ed2 = nombre("Ed");
        DtUsuario sinNick = new DtUsuario(null, null, null, d.getNombre(), d.getApellido(), null, null, null, null);
        ic.altaEdicionCurso(curso, new DtEdicion(ed2, 3, hoy, hoy, hoy, null, List.of(sinNick), "e.png"));
        assertEquals(doc, ic.getEdicion(ed2).getDocentes().get(0).getNickname());

        String ed3 = nombre("Ed");
        ic.altaEdicionCurso(curso, new DtEdicion(ed3, 3, hoy, hoy, hoy, null, List.of(d)));
        assertEquals(doc, ic.getEdicion(ed3).getDocentes().get(0).getNickname());

        // Dt sin listas de docentes (nulas) y con un docente nulo en la lista
        DtEdicion sinListas = mock(DtEdicion.class);
        String ed4 = nombre("Ed");
        when(sinListas.getNombre()).thenReturn(ed4);
        when(sinListas.getFechaInicio()).thenReturn(hoy);
        when(sinListas.getFechaFin()).thenReturn(hoy);
        when(sinListas.getFechaPublicacion()).thenReturn(hoy);
        when(sinListas.getDocentes()).thenReturn(null);
        when(sinListas.getNameDocentes()).thenReturn(null);
        ic.altaEdicionCurso(curso, sinListas);
        assertTrue(ic.getEdicion(ed4).getDocentes().isEmpty());

        DtEdicion conNulo = mock(DtEdicion.class);
        String ed5 = nombre("Ed");
        when(conNulo.getNombre()).thenReturn(ed5);
        when(conNulo.getFechaInicio()).thenReturn(hoy);
        when(conNulo.getFechaFin()).thenReturn(hoy);
        when(conNulo.getFechaPublicacion()).thenReturn(hoy);
        when(conNulo.getDocentes()).thenReturn(Arrays.asList((DtUsuario) null, d));
        ic.altaEdicionCurso(curso, conNulo);
        assertEquals(1, ic.getEdicion(ed5).getDocentes().size());

        assertEquals(set(ed1, ed2, ed3, ed4, ed5), new HashSet<>(ic.listaEdicionPorCurso(curso)));
        assertEquals(set(ed1, ed2, ed3, ed4, ed5), nombres(ic.listarEdicionPorCurso(curso)));
        assertNull(ic.getEdicion(nombre("NoEd")));
        assertNull(ic.buscarEdicion(nombre("NoEd")));
    }

    @Test
    public void testAgregarYQuitarDocente() throws Exception {
        String inst = instituto(ic);
        String curso = curso(ic, inst);
        String doc1 = docente(ic, inst);
        String doc2 = docente(ic, inst);
        String ed = edicionVigente(ic, curso, 10, doc1);

        assertThrows(Exception.class, () -> ic.agregarDocenteAEdicion(nombre("NoEd"), doc2));
        assertThrows(Exception.class, () -> ic.agregarDocenteAEdicion(ed, nombre("NoDoc")));
        ic.agregarDocenteAEdicion(ed, doc2);
        assertEquals(2, ic.getEdicion(ed).getDocentes().size());
        Exception e = assertThrows(Exception.class, () -> ic.agregarDocenteAEdicion(ed, doc2));
        assertTrue(e.getMessage().contains("ya está asignado"));

        assertThrows(Exception.class, () -> ic.quitarDocenteDeEdicion(nombre("NoEd"), doc2));
        assertThrows(Exception.class, () -> ic.quitarDocenteDeEdicion(ed, nombre("NoDoc")));
        ic.quitarDocenteDeEdicion(ed, doc2);
        assertEquals(1, ic.getEdicion(ed).getDocentes().size());
        Exception e2 = assertThrows(Exception.class, () -> ic.quitarDocenteDeEdicion(ed, doc2));
        assertTrue(e2.getMessage().contains("no está asignado"));
    }

    @Test
    public void testInscripcionCodigos() throws Exception {
        String curso = curso(ic, instituto(ic));
        String est = estudiante(ic);
        String vigente = edicionVigente(ic, curso, 10);
        String terminada = edicion(ic, curso, 10, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), "e.png");

        assertEquals(0, ic.setInscripcion(est, vigente));
        assertEquals(3, ic.setInscripcion(est, vigente), "Ya estaba inscripto");
        assertEquals(4, ic.setInscripcion(est, terminada), "La edicion ya finalizo");
        assertEquals(List.of(est), ic.getEstudiantesInscriptosEdicion(vigente));
        assertFalse(ic.getEstudiantesCandidatosEdicion(vigente).contains(est));
        assertTrue(ic.getEstudiantesCandidatosEdicion(terminada).contains(est));
        assertEquals(0, ic.delInscripcion(est, vigente));
        assertEquals(-1, ic.delInscripcion(est, vigente), "Ya no estaba inscripto");
    }

    //***************************** ProgramaFormacion ****************************//

    @Test
    public void testSetCrearProgramaFormacionNulo() throws Exception {
        // Programa nulo: no se crea (false), igual que setCrearProgramaFormacion(DtPrograma); ni se guarda la imagen
        try (MockedStatic<GestorImagenes> imagenes = mockStatic(GestorImagenes.class)) {
            assertFalse(ic.setCrearProgramaFormacion(null));
            assertFalse(ic.setCrearProgramaFormacion(null, new byte[]{1}, "foto.png"));
            assertFalse(ic.setCrearProgramaFormacion(null, new byte[]{1}, "foto.png", "doc"));
            imagenes.verifyNoInteractions();
        }
    }

    @Test
    public void testSetCrearProgramaFormacionImagenes() throws Exception {
        String doc = docente(ic);
        LocalDate hoy = LocalDate.now();

        String p1 = nombre("Prog");
        assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(p1, "d", hoy, hoy, hoy, "orig.png", doc), new byte[]{1}, "foto.png"));
        assertEquals("Programa_" + p1 + ".png", ic.buscarPrograma(p1).getImagen());
        assertTrue(nombres(ic.listarProgramasPorUsuario(doc)).contains(p1), "El docente ve los programas que creo");

        for (Object[] caso : new Object[][]{{null, "x.png"}, {new byte[0], "x.png"}, {new byte[]{1}, null}}) {
            String p = nombre("Prog");
            assertTrue(ic.setCrearProgramaFormacion(new DtPrograma(p, "d", hoy, hoy, hoy, "orig.png", null), (byte[]) caso[0], (String) caso[1]));
            assertEquals("orig.png", ic.buscarPrograma(p).getImagen());
        }

        // Fechas invertidas: no se crea
        String p2 = nombre("Prog");
        assertFalse(ic.setCrearProgramaFormacion(new DtPrograma(p2, "d", hoy, hoy.plusDays(1), hoy)));
        assertThrows(Exception.class, () -> ic.buscarPrograma(p2));
    }

    @Test
    public void testProgramasCursosYUsuarios() throws Exception {
        String inst = instituto(ic);
        String cat = categoria(ic);
        String previa = curso(ic, inst);
        String curso = curso(ic, inst, set(previa), set(cat), "c.png");
        String doc = docente(ic, inst);
        edicionVigente(ic, curso, 10, doc);
        String prog = programa(ic, set(curso), null);

        // El programa no existe y el curso si
        assertThrows(Exception.class, () -> ic.agregarProgramaCurso(nombre("NoProg"), curso));

        DtPrograma p = ic.buscarPrograma(prog);
        assertEquals(set(curso), p.getNameCursos());
        assertEquals(set(previa), p.getCursos().get(0).getNamePrevias());
        assertEquals(set(cat), p.getCategorias());

        List<DtCurso> cursos = ic.listarCursosPorPrograma(prog);
        assertEquals(set(previa), cursos.get(0).getNamePrevias());
        assertEquals(set(cat), cursos.get(0).getNameCategorias());

        DtPrograma enLista = ic.listarProgramas().stream().filter(x -> x.getNombre().equals(prog)).findFirst().orElseThrow();
        assertEquals(set(cat), enLista.getCategorias());
        assertEquals(inst, enLista.getCursos().get(0).getInstituto().getNombre());

        // Docente: programas de los cursos que dicta
        List<DtPrograma> delDocente = ic.listarProgramasPorUsuario(doc);
        assertEquals(set(prog), nombres(delDocente));
        assertEquals(set(cat), delDocente.get(0).getCategorias());

        // Estudiante: programas en los que esta inscripto
        String est = estudiante(ic);
        assertTrue(ic.listarProgramasPorUsuario(est).isEmpty());
        assertEquals(0, ic.setInscripcionPrograma(est, prog));
        assertEquals(1, ic.setInscripcionPrograma(est, prog), "Ya estaba inscripto");
        List<DtPrograma> delEstudiante = ic.listarProgramasPorUsuario(est);
        assertEquals(set(prog), nombres(delEstudiante));
        assertEquals(inst, delEstudiante.get(0).getCursos().get(0).getInstituto().getNombre());

        assertThrows(Exception.class, () -> ic.setInscripcionPrograma(nombre("NoEst"), prog));
        assertThrows(Exception.class, () -> ic.setInscripcionPrograma(est, nombre("NoProg")));
    }

    //********************************* Social **********************************//

    @Test
    public void testSeguirYDejarDeSeguir() throws Exception {
        String a = estudiante(ic);
        String b = docente(ic);

        ic.seguirUsuario(a, b);
        ic.seguirUsuario(a, b); // seguir dos veces no duplica
        assertEquals(List.of(b), ic.listarSeguidos(a));
        assertEquals(List.of(a), ic.listarSeguidores(b));

        Exception e = assertThrows(Exception.class, () -> ic.seguirUsuario(a, a.toUpperCase()));
        assertTrue(e.getMessage().contains("mismo"));
        assertThrows(Exception.class, () -> ic.seguirUsuario(nombre("NoExiste"), b));
        assertThrows(Exception.class, () -> ic.dejarDeSeguirUsuario(nombre("NoExiste"), b));

        ic.dejarDeSeguirUsuario(b, a); // b no sigue a a: no hace nada
        assertEquals(List.of(b), ic.listarSeguidos(a));
        ic.dejarDeSeguirUsuario(a, b);
        assertTrue(ic.listarSeguidos(a).isEmpty());
        assertTrue(ic.listarSeguidores(b).isEmpty());

        assertTrue(ic.listarSeguidos(nombre("NoExiste")).isEmpty());
        assertTrue(ic.listarSeguidores(nombre("NoExiste")).isEmpty());
    }

    @Test
    public void testInstitutosYCategorias() throws Exception {
        String inst = instituto(ic);
        String cat = categoria(ic);
        assertTrue(ic.listarInstitutos().stream().map(DtInstituto::getNombre).collect(Collectors.toSet()).contains(inst));
        assertTrue(ic.listarCategorias().stream().map(c -> c.getNombre()).collect(Collectors.toSet()).contains(cat));
        Exception e = assertThrows(Exception.class, () -> ic.crearCategoria(cat));
        assertTrue(e.getMessage().contains("ya existe"));
    }
}
