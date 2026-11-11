package edu.edext.tools;

import edu.edext.tools.Utils.CfgDB;

/**
 *
 * @author vdraco
 */
public class cfg {
    
    public static final String PATH=getHomePath()+getSeparador()+".edEXT";
    public static final String PATH_CONFIG_FILE=PATH+getSeparador()+"edEXT.cfg";
    public static final String PATH_JAXWS_PROPERTIES=PATH+getSeparador()+"jaxws.properties";
    public static final String PATH_CONFIG_DB=PATH+getSeparador()+"database.properties";
    public static final String PATH_TEMPORAL_PORT=PATH+getSeparador()+"Puerto.en.uso";
    public static final String PATH_INTERNAL_IMG="/Images/";
    //public static final String IMAGE_USER = "%#@.png";
    //%#@.jpeg
    public static final String IMAGE_USER = "usr.png";
    public static final String CLAVE_IMAGE="imagen",
                               CLAVE_IP="ip",
                               CLAVE_PORT="puerto";
    private static final String SYM_HOME="~",
                                VALOR_PORT="8080",
                                VALOR_IP="localhost",
                                VALOR_IMAGE="imagen";
    private static final int posIMAGE=0,
                             posIP=1,
                             posPORT=2;
    private static String valorIMAGE=VALOR_IMAGE, valorIP=VALOR_IP, valorPORT=VALOR_PORT, valorWSDL;
    
 
    
    public static void init(){
        if(!Utils.Disk.isExist(PATH_CONFIG_FILE))
            crearCFG();
        refreshWSDL();
        crearProperties();
    }
    
    public static void init(String imagen, String url, String puerto){
//        if(Utils.Disk.isExist(PATH_CONFIG_FILE)){
//            new File(PATH_CONFIG_FILE).delete();
        Utils.Disk.delete(PATH_CONFIG_FILE);
        crearCFG(imagen, url, puerto);
        refreshWSDL();
        crearProperties();
//        }
    }
    
    public static String pathImagen(){
        String ret=PATH;
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals("imagen"))
                ret=cfgDB.valor();
        ret=ret.replace(SYM_HOME, getHomePath());
        return ret;
    }
    
    public static String fullpathImagen(){
        return pathImagen()+getSeparador();
    }
            
    private static void refreshWSDL(){
        if(valorWSDL==null){
            String ret, ip, port;
            ret=ip=port=null;
            CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
            for (CfgDB cfgDB : tmp)
                if(cfgDB.clave().equals("ip"))
                    ip=cfgDB.valor();
                else if(cfgDB.clave().equals("puerto")){

                    port=isFreePort(cfgDB.valor());
                }
            valorWSDL="http://"+ip+":"+port+"/servidor_central";
            crearPortFile(port);
        }
    }
    
    public static String getWSDL(){
        refreshWSDL();
        return valorWSDL;
    }
    
    private static String isFreePort(String puerto){
        if(!Utils.OS.isPortFree(puerto))
//            puerto=Utils.Cast.valueOf(Utils.OS.getPortFree());
            puerto=Utils.Cast.valueOf(Utils.OS.scanPortFree(puerto));
        return puerto;
    }
    
    public int getPuerto(){ return Utils.Cast.valueof(getValor(CLAVE_PORT)); }
    
    public String getVALOR_IP(){ return getValor(CLAVE_IP); }
    
    public String getImagen(){ return getValor(CLAVE_IMAGE); }
    
    public String getValor(String clave){
        String ret=null;
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals(clave))
               ret=cfgDB.valor();
        return ret;
    }
    
    public static String consultaWSDL(){
        return valorWSDL+"?wsdl"; // solo para consultas de contrato
    }
    
    public static void crearCFG(){
        crearCFG(VALOR_IMAGE, VALOR_IP, VALOR_PORT);
    }
    
    private static void leerCFG(){
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals("imagen"))
                valorIMAGE=cfgDB.valor()!=null?cfgDB.valor():valorIMAGE;
            else if(cfgDB.clave().equals("ip"))
                valorIP=cfgDB.valor()!=null?cfgDB.valor():valorIP;
            else if(cfgDB.clave().equals("puerto"))
                valorPORT=cfgDB.valor()!=null?cfgDB.valor():valorPORT;
    }
    private static void crearCFG(String imagen, String url, String puerto){
        boolean img, ip, port;
        img=ip=port=true;
        leerCFG();
//        if(Utils.Validate.isEmpty(imagen) ) imagen=VALOR_IMAGE;
//        if(Utils.Validate.isEmpty(url)) url=VALOR_IP;
//        if(Utils.Validate.isEmpty(puerto)) puerto=VALOR_PORT;

        valorIMAGE=!Utils.Validate.isEmpty(imagen)?imagen:valorIMAGE;
        valorIP=!Utils.Validate.isEmpty(url)?url:valorIP;
        valorPORT=!Utils.Validate.isEmpty(puerto)?puerto:valorPORT;
        
        
//        imagen=imagen.replaceAll("~"+getSeparador()+".edEXT"+getSeparador(),"");
//        imagen=imagen.replaceAll("~"+getSeparador()+".edEXT","");
//        imagen=imagen.replaceAll("~"+getSeparador(),"");
//        imagen=imagen.replaceAll("~","");
        if(img){
            Utils.Disk.saveCfg("#", "Carpeta de imagen\n");
            Utils.Disk.saveCfg(CLAVE_IMAGE, "~"+getSeparador()+".edEXT"+getSeparador()+valorIMAGE+"\n");
            Utils.Disk.saveCfg("\n", "");
        }
        if(ip){
            Utils.Disk.saveCfg("#", "IP del Web Service\n");
            Utils.Disk.saveCfg(CLAVE_IP, valorIP+"\n");
            Utils.Disk.saveCfg("\n", "");
        }
        if(port){
            Utils.Disk.saveCfg("#", "Puerto del Web Service\n");
            Utils.Disk.saveCfg(CLAVE_PORT, VALOR_PORT+"\n");
            Utils.Disk.saveCfg("\n", "");
        }
    }
    
    private static void crearProperties(){
        String ruta=PATH_JAXWS_PROPERTIES;
        Utils.Disk.delete(ruta);
        Utils.Disk.saveCfg(ruta, " ", "# Configuración del Web Service para compilación\n");
        Utils.Disk.saveCfg(ruta, " ", "wsdl.url="+consultaWSDL());
    }
    
    private static void crearPortFile(String puerto){
        String ruta=PATH_TEMPORAL_PORT;
        Utils.Disk.saveCfg(ruta, CLAVE_PORT, puerto, false);
    }
   
    private static String getSeparador(){
        return java.io.File.separator;
    }

    private static String getHomePath(){
        return System.getProperty("user.home");
    }
}
