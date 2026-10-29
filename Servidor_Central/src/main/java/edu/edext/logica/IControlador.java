package edu.edext.logica;

import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtSessionUsuario;
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
    String obtenerInstitutoPorCurso(String nombreCurso) throws Exception;
    List<DtInstituto> listarInstitutos() throws Exception;

//******************************** Categrias *********************************//
    void crearCategoria(String nombre) throws Exception;
    List<DtCategoria> listarCategorias() throws Exception;

//********************************* Usuarios *********************************//
    void crearUsuario(DtUsuario usuario) throws Exception;
    boolean existeUsuario(String nickname) throws Exception;
    boolean existeEmail(String email) throws Exception;
    void modificarUsuario(DtUsuario usuarioModificado)throws Exception;
    List<DtUsuario> listarUsuarios()throws Exception;
    /** Busca un usuario por nickname (o email). Retorna null si no existe. */
    DtUsuario obtenerUsuario(String nicknameOEmail) throws Exception;
    List<String> listarDocentes() throws Exception;
    DtSessionUsuario validarNick(String nickname, String password) throws Exception;
    DtSessionUsuario validarEmail(String email, String password) throws Exception;

//********************************* Cursos ***********************************//
    void altaCurso(DtCurso curso, String nombreInstituto) throws Exception;
    DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception;
    List<String> listarNombresCursos() throws Exception;
    List<DtCurso> listarCursosPorUsuario(String nickname) throws Exception;
    List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception;
    List<String> listarCursosPorCategoria(String nombreInstituto) throws Exception;
    List<DtCurso> listarCursos() throws Exception;

//********************************* Edicion **********************************//
    void altaEdicionCurso(String nombreCurso, DtEdicion dt) throws Exception;
    DtEdicion getEdicion(String nombre) throws Exception;
    List<String> listaEdicionPorCurso(String curso) throws Exception;
    List<DtEdicion> listarEdicionPorCurso(String curso) throws Exception;
    List<DtEdicion> listarEdicionesPorUsuario(String nickname) throws Exception;
    DtEdicion buscarEdicion(String nombre) throws Exception;

//******************************* Inscripcion ********************************//
    int setInscripcion(String estudiante, String edicion) throws Exception;
    int delInscripcion(String estudiante, String edicion) throws Exception;
    List<String> getEstudiantesInscriptosEdicion(String nombre) throws Exception;
    List<String> getEstudiantesCandidatosEdicion(String nombre) throws Exception;

//**************************** ProgramaFormacion *****************************//
    boolean setCrearProgramaFormacion(DtPrograma programa) throws Exception;
    void agregarProgramaCurso(String programa, String curso) throws Exception;
    DtPrograma buscarPrograma(String nombre) throws Exception;
    List<DtCurso> listarCursosPorPrograma(String nombre) throws Exception;
    List<DtPrograma> listarProgramasPorUsuario(String nickname) throws Exception;
    List<DtPrograma> listarProgramas() throws Exception;

//***************************** Datos de Prueba ******************************//
    int cargarDatosDePrueba() throws Exception;
    
    
    
    
    //********************** probar *******************************************
    public void crearUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception;
    public void modificarUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception;
    public boolean setCrearProgramaFormacion(DtPrograma programa, byte[] bytesImagen, String nombreOriginal) throws Exception;
    

    
    void seguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception;
    void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameADejar) throws Exception;

    List<String> listarSeguidos(String nickname) throws Exception;

    List<String> listarSeguidores(String nickname) throws Exception;

    List<DtInscripcionEdicion> getInscripcionesCompletas(String nombreEdicion) throws Exception;
    void evaluarInscripciones(String nombreEdicion, Map<String, Integer> evaluaciones) throws Exception;    
    List<DtInscripcionEdicion> getInscripcionesDeEstudiante(String nicknameEstudiante) throws Exception;

    int setInscripcionPrograma(String nomEstudiante, String nomPrograma) throws Exception;
    public void agregarDocenteAEdicion(String nombreEdicion, String nicknameDocente) throws Exception;
    public void quitarDocenteDeEdicion(String nombreEdicion, String nicknameDocente) throws Exception;
}