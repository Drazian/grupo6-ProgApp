package com.edext.datatypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vdraco
 */
public class DtPrograma {
    private final String nombre;
    private final String descripcion;
    private final LocalDate fechaFin;
    private final LocalDate fechaInicio;
    private final LocalDate fechaRegistro;
    private final Set<DtCurso> cursos;


    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<DtCurso> cursos){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, cursos);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null);
    }

    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<DtCurso> cursos){
        this.cursos=cursos!=null?cursos:new HashSet<>();
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio = fechaInicio;
        this.descripcion=descripcion;
        this.fechaFin=fechaFin;
        this.nombre=nombre;
    }
    
    public String getNombre(){ return this.nombre; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public Set<DtCurso> getDtCursos(){  return this.cursos; }
    public String getDescripcion(){  return this.descripcion; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public LocalDate getFechaRegistro (){ return this.fechaRegistro; }
    public List<DtCurso> toArray(){ return new ArrayList<>(this.cursos); }

}