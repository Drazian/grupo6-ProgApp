package edu.edext.datatypes;

/** 
 *
 * @author vdraco
 */
public enum TipoInscripcion {
    NO_INSCRIPTO("No Inscripto"), //No usar. En su lugar usar INSCRIPTO. No se elimina por compatibilidad.
    INSCRIPTO("Inscripto"),
    ACEPTADO("Aceptado"),
    RECHAZADO("Rechazado");
    private final String mensaje;

    TipoInscripcion(String mensaje){ this.mensaje=mensaje; }

    public String getString(){ return this.mensaje; }
}