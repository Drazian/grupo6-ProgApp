package edu.edext.logica;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.util.Date;

/**
 *
 * @author Diego
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
@Table(name="Usuario")
public class Usuario {
    
    @Id
    @NotBlank
    @Column(unique = true, nullable = false)
    private String nickname;
    
    @NotBlank
    @Email(message = "No es un email valido")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Column(nullable=false, length=60)
    private String password;
    
    @Basic
    private String nombre;
    private String apellido;
    private String imagen;
    
    @NotNull
    @Past(message = "La fecha de nacimiento tiene que ser anterior a la fecha actual")
    @Temporal(TemporalType.DATE)
    private Date fNacimiento;
    

    public Usuario(String nickname, String password, String email, String nombre, String apellido, Date fNacimiento, String imagen) {
        this.nickname = nickname;
        this.password=password;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fNacimiento = fNacimiento;
        this.imagen = imagen;
    }

    public Usuario() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword(){ return this.password; }

    public void setPassword(String password){ this.password=password; }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Date getfNacimiento() {
        return fNacimiento;
    }

    public void setfNacimiento(Date fNacimiento) {
        this.fNacimiento = fNacimiento;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
}