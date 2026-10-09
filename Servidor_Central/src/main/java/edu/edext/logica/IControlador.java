package edu.edext.logica;

import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtCurso;
import java.util.List;

public interface IControlador {
    
    void crearInstituto(String nombre) throws Exception;
    void eliminarInstituto(String nombre) throws Exception;
    List<DtInstituto> listarInstitutos() throws Exception;
    
    void crearCategoria(String nombre) throws Exception;
    List<DtCategoria> listarCategorias() throws Exception;
    
    void crearUsuario(DtUsuario usuario) throws Exception;
    void crearUsuario(DtUsuario usuario, java.io.File imagenTemporal)
        throws Exception;
    boolean existeUsuario(String nickname) throws Exception;
    boolean existeEmail(String email) throws Exception;
    public void modificarUsuario(DtUsuario usuarioModificado)throws Exception;
    List<DtUsuario> listarUsuarios()throws Exception;
    List<String> listarDocentes() throws Exception;

    
    void altaCurso(DtCurso curso, String nombreInstituto) throws Exception;
    DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception;
    List<String> listarNombresCursos() throws Exception;
    List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception;
    List<String> listarCursosPorCategoria(String nombreInstituto) throws Exception;
    List<DtCurso> listarCursos() throws Exception;
    
    
    void altaEdicionCurso(String nombreCurso, edu.edext.datatypes.DtEdicion dt) throws Exception;
    DtEdicion getEdicion(String nombre) throws Exception;
    List<DtEdicion> listarEdicionPorCurso(String curso) throws Exception;
    List<String> listaEdicionPorCurso(String curso) throws Exception;
    DtEdicion buscarEdicion(String nombre) throws Exception;

    int setInscripcion(String estudiante, String edicion) throws Exception;
    int delInscripcion(String estudiante, String edicion) throws Exception;
    List<String> getEstudiantesInscriptosEdicion(String nombre) throws Exception;
    List<String> getEstudiantesCandidatosEdicion(String nombre) throws Exception;
    
    
    boolean setCrearProgramaFormacion(DtPrograma programa) throws Exception;
    void agregarProgramaCurso(String programa, String curso) throws Exception;
    DtPrograma buscarPrograma(String nombre) throws Exception;
    List<DtCurso> listarCursosPorPrograma(String nombre) throws Exception;
    List<DtPrograma> listarProgramas() throws Exception;
    
    
    //*********************** Revisar contribucion de Diego*********************    
    int cargarDatosDePrueba() throws Exception;
    List<DtCurso> listarCursosPorUsuario(String nickname) throws Exception;
    List<DtEdicion> listarEdicionesPorUsuario(String nickname) throws Exception;
    List<DtPrograma> listarProgramasPorUsuario(String nickname) throws Exception;
    String obtenerInstitutoPorCurso(String nombreCurso) throws Exception;

    //**************************************************************************

    
    
}