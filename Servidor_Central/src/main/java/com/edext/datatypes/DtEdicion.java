package com.edext.datatypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vdraco
 */
public class DtEdicion {
    private final String nombre;
    private final Integer cupo;
    private final LocalDate fechaFin;
    private final LocalDate fechaInicio;
    private final LocalDate fechaPublicacion;
    private List<DtUsuario> docentes;
    private Set<String> nameDocentes;
    private final DtCurso curso;
    
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso){
        this.fechaPublicacion=fechaPublicacion;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
        this.nombre=nombre;
        this.curso=curso;
        this.cupo=cupo;
    }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso);
        Set<String> tmpDocentes=new HashSet<>();
        this.docentes=docentes;
        for (DtUsuario docente : docentes) tmpDocentes.add(docente.getNickname());
        this.nameDocentes=tmpDocentes;        
    }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, Set<String> docentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso);
        this.nameDocentes=docentes;
    }
    
    public Integer getCupo(){ return this.cupo;}
    public DtCurso getCurso(){  return this.curso; }
    public String getNombre(){ return this.nombre; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public List<DtUsuario> getDocentes(){ return this.docentes; }
    public Set<String> getNameDocentes(){ return this.nameDocentes; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public LocalDate getFechaPublicacion(){ return this.fechaPublicacion; }
}
