package edu.edext.logica;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author vdraco
 */
public class InscripcionProgramaID implements Serializable {
 
    private String estudiante; 
    private String programa;   
    
    public InscripcionProgramaID(){}
    
    public InscripcionProgramaID(String estudiante, String programa){
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
        InscripcionProgramaID that = (InscripcionProgramaID) o;
        return Objects.equals(estudiante, that.estudiante) && 
               Objects.equals(programa, that.programa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, programa);
    }
}
