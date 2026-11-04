package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List; 
import java.util.Set;

@XmlRootElement(name = "DtCurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtCurso {
    private String nombre;
    private String descripcion;
    private String duracion;
    private int cantidadHoras;
    private int creditos;
    private String url;
    private String imagen;
//    @jakarta.xml.bind.annotation.XmlSchemaType(name = "fechaRegistro")
    @XmlElement(name = "fechaRegistro")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaRegistro;
    private DtInstituto instituto;
    private Set<String> namePrevias;
    private Set<String> nameCategorias;
    private List<DtCurso> previas;
    private List<DtCategoria> categorias;
    
    public DtCurso(){};
    public DtCurso(String nombre, String imagen){
        this(nombre, null, null, 0, 0, null, null, null, null, null, null, null, imagen);
    }
    
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, LocalDate fechaRegistro, 
            DtInstituto instituto, String imagen){
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, null, null, null, null, imagen);
    }
    
    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, LocalDate fechaRegistro, 
            DtInstituto instituto, Set<String> namePrevias, Set<String> nameCategorias, String imagen){
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, null, null, namePrevias, nameCategorias, imagen);
    }

    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, LocalDate fechaRegistro, 
            DtInstituto instituto, List<DtCurso> previas, List<DtCategoria> categorias, String imagen){
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, previas, categorias, null, null, imagen);
        for(DtCategoria categoria : categorias)
            this.nameCategorias.add(categoria.getNombre());
        for (DtCurso curso : previas)
            this.namePrevias.add(curso.getNombre());
    }

    public DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, LocalDate fechaRegistro, 
            DtInstituto instituto, List<DtCurso> previas, List<DtCategoria> categorias, Set<String> namePrevias, Set<String> nameCategorias, String imagen){
        Set<String> tmpPrevias=new HashSet<>(), tmpCategorias=new HashSet<>();
        if(namePrevias!=null)
            tmpPrevias=namePrevias;
        else if(previas!=null)
            for(DtCurso previa : previas)
                tmpPrevias.add(previa.getNombre());
        if(nameCategorias!=null)
            tmpCategorias=nameCategorias;
        else if(categorias!=null)
            for(DtCategoria categoria : categorias)
                tmpCategorias.add(categoria.getNombre());
        this.namePrevias=tmpPrevias;
        this.nameCategorias=tmpCategorias;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.url = url;
        this.imagen=imagen;
        this.fechaRegistro = fechaRegistro;
        this.instituto=instituto;
//        System.out.println("cantidas de previas : "+this.namePrevias.size()+", llego : "+namePrevias.size());
//        System.out.println("cantidas de categorias : "+this.nameCategorias.size()+", llego : "+nameCategorias.size());
        this.previas=previas;
        this.categorias=categorias;
    }
    
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public int getCantidadHoras() { return cantidadHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public String getImagen(){ return this.imagen; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public DtInstituto getInstituto() { return  this.instituto; }
    public Set<String> getNamePrevias(){ return this.namePrevias; }
    public Set<String> getNameCategorias(){ return this.nameCategorias; }
    public List<DtCurso> getPrevias() { return this.previas; }
    public List<DtCategoria> getCategorias(){ return this.categorias; }
    
    public List<String> getPrevias_toArray() { return new ArrayList<>(this.namePrevias); }
    public List<String> getCategorias_toArray() { return new ArrayList<>(this.nameCategorias); }

}