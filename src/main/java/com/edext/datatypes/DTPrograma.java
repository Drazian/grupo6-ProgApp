package com.edext.datatypes;

import java.time.LocalDate;

/**
 *
 * @author vdraco
 */
public class DTPrograma {
    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaFin;
    private LocalDate fechaInicio;
    private LocalDate fechaRegistro;

    public DTPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(null , nombre, "", fechaRegistro, fechaInicio, fechaFin);
    }
    
    public DTPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(null , nombre, descripcion, fechaRegistro, fechaInicio, fechaFin);
    }
    
    public DTPrograma(Integer id, String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this.id=id;
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
    }
    
    public Integer getID(){ return this.id; }
    public String getNombre(){ return this.nombre; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public String getDescripcion(){ return this.descripcion; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public LocalDate getFechaRegistro (){ return this.fechaRegistro; }

}