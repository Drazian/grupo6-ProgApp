package edu.edext.logica;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author vdraco
 */
public class InscripcionEdicionID implements Serializable {
 
    private String estudiante; 
    private String edicion;   
    
    public InscripcionEdicionID(){}
    
    public InscripcionEdicionID(String estudiante, String edicion){
        this.estudiante=estudiante;
        this.edicion=edicion;
    }
    
    public void setEstudiante(String estudiante) { this.estudiante = estudiante; }
    public void setEdicion(String edicion) { this.edicion = edicion; }

    public String getEstudiante() { return estudiante; }
    public String getEdicion() { return edicion; }
    
      @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InscripcionEdicionID that = (InscripcionEdicionID) o;
        return Objects.equals(estudiante, that.estudiante) && 
               Objects.equals(edicion, that.edicion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, edicion);
    }
}
