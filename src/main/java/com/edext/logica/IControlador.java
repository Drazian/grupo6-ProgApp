package com.edext.logica;
import com.edext.datatypes.DtInstituto;
import java.util.List;

public interface IControlador {
    void crearInstituto(String nombre) throws Exception;
    void eliminarInstituto(String nombre) throws Exception;
    List<DtInstituto> listarInstitutos() throws Exception;
    
}
