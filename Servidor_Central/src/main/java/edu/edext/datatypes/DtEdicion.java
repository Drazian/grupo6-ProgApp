package edu.edext.datatypes;

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
    private final String imagen; 

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, String imagen){
        this.fechaPublicacion=fechaPublicacion;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
        this.nombre=nombre;
        this.curso=curso;
        this.cupo=cupo;
        this.imagen = (imagen != null && !imagen.isEmpty()) ? imagen : "usr.png"; 
    }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, "usr.png");
    }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, "usr.png");
        Set<String> tmpDocentes=new HashSet<>();
        this.docentes=docentes;
        if (docentes != null) {
            for (DtUsuario docente : docentes) tmpDocentes.add(docente.getNickname());
        }
        this.nameDocentes=tmpDocentes;        
    }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, List<DtUsuario> docentes, String imagen){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, imagen);
        Set<String> tmpDocentes=new HashSet<>();
        this.docentes=docentes;
        if (docentes != null) {
            for (DtUsuario docente : docentes) tmpDocentes.add(docente.getNickname());
        }
        this.nameDocentes=tmpDocentes;        
    }

    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, Set<String> docentes){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, "usr.png");
        this.nameDocentes=docentes;
    }
    public DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, Set<String> docentes, String imagen){
        this(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, imagen);
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
    public String getImagen(){ return this.imagen; } 
}