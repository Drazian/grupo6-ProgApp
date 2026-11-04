package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DtCategoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtCategoria {
    private String nombre;

    public DtCategoria(){};
    public DtCategoria(String nombre){
        this.nombre=nombre;
    }
    
    public String getNombre() {return nombre;}
    
} 
