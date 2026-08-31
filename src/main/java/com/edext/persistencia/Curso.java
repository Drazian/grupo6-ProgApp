package com.edext.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 *
 * @author vdraco
 */
@Entity
@Table(name="Curso")
public class Curso{
   
    @Id
    private String nombre;
    private String descripcion;
    private Integer duracion;
    private Integer canHoras;
    private Integer cantCreditos;
    @Column(nullable=false)
    private LocalDate fechaRegistro;
    private String link;

   
}
