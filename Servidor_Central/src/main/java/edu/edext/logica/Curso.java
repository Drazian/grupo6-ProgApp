package edu.edext.logica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="Curso") // Aporte de Draco conservado
public class Curso {
    
    @Id
    private String nombre;
    @Column(length = 2000)
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private String url;
    private String imagen;

//    Sustituida la falsa FK por una real
//    @jakarta.persistence.ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
//    private List<Categoria> categorias;
   
    @Column(nullable=false) // Aporte de Draco conservado
    private Date fechaRegistro;

    @ManyToOne
    //@Column(name = "instituto") //
    private Instituto instituto;

    @ManyToMany
    @JoinTable(name="Previas")    // Para mejorar la legibilidad externa
    private List<Curso> previas;

    @ManyToMany
    @JoinTable(name = "Agrupa",
        joinColumns=@JoinColumn(name="curso"),
        inverseJoinColumns=@JoinColumn(name="categoria")
//        joinColumns=@JoinColumn(name="curso_nombre"),
//        inverseJoinColumns=@JoinColumn(name="categoria_nombre")
    )
    private List<Categoria> categorias;
    
    
    public Curso() {}

    public Curso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, 
                 Date fechaRegistro, Instituto instituto, List<Categoria> categorias, List<Curso> previas, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.url = url;
        this.fechaRegistro = fechaRegistro;
        this.instituto = instituto;
        this.categorias=categorias;
        this.previas = previas;
        this.imagen=imagen;
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
    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias=categorias; }
    public List<Curso> getPrevias() { return previas; }
    public void setPrevias(List<Curso> previas) { this.previas = previas; }
    public String getImagen(){ return this.imagen; }
    public void setImagen(String imagen){ this.imagen=imagen; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curso that = (Curso) o;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
    
}