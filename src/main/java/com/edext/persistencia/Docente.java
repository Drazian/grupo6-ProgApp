
package com.edext.persistencia;

import java.util.Date;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.List;

@Entity
@DiscriminatorValue("DOCENTE")
public class Docente extends Usuario {

    @ManyToMany
    private List<Instituto> institutos;

    public Docente() {
    }

    public Docente(String nickname, String email, String nombre,
                   String apellido, Date fNacimiento, String imagen,
                   List<Instituto> instituto) {

        super(nickname, email, nombre, apellido, fNacimiento, imagen);
        this.institutos = instituto;
    }

    public List<Instituto> getInstitutos() {
        return institutos;
    }

    public void setInstitutos(List <Instituto> instituto) {
        this.institutos = instituto;
    }
    
}
