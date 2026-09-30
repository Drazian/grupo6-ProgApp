package edu.edext.logica;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Categoria")
public class Categoria {
    @Id
    private String nombre;

    public Categoria(){}
    public Categoria(String nombre){
        this.nombre=nombre;
    }
    
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre=nombre;}

}