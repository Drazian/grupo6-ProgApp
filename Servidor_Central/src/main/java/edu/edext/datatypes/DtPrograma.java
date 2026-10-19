package edu.edext.datatypes;

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
    private final String imagen;
    private final String nombre;
    private final String descripcion;
    private final LocalDate fechaFin;
    private final LocalDate fechaInicio;
    private final LocalDate fechaRegistro;
    private final List<DtCurso> cursos;
    private Set<String> nameCursos;

    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, null, null);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, String imagen){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, null, imagen);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, cursos, null);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos, String imagen){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, cursos, imagen);
    }
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, cursos, null, null);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, String imagen){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, cursos, null, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, null, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, null, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, cursos, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, cursos, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, cursos, null, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, cursos, null, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, Set<String> nameCursos, String imagen){
        Set<String> tmpCurso=new HashSet<>(), tmpCategorias=new HashSet<>();
        if(nameCursos!=null)
            tmpCurso=nameCursos;
        else if(cursos!=null)
            for(DtCurso curso : cursos)
                tmpCurso.add(curso.getNombre());
        this.nameCursos=tmpCurso;
        this.cursos=cursos;
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio = fechaInicio;
        this.descripcion=descripcion;
        this.fechaFin=fechaFin;
        this.nombre=nombre;
        this.imagen=imagen;
    }

    public String getImagen() {return this.imagen; }
    public String getNombre(){ return this.nombre; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public List<DtCurso> getCursos(){  return this.cursos; }
    public List<DtCurso> getDtCursos(){  return this.cursos; }
    public String getDescripcion(){  return this.descripcion; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public Set<String> getNameCursos(){ return this.nameCursos; }
    public LocalDate getFechaRegistro (){ return this.fechaRegistro; }
    public List<String> getCursos_toArray(){ return new ArrayList<>(nameCursos); }
    public List<String> getCategorias_toArray(){ return new ArrayList<>(getCategorias()); }
    
    //Las categorias se obtienen de los Cursos del Programa.
    public Set<String> getCategorias() {
        Set<String> categoriasPrograma = new HashSet<>();
        if (this.cursos != null) {
            for (DtCurso curso : this.cursos) {
                if (curso.getNameCategorias()!= null) {
                    categoriasPrograma.addAll(curso.getNameCategorias());
                }
            }
        }
        return categoriasPrograma;
    }

}