
package com.edext.datatypes;

import java.util.Date;
import java.util.List;

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
    
    
    
}
