package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DtInstituto")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtInstituto {
    private String nombre;

    public DtInstituto(){};
    public DtInstituto(String nombre){ 
        this.nombre=nombre;
    }
    
    public String getNombre() {return nombre;}
    
}