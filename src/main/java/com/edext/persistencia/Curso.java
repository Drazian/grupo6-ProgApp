package com.edext.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Curso {
    @Id
    private String nombre;
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private String url;
    private Date fechaRegistro;

    @ManyToOne
    private Instituto instituto;

    @ManyToMany
    private List<Curso> previas;

    public Curso() {}

    public Curso(String nombre, String descripcion, String duracion, int cantidadHoras, 
                 int creditos, String url, Date fechaRegistro, Instituto instituto, List<Curso> previas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.url = url;
        this.fechaRegistro = fechaRegistro;
        this.instituto = instituto;
        this.previas = previas;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public int getCantidadHoras() { return cantidadHoras; }
    public void setCantidadHoras(int cantidadHoras) { this.cantidadHoras = cantidadHoras; }
    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }
    public List<Curso> getPrevias() { return previas; }
    public void setPrevias(List<Curso> previas) { this.previas = previas; }
}