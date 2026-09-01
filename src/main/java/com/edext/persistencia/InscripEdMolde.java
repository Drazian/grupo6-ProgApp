package com.edext.persistencia;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author vdraco
 */
public class InscripEdMolde implements Serializable {
 
    private String estudiante; 
    private String edicion;   
    
    public InscripEdMolde(){}
    
    public InscripEdMolde(String estudiante, String edicion){
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
        InscripEdMolde that = (InscripEdMolde) o;
        return Objects.equals(estudiante, that.estudiante) && 
               Objects.equals(edicion, that.edicion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, edicion);
    }
    
}
