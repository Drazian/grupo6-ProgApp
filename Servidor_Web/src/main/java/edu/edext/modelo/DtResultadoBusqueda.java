package edu.edext.modelo;

import java.time.LocalDate;

public class DtResultadoBusqueda {
    private String nombre; //En caso de usuario es nickname
    private String descripcion; //En caso de usuario es el nombre+apellido
    private String tipo; //curso, programa o usuario (DOCENTE o ESTUDIANTE)
    private LocalDate fechaPublicacion;
    private String imagen;
    
    public DtResultadoBusqueda(){}
    public DtResultadoBusqueda(String nombre, String descripcion, LocalDate fechaPublicacion, String imagen, String tipo){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.fechaPublicacion=fechaPublicacion;
        this.imagen=imagen;
        this.tipo=tipo;
    }
    
    public String getNombre(){return nombre;}
    public String getDescripcion(){return descripcion;}
    public String getTipo(){return tipo;}
    public LocalDate getFechaPublicacion(){return fechaPublicacion;}
    public String getImagen(){return imagen;}
    
}
