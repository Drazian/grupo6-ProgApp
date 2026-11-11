package edu.edext.tools;

import edu.edext.tools.Utils.CfgDB;

/**
 *
 * @author vdraco
 */
public class cfgw {
    
    public static final String PATH=getHomePath()+getSeparador()+".edEXT";
    public static final String PATH_CONFIG_FILE=PATH+getSeparador()+"edEXTweb.cfg";
    public static final String PATH_JAXWS_PROPERTIES=PATH+getSeparador()+"jaxws.properties";
//    public static final String PATH_CONFIG_DB=PATH+getSeparador()+"database.properties";
//    public static final String PATH_TEMPORAL_PORT=PATH+getSeparador()+"Puerto.en.uso";
//    public static final String PATH_INTERNAL_IMG="/Images/";
    //public static final String IMAGE_USER = "%#@.png";
    //%#@.jpeg
//    public static final String IMAGE_USER = "usr.png";
    public static final String CLAVE_IP="ip",
                               CLAVE_PORT="puerto";
    private static final String SYM_HOME="~",
                                VALOR_PORT="8080",
                                VALOR_IP="localhost";
    private static final int posIP=0,
                             posPORT=1;
    private static String valorIP=VALOR_IP, valorPORT=VALOR_PORT, valorWSDL;
    
 
    
    public static void init(){
        if(!Utils.Disk.isExist(PATH_CONFIG_FILE))
            crearCFG();
//        refreshWSDL();
//        crearProperties();
    }
    
    public static void init(String url, String puerto){
//        if(Utils.Disk.isExist(PATH_CONFIG_FILE)){
//            new File(PATH_CONFIG_FILE).delete();
        Utils.Disk.delete(PATH_CONFIG_FILE);
        crearCFG(url, puerto);
//        refreshWSDL();
//        crearProperties();
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
//        if(valorWSDL==null){
            String ret, ip, port;
            ret=ip=port=null;
            CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
            for (CfgDB cfgDB : tmp)
                if(cfgDB.clave().equals("ip"))
                    ip=cfgDB.valor();
                else if(cfgDB.clave().equals("puerto")){
                    port=cfgDB.valor();
                }
            valorWSDL="http://"+ip+":"+port+"/servidor_central";
//            crearPortFile(port);
//        }
    }
    
    public static String getWSDL(){
        init();
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
        crearCFG(VALOR_IP, VALOR_PORT);
    }
    
    private static void leerCFG(){
        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals("ip"))
                valorIP=cfgDB.valor()!=null?cfgDB.valor():valorIP;
            else if(cfgDB.clave().equals("puerto"))
                valorPORT=cfgDB.valor()!=null?cfgDB.valor():valorPORT;
    }
    private static void crearCFG(String url, String puerto){
        boolean ip, port;
        ip=port=true;
        leerCFG();
        valorIP=!Utils.Validate.isEmpty(url)?url:valorIP;
        valorPORT=!Utils.Validate.isEmpty(puerto)?puerto:valorPORT;
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
    
//    private static void crearProperties(){
//        String ruta=PATH_JAXWS_PROPERTIES;
//        Utils.Disk.delete(ruta);
//        Utils.Disk.saveCfg(ruta, " ", "# Configuración del Web Service para compilación\n");
//        Utils.Disk.saveCfg(ruta, " ", "wsdl.url="+consultaWSDL());
//    }
    
//    private static void crearPortFile(String puerto){
//        String ruta=PATH_TEMPORAL_PORT;
//        Utils.Disk.saveCfg(ruta, CLAVE_PORT, puerto, false);
//    }
   
    private static String getSeparador(){
        return java.io.File.separator;
    }

    private static String getHomePath(){
        return System.getProperty("user.home");
    }
}