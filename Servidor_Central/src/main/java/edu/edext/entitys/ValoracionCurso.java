package edu.edext.entitys;

import java.util.Objects;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * CU "Valorar un Curso": puntaje (1 a 5) que un estudiante le asigna a un curso en el que participa.
 * La clave (estudiante, curso) garantiza que cada estudiante valore un curso una unica vez.
 */
@Entity
@IdClass(ValoracionCursoID.class)
@Table(name="ValoracionCurso")
public class ValoracionCurso {
    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(nullable=false)
    private Estudiante estudiante;

    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(nullable=false)
    private Curso curso;

    @Min(1)
    @Max(5)
    @Column(nullable=false)
    private int puntaje;

    @NotNull
    @Column(nullable=false)
    private LocalDate fecha;

    public ValoracionCurso(){}
    public ValoracionCurso(Estudiante estudiante, Curso curso, int puntaje, LocalDate fecha){
        this.estudiante=estudiante;
        this.curso=curso;
        this.puntaje=puntaje;
        this.fecha=fecha;
    }

    public Estudiante getEstudiante(){ return this.estudiante; }
    public Curso getCurso(){ return this.curso; }
    public int getPuntaje(){ return this.puntaje; }
    public LocalDate getFecha(){ return this.fecha; }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, curso);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final ValoracionCurso other = (ValoracionCurso) obj;
        return Objects.equals(this.estudiante, other.estudiante) && Objects.equals(this.curso, other.curso);
    }
}
