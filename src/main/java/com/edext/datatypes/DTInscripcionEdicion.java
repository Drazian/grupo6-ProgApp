package com.edext.datatypes;

import java.time.LocalDate;
import com.edext.persistencia.Edicion;
import com.edext.persistencia.Estudiante;

/**
 *
 * @author vdraco
 */
public class DTInscripcionEdicion {
   
    private final Integer id;
    private final Edicion edicion;
    private final Estudiante estudiante;
    private final LocalDate fechaInscripcion;
    
    public DTInscripcionEdicion(Estudiante estudiante, Edicion edicion, LocalDate fechaInscripcion){
        this(null , estudiante, edicion, fechaInscripcion);
    }

    public DTInscripcionEdicion(Integer id, Estudiante estudiante, Edicion edicion, LocalDate fechaInscripcion){
        this.id=id;
        this.edicion=edicion;
        this.estudiante=estudiante;
        this.fechaInscripcion=fechaInscripcion;
    }
    
    public Integer getID(){ return this.id; }
    public Edicion getEdicion(){ return this.edicion; }
    public Estudiante getEstudiante(){return this.estudiante; }
    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    
}
