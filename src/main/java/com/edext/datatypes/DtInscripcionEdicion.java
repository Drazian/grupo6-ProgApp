package com.edext.datatypes;

import java.time.LocalDate;

/**
 *
 * @author vdraco
 */
public class DtInscripcionEdicion {
    private final DtEdicion edicion;
    private final DtUsuario estudiante;
    private final LocalDate fechaInscripcion;
    
    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion){
        this.fechaInscripcion=fechaInscripcion;
        this.estudiante=estudiante;
        this.edicion=edicion;
    }
    
    public DtEdicion getEdicion(){ return this.edicion; }
    public DtUsuario getEstudiante(){ return this.estudiante; }
    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    
}
