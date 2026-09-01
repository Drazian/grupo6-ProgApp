package com.edext.datatypes;

import java.time.LocalDate;

/**
 *
 * @author vdraco
 */
public class DTEdicion {
    private Integer id;
    private String nombre;
    private Integer cupo;
    private LocalDate fechaRegistro;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaPublicacion;
 
    
    public DTEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion){
        this(null, nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, null);
    }

    public DTEdicion(Integer id, String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, LocalDate fechaRegistro){
        this.id=id;
        this.nombre=nombre;
        this.cupo=cupo;
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
        this.fechaPublicacion=fechaPublicacion;
    }
    
    public Integer getID(){ return this.id; }
    public String getNombre(){ return this.nombre; }
    public Integer getCupo(){ return this.cupo; }
    public LocalDate getFechaRegisto (){ return this.fechaRegistro; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public LocalDate getFechaPublicacion(){ return this.fechaPublicacion; }
        
}
