
package com.edext.datatypes;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Diego
 */
public class DtUsuario {
    
   
    private String nickname;
    private String email;
    private String nombre;
    private String apellido;
    private String imagen;
    private Date fNacimiento;
    private List<String> institutos;
    private TipoUsuario tipoUsuario;

    public DtUsuario(String nickname, String email, String nombre, String apellido, String imagen, Date fNacimiento, List<String> institutos, TipoUsuario tipoUsuario) {
        this.nickname = nickname;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.imagen = imagen;
        this.fNacimiento = fNacimiento;
        this.institutos = institutos;
        this.tipoUsuario = tipoUsuario;
    }
    
    

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getImagen() {
        return imagen;
    }

    public Date getfNacimiento() {
        return fNacimiento;
    }

    public List<String> getInstitutos() {
        return institutos;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    

    @Override
    public String toString() {
       return nickname;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.nickname);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final DtUsuario other = (DtUsuario) obj;
        return Objects.equals(this.nickname, other.nickname);
    }
    
    
    
}
