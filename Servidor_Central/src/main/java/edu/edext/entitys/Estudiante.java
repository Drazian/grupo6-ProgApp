package edu.edext.entitys;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.Date;

@Entity
@DiscriminatorValue("ESTUDIANTE")
public class Estudiante extends Usuario {

    public Estudiante() {
    }
 
    public Estudiante(String nickname, String password, String email, String nombre, String apellido, LocalDate fNacimiento, String imagen) {
        super(nickname, password, email, nombre, apellido, fNacimiento, imagen);
    }
    
}