package edu.edext.datatypes;

import java.time.LocalDate;

/**
 *
 * @author vdraco
 * 0 sin accion
 * 1 inscripto
 * 2 aceptado
 * 3 recharado
 */
public class DtInscripcionEdicion {
    private final int estado;
    public final int ACEPTADO=2;
    public final int INSCRIPTO=1;
    public final int RECHAZADO=3;
    public final int NOINSCRIPTO=0;
    private final DtEdicion edicion;
    private final DtUsuario estudiante;
    private final LocalDate fechaInscripcion;
    private final String[] nomEstado={"No Inscripto", "Inscripto", "Aceptado", "Rechazado"};
    private final double prioridad;

    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado, double prioridad){
        this.fechaInscripcion=fechaInscripcion;
        this.estudiante=estudiante;
        this.edicion=edicion;
        this.estado=estado;
        this.prioridad = prioridad;
    }
    
    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado){
        this(estudiante, edicion, fechaInscripcion, estado, 0.0);
    }
   
    public int getValorEstado(){ return this.estado; }
    public DtEdicion getEdicion(){ return this.edicion; }
    public String getEstado(){ return nomEstado[this.estado]; }
    public DtUsuario getEstudiante(){  return this.estudiante;  }
    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public double getPrioridad(){ return this.prioridad; }
}