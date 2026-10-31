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
public class DtEdicion {
    private final Integer cupo;
    private final String nombre;
    private final DtCurso curso;
    private final String imagen;
    private final LocalDate fechaFin;
    private final LocalDate fechaInicio;
    private final LocalDate fechaPublicacion;
    private List<DtUsuario> docentes;
    private Set<String> nameDocentes;
    
    // Requerido por JAXB para recibir este datatype por los Web Services
    private DtEdicion(){ this(null, null, null, null, null, null, null, null, null); }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, null, null, null, null);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, String imagen){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, null, null, null, imagen);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, String imagen){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, null, null, imagen);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, Set<String> docentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, null, docentes, null);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, Set<String> docentes, String imagen){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, null, docentes, imagen);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, docentes, null, null);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes, String imagen){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, docentes, null, imagen);
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes, Set<String> nameDocentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, docentes, nameDocentes, null);
    }
public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes, Set<String> nameDocentes, String imagen){
        
        if (nameDocentes != null) {
            this.nameDocentes = nameDocentes;
        } else if (docentes != null) {
            Set<String> tmpDocentes = new HashSet<>();
            for (DtUsuario docente : docentes) {
                // Se usa el nickname (clave primaria) para poder resolver el docente en la persistencia
                if (docente.getNickname() != null) tmpDocentes.add(docente.getNickname());
                else tmpDocentes.add(docente.getNombre() + " " + docente.getApellido());
            }
            this.nameDocentes = tmpDocentes;
        } else {
            this.nameDocentes = new HashSet<>();
        }
        
        this.docentes = docentes != null ? docentes : new ArrayList<>();
        this.fechaPublicacion = fechaPublicacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.nombre = nombre;
        this.imagen = imagen;
        this.curso = curso;
        this.cupo = cupo;
    }
        
    public Integer getCupo(){ return this.cupo;}
    public DtCurso getCurso(){ return this.curso; }
    public String getImagen(){ return this.imagen; }
    public String getNombre(){ return this.nombre; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public List<DtUsuario> getDocentes(){ return this.docentes; }
    public Set<String> getNameDocentes(){ return this.nameDocentes; }
    public LocalDate getFechaPublicacion(){ return this.fechaPublicacion; }
    public List<String> getDocentes_toArray(){ return new ArrayList<>(nameDocentes); }
    
}