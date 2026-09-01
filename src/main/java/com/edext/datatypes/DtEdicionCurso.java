package com.edext.datatypes;

import java.util.Date;
import java.util.List;

public class DtEdicionCurso {
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private Integer cupo;
    private Date fechaPublicacion;
    private List<String> docentes; // El nuevo campo para este caso de uso

    public DtEdicionCurso() {}

    // Constructor ORIGINAL (Mantiene la compatibilidad con el resto del proyecto)
    public DtEdicionCurso(String nombre, Date fechaInicio, Date fechaFin, Integer cupo, Date fechaPublicacion) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Constructor NUEVO (Para el Alta de Edición de Curso)
    public DtEdicionCurso(String nombre, Date fechaInicio, Date fechaFin, Integer cupo, Date fechaPublicacion, List<String> docentes) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = fechaPublicacion;
        this.docentes = docentes;
    }

    public String getNombre() { return nombre; }
    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public Integer getCupo() { return cupo; }
    public Date getFechaPublicacion() { return fechaPublicacion; }
    public List<String> getDocentes() { return docentes; }
}