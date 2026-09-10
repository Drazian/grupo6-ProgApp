package com.edext.logica;
import com.edext.datatypes.DtPrograma;
import com.edext.datatypes.DtInstituto;
import com.edext.datatypes.DtUsuario;
import com.edext.datatypes.DtCurso;
import com.edext.datatypes.DtEdicion;

import java.util.List;

public interface IControlador {
    void crearInstituto(String nombre) throws Exception;
    void eliminarInstituto(String nombre) throws Exception;
    List<DtInstituto> listarInstitutos() throws Exception;
    void crearUsuario(DtUsuario usuario) throws Exception;
    boolean existeUsuario(String nickname) throws Exception;
    boolean existeEmail(String email) throws Exception;
    List<DtUsuario> listarUsuarios()throws Exception;

    public void modificarUsuario(DtUsuario usuarioModificado)throws Exception;
    
    void altaCurso(DtCurso curso, String nombreInstituto) throws Exception;
    List<String> listarNombresCursos() throws Exception;
    
    List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception;
    com.edext.datatypes.DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception;
    
    List<String> listarDocentes() throws Exception;
    void altaEdicionCurso(String nombreCurso, com.edext.datatypes.DtEdicion dt) throws Exception;
    
    boolean setCrearProgramaFormacion(DtPrograma programa) throws Exception;
      
    void agregarProgramaCurso(String programa, String curso) throws Exception;
    List<DtPrograma> listarProgramas() throws Exception;
    List<DtCurso> listarCursos() throws Exception;
    List<DtCurso> listarCursosPorPrograma(String nombre) throws Exception;
    
    DtPrograma buscarPrograma(String nombre) throws Exception;
    
    void cargarDatosDePrueba() throws Exception;
     
}
