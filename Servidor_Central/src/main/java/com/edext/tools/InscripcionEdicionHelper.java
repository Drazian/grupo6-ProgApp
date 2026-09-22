package com.edext.tools;
//************************ Capa presentacion ***********************************

import com.edext.logica.Fabrica;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author vdraco
 */
public class InscripcionEdicionHelper {

    public InscripcionEdicionHelper(){
        
    }
    
    public int  setInscripcion(String estudiante, String edicion) throws Exception{
        return new Pipe().setInscripcion(estudiante, edicion);
    }
     
    public int  detInscripcion(String estudiante, String edicion) throws Exception{
        return new Pipe().delInscripcion(estudiante, edicion);
    }
    
    public String[] getEstudiantesInscriptos(String nombre){
        return getInscriptos(nombre).toArray(String[]::new);
    }
    
    public String[] getEstudiantesCandidatos(String nombre){
        return getCandidatos(nombre).toArray(String[]::new);
    }
    
    private List<String> getInscriptos(String nombre){
        Pipe tmp=new Pipe();
        List<String> ret;
        try { ret=tmp.getPipeEstudiantesInscriptos(nombre); }
        catch (Exception e) { ret=new ArrayList<>(); }
        tmp=null;
        return  ret;
    }
    
    private List<String> getCandidatos(String nombre){
        Pipe tmp=new Pipe();
        List<String> ret;
        try { ret=tmp.getPipeEstudiantesCandidatos(nombre); }
        catch (Exception e) { ret=new ArrayList<>(); }
        tmp=null;
        return  ret;
    }
    

//******************** Pipeline Presentacion -> Logica *********************    
    class Pipe{
        private List<String> getPipeEstudiantesInscriptos(String nombre) throws Exception{
            return Fabrica.getInstance().getIControlador().getEstudiantesInscriptosEdicion(nombre);
        }
        
        private List<String> getPipeEstudiantesCandidatos(String nombre) throws Exception{
            return Fabrica.getInstance().getIControlador().getEstudiantesCandidatosEdicion(nombre);
        }
        
        private int setInscripcion(String estudiante, String edicion) throws Exception{
            return Fabrica.getInstance().getIControlador().setInscripcion(estudiante, edicion);
        }
        
        private int delInscripcion(String estudiante, String edicion) throws Exception{
            return Fabrica.getInstance().getIControlador().delInscripcion(estudiante, edicion);
        }
    }
}
