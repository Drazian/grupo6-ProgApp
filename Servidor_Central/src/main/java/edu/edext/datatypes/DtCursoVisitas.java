package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Una fila del Listado de tendencias (requerimiento 7.3): un curso y la cantidad de veces que fue consultado.
 * Todo es campo para que viaje completo también por SOAP.
 */
@XmlRootElement(name = "DtCursoVisitas")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtCursoVisitas {
    private int posicion;          // 1 = el más visitado
    private String curso;
    private String instituto;
    private String imagen;
    private long visitas;

    public DtCursoVisitas(){}

    public DtCursoVisitas(int posicion, String curso, String instituto, String imagen, long visitas){
        this.posicion = posicion;
        this.curso = curso;
        this.instituto = instituto;
        this.imagen = imagen;
        this.visitas = visitas;
    }

    public int getPosicion(){ return this.posicion; }
    public String getCurso(){ return this.curso; }
    public String getInstituto(){ return this.instituto; }
    public String getImagen(){ return this.imagen; }
    public long getVisitas(){ return this.visitas; }
}
