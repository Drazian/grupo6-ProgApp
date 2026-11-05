package edu.edext.logica;

import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtCurso;
import java.util.List;
import java.util.Map;

public interface IControlador {

//******************************** Institutos ********************************//
    void crearInstituto(String nombre) throws Exception;
    void eliminarInstituto(String nombre) throws Exception;
    String obtenerInstitutoPorCurso(String nombreCurso) throws Exception;   // exporta a Web
    List<DtInstituto> listarInstitutos() throws Exception;   // exporta a Web

//******************************** Categrias *********************************//
    void crearCategoria(String nombre) throws Exception;
    List<DtCategoria> listarCategorias() throws Exception;      // exporta a Web

//********************************* Usuarios *********************************//
    void crearUsuario(DtUsuario usuario) throws Exception;
    void crearUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception;     // exporta a Web
    boolean existeUsuario(String nickname) throws Exception;    // exporta a Web
    boolean existeEmail(String email) throws Exception;         // exporta a Web
    void modificarUsuario(DtUsuario usuarioModificado)throws Exception;
    void modificarUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception; // exporta a Web
    List<DtUsuario> listarUsuarios()throws Exception;           // exporta a Web
    DtUsuario obtenerUsuario(String nicknameOEmail) throws Exception;   // exporta a Web
    List<String> listarDocentes() throws Exception;             // exporta a Web
    DtUsuario validarNick(String nickname, String password) throws Exception; // exporta a Web
    DtUsuario validarEmail(String email, String password) throws Exception;   // exporta a Web
    void seguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception;       // exporta a Web
    void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameADejar) throws Exception; // exporta a Web
    List<String> listarSeguidos(String nickname) throws Exception;   // exporta a Web
    List<String> listarSeguidores(String nickname) throws Exception; // exporta a Web
    void agregarDocenteAEdicion(String nombreEdicion, String nicknameDocente) throws Exception; // exporta a Web
    void quitarDocenteDeEdicion(String nombreEdicion, String nicknameDocente) throws Exception; // exporta a Web

//********************************* Cursos ***********************************//
    void altaCurso(DtCurso curso, String nombreInstituto) throws Exception; // exporta a Web
    DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception; // exporta a Web
    List<String> listarNombresCursos() throws Exception;        // exporta a Web
    List<DtCurso> listarCursosPorUsuario(String nickname) throws Exception;
    List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception; // exporta a Web
    List<String> listarCursosPorCategoria(String nombreInstituto) throws Exception; // exporta a Web
    List<DtCurso> listarCursos() throws Exception;              // exporta a Web

//********************************* Edicion **********************************//
    void altaEdicionCurso(String nombreCurso, DtEdicion dt) throws Exception;   // exporta a Web
    DtEdicion getEdicion(String nombre) throws Exception;       // exporta a Web
    List<String> listaEdicionPorCurso(String curso) throws Exception;   // exporta a Web
    List<DtEdicion> listarEdicionPorCurso(String curso) throws Exception;
    List<DtEdicion> listarEdicionesPorUsuario(String nickname) throws Exception;    // exporta a Web
//    DtEdicion buscarEdicion(String nombre) throws Exception;

//******************************* Inscripcion ********************************//
    int setInscripcion(String estudiante, String edicion) throws Exception;
    int setInscripcion(String nomEstudiante, String nomEdicion, String videoURL) throws Exception;   // exporta a Web
    int delInscripcion(String estudiante, String edicion) throws Exception; // exporta a Web
    List<String> getEstudiantesInscriptosEdicion(String nombre) throws Exception;
    List<String> getEstudiantesCandidatosEdicion(String nombre) throws Exception;
    List<DtUsuario> getEstudiantesInscriptosPrograma(String nombre) throws Exception;
    List<DtUsuario> getEstudiantesIncriptosEdicion(String nombre) throws Exception;
    List<DtInscripcionEdicion> getInscripcionesCompletas(String nombreEdicion) throws Exception;         // exporta a Web
    void evaluarInscripciones(String nombreEdicion, Map<String, Integer> evaluaciones) throws Exception; // exporta a Web
    List<DtInscripcionEdicion> getInscripcionesDeEstudiante(String nicknameEstudiante) throws Exception; // exporta a Web
    List<String> listarEdicionesCerradasDeDocente(String nicknameDocente, String nombreCurso) throws Exception; // exporta a Web
    List<DtInscripcionEdicion> getActaEdicion(String nombreEdicion, String nicknameDocente) throws Exception;   // exporta a Web
    List<String> listarEdicionesAbiertasDeDocente(String nicknameDocente, String nombreCurso) throws Exception; // exporta a Web
    List<DtInscripcionEdicion> getParticipantesParaEvaluar(String nombreEdicion, String nicknameDocente) throws Exception; // exporta a Web
    void ingresarResultados(String nombreEdicion, String nicknameDocente, Map<String, Integer> notas, boolean cerrarEdicion) throws Exception; // exporta a Web
    int setInscripcionPrograma(String nomEstudiante, String nomPrograma) throws Exception;  // exporta a Web

//**************************** ProgramaFormacion *****************************//
    boolean setCrearProgramaFormacion(DtPrograma programa) throws Exception;
    boolean setCrearProgramaFormacion(DtPrograma programa, byte[] bytesImagen, String nombreOriginal, String docente) throws Exception;
    void agregarProgramaCurso(String programa, String curso) throws Exception; // exporta a Web
    DtPrograma buscarPrograma(String nombre) throws Exception;  // exporta a Web
    List<DtCurso> listarCursosPorPrograma(String nombre) throws Exception;
    List<DtPrograma> listarProgramasPorUsuario(String nickname) throws Exception;   // exporta a Web
    List<DtPrograma> listarProgramas() throws Exception;        // exporta a Web
    boolean setCrearProgramaFormacion(DtPrograma programa, byte[] bytesImagen, String nombreOriginal) throws Exception; // exporta a Web

//***************************** Datos de Prueba ******************************//
    int cargarDatosDePrueba() throws Exception;
    
}
