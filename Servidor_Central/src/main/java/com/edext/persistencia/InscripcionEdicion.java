package com.edext.persistencia;

import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.util.Objects;

/**
 *
 * @author vdraco
 */
@Entity
@IdClass(InscripEditMolde.class)
@Table(name="InscripcionEdicion")
public class InscripcionEdicion {
    
    @Id
    @ManyToOne
    @JoinColumn(nullable=false)
    private Estudiante estudiante;
    @Column(nullable=false)
    private LocalDate fechaInscripcion;
    @Id 
    @ManyToOne
    @JoinColumn(nullable=false)
    private Edicion edicion;

    public InscripcionEdicion(){}
    
    public InscripcionEdicion(Estudiante estudiante, Edicion edicion, LocalDate fechaInscripcion){
        this.fechaInscripcion=fechaInscripcion;
        this.estudiante=estudiante;
        this.edicion=edicion;
    }
    
    public void setEdicion(Edicion edicion){ this.edicion=edicion; }
    public void setEstudiante(Estudiante estudiante){ this.estudiante=estudiante; }
    public void setFechaInscripcion(LocalDate fechaInscripcion){ this.fechaInscripcion=fechaInscripcion; }

    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public Estudiante getEstudiante(){return this.estudiante; }
    public Edicion getEdicion(){ return this.edicion; }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.estudiante);
        hash = 17 * hash + Objects.hashCode(this.edicion);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final InscripcionEdicion other = (InscripcionEdicion) obj;
        if (!Objects.equals(this.estudiante, other.estudiante)) return false;
        return Objects.equals(this.edicion, other.edicion);
    }
   
}
