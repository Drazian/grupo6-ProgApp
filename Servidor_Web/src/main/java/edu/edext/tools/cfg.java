package edu.edext.tools;

import edu.edext.tools.Utils.CfgDB;
import java.io.File;

/**
 *
 * @author vdraco
 */
public class cfg {
    
    public static final String PATH=getHomePath()+getSeparador()+".edEXT";
    public static final String PATH_FILE=PATH+getSeparador()+"edEXT.cfg";
    public static final String PATH_INTERNAL="/Images/";
    //public static final String IMAGE_USER = "%#@.png";
    //%#@.jpeg
    public static final String IMAGE_USER = "usr.png";
    public static final String CLAVE_IMAGE="imagen",
                               CLAVE_IP="ip",
                               CLAVE_PORT="puerto";
    private static final String SYM_HOME="~",
                                PORT="8080",
                                IP="localhost",
                                IMAGE="imagen";
    
 
    
    public static void init(){
        if(!Utils.Disk.isExist(PATH_FILE))
            crearCFG();
    }
    
    public static void init(String imagen, String url, String puerto){
        if(Utils.Disk.isExist(PATH_FILE)){
            //System.out.println("existe");
            new File(PATH_FILE).delete();
            crearCFG(imagen, url, puerto);
        }
    }
    
    public static String pathImagen(){
        String ret=PATH;
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals("imagen"))
                ret=cfgDB.valor();
        ret=ret.replace(SYM_HOME, getHomePath());
        return ret;
    }
    
    public static String fullpathImagen(){
        return pathImagen()+getSeparador();
    }
            
    public static String wsdl(){
        String ret, ip, port;
        ret=ip=port=null;
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals("ip"))
                ip=cfgDB.valor();
            else if(cfgDB.clave().equals("puerto"))
                port=cfgDB.valor();
        ret="http://"+ip+":"+port+"/servidor_central";
        return ret;
    }
    
    public static String consultaWSDL(){
        return wsdl()+"?wsdl"; // solo para consultas de contrato
    }
    
    public static void crearCFG(){
        crearCFG(IMAGE, IP, PORT);
    }
    private static void crearCFG(String imagen, String url, String puerto){
        boolean img, ip, port;
        img=ip=port=true;
        if(Utils.Validate.isEmpty(imagen)) imagen=IMAGE;
        if(Utils.Validate.isEmpty(url)) url=IP;
        if(Utils.Validate.isEmpty(puerto)) puerto=PORT;
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals("imagen"))
                img=false;
            else if(ip && cfgDB.clave().equals("ip"))
                ip=false;
            else if(port && cfgDB.clave().equals("puerto"))
                port=false;
        imagen=imagen.replaceAll("~"+getSeparador()+".edEXT"+getSeparador(),"");
        imagen=imagen.replaceAll("~"+getSeparador()+".edEXT","");
        imagen=imagen.replaceAll("~"+getSeparador(),"");
        imagen=imagen.replaceAll("~","");
        if(img){
            Utils.Disk.saveCfg("#", "Carpeta de imagen\n");
            Utils.Disk.saveCfg(CLAVE_IMAGE, "~"+getSeparador()+".edEXT"+getSeparador()+imagen+"\n");
            Utils.Disk.saveCfg("\n", "");
        }
        if(ip){
            Utils.Disk.saveCfg("#", "IP del Web Service\n");
            Utils.Disk.saveCfg(CLAVE_IP, url+"\n");
            Utils.Disk.saveCfg("\n", "");
        }
        if(port){
            Utils.Disk.saveCfg("#", "Puerto del Web Service\n");
            Utils.Disk.saveCfg(CLAVE_PORT, puerto+"\n");
            Utils.Disk.saveCfg("\n", "");
        }
    }
   
    private static String getSeparador(){
        return java.io.File.separator;
    }

    private static String getHomePath(){
        return System.getProperty("user.home");
    }
}