package edu.edext.entitys;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clave compuesta de ValoracionCurso: un estudiante valora un curso una unica vez.
 */
public class ValoracionCursoID implements Serializable {
    private String estudiante;
    private String curso;

    public ValoracionCursoID(){}
    public ValoracionCursoID(String estudiante, String curso){
        this.estudiante=estudiante;
        this.curso=curso;
    }

    public void setEstudiante(String estudiante) { this.estudiante = estudiante; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getEstudiante() { return estudiante; }
    public String getCurso() { return curso; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValoracionCursoID that = (ValoracionCursoID) o;
        return Objects.equals(estudiante, that.estudiante) &&
               Objects.equals(curso, that.curso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, curso);
    }
}
