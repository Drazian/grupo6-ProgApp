package com.edext.persistencia;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

/**
 *
 * @author vdraco
 */
@Entity
@Table(name="Edicion")
public class Edicion {

    @Id
    private String nombre;
    @Column(nullable=false)
    private Integer cupo;
    @Column(nullable=false)
    private LocalDate fechaFin;
    @Column(nullable=false)
    private LocalDate fechaInicio;
    @Column(nullable=false)
    private LocalDate fechaPublicacion;

    @ManyToMany
    @JoinTable(
        name="Organiza",
        joinColumns=@JoinColumn(name="edicion_nombre"),
        inverseJoinColumns=@JoinColumn(name="docente_nickname")
    )
    private Set<Docente> docentes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="curso_nombre", nullable=false)
    private Curso curso;
 
    public Edicion(){}
    
    public Edicion(String nombre, Integer cupo, LocalDate fechaPublicacion, LocalDate fechaInicio, LocalDate fechaFin, Curso curso, Set<Docente> docente){
        this.fechaPublicacion=fechaPublicacion;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
        this.docentes=docente;
        this.nombre=nombre;
        this.curso=curso;
        this.cupo=cupo;
    }
    
    public void setCupo(Integer cupo){ this.cupo=cupo; }
    public void setCurso(Curso curso){ this.curso=curso; }
    public void setNombre(String nombre){ this.nombre=nombre;  }
    public void setFechaFin(LocalDate fechaFin){ this.fechaFin=fechaFin; }
    public void setDocentes(Set<Docente> docentes){ this.docentes=docentes; }
    public void setFechaInicio(LocalDate fechaInicio){ this.fechaInicio=fechaInicio; }
    public void setFechaPublicacion (LocalDate fechaPublicacion){ this.fechaPublicacion=fechaPublicacion; }

    public LocalDate getFechaPublicacion(){ return this.fechaPublicacion; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public Set<Docente> getDocentes(){ return this.docentes; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public String getNombre(){ return this.nombre; }
    public Integer getCupo(){ return this.cupo; }
    public Curso getCurso(){ return this.curso; }
    
        @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.nombre);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Edicion other = (Edicion) obj;
        return Objects.equals(this.nombre, other.nombre);
    }
}
