package com.edext.persistencia;

import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vdraco
 */
@Entity
@Table(name="ProgramaFormacion")
public class ProgramaFormacion {

    @Id
    private String nombre;
    private String descripcion;
    @Column(nullable=false)
    private LocalDate fechaRegistro;
    @Column(nullable=false)
    private LocalDate fechaInicio;
    @Column(nullable=false)
    private LocalDate fechaFin;

//    @ManyToMany
//    @JoinTable(
//        name="programa_curso",
//        joinColumns=@JoinColumn(name="programa"),
//        inverseJoinColumns=@JoinColumn(name="cursos"))
//    private Set<Curso> cursos=new HashSet<>();

    @ManyToMany
    private List<Curso> cursos = new ArrayList<>(); //Los programas inicializan sin cursos asociados.
        
    public ProgramaFormacion(){}
    
    public ProgramaFormacion(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this.nombre=nombre;
        this.descripcion=descripcion;
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio=fechaInicio;
        this.fechaFin=fechaFin;
    }
    
    public void setNombre(String nombre){ this.nombre=nombre;  }
    public void setDescripcion(String descripcion){ this.descripcion=descripcion; }
    public void setFechaRegistro(LocalDate fechaRegistro){ this.fechaRegistro=fechaRegistro; }
    public void setFechaInicio(LocalDate fechaInicio){ this.fechaInicio=fechaInicio; }
    public void setFechaFin(LocalDate fechaFin){ this.fechaFin=fechaFin; }
    public void setCursos (List<Curso> cursos){this.cursos=cursos;}
        
    public String getNombre(){ return this.nombre; }
    public String getDescripcion(){ return this.descripcion; }
    public LocalDate getFechaRegistro (){ return this.fechaRegistro; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public List<Curso> getCursos() { return this.cursos;}
    
    public void agregarCurso(Curso curso){
        if (!this.cursos.contains(curso)){
            this.cursos.add(curso);
        }
    }
    
}