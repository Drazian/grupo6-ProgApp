package com.edext.persistencia;

import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.IdClass;

/**
 *
 * @author vdraco
 */
@Entity
@IdClass(InscripEdMolde.class)
@Table(name="InscripcionEdicion")
public class InscripcionEdicion {
    
    @Column(nullable=false)
    private LocalDate fechaInscripcion;
    @Id
    @ManyToOne
    @JoinColumn(nullable=false)
    private Estudiante estudiante;
    @Id 
    @ManyToOne
    @JoinColumn(nullable=false)
    private Edicion edicion;

    public InscripcionEdicion(){}
    
    public InscripcionEdicion(Estudiante estudiante, Edicion edicion, LocalDate fechaInscripcion){
        this.estudiante=estudiante;
        this.edicion=edicion;
        this.fechaInscripcion=fechaInscripcion;
    }
    
    public void setFechaInscripcion(LocalDate fechaInscripcion){ this.fechaInscripcion=fechaInscripcion; }
    public void setEstudiante(Estudiante estudiante){ this.estudiante=estudiante; }
    public void setEdicion(Edicion edicion){ this.edicion=edicion; }

    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public Edicion getEdicion(){ return this.edicion; }
    public Estudiante getEstudiante(){return this.estudiante; }
    
}
