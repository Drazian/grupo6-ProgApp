package edu.edext.logica;

import java.util.Objects;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author vdraco
 */
@Entity
@IdClass(InscripcionEdicionID.class)
@Table(name="InscripcionEdicion")
public class InscripcionEdicion {
    
    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(nullable=false)
    private Estudiante estudiante;

    @NotNull
    @Column(nullable=false)
    private LocalDate fechaInscripcion;
    
    @Min(0)
    @Max(3)
    private int estado;

    @Id 
    @ManyToOne
    @NotNull
    @JoinColumn(nullable=false)
    private Edicion edicion;

    public InscripcionEdicion(){}
    
    public InscripcionEdicion(Estudiante estudiante, Edicion edicion, LocalDate fechaInscripcion, int estado){
        this.fechaInscripcion=fechaInscripcion;
        this.estudiante=estudiante;
        this.edicion=edicion;
        this.estado=estado;
    }
    
    public void setEstado(int estado){ this.estado=estado; }
    public void setEdicion(Edicion edicion){ this.edicion=edicion; }
    public void setEstudiante(Estudiante estudiante){ this.estudiante=estudiante; }
    public void setFechaInscripcion(LocalDate fechaInscripcion){ this.fechaInscripcion=fechaInscripcion; }

    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public Estudiante getEstudiante(){return this.estudiante; }
    public Edicion getEdicion(){ return this.edicion; }
    public int getEstado(){ return this.estado; }

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