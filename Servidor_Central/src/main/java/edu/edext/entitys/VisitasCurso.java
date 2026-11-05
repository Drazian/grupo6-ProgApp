package edu.edext.entitys;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Requerimiento 7.3 "Listado de tendencias": cantidad de veces que usuarios y visitantes consultaron cada curso.
 * La registra un filtro del Servidor Web cada vez que se muestra el detalle de un curso.
 */
@Entity
@Table(name="VisitasCurso")
public class VisitasCurso {
    @Id
    private String curso;           // nombre del curso (clave de Curso)

    @Column(nullable=false)
    private long cantidad;

    private LocalDateTime ultimaVisita;

    public VisitasCurso(){}
    public VisitasCurso(String curso, long cantidad, LocalDateTime ultimaVisita){
        this.curso=curso;
        this.cantidad=cantidad;
        this.ultimaVisita=ultimaVisita;
    }

    public String getCurso(){ return this.curso; }
    public long getCantidad(){ return this.cantidad; }
    public LocalDateTime getUltimaVisita(){ return this.ultimaVisita; }
}
