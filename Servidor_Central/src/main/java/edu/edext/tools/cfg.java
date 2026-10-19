package edu.edext.tools;

import edu.edext.tools.Utils.CfgDB;

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
    private static final String SYM_HOME="~";
    
 
    
    public static void init(){
        if(!Utils.Disk.isExist(cfg.PATH_FILE))
            
            crearCFG();
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
            
    
    public static void crearCFG(){
        Utils.Disk.saveCfgPath("#", "Carpeta de imagen\n");
        Utils.Disk.saveCfgPath("imagen", "~"+getSeparador()+".edEXT"+getSeparador()+"imagen\n");
    }
   
    private static String getSeparador(){
        return java.io.File.separator;
    }

    private static String getHomePath(){
        return System.getProperty("user.home");
    }
}
