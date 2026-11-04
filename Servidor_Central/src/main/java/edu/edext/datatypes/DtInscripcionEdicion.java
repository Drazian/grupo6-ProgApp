package edu.edext.datatypes;

import java.time.LocalDate;

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
    private final String videoURL; 

    // Constructor principal
    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado, double prioridad, String videoURL){
        this.fechaInscripcion = fechaInscripcion;
        this.estudiante = estudiante;
        this.edicion = edicion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.videoURL = videoURL;
    }
    
    // Sobrecarga previa para mantener compatibilidad
    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado, double prioridad){
        this(estudiante, edicion, fechaInscripcion, estado, prioridad, null);
    }
    
    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado){
        this(estudiante, edicion, fechaInscripcion, estado, 0.0, null);
    }
   
    public int getValorEstado(){ return this.estado; }
    public DtEdicion getEdicion(){ return this.edicion; }
    public String getEstado(){ return nomEstado[this.estado]; }
    public DtUsuario getEstudiante(){  return this.estudiante;  }
    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public double getPrioridad(){ return this.prioridad; }
    public String getVideoURL(){ return this.videoURL; } // <-- NUEVO GETTER
}