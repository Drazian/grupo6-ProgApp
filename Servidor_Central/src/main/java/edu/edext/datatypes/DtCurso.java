package edu.edext.datatypes;

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
    private List<DtCurso> previas;
    private DtInstituto instituto;
    private Set<String> categorias;
    
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
            int creditos, String url, Date fechaRegistro, DtInstituto instituto, List<DtCurso> previas, Set<String> categorias) {
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, categorias);
        Set<String> tmpPrevias=new HashSet<>();
        for (DtCurso previa : previas) tmpPrevias.add(previa.getNombre());
        this.namePrevias = tmpPrevias;
        this.previas=previas;
        tmpPrevias=null;
    }
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
                   int creditos, String url, Date fechaRegistro, DtInstituto instituto ,Set<String> previas, Set<String> categorias) {
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, categorias);
        List<DtCurso> tmpPrevias=new ArrayList<>();
        for (String previa : previas) tmpPrevias.add(new DtCurso(previa, descripcion, duracion, cantidadHoras,creditos, url, fechaRegistro, instituto, previas, categorias));
        this.namePrevias = previas;
        this.previas=tmpPrevias;
        tmpPrevias=null;
    }
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, 
                   String url, Date fechaRegistro, DtInstituto instituto, Set<String> categorias){
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, null, categorias, null);
    }
    
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, 
                   String url, Date fechaRegistro, DtInstituto instituto, Set<String> previas, Set<String> categorias, Set<String> namePrevias){
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.duracion = duracion;
            this.cantidadHoras = cantidadHoras;
            this.creditos = creditos;
            this.url = url;
            this.fechaRegistro = fechaRegistro;
            this.instituto=instituto;
            this.namePrevias = (namePrevias != null) ? namePrevias : new HashSet<>();
            this.categorias = (categorias != null) ? categorias : new HashSet<>();
            this.previas = new ArrayList<>(); // Inicializado para evitar NullPointer
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
    public Set<String> getSetCategorias() { return categorias; }
    public List<String> getListCategorias() { return new ArrayList<>(this.categorias); }
}
