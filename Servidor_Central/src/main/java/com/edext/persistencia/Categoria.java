package com.edext.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Categoria {
    @Id
    private String nombre;

    public Categoria(){}
    public Categoria(String nombre){
        setNombre(nombre);
    }
    
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre=nombre;}

}
