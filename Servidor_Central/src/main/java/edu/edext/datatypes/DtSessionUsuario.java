package edu.edext.datatypes;

/**
 *
 * @author vdraco 
 */
public class DtSessionUsuario {

    private final String nickname;
    private final String email;
    private final String imagen;
    private final TipoUsuario tipoUsuario;
    
    public DtSessionUsuario(String nickname, String email, String imagen, TipoUsuario tipoUsuario){
        this.nickname=nickname;
        this.email=email;
        this.imagen=imagen;
        this.tipoUsuario=tipoUsuario;
    }
    
    public String getNickname(){ return this.nickname; }
    public String getEmail(){ return this.email; }
    public String getImagen(){ return this.imagen; }
    public TipoUsuario getTipoUsuario(){ return  this.tipoUsuario; }
    
}