package edu.edext.tools;

import edu.edext.tools.utilsCCli.CfgDB;

/**
 *
 * @author vdraco
 */
public class cfgCCli {
    
    public static final String PATH=getHomePath()+getSeparador()+".edEXT";
    public static final String PATH_CONFIG_FILE=PATH+getSeparador()+"daemon.cfg";
//    public static final String PATH_JAXWS_PROPERTIES=PATH+getSeparador()+"jaxws.properties";
    public static final String PATH_CONFIG_DB=PATH+getSeparador()+"database.properties";
    //public static final String PATH_TEMPORAL_PORT=PATH+getSeparador()+"DProt.cfg";
//    public static final String PATH_INTERNAL_IMG="/Images/";
    //public static final String IMAGE_USER = "%#@.png";
    //%#@.jpeg
//    public static final String IMAGE_USER = "usr.png";
    public static final String CLAVE_SERVER="Server",
                               CLAVE_IP="ip",
                               CLAVE_PORT="cli";
    private static final String SYM_HOME="~",
                                VALOR_PORT="9999",
                                VALOR_IP="localhost";
    private static final int posIMAGE=0,
                             posIP=1,
                             posPORT=2;
    private static String valorIP=VALOR_IP, valorPORT=VALOR_PORT;
    
 
    
    public static void init(){
//        if(!Utils.Disk.isExist(PATH_CONFIG_FILE))
//            crearCFG();
//        refreshWSDL();
//        crearProperties();
    }
    
//    public static void init(String imagen, String url, String puerto){
//        if(Utils.Disk.isExist(PATH_CONFIG_FILE)){
//            new File(PATH_CONFIG_FILE).delete();
//        Utils.Disk.delete(PATH_CONFIG_FILE);
//        crearCFG(imagen, url, puerto);
//        refreshWSDL();
//        crearProperties();
//        }
//    }
    
//    public void setPort(String puerto){
//        valorPORT=puerto;
//        crearPortFile(puerto);
//    }
        
    
    
//    public static String pathImagen(){
//        String ret=PATH;
//        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
//        for (CfgDB cfgDB : tmp)
//            if(cfgDB.clave().equals("imagen"))
//                ret=cfgDB.valor();
//        ret=ret.replace(SYM_HOME, getHomePath());
//        return ret;
//    }
    
//    public static String fullpathImagen(){
//        return pathImagen()+getSeparador();
//    }
            
//    private static void refreshWSDL(){
//        String ret, ip, port;
//        ret=ip=port=null;
//        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
//        for (CfgDB cfgDB : tmp)
//            if(cfgDB.clave().equals("ip"))
//                ip=cfgDB.valor();
//            else if(cfgDB.clave().equals("puerto")){
//                
//                port=isFreePort(cfgDB.valor());
//            }
//        valorWSDL="http://"+ip+":"+port+"/servidor_central";
//        crearPortFile(port);
//        
//    }
//    
//    public static String getWSDL(){
//        refreshWSDL();
//        return valorWSDL;
//    }
    
    private static String isFreePort(String puerto){
        if(!utilsCCli.OS.isPortFree(puerto))
//            puerto=Utils.Cast.valueOf(Utils.OS.getPortFree());
            puerto=utilsCCli.Cast.valueOf(utilsCCli.OS.scanPortFree(puerto));
        return puerto;
    }
    
    public static String getPuerto(){
        if(utilsCCli.Disk.isExist(PATH_CONFIG_FILE))
            valorPORT=getValor(CLAVE_PORT);
        else{
            valorPORT=isFreePort(VALOR_PORT);
            if(!valorPORT.equals(VALOR_PORT)){
                crearCFG(valorPORT);
                crearCFG(PATH+getSeparador()+"cli.cfg", valorPORT);
            }
        }
        return valorPORT; 
    }
    
//    public String getVALOR_IP(){ return getValor(CLAVE_IP); }
//    
//    public String getImagen(){ return getValor(CLAVE_IMAGE); }
    
    public static String getValor(String clave){
        String ret=null;
        CfgDB[] tmp=utilsCCli.Disk.loadCFG(PATH_CONFIG_FILE);
        for (CfgDB cfgDB : tmp)
            if(cfgDB.clave().equals(clave))
               ret=cfgDB.valor();
        return ret;
    }
    
//    public static String consultaWSDL(){
//        return valorWSDL+"?wsdl"; // solo para consultas de contrato
//    }
    
//    public static void crearCFG(){
//        crearCFG(VALOR_IMAGE, VALOR_IP, VALOR_PORT);
//    }
    
//    private static void leerCFG(){
//        CfgDB[] tmp=Utils.Disk.loadCFG(PATH_CONFIG_FILE);
//        for (CfgDB cfgDB : tmp)
//            if(cfgDB.clave().equals("imagen"))
//                valorIMAGE=cfgDB.valor()!=null?cfgDB.valor():valorIMAGE;
//            else if(cfgDB.clave().equals("ip"))
//                valorIP=cfgDB.valor()!=null?cfgDB.valor():valorIP;
//            else if(cfgDB.clave().equals("puerto"))
//                valorPORT=cfgDB.valor()!=null?cfgDB.valor():valorPORT;
//    }
    private static void crearCFG(String puerto){
        crearCFG(PATH_CONFIG_FILE, puerto);
    }
    private static void crearCFG(String ruta, String puerto){
        valorPORT=!utilsCCli.Validate.isEmpty(puerto)?puerto:valorPORT;
        
        utilsCCli.Disk.saveCfg(ruta, CLAVE_PORT, valorPORT+"\n");
        utilsCCli.Disk.saveCfg("\n", "");
    }
    
//    private static void crearProperties(){
//        String ruta=PATH_JAXWS_PROPERTIES;
//        Utils.Disk.delete(ruta);
//        Utils.Disk.saveCfg(ruta, " ", "# Configuración del Web Service para compilación\n");
//        Utils.Disk.saveCfg(ruta, " ", "wsdl.url="+consultaWSDL());
//    }
    
//    private static void crearPortFile(){
//        Utils.Disk.delete(PATH_TEMPORAL_PORT);
//        Utils.Disk.saveCfg(PATH_TEMPORAL_PORT, CLAVE_PORT, valorPORT, false);
//    }
   
    private static String getSeparador(){
        return java.io.File.separator;
    }

    private static String getHomePath(){
        return System.getProperty("user.home");
    }
}