package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List; 
import java.util.Set;

/**
 *
 * @author vdraco
 */
@XmlRootElement(name = "DtPrograma")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtPrograma {
    private String imagen;
    private String nombre;
    private String descripcion;
    @XmlElement(name = "fechaFin")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaFin;
    @XmlElement(name = "fechaInicio")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaInicio;
    @XmlElement(name = "fechaRegistro")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaRegistro;
    private List<DtCurso> cursos;
    private Set<String> nameCursos;
    private Set<String> categorias;
    private String docente;

    public DtPrograma(){};
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, null, null);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, String imagen){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, null, imagen);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, cursos, null);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos, String imagen){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, null, cursos, imagen);
    }
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, cursos, null, null);
    }    
    public DtPrograma(String nombre, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, String imagen){
        this(nombre, null, fechaRegistro, fechaInicio, fechaFin, cursos, null, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, null, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, null, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, String imagen, String docente){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, null, imagen);
        this.docente = docente;
    }
    
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, cursos, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, Set<String> cursos, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, cursos, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, cursos, null, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, cursos, null, imagen);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, Set<String> nameCursos, String imagen){
        this(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, cursos, nameCursos, imagen, null);
    }
    public DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin, List<DtCurso> cursos, Set<String> nameCursos, String imagen, String docente){
        Set<String> tmpCurso=new HashSet<>(), categoriasPrograma = new HashSet<>();
        if(nameCursos!=null)
            tmpCurso=nameCursos;
        else if(cursos!=null)
            for(DtCurso curso : cursos){
                tmpCurso.add(curso.getNombre());
                if(curso.getNameCategorias()!= null)
                    categoriasPrograma.addAll(curso.getNameCategorias()); }
        this.nameCursos=tmpCurso;
        this.cursos=cursos;
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio = fechaInicio;
        this.descripcion=descripcion;
        this.fechaFin=fechaFin;
        this.nombre=nombre;
        this.imagen=imagen;
        this.categorias=categoriasPrograma;
        this.docente=docente;
    }

    public String getDocente() { return this.docente;}
    public String getImagen() {return this.imagen; }
    public String getNombre(){ return this.nombre; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public List<DtCurso> getCursos(){  return this.cursos; }
    public String getDescripcion(){  return this.descripcion; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public Set<String> getNameCursos(){ return this.nameCursos; }
    public LocalDate getFechaRegistro (){ return this.fechaRegistro; }
    public List<String> getCursos_toArray(){ return new ArrayList<>(nameCursos); }
    public List<String> getCategorias_toArray(){ return new ArrayList<>(getCategorias()); }
    public Set<String> getCategorias(){ return this.categorias; }
    //Las categorias se obtienen de los Cursos del Programa.
//    public Set<String> getCategorias() {
//        Set<String> categoriasPrograma = new HashSet<>();
//        if (this.cursos != null) {
//            for (DtCurso curso : this.cursos) {
//                if (curso.getNameCategorias()!= null) {
//                    categoriasPrograma.addAll(curso.getNameCategorias());
//                }
//            }
//        }
//        return categoriasPrograma;
//    }

}
