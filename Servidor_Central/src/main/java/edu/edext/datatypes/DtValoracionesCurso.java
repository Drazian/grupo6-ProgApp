package edu.edext.datatypes;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Resumen de las valoraciones de un curso (CU "Valorar un Curso" y requerimiento 7.6).
 * Todo es campo (sin getters calculados) para que viaje completo tambien por SOAP.
 */
@XmlRootElement(name = "DtValoracionesCurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtValoracionesCurso {
    private String curso;
    private double promedio;                 // 0 si no tiene valoraciones
    private int cantidad;                    // cantidad total de valoraciones
    private List<Integer> cantidadPorPuntaje; // 5 posiciones: [0] = 1 estrella ... [4] = 5 estrellas
    private List<Integer> porcentajePorPuntaje; // mismo orden, porcentaje entero sobre el total
    private Integer puntajeEstudiante;       // puntaje que asigno el estudiante consultado (null si no valoro)
    private boolean participa;               // el estudiante consultado participa del curso (puede valorarlo)

    public DtValoracionesCurso(){}

    public DtValoracionesCurso(String curso, List<Integer> cantidadPorPuntaje, Integer puntajeEstudiante, boolean participa){
        this.curso = curso;
        this.cantidadPorPuntaje = new ArrayList<>(cantidadPorPuntaje);
        this.puntajeEstudiante = puntajeEstudiante;
        this.participa = participa;

        int total = 0, suma = 0;
        for (int i = 0; i < this.cantidadPorPuntaje.size(); i++) {
            total += this.cantidadPorPuntaje.get(i);
            suma += this.cantidadPorPuntaje.get(i) * (i + 1);
        }
        this.cantidad = total;
        this.promedio = total == 0 ? 0 : Math.round(suma * 10.0 / total) / 10.0;   // con un decimal

        this.porcentajePorPuntaje = new ArrayList<>();
        for (Integer c : this.cantidadPorPuntaje)
            this.porcentajePorPuntaje.add(total == 0 ? 0 : (int) Math.round(c * 100.0 / total));
    }

    public String getCurso(){ return this.curso; }
    public double getPromedio(){ return this.promedio; }
    public int getCantidad(){ return this.cantidad; }
    public List<Integer> getCantidadPorPuntaje(){ return this.cantidadPorPuntaje; }
    public List<Integer> getPorcentajePorPuntaje(){ return this.porcentajePorPuntaje; }
    public Integer getPuntajeEstudiante(){ return this.puntajeEstudiante; }
    public boolean isParticipa(){ return this.participa; }
}
