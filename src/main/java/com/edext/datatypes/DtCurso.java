package com.edext.datatypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DtCurso {
    private String nombre;
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private String url;
    private Date fechaRegistro;
    private Set<String> namePrevias;
    private List<DtCurso> previas;  //  Agregado para compatibilidad con la Entity
    private DtInstituto instituto;  //  Agregado para compatibilidad con la Entity
    
    public DtCurso() {}

    //*************************************** Agregada sobrecarga secundaria para compatibilidad

    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
            int creditos, String url, Date fechaRegistro, DtInstituto instituto, List<DtCurso> previas) {
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto);
        Set<String> tmpPrevias=new HashSet<>();
        for (DtCurso previa : previas) tmpPrevias.add(previa.getNombre());
        this.namePrevias = tmpPrevias;
        this.previas=previas;
        tmpPrevias=null;
    }
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
                   int creditos, String url, Date fechaRegistro, DtInstituto instituto ,Set<String> previas) {
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto);
        List<DtCurso> tmpPrevias=new ArrayList<>();
        for (String previa : previas) tmpPrevias.add(new DtCurso(previa, descripcion, duracion, cantidadHoras,creditos, url, fechaRegistro, instituto, new ArrayList<>()));
        this.namePrevias = previas;
        this.previas=tmpPrevias;
        tmpPrevias=null;
    }
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, 
                   String url, Date fechaRegistro, DtInstituto instituto){
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.duracion = duracion;
            this.cantidadHoras = cantidadHoras;
            this.creditos = creditos;
            this.url = url;
            this.fechaRegistro = fechaRegistro;
            this.instituto=instituto;
    }
    
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public int getCantidadHoras() { return cantidadHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public DtInstituto getInstituto() { return  this.instituto; }
    public List<DtCurso> getPrevias() { return this.previas; }
    public Set<String> getSetPrevias() { return namePrevias; }
    public List<String> getListPrevias() { return new ArrayList<>(this.namePrevias); }
}