package com.edext.persistencia;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author vdraco
 */
public class InscripProgMolde implements Serializable {
 
    private String estudiante; 
    private String programa;   
    
    public InscripProgMolde(){}
    
    public InscripProgMolde(String estudiante, String programa){
        this.estudiante=estudiante;
        this.programa=programa;
    }
    
    public void setEstudiante(String estudiante) { this.estudiante = estudiante; }
    public void setProgramaFormacion(String programa) { this.programa = programa; }

    public String getEstudiante() { return estudiante; }
    public String getProgramaFormacion() { return programa; }
    
      @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InscripProgMolde that = (InscripProgMolde) o;
        return Objects.equals(estudiante, that.estudiante) && 
               Objects.equals(programa, that.programa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, programa);
    }
}
