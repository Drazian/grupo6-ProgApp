
package com.edext.persistencia;

import java.util.Date;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("DOCENTE")
public class Docente extends Usuario {

    @ManyToOne
    private Instituto instituto;

    public Docente() {
    }

    public Docente(String nickname, String email, String nombre,
                   String apellido, Date fNacimiento, String imagen,
                   Instituto instituto) {

        super(nickname, email, nombre, apellido, fNacimiento, imagen);
        this.instituto = instituto;
    }

    public Instituto getInstituto() {
        return instituto;
    }

    public void setInstituto(Instituto instituto) {
        this.instituto = instituto;
    }
    
}
