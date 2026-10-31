package edu.edext.datatypes;

public class DtCategoria {
    private final String nombre;

    // Requerido por JAXB para recibir este datatype por los Web Services
    private DtCategoria(){ this(null); }

    public DtCategoria(String nombre){
        this.nombre=nombre;
    }
    
    public String getNombre() {return nombre;}
    
} 