package edu.edext.datatypes;

import java.util.Date;
import java.util.List;

public class DtConsultaCurso {
    private final String nombre;
    private final String descripcion;
    private final String duracion;
    private final int cantidadHoras;
    private final int creditos;
    private final String url;
    private final Date fechaRegistro;
    private final List<String> ediciones;
    private final List<String> programas;
    private final List<String> categorias;

    public DtConsultaCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
                           int creditos, String url, Date fechaRegistro, List<String> ediciones, List<String> programas, List<String> categorias) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.url = url;
        this.fechaRegistro = fechaRegistro;
        this.ediciones = ediciones;
        this.programas = programas;
        this.categorias=categorias;
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
    public List<String> getCategorias() { return categorias; }
}