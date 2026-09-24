package com.edext.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author vdraco
 */
@Entity
@IdClass(InscripProgMolde.class)
@Table(name="InscripcionPrograma")
public class InscripcionPrograma {
    
    @Id
    @ManyToOne
    @JoinColumn(nullable=false)
    private Estudiante estudiante;
    @Column(nullable=false)
    private LocalDate fechaInscripcion;
    @Id 
    @ManyToOne
    @JoinColumn(nullable=false)
    private ProgramaFormacion programa;

    public InscripcionPrograma(){}
    
    public InscripcionPrograma(Estudiante estudiante, ProgramaFormacion programa, LocalDate fechaInscripcion){
        setFechaInscripcion(fechaInscripcion);
        setEstudiante(estudiante);
        setProgramaFormacion(programa);
    }
    
    public void setProgramaFormacion(ProgramaFormacion programa){ this.programa=programa; }
    public void setEstudiante(Estudiante estudiante){ this.estudiante=estudiante; }
    public void setFechaInscripcion(LocalDate fechaInscripcion){ this.fechaInscripcion=fechaInscripcion; }

    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public Estudiante getEstudiante(){return this.estudiante; }
    public ProgramaFormacion getProgramaFormacion(){ return this.programa; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.estudiante);
        hash = 41 * hash + Objects.hashCode(this.programa);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final InscripcionPrograma other = (InscripcionPrograma) obj;
        if (!Objects.equals(this.estudiante, other.estudiante)) return false;
        return Objects.equals(this.programa, other.programa);
    }

}
