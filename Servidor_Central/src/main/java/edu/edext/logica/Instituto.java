package edu.edext.logica;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Instituto")
public class Instituto {
    @Id
    private String nombre;
    
    public Instituto(){}
    public Instituto(String nombre){
        this.nombre=nombre;
    }
    
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre=nombre;}

}