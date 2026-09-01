package com.edext.persistencia;

import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 *
 * @author vdraco
 */
@Entity
@Table(name="Edicion")
public class Edicion {

    @Id
    private String nombre;
    @Column(nullable=false)
    private Integer cupo;
    @Column(nullable=false)
    private LocalDate fechaInicio;
    @Column(nullable=false)
    private LocalDate fechaFin;
    @Column(nullable=false)
    private LocalDate fechaPublicacion;

    @ManyToOne
    @JoinColumn(name="instituto_nombre", nullable=false)
    private Instituto instituto;

//    @ManyToOne
//    @JoinColumn(name="curso_nombre", nullable=false)
//    private Curso curso;
 
    public Edicion(){}
    
    public Edicion(String nombre, Integer cupo, LocalDate fechaPublicacion, LocalDate fechaInicio, LocalDate fechaFin){
        this.nombre=nombre;
        this.cupo=cupo;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
        this.fechaPublicacion=fechaPublicacion;
    }
    
    public void setNombre(String nombre){ this.nombre=nombre;  }
    public void setCupo(Integer cupo){ this.cupo=cupo; }
    public void setFechaPublicacion (LocalDate fechaPublicacion){ this.fechaPublicacion=fechaPublicacion; }
    public void setFechaInicio(LocalDate fechaInicio){ this.fechaInicio=fechaInicio; }
    public void setFechaFin(LocalDate fechaFin){ this.fechaFin=fechaFin; }

    public String getNombre(){ return this.nombre; }
    public Integer getCupo(){ return this.cupo; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public LocalDate getFechaPublicacion(){ return this.fechaPublicacion; }
    
}
