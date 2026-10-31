package edu.edext.webservices;

import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtEvaluacion;
import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Cada operacion del Publicador delega en el IControlador (mock) con los mismos datos.
 * Las conversiones propias del Web Service (contraseña aparte, lista de evaluaciones en vez de Map,
 * login verificado en el servidor) se prueban en detalle.
 */
public class PublicadorTest {

    private MockedStatic<Fabrica> fabricaEstatica;
    private IControlador ic;
    private Publicador publicador;

    @BeforeEach
    public void setUp() {
        ic = mock(IControlador.class);
        Fabrica fabrica = mock(Fabrica.class);
        when(fabrica.getIControlador()).thenReturn(ic);
        fabricaEstatica = mockStatic(Fabrica.class);
        fabricaEstatica.when(Fabrica::getInstance).thenReturn(fabrica);
        publicador = new Publicador();
    }

    @AfterEach
    public void tearDown() {
        fabricaEstatica.close();
    }

    private static DtUsuario usuario(String password) {
        return new DtUsuario("nick", password, "nick@mail.com", "Nombre", "Apellido", "img.png", new Date(0),
                List.of("INCO"), TipoUsuario.DOCENTE, List.of("seguido"), List.of("seguidor"));
    }

    //******************************** Sesion ********************************//

    @Test
    public void testIniciarSesionDatosIncompletos() throws Exception {
        assertNull(publicador.iniciarSesion(null, "clave"));
        assertNull(publicador.iniciarSesion("   ", "clave"));
        assertNull(publicador.iniciarSesion("nick", null));
        verifyNoInteractions(ic);
    }

    @Test
    public void testIniciarSesionUsuarioInexistente() throws Exception {
        when(ic.obtenerUsuario("nadie")).thenReturn(null);
        assertNull(publicador.iniciarSesion(" nadie ", "clave"));
    }

    @Test
    public void testIniciarSesionPasswordCorrectaEIncorrecta() throws Exception {
        DtUsuario guardado = usuario(Utils.Crypt.hashPassword("clave123"));
        when(ic.obtenerUsuario("nick")).thenReturn(guardado);

        assertSame(guardado, publicador.iniciarSesion("nick", "clave123"));
        assertNull(publicador.iniciarSesion("nick", "otra"));
    }

    //******************************** Usuarios ********************************//

    @Test
    public void testCrearYModificarUsuarioPasanLaPasswordAparte() throws Exception {
        ArgumentCaptor<DtUsuario> captor = ArgumentCaptor.forClass(DtUsuario.class);
        DtUsuario recibido = usuario(null); // asi llega por el Web Service: sin contraseña
        byte[] imagen = {1, 2, 3};

        publicador.crearUsuario(recibido, "nueva");
        publicador.crearUsuarioConImagen(recibido, "conImagen", imagen, "foto.png");
        publicador.modificarUsuario(recibido, null);
        publicador.modificarUsuarioConImagen(recibido, "cambiada", imagen, "foto.png");

        verify(ic).crearUsuario(captor.capture());
        assertEquals("nueva", captor.getValue().getPassword());
        verify(ic).crearUsuario(captor.capture(), same(imagen), eq("foto.png"));
        assertEquals("conImagen", captor.getValue().getPassword());
        verify(ic).modificarUsuario(captor.capture());
        assertNull(captor.getValue().getPassword(), "null mantiene la contraseña actual");
        verify(ic).modificarUsuario(captor.capture(), same(imagen), eq("foto.png"));
        assertEquals("cambiada", captor.getValue().getPassword());

        // El resto de los datos se copia tal cual
        DtUsuario copia = captor.getValue();
        assertEquals(recibido.getNickname(), copia.getNickname());
        assertEquals(recibido.getEmail(), copia.getEmail());
        assertEquals(recibido.getNombre(), copia.getNombre());
        assertEquals(recibido.getApellido(), copia.getApellido());
        assertEquals(recibido.getImagen(), copia.getImagen());
        assertEquals(recibido.getfNacimiento(), copia.getfNacimiento());
        assertEquals(recibido.getInstitutos(), copia.getInstitutos());
        assertEquals(recibido.getTipoUsuario(), copia.getTipoUsuario());
        assertEquals(recibido.getSeguidos(), copia.getSeguidos());
        assertEquals(recibido.getSeguidores(), copia.getSeguidores());
    }

    @Test
    public void testConsultasDeUsuarios() throws Exception {
        DtUsuario u = usuario(null);
        when(ic.existeUsuario("nick")).thenReturn(true);
        when(ic.existeEmail("a@b.com")).thenReturn(false);
        when(ic.listarUsuarios()).thenReturn(List.of(u));
        when(ic.obtenerUsuario("nick@mail.com")).thenReturn(u);
        when(ic.listarDocentes()).thenReturn(List.of("doc"));
        when(ic.listarSeguidos("nick")).thenReturn(List.of("s1"));
        when(ic.listarSeguidores("nick")).thenReturn(List.of("s2"));

        assertTrue(publicador.existeUsuario("nick"));
        assertFalse(publicador.existeEmail("a@b.com"));
        assertEquals(List.of(u), publicador.listarUsuarios());
        assertSame(u, publicador.obtenerUsuario("nick@mail.com"));
        assertEquals(List.of("doc"), publicador.listarDocentes());
        assertEquals(List.of("s1"), publicador.listarSeguidos("nick"));
        assertEquals(List.of("s2"), publicador.listarSeguidores("nick"));

        publicador.seguirUsuario("a", "b");
        publicador.dejarDeSeguirUsuario("a", "b");
        verify(ic).seguirUsuario("a", "b");
        verify(ic).dejarDeSeguirUsuario("a", "b");
    }

    //******************************** Institutos y categorias ********************************//

    @Test
    public void testInstitutosYCategorias() throws Exception {
        List<DtInstituto> institutos = List.of(new DtInstituto("INCO"));
        List<DtCategoria> categorias = List.of(new DtCategoria("Programacion"));
        when(ic.listarInstitutos()).thenReturn(institutos);
        when(ic.listarCategorias()).thenReturn(categorias);
        when(ic.obtenerInstitutoPorCurso("Curso")).thenReturn("INCO");

        assertSame(institutos, publicador.listarInstitutos());
        assertSame(categorias, publicador.listarCategorias());
        assertEquals("INCO", publicador.obtenerInstitutoPorCurso("Curso"));
        publicador.crearInstituto("Nuevo");
        verify(ic).crearInstituto("Nuevo");
    }

    //******************************** Cursos ********************************//

    @Test
    public void testCursos() throws Exception {
        DtCurso curso = new DtCurso("Curso", "img.png");
        DtConsultaCurso consulta = mock(DtConsultaCurso.class);
        when(ic.obtenerDatosCurso("Curso")).thenReturn(consulta);
        when(ic.listarNombresCursos()).thenReturn(List.of("Curso"));
        when(ic.listarCursos()).thenReturn(List.of(curso));
        when(ic.listarCursosPorUsuario("nick")).thenReturn(List.of(curso));
        when(ic.listarCursosPorInstituto("INCO")).thenReturn(List.of("Curso"));
        when(ic.listarCursosPorCategoria("Cat")).thenReturn(List.of("Curso"));
        when(ic.listarCursosPorPrograma("Prog")).thenReturn(List.of(curso));

        publicador.altaCurso(curso, "INCO");
        verify(ic).altaCurso(curso, "INCO");
        assertSame(consulta, publicador.obtenerDatosCurso("Curso"));
        assertEquals(List.of("Curso"), publicador.listarNombresCursos());
        assertEquals(List.of(curso), publicador.listarCursos());
        assertEquals(List.of(curso), publicador.listarCursosPorUsuario("nick"));
        assertEquals(List.of("Curso"), publicador.listarCursosPorInstituto("INCO"));
        assertEquals(List.of("Curso"), publicador.listarCursosPorCategoria("Cat"));
        assertEquals(List.of(curso), publicador.listarCursosPorPrograma("Prog"));
    }

    //******************************** Ediciones ********************************//

    @Test
    public void testEdiciones() throws Exception {
        DtEdicion edicion = new DtEdicion("Ed", 10, null, null, null);
        when(ic.getEdicion("Ed")).thenReturn(edicion);
        when(ic.buscarEdicion("Ed")).thenReturn(edicion);
        when(ic.listaEdicionPorCurso("Curso")).thenReturn(List.of("Ed"));
        when(ic.listarEdicionPorCurso("Curso")).thenReturn(List.of(edicion));
        when(ic.listarEdicionesPorUsuario("nick")).thenReturn(List.of(edicion));

        publicador.altaEdicionCurso("Curso", edicion);
        verify(ic).altaEdicionCurso("Curso", edicion);
        assertSame(edicion, publicador.getEdicion("Ed"));
        assertSame(edicion, publicador.buscarEdicion("Ed"));
        assertEquals(List.of("Ed"), publicador.listaEdicionPorCurso("Curso"));
        assertEquals(List.of(edicion), publicador.listarEdicionPorCurso("Curso"));
        assertEquals(List.of(edicion), publicador.listarEdicionesPorUsuario("nick"));
        publicador.agregarDocenteAEdicion("Ed", "doc");
        publicador.quitarDocenteDeEdicion("Ed", "doc");
        verify(ic).agregarDocenteAEdicion("Ed", "doc");
        verify(ic).quitarDocenteDeEdicion("Ed", "doc");
    }

    //******************************** Inscripciones ********************************//

    @Test
    public void testInscripciones() throws Exception {
        List<DtInscripcionEdicion> inscripciones = List.of();
        when(ic.setInscripcion("est", "Ed")).thenReturn(0);
        when(ic.delInscripcion("est", "Ed")).thenReturn(2);
        when(ic.getEstudiantesInscriptosEdicion("Ed")).thenReturn(List.of("est"));
        when(ic.getEstudiantesCandidatosEdicion("Ed")).thenReturn(List.of("otro"));
        when(ic.getInscripcionesCompletas("Ed")).thenReturn(inscripciones);
        when(ic.getInscripcionesDeEstudiante("est")).thenReturn(inscripciones);
        when(ic.setInscripcionPrograma("est", "Prog")).thenReturn(4);

        assertEquals(0, publicador.setInscripcion("est", "Ed"));
        assertEquals(2, publicador.delInscripcion("est", "Ed"));
        assertEquals(List.of("est"), publicador.getEstudiantesInscriptosEdicion("Ed"));
        assertEquals(List.of("otro"), publicador.getEstudiantesCandidatosEdicion("Ed"));
        assertSame(inscripciones, publicador.getInscripcionesCompletas("Ed"));
        assertSame(inscripciones, publicador.getInscripcionesDeEstudiante("est"));
        assertEquals(4, publicador.setInscripcionPrograma("est", "Prog"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluarInscripcionesConvierteLaListaEnMap() throws Exception {
        ArgumentCaptor<Map<String, Integer>> captor = ArgumentCaptor.forClass(Map.class);

        publicador.evaluarInscripciones("Ed", List.of(new DtEvaluacion("ana", 2), new DtEvaluacion("beto", 3)));
        publicador.evaluarInscripciones("Ed", null);

        verify(ic, times(2)).evaluarInscripciones(eq("Ed"), captor.capture());
        assertEquals(Map.of("ana", 2, "beto", 3), captor.getAllValues().get(0));
        assertTrue(captor.getAllValues().get(1).isEmpty(), "Sin evaluaciones llega un Map vacio");
    }

    //******************************** Programas ********************************//

    @Test
    public void testProgramas() throws Exception {
        DtPrograma programa = new DtPrograma("Prog", null, null, null);
        byte[] imagen = {9};
        when(ic.setCrearProgramaFormacion(programa)).thenReturn(true);
        when(ic.setCrearProgramaFormacion(programa, imagen, "p.png")).thenReturn(true);
        when(ic.setCrearProgramaFormacion(programa, imagen, "p.png", "doc")).thenReturn(false);
        when(ic.buscarPrograma("Prog")).thenReturn(programa);
        when(ic.listarProgramas()).thenReturn(List.of(programa));
        when(ic.listarProgramasPorUsuario("doc")).thenReturn(List.of(programa));
        when(ic.listarInscriptosPorPrograma("Prog")).thenReturn(List.of());

        assertTrue(publicador.setCrearProgramaFormacion(programa));
        assertTrue(publicador.setCrearProgramaFormacionConImagen(programa, imagen, "p.png"));
        assertFalse(publicador.setCrearProgramaFormacionConDocente(programa, imagen, "p.png", "doc"));
        publicador.agregarProgramaCurso("Prog", "Curso");
        verify(ic).agregarProgramaCurso("Prog", "Curso");
        assertSame(programa, publicador.buscarPrograma("Prog"));
        assertEquals(List.of(programa), publicador.listarProgramas());
        assertEquals(List.of(programa), publicador.listarProgramasPorUsuario("doc"));
        assertEquals(List.of(), publicador.listarInscriptosPorPrograma("Prog"));
    }

    @Test
    public void testLasExcepcionesDelControladorSePropagan() throws Exception {
        when(ic.listarInstitutos()).thenThrow(new Exception("sin base"));
        Exception e = assertThrows(Exception.class, () -> publicador.listarInstitutos());
        assertEquals("sin base", e.getMessage());
    }
}
