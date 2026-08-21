
package com.edext.persistencia;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Date;

@Entity
@DiscriminatorValue("ESTUDIANTE")
public class Estudiante extends Usuario {

    public Estudiante() {
    }

    public Estudiante(String nickname, String email, String nombre, String apellido, Date fNacimiento, String imagen) {
        super(nickname, email, nombre, apellido, fNacimiento, imagen);
    }
    
}
