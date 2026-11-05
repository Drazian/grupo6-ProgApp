package edu.edext.datatypes;
 
/**
 *
 * @author vdraco 
 */
public enum ResultadoCurso {
    APROBADO("Aprobado"),
    NO_APROBADO("No aprobado"),
    EN_CURSO("En curso"),
    NO_CURSADO("No cursado");

    private final String texto;

    ResultadoCurso(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return this.texto;
    }
}
