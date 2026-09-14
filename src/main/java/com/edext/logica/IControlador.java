package com.edext.logica;

import com.edext.datatypes.DtConsultaCurso;
import com.edext.datatypes.DtPrograma;
import com.edext.datatypes.DtInstituto;
import com.edext.datatypes.DtUsuario;
import com.edext.datatypes.DtEdicion;
import com.edext.datatypes.DtCurso;
import java.util.List;

public interface IControlador {
    
    void crearInstituto(String nombre) throws Exception;
    void eliminarInstituto(String nombre) throws Exception;
    List<DtInstituto> listarInstitutos() throws Exception;

    
    void crearUsuario(DtUsuario usuario) throws Exception;
    boolean existeUsuario(String nickname) throws Exception;
    boolean existeEmail(String email) throws Exception;
    public void modificarUsuario(DtUsuario usuarioModificado)throws Exception;
    List<DtUsuario> listarUsuarios()throws Exception;
    List<String> listarDocentes() throws Exception;

    
    void altaCurso(DtCurso curso, String nombreInstituto) throws Exception;
    DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception;
    List<String> listarNombresCursos() throws Exception;
    List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception;
    List<DtCurso> listarCursos() throws Exception;
    
    
    void altaEdicionCurso(String nombreCurso, com.edext.datatypes.DtEdicion dt) throws Exception;
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