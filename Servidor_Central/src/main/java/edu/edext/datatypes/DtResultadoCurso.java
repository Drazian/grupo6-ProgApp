package edu.edext.datatypes;

import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
 
/**
 * Una fila del Acta de Estudio (CU "Tramitar Acta de Estudio", requerimiento 7.4):
 * el resultado de un estudiante en un curso del programa de formacion.
 */
@XmlRootElement(name = "DtResultadoCurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtResultadoCurso {
    public static final String APROBADO = "Aprobado";
    public static final String NO_APROBADO = "No aprobado";
    public static final String EN_CURSO = "En curso";
    public static final String NO_CURSADO = "No cursado";

    private String curso;
    private int creditos;
    private String edicion;          // edicion de la que sale el resultado (null si no lo curso)
    private Integer nota;            // 1 a 12; null si todavia no hay resultado
    private String resultado;        // Aprobado / No aprobado / En curso / No cursado
    @XmlElement(name = "fechaResultado")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaResultado; // fecha de cierre de la edicion
    private ResultadoCurso tipoResultado;
    
    public DtResultadoCurso(){}

    public DtResultadoCurso(String curso, int creditos, String edicion, Integer nota, String resultado, LocalDate fechaResultado){
        this.curso = curso;
        this.creditos = creditos;
        this.edicion = edicion;
        this.nota = nota;
        this.resultado = resultado;
        this.fechaResultado = fechaResultado;
    }

    public String getCurso(){ return this.curso; }
    public int getCreditos(){ return this.creditos; }
    public String getEdicion(){ return this.edicion; }
    public Integer getNota(){ return this.nota; }
    public String getResultado(){ return this.resultado; }
    public LocalDate getFechaResultado(){ return this.fechaResultado; }
//    public boolean isAprobado(){ return APROBADO.equals(this.resultado); }
    
    public boolean isAprobado(){ 
        //return ResultadoCurso.APROBADO.equals(this.resultado); 
        return tipoResultado.equals(this.resultado);
    }
    
}
