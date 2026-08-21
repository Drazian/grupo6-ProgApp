package com.edext.logica;
import com.edext.datatypes.DtInstituto;
import com.edext.datatypes.DtUsuario;

import java.util.List;

public interface IControlador {
    void crearInstituto(String nombre) throws Exception;
    void eliminarInstituto(String nombre) throws Exception;
    List<DtInstituto> listarInstitutos() throws Exception;
    void crearUsuario(DtUsuario usuario) throws Exception;
    boolean existeUsuario(String nickname) throws Exception;
    boolean existeEmail(String email) throws Exception;
    
}
