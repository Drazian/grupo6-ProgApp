package com.edext.datatypes;

import java.util.Date;
import java.util.List;

public class DtConsultaCurso {
    private String nombre;
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private String url;
    private Date fechaRegistro;
    private List<String> ediciones;
    private List<String> programas;

    public DtConsultaCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
                           int creditos, String url, Date fechaRegistro, List<String> ediciones, List<String> programas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.url = url;
        this.fechaRegistro = fechaRegistro;
        this.ediciones = ediciones;
        this.programas = programas;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public int getCantidadHoras() { return cantidadHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public List<String> getEdiciones() { return ediciones; }
    public List<String> getProgramas() { return programas; }
}