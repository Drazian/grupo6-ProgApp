package edu.edext.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DtResultadoBusqueda {
    private String nombre; //En caso de usuario es nickname
    private String descripcion; //En caso de usuario es el nombre+apellido
    private String tipo; //curso, programa o usuario (DOCENTE o ESTUDIANTE)
    private LocalDate fechaPublicacion;
    private String imagen;
    private double promedioValoracion;   // solo cursos (requerimiento 7.6); 0 si no tiene valoraciones
    private int cantidadValoraciones;

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
    // Para mostrar: dd/MM/yyyy, como en el resto del sitio (getFechaPublicacion queda en ISO para ordenar)
    public String getFechaTexto(){return fechaPublicacion == null ? "" : fechaPublicacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
    public String getImagen(){return imagen;}
    public double getPromedioValoracion(){return promedioValoracion;}
    public int getCantidadValoraciones(){return cantidadValoraciones;}

    public void setValoracion(double promedio, int cantidad){
        this.promedioValoracion=promedio;
        this.cantidadValoraciones=cantidad;
    }

}
