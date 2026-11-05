package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 *
 * @author vdraco
 * 0 sin accion
 * 1 inscripto
 * 2 aceptado 
 * 3 recharado
 */
@XmlRootElement(name = "DtInscripcionEdicion")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtInscripcionEdicion {
    private int estado;
    public int ACEPTADO=2;
    public int INSCRIPTO=1;
    public int RECHAZADO=3;
    public int NOINSCRIPTO=0;
    private DtEdicion edicion;
    private DtUsuario estudiante;
    @XmlElement(name = "fechaInscripcion")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaInscripcion;
    private String[] nomEstado={"No Inscripto", "Inscripto", "Aceptado", "Rechazado"};
    private double prioridad;
    private String videoURL;
    private Integer nota;   // nota de la evaluacion (1 al 12); null si todavia no se ingreso

    public DtInscripcionEdicion(){};
    public DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado, double prioridad, String videoURL){
        this.fechaInscripcion=fechaInscripcion;
        this.estudiante=estudiante;
        this.edicion=edicion;
        this.estado=estado;
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
   
    public int getEstado(){ return this.estado; }
    public DtEdicion getEdicion(){ return this.edicion; }
    public String getNameEstado(){ return nomEstado[this.estado]; }
    public DtUsuario getEstudiante(){  return this.estudiante;  }
    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    public double getPrioridad(){ return this.prioridad; }
    public String getVideoURL(){ return this.videoURL; } // <-- NUEVO GETTER
    public Integer getNota(){ return this.nota; }
    public void setNota(Integer nota){ this.nota = nota; }
}
