package edu.edext.controlador;

import edu.edext.controlador.ws.DtUsuario;
import org.tinylog.Logger;

public class LoginHelper {

    public LoginHelper(){}
    
    public boolean isExistNick(String nick){
        boolean ret;
        try {
            ret=findNick(nick); } 
        catch (Exception ex) {
            ret=false;
            Logger.debug(ex, "Nick '{}' no existene ",nick); }
        return ret;       
    }
    
    public boolean isExistEmail(String email){
        boolean ret;
        try {
            ret=findEmail(email); }
        catch (Exception ex) {
            ret=false;
            Logger.debug(ex, "Email '{}' no existene ",email); }
        return ret;
    }

   public DtUsuario validarNick(String nickname, String pass){
       DtUsuario ret;
       try {
           ret=validateNick(nickname, pass); }
       catch(Exception e) { ret=null; }
       return ret;
   }
   
   public DtUsuario validarEmail(String email, String pass){
       DtUsuario ret;
       try {
           ret=validateEmail(email, pass); }
       catch(Exception e) { ret=null; }
       return ret;
   }
    
    private boolean findNick(String nick) throws Exception{
        return Fabrica.getConexion().existeUsuario(nick);
    }

    private boolean findEmail(String email) throws Exception{
        return Fabrica.getConexion().existeEmail(email);
    }
    
    private DtUsuario validateNick(String nick, String pass) throws Exception{ 
        return Fabrica.getConexion().validarNick(nick, pass);
    }
    
    private DtUsuario validateEmail(String email, String pass) throws Exception{
        return Fabrica.getConexion().validarEmail(email, pass);
    }

}