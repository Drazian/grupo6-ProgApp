package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlTransient;

import java.time.LocalDate;

/**
 *
 * @author vdraco 
 * 0 sin accion
 * 1 inscripto
 * 2 aceptado
 * 3 recharado
 */
public class DtInscripcionPrograma {
    private final int estado;
    @XmlTransient
    public final int ACEPTADO=2;
    @XmlTransient
    public final int INSCRIPTO=1;
    @XmlTransient
    public final int RECHAZADO=3;
    @XmlTransient
    public final int NOINSCRIPTO=0;
    private final DtPrograma programa;
    private final DtUsuario estudiante;
    private final LocalDate fechaInscripcion;
    @XmlTransient
    private final String[] nomEstado={"No Inscripto", "Inscripto", "Aceptado", "Rechazado"};

    // Requerido por JAXB para recibir este datatype por los Web Services
    private DtInscripcionPrograma(){ this(null, null, null, 0); }

    public DtInscripcionPrograma(DtUsuario estudiante, DtPrograma programa, LocalDate fechaInscripcion, int estado){
        this.fechaInscripcion=fechaInscripcion;
        this.estudiante=estudiante;
        this.programa=programa;
        this.estado=estado;
    }
   
    public int getValorEstado(){ return this.estado; }
    public DtPrograma getPrograma(){ return this.programa; }
    public String getEstado(){ return nomEstado[this.estado]; }
    public DtUsuario getEstudiante(){  return this.estudiante;  }
    public LocalDate getFechaInscripcion(){ return this.fechaInscripcion; }
    
}