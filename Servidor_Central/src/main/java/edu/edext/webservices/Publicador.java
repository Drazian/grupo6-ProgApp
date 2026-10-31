package edu.edext.webservices;

import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtEvaluacion;
import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtInscripcionPrograma;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Services del Servidor Central (requerimiento 7.7): expone la logica del IControlador
 * para que la consuman el Servidor Web y el Dispositivo Movil.
 *
 * Diferencias con IControlador, porque SOAP/JAXB no las soporta o por seguridad:
 *  - Los metodos sobrecargados tienen un nombre de operacion propio (p.ej. crearUsuarioConImagen).
 *  - evaluarInscripciones recibe una lista de DtEvaluacion en lugar de un Map.
 *  - La contraseña nunca viaja dentro de DtUsuario (@XmlTransient): se manda como parametro en
 *    crearUsuario/modificarUsuario, y el login se verifica en el servidor con iniciarSesion.
 *  - No se exponen las operaciones exclusivas del Administrador (Estacion de Trabajo):
 *    crearCategoria, eliminarInstituto y cargarDatosDePrueba.
 */
@WebService(serviceName = "PublicadorService", portName = "PublicadorPort")
public class Publicador {

    private IControlador ic() {
        return Fabrica.getInstance().getIControlador();
    }

    // Copia del usuario recibido con la contraseña indicada (DtUsuario no la trae por el Web Service)
    private static DtUsuario conPassword(DtUsuario u, String password) {
        return new DtUsuario(u.getNickname(), password, u.getEmail(), u.getNombre(), u.getApellido(), u.getImagen(),
                u.getfNacimiento(), u.getInstitutos(), u.getTipoUsuario(), u.getSeguidos(), u.getSeguidores());
    }

//******************************** Sesion ********************************//

    /** Valida nick o email y contraseña. Devuelve el usuario (sin contraseña) o null si no son validos. */
    @WebMethod
    public DtUsuario iniciarSesion(@WebParam(name = "identificador") String identificador,
                                  @WebParam(name = "password") String password) throws Exception {
        if (identificador == null || identificador.isBlank() || password == null)
            return null;
        DtUsuario u = ic().obtenerUsuario(identificador.trim());
        if (u == null || !Utils.Crypt.verifyPassword(password, u.getPassword()))
            return null;
        return u;
    }

//******************************** Institutos y categorias ********************************//

    @WebMethod
    public List<DtInstituto> listarInstitutos() throws Exception {
        return ic().listarInstitutos();
    }

    @WebMethod
    public void crearInstituto(@WebParam(name = "nombre") String nombre) throws Exception {
        ic().crearInstituto(nombre);
    }

    @WebMethod
    public String obtenerInstitutoPorCurso(@WebParam(name = "nombreCurso") String nombreCurso) throws Exception {
        return ic().obtenerInstitutoPorCurso(nombreCurso);
    }

    @WebMethod
    public List<DtCategoria> listarCategorias() throws Exception {
        return ic().listarCategorias();
    }

//******************************** Usuarios ********************************//

    @WebMethod
    public void crearUsuario(@WebParam(name = "usuario") DtUsuario usuario,
                             @WebParam(name = "password") String password) throws Exception {
        ic().crearUsuario(conPassword(usuario, password));
    }

    @WebMethod
    public void crearUsuarioConImagen(@WebParam(name = "usuario") DtUsuario usuario,
                                      @WebParam(name = "password") String password,
                                      @WebParam(name = "imagen") byte[] imagen,
                                      @WebParam(name = "nombreImagen") String nombreImagen) throws Exception {
        ic().crearUsuario(conPassword(usuario, password), imagen, nombreImagen);
    }

    /** password null o vacio: se mantiene la contraseña actual. */
    @WebMethod
    public void modificarUsuario(@WebParam(name = "usuario") DtUsuario usuario,
                                 @WebParam(name = "password") String password) throws Exception {
        ic().modificarUsuario(conPassword(usuario, password));
    }

    /** password null o vacio: se mantiene la contraseña actual. */
    @WebMethod
    public void modificarUsuarioConImagen(@WebParam(name = "usuario") DtUsuario usuario,
                                          @WebParam(name = "password") String password,
                                          @WebParam(name = "imagen") byte[] imagen,
                                          @WebParam(name = "nombreImagen") String nombreImagen) throws Exception {
        ic().modificarUsuario(conPassword(usuario, password), imagen, nombreImagen);
    }

    @WebMethod
    public boolean existeUsuario(@WebParam(name = "nickname") String nickname) throws Exception {
        return ic().existeUsuario(nickname);
    }

    @WebMethod
    public boolean existeEmail(@WebParam(name = "email") String email) throws Exception {
        return ic().existeEmail(email);
    }

    @WebMethod
    public List<DtUsuario> listarUsuarios() throws Exception {
        return ic().listarUsuarios();
    }

    /** Acepta nickname o email. */
    @WebMethod
    public DtUsuario obtenerUsuario(@WebParam(name = "nicknameOEmail") String nicknameOEmail) throws Exception {
        return ic().obtenerUsuario(nicknameOEmail);
    }

    @WebMethod
    public List<String> listarDocentes() throws Exception {
        return ic().listarDocentes();
    }

    @WebMethod
    public void seguirUsuario(@WebParam(name = "nicknameSeguidor") String nicknameSeguidor,
                              @WebParam(name = "nicknameASeguir") String nicknameASeguir) throws Exception {
        ic().seguirUsuario(nicknameSeguidor, nicknameASeguir);
    }

    @WebMethod
    public void dejarDeSeguirUsuario(@WebParam(name = "nicknameSeguidor") String nicknameSeguidor,
                                     @WebParam(name = "nicknameADejar") String nicknameADejar) throws Exception {
        ic().dejarDeSeguirUsuario(nicknameSeguidor, nicknameADejar);
    }

    @WebMethod
    public List<String> listarSeguidos(@WebParam(name = "nickname") String nickname) throws Exception {
        return ic().listarSeguidos(nickname);
    }

    @WebMethod
    public List<String> listarSeguidores(@WebParam(name = "nickname") String nickname) throws Exception {
        return ic().listarSeguidores(nickname);
    }

//******************************** Cursos ********************************//

    @WebMethod
    public void altaCurso(@WebParam(name = "curso") DtCurso curso,
                          @WebParam(name = "nombreInstituto") String nombreInstituto) throws Exception {
        ic().altaCurso(curso, nombreInstituto);
    }

    @WebMethod
    public DtConsultaCurso obtenerDatosCurso(@WebParam(name = "nombreCurso") String nombreCurso) throws Exception {
        return ic().obtenerDatosCurso(nombreCurso);
    }

    @WebMethod
    public List<String> listarNombresCursos() throws Exception {
        return ic().listarNombresCursos();
    }

    @WebMethod
    public List<DtCurso> listarCursos() throws Exception {
        return ic().listarCursos();
    }

    @WebMethod
    public List<DtCurso> listarCursosPorUsuario(@WebParam(name = "nickname") String nickname) throws Exception {
        return ic().listarCursosPorUsuario(nickname);
    }

    @WebMethod
    public List<String> listarCursosPorInstituto(@WebParam(name = "nombreInstituto") String nombreInstituto) throws Exception {
        return ic().listarCursosPorInstituto(nombreInstituto);
    }

    @WebMethod
    public List<String> listarCursosPorCategoria(@WebParam(name = "nombreCategoria") String nombreCategoria) throws Exception {
        return ic().listarCursosPorCategoria(nombreCategoria);
    }

    @WebMethod
    public List<DtCurso> listarCursosPorPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma) throws Exception {
        return ic().listarCursosPorPrograma(nombrePrograma);
    }

//******************************** Ediciones ********************************//

    @WebMethod
    public void altaEdicionCurso(@WebParam(name = "nombreCurso") String nombreCurso,
                                 @WebParam(name = "edicion") DtEdicion edicion) throws Exception {
        ic().altaEdicionCurso(nombreCurso, edicion);
    }

    @WebMethod
    public DtEdicion getEdicion(@WebParam(name = "nombre") String nombre) throws Exception {
        return ic().getEdicion(nombre);
    }

    @WebMethod
    public DtEdicion buscarEdicion(@WebParam(name = "nombre") String nombre) throws Exception {
        return ic().buscarEdicion(nombre);
    }

    @WebMethod
    public List<String> listaEdicionPorCurso(@WebParam(name = "curso") String curso) throws Exception {
        return ic().listaEdicionPorCurso(curso);
    }

    @WebMethod
    public List<DtEdicion> listarEdicionPorCurso(@WebParam(name = "curso") String curso) throws Exception {
        return ic().listarEdicionPorCurso(curso);
    }

    @WebMethod
    public List<DtEdicion> listarEdicionesPorUsuario(@WebParam(name = "nickname") String nickname) throws Exception {
        return ic().listarEdicionesPorUsuario(nickname);
    }

    @WebMethod
    public void agregarDocenteAEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion,
                                       @WebParam(name = "nicknameDocente") String nicknameDocente) throws Exception {
        ic().agregarDocenteAEdicion(nombreEdicion, nicknameDocente);
    }

    @WebMethod
    public void quitarDocenteDeEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion,
                                       @WebParam(name = "nicknameDocente") String nicknameDocente) throws Exception {
        ic().quitarDocenteDeEdicion(nombreEdicion, nicknameDocente);
    }

//******************************** Inscripciones ********************************//

    @WebMethod
    public int setInscripcion(@WebParam(name = "estudiante") String estudiante,
                              @WebParam(name = "edicion") String edicion) throws Exception {
        return ic().setInscripcion(estudiante, edicion);
    }

    @WebMethod
    public int delInscripcion(@WebParam(name = "estudiante") String estudiante,
                              @WebParam(name = "edicion") String edicion) throws Exception {
        return ic().delInscripcion(estudiante, edicion);
    }

    @WebMethod
    public List<String> getEstudiantesInscriptosEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception {
        return ic().getEstudiantesInscriptosEdicion(nombreEdicion);
    }

    @WebMethod
    public List<String> getEstudiantesCandidatosEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception {
        return ic().getEstudiantesCandidatosEdicion(nombreEdicion);
    }

    @WebMethod
    public List<DtInscripcionEdicion> getInscripcionesCompletas(@WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception {
        return ic().getInscripcionesCompletas(nombreEdicion);
    }

    @WebMethod
    public List<DtInscripcionEdicion> getInscripcionesDeEstudiante(@WebParam(name = "nicknameEstudiante") String nicknameEstudiante) throws Exception {
        return ic().getInscripcionesDeEstudiante(nicknameEstudiante);
    }

    @WebMethod
    public void evaluarInscripciones(@WebParam(name = "nombreEdicion") String nombreEdicion,
                                     @WebParam(name = "evaluaciones") List<DtEvaluacion> evaluaciones) throws Exception {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        if (evaluaciones != null)
            for (DtEvaluacion e : evaluaciones)
                mapa.put(e.getNickname(), e.getEstado());
        ic().evaluarInscripciones(nombreEdicion, mapa);
    }

    @WebMethod
    public int setInscripcionPrograma(@WebParam(name = "estudiante") String estudiante,
                                      @WebParam(name = "programa") String programa) throws Exception {
        return ic().setInscripcionPrograma(estudiante, programa);
    }

//******************************** Programas de formacion ********************************//

    @WebMethod
    public boolean setCrearProgramaFormacion(@WebParam(name = "programa") DtPrograma programa) throws Exception {
        return ic().setCrearProgramaFormacion(programa);
    }

    @WebMethod
    public boolean setCrearProgramaFormacionConImagen(@WebParam(name = "programa") DtPrograma programa,
                                                      @WebParam(name = "imagen") byte[] imagen,
                                                      @WebParam(name = "nombreImagen") String nombreImagen) throws Exception {
        return ic().setCrearProgramaFormacion(programa, imagen, nombreImagen);
    }

    @WebMethod
    public boolean setCrearProgramaFormacionConDocente(@WebParam(name = "programa") DtPrograma programa,
                                                       @WebParam(name = "imagen") byte[] imagen,
                                                       @WebParam(name = "nombreImagen") String nombreImagen,
                                                       @WebParam(name = "docente") String docente) throws Exception {
        return ic().setCrearProgramaFormacion(programa, imagen, nombreImagen, docente);
    }

    @WebMethod
    public void agregarProgramaCurso(@WebParam(name = "programa") String programa,
                                     @WebParam(name = "curso") String curso) throws Exception {
        ic().agregarProgramaCurso(programa, curso);
    }

    @WebMethod
    public DtPrograma buscarPrograma(@WebParam(name = "nombre") String nombre) throws Exception {
        return ic().buscarPrograma(nombre);
    }

    @WebMethod
    public List<DtPrograma> listarProgramas() throws Exception {
        return ic().listarProgramas();
    }

    @WebMethod
    public List<DtInscripcionPrograma> listarInscriptosPorPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma) throws Exception {
        return ic().listarInscriptosPorPrograma(nombrePrograma);
    }

    @WebMethod
    public List<DtPrograma> listarProgramasPorUsuario(@WebParam(name = "nickname") String nickname) throws Exception {
        return ic().listarProgramasPorUsuario(nickname);
    }
}
