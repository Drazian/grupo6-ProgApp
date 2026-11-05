package edu.edext.logica;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.MapAdapter;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtCurso;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vdraco
 */
//@WebService(serviceName="IBridgeSOAP")
@WebService
public interface IBridgeSOAP {

//******************************** Institutos ********************************//
    @WebMethod(operationName="listarInstitutos")
    List<DtInstituto> listarInstitutos() throws Exception;
    @WebMethod(operationName="obtenerInstitutoPorCurso")
    String obtenerInstitutoPorCurso(
            @WebParam(name="nombreCurso") String nombreCurso) throws Exception;
//******************************** Categrias *********************************//
    @WebMethod(operationName="listarCategorias")
    List<DtCategoria> listarCategorias() throws Exception;
//********************************* Usuarios *********************************//
    @WebMethod(operationName="crearUsuario")
    void crearUsuario(
            @WebParam(name="usuario") DtUsuario usuario,
            @WebParam(name="bytesImagen") byte[] bytesImagen,
            @WebParam(name="nombreOriginal") String nombreOriginal) throws Exception;
    @WebMethod(operationName="existeUsuario")
    boolean existeUsuario(@WebParam(name="nickname") String nickname) throws Exception;
    @WebMethod(operationName="existeEmail")
    boolean existeEmail(@WebParam(name="email") String email) throws Exception;
    @WebMethod(operationName="modificarUsuario")
    void modificarUsuario(
            @WebParam(name="usuario") DtUsuario usuario,
            @WebParam(name="bytesImagen") byte[] bytesImagen,
            @WebParam(name="nombreOriginal") String nombreOriginal) throws Exception;
    @WebMethod(operationName="listarUsuarios")
    List<DtUsuario> listarUsuarios()throws Exception;
    @WebMethod(operationName="listarDocentes")
    List<String> listarDocentes() throws Exception;
    @WebMethod(operationName="obtenerUsuario")
    DtUsuario obtenerUsuario(
            @WebParam(name="nicknameOEmail" )String nicknameOEmail) throws Exception;
    @WebMethod(operationName="validarNick")
    DtUsuario validarNick(
            @WebParam(name="nickname") String nickname,
            @WebParam(name="password") String password) throws Exception;
    @WebMethod(operationName="validarEmail")
    DtUsuario validarEmail(
            @WebParam(name="email") String email,
            @WebParam(name="password") String password) throws Exception;
    @WebMethod(operationName="seguirUsuario")
    void seguirUsuario(
            @WebParam(name="nicknameSeguidor") String nicknameSeguidor,
            @WebParam(name="nicknameASeguir") String nicknameASeguir) throws Exception;
    @WebMethod(operationName="dejarDeSeguirUsuario")
    void dejarDeSeguirUsuario(
            @WebParam(name="nicknameSeguidor") String nicknameSeguidor,
            @WebParam(name="nicknameADejar") String nicknameADejar) throws Exception;
    @WebMethod(operationName="listarSeguidos")
    List<String> listarSeguidos(@WebParam(name="nickname") String nickname) throws Exception;
    @WebMethod(operationName="listarSeguidores")
    List<String> listarSeguidores(@WebParam(name="nickname") String nickname) throws Exception;
    @WebMethod(operationName="agregarDocenteAEdicion")
    void agregarDocenteAEdicion(
            @WebParam(name="nombreEdicion") String nombreEdicion,
            @WebParam(name="nicknameDocente") String nicknameDocente) throws Exception;
    @WebMethod(operationName="quitarDocenteDeEdicion")
    void quitarDocenteDeEdicion(
            @WebParam(name="nombreEdicion") String nombreEdicion,
            @WebParam(name="nicknameDocente") String nicknameDocente) throws Exception;
//********************************* Cursos ***********************************//
    @WebMethod(operationName="altaCurso")
    void altaCurso(
            @WebParam(name="curso") DtCurso curso,
            @WebParam(name="nombreInstituto") String nombreInstituto) throws Exception;
    @WebMethod(operationName="obtenerDatosCurso")
    DtConsultaCurso obtenerDatosCurso(@WebParam(name="nombreCurso") String nombreCurso) throws Exception;
    @WebMethod(operationName="listarNombresCursos")
    List<String> listarNombresCursos() throws Exception;
    @WebMethod(operationName="listarCursosPorInstituto")
    List<String> listarCursosPorInstituto(@WebParam(name="nombreInstituto") String nombreInstituto) throws Exception;
    @WebMethod(operationName="listarCursosPorCategoria")
    List<String> listarCursosPorCategoria(@WebParam(name="nombreInstituto") String nombreInstituto) throws Exception;
    @WebMethod(operationName="listarCursos")
    List<DtCurso> listarCursos() throws Exception;
//********************************* Edicion **********************************//
    @WebMethod(operationName="altaEdicionCurso")
    void altaEdicionCurso(
            @WebParam(name="nombreCurso") String nombreCurso,
            @WebParam(name="dt") DtEdicion dt) throws Exception;
    @WebMethod(operationName="getEdicion")
    DtEdicion getEdicion(@WebParam(name="nombre") String nombre) throws Exception;
    @WebMethod(operationName="listaEdicionPorCurso")
    List<String> listaEdicionPorCurso(@WebParam(name="curso") String curso) throws Exception;
    @WebMethod(operationName="listarEdicionesPorUsuario")
    List<DtEdicion> listarEdicionesPorUsuario(@WebParam(name="nickname") String nickname) throws Exception;
//******************************* Inscripcion ********************************//
//    @WebMethod(operationName="setInscripcion")
//    int setInscripcion(
//            @WebParam(name="estudiante") String estudiante,
//            @WebParam(name="edicion") String edicion) throws Exception;
    @WebMethod(operationName="setInscripcion")
    int setInscripcion(
            @WebParam(name="nomEstudiante") String nomEstudiante,
            @WebParam(name="nomEdicion") String nomEdicion,
            @WebParam(name="videoURL") String videoURL) throws Exception;
    @WebMethod(operationName="delInscripcion")
    int delInscripcion(
            @WebParam(name="estudiante") String estudiante,
            @WebParam(name="edicion") String edicion) throws Exception;
    @WebMethod(operationName="getInscripcionesCompletas")
    List<DtInscripcionEdicion> getInscripcionesCompletas(
            @WebParam(name="nombreEdicion") String nombreEdicion) throws Exception;
//    @WebMethod(operationName="evaluarInscripciones")
//    void evaluarInscripciones(
//            @WebParam(name="nombreEdicion") String nombreEdicion,
//            @WebParam(name="evaluaciones") Map<String, Integer> evaluaciones) throws Exception;
    @WebMethod(operationName="evaluarInscripciones")
    void evaluarInscripciones(
            @WebParam(name="nombreEdicion") String nombreEdicion,
            @WebParam(name="evaluaciones") 
            @XmlJavaTypeAdapter(MapAdapter.class) Map<String, Integer> evaluaciones) throws Exception;
    @WebMethod(operationName="getInscripcionesDeEstudiante")
    List<DtInscripcionEdicion> getInscripcionesDeEstudiante(
            @WebParam(name="nicknameEstudiante") String nicknameEstudiante) throws Exception;
    @WebMethod(operationName="listarEdicionesCerradasDeDocente")
    List<String> listarEdicionesCerradasDeDocente(
            @WebParam(name="nicknameDocente") String nicknameDocente,
            @WebParam(name="nombreCurso") String nombreCurso) throws Exception;
    @WebMethod(operationName="getActaEdicion")
    List<DtInscripcionEdicion> getActaEdicion(
            @WebParam(name="nombreEdicion") String nombreEdicion,
            @WebParam(name="nicknameDocente") String nicknameDocente) throws Exception;
    @WebMethod(operationName="listarEdicionesAbiertasDeDocente")
    List<String> listarEdicionesAbiertasDeDocente(
            @WebParam(name="nicknameDocente") String nicknameDocente,
            @WebParam(name="nombreCurso") String nombreCurso) throws Exception;
    @WebMethod(operationName="getParticipantesParaEvaluar")
    List<DtInscripcionEdicion> getParticipantesParaEvaluar(
            @WebParam(name="nombreEdicion") String nombreEdicion,
            @WebParam(name="nicknameDocente") String nicknameDocente) throws Exception;
    @WebMethod(operationName="ingresarResultados")
    void ingresarResultados(
            @WebParam(name="nombreEdicion") String nombreEdicion,
            @WebParam(name="nicknameDocente") String nicknameDocente,
            @WebParam(name="notas")
            @XmlJavaTypeAdapter(MapAdapter.class) Map<String, Integer> notas,
            @WebParam(name="cerrarEdicion") boolean cerrarEdicion) throws Exception;
//******************************* Valoraciones *******************************//
    @WebMethod(operationName="listarCursosParaValorar")
    List<String> listarCursosParaValorar(
            @WebParam(name="nicknameEstudiante") String nicknameEstudiante) throws Exception;
    @WebMethod(operationName="getValoracionesCurso")
    edu.edext.datatypes.DtValoracionesCurso getValoracionesCurso(
            @WebParam(name="nombreCurso") String nombreCurso,
            @WebParam(name="nicknameEstudiante") String nicknameEstudiante) throws Exception;
    @WebMethod(operationName="valorarCurso")
    void valorarCurso(
            @WebParam(name="nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name="nombreCurso") String nombreCurso,
            @WebParam(name="puntaje") int puntaje) throws Exception;
    @WebMethod(operationName="listarResumenesValoraciones")
    List<edu.edext.datatypes.DtValoracionesCurso> listarResumenesValoraciones() throws Exception;
    @WebMethod(operationName="setInscripcionPrograma")
    int setInscripcionPrograma(
            @WebParam(name="nomEstudiante") String nomEstudiante,
            @WebParam(name="nomPrograma") String nomPrograma) throws Exception;
//**************************** ProgramaFormacion *****************************//
    @WebMethod(operationName="agregarProgramaCurso")
    void agregarProgramaCurso(
            @WebParam(name="programa") String programa,
            @WebParam(name="curso") String curso) throws Exception;
    @WebMethod(operationName="buscarPrograma")
    DtPrograma buscarPrograma(@WebParam(name="nombre") String nombre) throws Exception;
    @WebMethod(operationName="listarProgramasPorUsuario")
    List<DtPrograma> listarProgramasPorUsuario(
            @WebParam(name="nickname") String nickname) throws Exception;
    @WebMethod(operationName="listarProgramas")
    List<DtPrograma> listarProgramas() throws Exception;
    @WebMethod(operationName="setCrearProgramaFormacion")
    boolean setCrearProgramaFormacion(
            @WebParam(name="programa") DtPrograma programa,
            @WebParam(name="bytesImagen") byte[] bytesImagen,
            @WebParam(name="nombreOriginal") String nombreOriginal) throws Exception;

}
