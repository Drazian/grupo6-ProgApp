package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/** 
 *
 * @author Diego
 */
@XmlRootElement(name = "DtUsuario")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtUsuario {
    
    private String nickname;
    private String password;
    private String email;
    private String nombre;
    private String apellido;
    private String imagen;
//    @jakarta.xml.bind.annotation.XmlSchemaType(name = "fNacimiento")
    @XmlElement(name = "fNacimiento")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fNacimiento;
    private List<String> institutos;
    private TipoUsuario tipoUsuario;
    private List<String> seguidos;
    private List<String> seguidores;

    public DtUsuario(){};
    public DtUsuario(String nickname, String password, String email, String nombre, String apellido, String imagen, LocalDate fNacimiento, List<String> institutos, TipoUsuario tipoUsuario) {
        this(nickname, password, email, nombre, apellido, imagen, fNacimiento, institutos, tipoUsuario, new ArrayList<>(), new ArrayList<>());
    }

    public DtUsuario(String nickname, String password, String email, String nombre, String apellido, String imagen, LocalDate fNacimiento, List<String> institutos, TipoUsuario tipoUsuario, List<String> seguidos, List<String> seguidores) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.imagen = imagen;
        this.fNacimiento = fNacimiento;
        this.institutos = institutos;
        this.tipoUsuario = tipoUsuario;
        this.seguidos = seguidos;
        this.seguidores = seguidores;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword(){ return this.password; }
    
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

    public LocalDate getfNacimiento() {
        return fNacimiento;
    }

    public List<String> getInstitutos() {
        return institutos;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public List<String> getSeguidos() {
        return seguidos;
    }

    public List<String> getSeguidores() {
        return seguidores;
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