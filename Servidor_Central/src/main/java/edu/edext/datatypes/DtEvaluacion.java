package edu.edext.datatypes;

/**
 * Estado asignado a la inscripcion de un estudiante al evaluar una edicion.
 * Reemplaza al Map&lt;nickname, estado&gt; de IControlador.evaluarInscripciones en los Web Services (JAXB no serializa Map).
 */
public class DtEvaluacion {
    private final String nickname;
    private final int estado;

    public DtEvaluacion(String nickname, int estado){
        this.nickname=nickname;
        this.estado=estado;
    }

    // Requerido por JAXB
    private DtEvaluacion(){ this(null, 0); }

    public String getNickname(){ return nickname; }
    public int getEstado(){ return estado; }
}
