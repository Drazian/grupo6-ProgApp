package edu.edext.datatypes;

public class DtInstituto {
    private final String nombre;

    // Requerido por JAXB para recibir este datatype por los Web Services
    private DtInstituto(){ this(null); }

    public DtInstituto(String nombre){ 
        this.nombre=nombre;
    }
    
    public String getNombre() {return nombre;}
    
}