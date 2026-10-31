package edu.edext.datatypes;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Los datatypes viajan por los Web Services serializados con JAXB: se prueba la ida y vuelta a XML
 * (constructor vacio, LocalDate como yyyy-MM-dd, y que la contraseña y las constantes no se serialicen).
 */
public class DatatypesJaxbTest {

    private static JAXBContext contexto;
    private static final LocalDate INICIO = LocalDate.of(2026, 3, 10);
    private static final LocalDate FIN = LocalDate.of(2026, 7, 15);
    private static final LocalDate PUBLICACION = LocalDate.of(2026, 2, 1);

    @BeforeAll
    public static void crearContexto() throws Exception {
        contexto = JAXBContext.newInstance(DtCategoria.class, DtInstituto.class, DtSessionUsuario.class, DtUsuario.class,
                DtCurso.class, DtConsultaCurso.class, DtEdicion.class, DtPrograma.class, DtInscripcionEdicion.class,
                DtInscripcionPrograma.class, DtEvaluacion.class);
    }

    @SuppressWarnings("unchecked")
    private static String xml(Object objeto) throws Exception {
        Marshaller m = contexto.createMarshaller();
        StringWriter salida = new StringWriter();
        m.marshal(new JAXBElement<>(new QName("dato"), (Class<Object>) objeto.getClass(), objeto), salida);
        return salida.toString();
    }

    private static <T> T leer(String xml, Class<T> clase) throws Exception {
        return contexto.createUnmarshaller().unmarshal(new StreamSource(new StringReader(xml)), clase).getValue();
    }

    private static <T> T idaYVuelta(T objeto, Class<T> clase) throws Exception {
        return leer(xml(objeto), clase);
    }

    private static DtUsuario usuario() {
        return new DtUsuario("nick", "$2a$hash-secreto", "nick@mail.com", "Nombre", "Apellido", "img.png", new Date(0),
                List.of("INCO"), TipoUsuario.DOCENTE, List.of("s1"), List.of("s2"));
    }

    @Test
    public void testDatatypesSimples() throws Exception {
        assertEquals("Prog", idaYVuelta(new DtCategoria("Prog"), DtCategoria.class).getNombre());
        assertEquals("INCO", idaYVuelta(new DtInstituto("INCO"), DtInstituto.class).getNombre());

        DtSessionUsuario sesion = idaYVuelta(new DtSessionUsuario("nick", "n@m.com", "i.png", TipoUsuario.ESTUDIANTE), DtSessionUsuario.class);
        assertEquals("nick", sesion.getNickname());
        assertEquals("n@m.com", sesion.getEmail());
        assertEquals("i.png", sesion.getImagen());
        assertEquals(TipoUsuario.ESTUDIANTE, sesion.getTipoUsuario());

        DtEvaluacion evaluacion = idaYVuelta(new DtEvaluacion("ana", 2), DtEvaluacion.class);
        assertEquals("ana", evaluacion.getNickname());
        assertEquals(2, evaluacion.getEstado());
    }

    @Test
    public void testUsuarioSinPassword() throws Exception {
        String texto = xml(usuario());
        assertFalse(texto.contains("password"));
        assertFalse(texto.contains("hash-secreto"), "La contraseña nunca se serializa");

        DtUsuario leido = leer(texto, DtUsuario.class);
        assertNull(leido.getPassword());
        assertEquals("nick", leido.getNickname());
        assertEquals("Apellido", leido.getApellido());
        assertEquals(new Date(0), leido.getfNacimiento());
        assertEquals(List.of("INCO"), leido.getInstitutos());
        assertEquals(TipoUsuario.DOCENTE, leido.getTipoUsuario());
        assertEquals(List.of("s1"), leido.getSeguidos());
        assertEquals(List.of("s2"), leido.getSeguidores());
    }

    @Test
    public void testCursoYConsultaDeCurso() throws Exception {
        DtCurso curso = new DtCurso("Curso", "desc", "4 semanas", 40, 6, "http://curso", new Date(1000), new DtInstituto("INCO"),
                Set.of("Previa"), Set.of("Cat"), "c.png");
        DtCurso cursoLeido = idaYVuelta(curso, DtCurso.class);
        assertEquals("Curso", cursoLeido.getNombre());
        assertEquals(40, cursoLeido.getCantidadHoras());
        assertEquals("INCO", cursoLeido.getInstituto().getNombre());
        assertEquals(new Date(1000), cursoLeido.getFechaRegistro());

        DtConsultaCurso consulta = new DtConsultaCurso("Curso", "desc", "4 semanas", 40, 6, "http://curso", new Date(1000),
                Set.of("Ed 1"), Set.of("Prog"), Set.of("Cat"), Set.of("Previa"), "c.png");
        DtConsultaCurso consultaLeida = idaYVuelta(consulta, DtConsultaCurso.class);
        assertEquals("Curso", consultaLeida.getNombre());
        assertEquals(6, consultaLeida.getCreditos());
        assertEquals("http://curso", consultaLeida.getUrl());
    }

    @Test
    public void testFechasLocalDate() throws Exception {
        DtEdicion edicion = new DtEdicion("Ed", 30, INICIO, FIN, PUBLICACION, new DtCurso("Curso", null), Set.of("doc"), "e.png");
        String texto = xml(edicion);
        assertTrue(texto.contains("<fechaInicio>2026-03-10</fechaInicio>"));
        DtEdicion leida = leer(texto, DtEdicion.class);
        assertEquals(INICIO, leida.getFechaInicio());
        assertEquals(FIN, leida.getFechaFin());
        assertEquals(PUBLICACION, leida.getFechaPublicacion());
        assertEquals(30, leida.getCupo());
        assertEquals("Curso", leida.getCurso().getNombre());

        DtPrograma programa = new DtPrograma("Prog", "desc", PUBLICACION, INICIO, FIN, "p.png", "doc");
        DtPrograma programaLeido = idaYVuelta(programa, DtPrograma.class);
        assertEquals(INICIO, programaLeido.getFechaInicio());
        assertEquals("doc", programaLeido.getDocente());

        // Sin fecha: el elemento no se envia y vuelve null
        DtEdicion sinFechas = idaYVuelta(new DtEdicion("SinFechas", null, null, null, null), DtEdicion.class);
        assertNull(sinFechas.getFechaInicio());
    }

    @Test
    public void testInscripcionesSinConstantesNiPassword() throws Exception {
        DtInscripcionEdicion inscripcion = new DtInscripcionEdicion(usuario(), new DtEdicion("Ed", 5, INICIO, FIN, PUBLICACION), INICIO, 2, 0.5);
        String texto = xml(inscripcion);
        assertFalse(texto.contains("ACEPTADO"), "Las constantes no se serializan");
        assertFalse(texto.contains("nomEstado"));
        assertFalse(texto.contains("hash-secreto"), "Tampoco la contraseña del estudiante anidado");
        DtInscripcionEdicion leida = leer(texto, DtInscripcionEdicion.class);
        assertEquals(2, leida.getValorEstado());
        assertEquals("Aceptado", leida.getEstado());
        assertEquals(INICIO, leida.getFechaInscripcion());
        assertEquals(0.5, leida.getPrioridad());
        assertEquals("nick", leida.getEstudiante().getNickname());

        DtInscripcionPrograma inscPrograma = idaYVuelta(new DtInscripcionPrograma(usuario(), new DtPrograma("Prog", PUBLICACION, INICIO, FIN), INICIO, 1),
                DtInscripcionPrograma.class);
        assertEquals("Inscripto", inscPrograma.getEstado());
        assertEquals("Prog", inscPrograma.getPrograma().getNombre());
    }

    @Test
    public void testLocalDateAdapter() {
        LocalDateAdapter adaptador = new LocalDateAdapter();
        assertEquals(INICIO, adaptador.unmarshal(" 2026-03-10 "));
        assertNull(adaptador.unmarshal(null));
        assertNull(adaptador.unmarshal("  "));
        assertEquals("2026-03-10", adaptador.marshal(INICIO));
        assertNull(adaptador.marshal(null));
    }
}
