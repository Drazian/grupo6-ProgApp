package edu.edext.datatypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet; 
import java.util.List;
import java.util.Set;

public class DtConsultaCurso {
    
    private final String nombre;
    private final String descripcion;
    private final String duracion;
    private final int cantidadHoras;
    private final String imagen;
    private final String url;
    private final int creditos;
    private final Date fechaRegistro;
    private final List<DtEdicion> ediciones;
    private final List<DtPrograma> programas;
    private final List<DtCategoria> categorias;
    private final List<DtCurso> previas;
    private Set<String> nameEdiciones;
    private Set<String> nameProgramas;
    private Set<String> nameCategorias;
    private Set<String> namePrevias;

    public DtConsultaCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, Date fechaRegistro, 
            List<DtEdicion> ediciones, List<DtPrograma> programas, List<DtCategoria> categorias, List<DtCurso> previas, String imagen) {
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, ediciones, programas, categorias, previas, null, null, null, null, imagen);
        
    }
    public DtConsultaCurso(String nombre, String descripcion, String duracion, int cantidadHoras, 
                           int creditos, String url, Date fechaRegistro, Set<String> ediciones, Set<String> programas, Set<String> categorias, Set<String> previas, String imagen) {
        this(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, null, null, null, null, ediciones, programas, categorias, previas, imagen);
    }
    public DtConsultaCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, Date fechaRegistro, 
            List<DtEdicion> ediciones, List<DtPrograma> programas, List<DtCategoria> categorias, List<DtCurso> previas,
            Set<String> nameEdiciones, Set<String> nameProgramas, Set<String> nameCategorias, Set<String> namePrevias, String imagen) {
        Set<String> tmpEdiciones=new HashSet<>(), tmpProgramas=new HashSet<>(), tmpCategorias=new HashSet<>(), tmpPrevias=new HashSet<>(); 
        if(nameEdiciones!=null) tmpEdiciones=nameEdiciones;
        else if(ediciones!=null)
            for(DtEdicion edicion : ediciones) tmpEdiciones.add(edicion.getNombre());
        if(nameProgramas!=null) tmpProgramas=nameProgramas;
        else if(programas!=null)
            for(DtPrograma programa : programas) tmpProgramas.add(programa.getNombre());
        if(nameCategorias!=null) tmpCategorias=nameCategorias;
        else if(categorias!=null)
            for(DtCategoria categoria : categorias) tmpCategorias.add(categoria.getNombre());
        if(namePrevias!=null) tmpPrevias=namePrevias;
        else if(previas!=null)
            for(DtCurso previa : previas) tmpPrevias.add(previa.getNombre());
        this.nameCategorias=tmpCategorias;
        this.nameEdiciones=tmpEdiciones;
        this.nameProgramas=tmpProgramas;
        this.namePrevias=tmpPrevias;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantidadHoras = cantidadHoras;
        this.creditos = creditos;
        this.imagen=imagen;
        this.url = url;
        this.fechaRegistro = fechaRegistro;
        this.ediciones = ediciones;
        this.programas = programas;
        this.categorias=categorias;
        this.previas=previas;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public int getCantidadHoras() { return cantidadHoras; }
    public int getCreditos() { return creditos; }
    public String getImagen(){ return this.imagen; }
    public String getUrl() { return url; }
    public Date getFechaRegistro() { return fechaRegistro; }

    public List<DtEdicion> getEdiciones() { return ediciones; }
    public List<DtPrograma> getProgramas() { return programas; }
    public List<DtCategoria> getCategorias() { return categorias; }
    public List<DtCurso> getPrevias() { return previas; }
    
    public Set<String> getNameEdiciones() { return nameEdiciones; }
    public Set<String> getNameProgramas() { return nameProgramas; }
    public Set<String> getNameCategorias() { return nameCategorias; }
    public Set<String> getNamePrevias() { return namePrevias; }

    public List<String> getEdiciones_toArray() { return new ArrayList<>(nameEdiciones); }
    public List<String> getProgramas_toArray() { return new ArrayList<>(nameProgramas); }
    public List<String> getCategorias_toArray() { return new ArrayList<>(nameCategorias); }
    public List<String> getPrevias_toArray() { return new ArrayList<>(namePrevias); }
    
}