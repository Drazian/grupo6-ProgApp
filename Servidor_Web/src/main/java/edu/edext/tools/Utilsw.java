package edu.edext.tools;
//****************************** Libreria **************************************
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.time.format.DateTimeFormatter;
import java.security.InvalidKeyException;
import java.nio.file.StandardCopyOption;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.mindrot.jbcrypt.BCrypt;
import java.text.SimpleDateFormat;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.io.InputStream;
import java.time.LocalDate;
import javax.crypto.Cipher;
import java.nio.file.Path;
import org.tinylog.Logger;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.io.File;
 
/**
 *
 * @author vdraco
 */
public class Utilsw {
    
    private static final String FORMATO_FECHA="dd/MM/yyyy";
//    private static final String MASK_FECHA="##/##/####";
    public record CfgDB(String clave, String valor) {}
//    public record dbData(String clave, String valor) {}
    public static final int PATHFILE=0;
    public static final int NAMEFILE=1;
    public static final int EXTFILE=2;     
    
    public static String[] ArrayNull(){
        String[] ret=null;
        return ret;
    }
    
    public final class Validate{
        
        public static boolean isFecha(String fecha){
            boolean ret=false;
            try{
                LocalDate.parse(fecha, DateTimeFormatter.ofPattern(FORMATO_FECHA));
                ret=true;
            } catch (Exception e) {}
            return ret;
        }
        
        public static boolean isEmpty(String cadena){
            return cadena==null||cadena.isBlank();
        }
        
        public static boolean isEmpty(List<?> coleccion){
            return coleccion==null||coleccion.isEmpty();
        }
        
        public static boolean isEmpty(Set<?> coleccion){
            return coleccion==null||coleccion.isEmpty();
        }
        
        public static boolean isEmail(String email){
            boolean ret=email!=null&&!email.isBlank();
            if(ret){
                email=email.trim();
                int posArroba1=email.indexOf("@"), posArroba2=email.lastIndexOf("@"), posPunto=email.lastIndexOf(".");
                ret=posArroba1==posArroba2&&posArroba1>0&&posPunto>posArroba1+1&&email.length()-1-posPunto>=2;
            }
            return ret;
        }
    
    }

    public final class Fecha{
        
        public static String assignFormato(Date fecha){
            return assignFormato(fecha, FORMATO_FECHA);
        }
        
        public static String assignFormato(LocalDate fecha){
            return assignFormato(fecha, FORMATO_FECHA);
        }

        public static String assignFormato(Date fecha, String formato){
            return fecha!=null?(new SimpleDateFormat(formato)).format(fecha):null;
        }
        
        public static String assignFormato(LocalDate fecha, String formato){
            return fecha!=null?DateTimeFormatter.ofPattern(formato).format(fecha):null;
        }

        public static boolean validate(String fecha){
            return Validate.isFecha(fecha);
        }

        public static boolean isCoherente(Date fecha1, Date fecha2){
            return fecha1.before(fecha2);
        }   
        
        public static boolean isCoherente(LocalDate fecha1, LocalDate fecha2){
            return fecha1.isBefore(fecha2);
        }   

    }
    
    public final class Cast{

        public static LocalDate valueOf(Date fecha){
            return fecha!=null?fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate():null;
        }
        
        public static Date valueOf(LocalDate fecha){
            return fecha!=null?Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()):null;
        }

        public static LocalDate valueOf(String fecha){
            try{ return LocalDate.parse(fecha, DateTimeFormatter.ofPattern(FORMATO_FECHA)); }
            catch (Exception e) { return null; }
        }
        
        public static Date valudf(String fecha){
            return valueOf(valueOf(fecha));
        }

        public static Integer valuOf(String cadena){
            try { return Integer.valueOf(cadena); }
            catch (NumberFormatException e) { return null; }
        }
        
        public static int valueof(String cadena){
            try { return Integer.parseInt(cadena); }
            catch (NumberFormatException e) { return Integer.MIN_VALUE ; }
        }
        
        public static String valueOf(Integer numero){
            try { return Integer.toString(numero); }
            catch (Exception e) { return null; }
        }

    }

    public final class Crypt{
        private static final String CLAVE_AES="ClaveSecreta1234"; 
        private static final String ALGORITMO="AES";
        
        public static String encodeBCrypt(String password){
            return encodeBCrypt(password, 7);
        }
        // level = 2^n, donde n=level
        public static String encodeBCrypt(String password, int level){
            return BCrypt.hashpw(password, BCrypt.gensalt(level));
        }
        
        public static boolean compareBCrypt(String password, String passSaved){
            return BCrypt.checkpw(password, passSaved);
        }
        
        public static String cifrarAES(String password){
            try {
                SecretKeySpec secretKey = new SecretKeySpec(CLAVE_AES.getBytes(), ALGORITMO);
                Cipher cipher = Cipher.getInstance(ALGORITMO);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] textoCifradoBytes = cipher.doFinal(password.getBytes());
                return Base64.getEncoder().encodeToString(textoCifradoBytes);
            } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                Logger.error(e, "Error al cifrar");
                throw new RuntimeException("Error al cifrar", e);
            }
        }
        
        public static String descifrarAES(String password) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(CLAVE_AES.getBytes(), ALGORITMO);
                Cipher cipher = Cipher.getInstance(ALGORITMO);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] textoCifradoBytes = Base64.getDecoder().decode(password);
                byte[] textoPlanoBytes = cipher.doFinal(textoCifradoBytes);
                return new String(textoPlanoBytes);
            } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                Logger.error(e, "Error al cifrar");
                throw new RuntimeException("Error al descifrar", e);
            }
        }
    
    }

    public final class Imagen {
   
        public static InputStream toStream(String ruta) throws IOException {
            return Files.newInputStream(Path.of(ruta));
        }
        
        public static boolean save(InputStream imagen, String path, String nombre){
            String nameFile=path+OS.getSeparador()+nombre;
            long ret=0;
            try {
                if(!Disk.isExist(path)) Disk.createFolder(path);
                ret=Files.copy(imagen, Path.of(nameFile), StandardCopyOption.REPLACE_EXISTING);
                Logger.info("Imagen guardada en ..: {}", nameFile);
            } catch (IOException e) {
                Logger.error(e.getMessage(), "Error al escribir el Imagen en disco: "); }
            path=nameFile=null;
            return ret!=0;
        }
        
    }
    
    public final class Disk {

        public static boolean isExist(String ruta){
            return (new File(ruta)).exists();
        }
        
        public static void delete(String rutaFile){
            if(isExist(rutaFile))
                new File(rutaFile).delete();
        }
        
        public static String[] splitPath(String path){
            int posS=path.lastIndexOf(File.separator), posE=path.lastIndexOf(".");
            if(posS==-1) posS=path.length()-1;
            if(posE==-1) posE=path.length();
            String[] ret={path.substring(0, posS+1), path.substring(posS+1, posE), path.substring(posE)};
            return ret;
        }

        public static String openPathOS(){
            return openPathOS(null);
        }

        public static String openPathOS(String titulo){
            String retPath="";
            if(titulo!=null && titulo.isEmpty()) titulo="Selecciona una carpeta";
            if(isWin()){
                try {
                    String sendOS = "[void][System.Reflection.Assembly]::LoadWithPartialName('System.Windows.Forms'); " +
                                     "$objForm = New-Object System.Windows.Forms.FolderBrowserDialog; " +
                                     "$objForm.Description = '"+titulo+"'; " +
                                     "if($objForm.ShowDialog() -eq 'OK') { Write-Host $objForm.SelectedPath }";

                    Process procOS = (new ProcessBuilder("powershell", "-NoProfile", "-Command", sendOS)).start();
                    BufferedReader retOS = new BufferedReader(new InputStreamReader(procOS.getInputStream(), "UTF-8"));
                    retPath = retOS.readLine();
                } catch (IOException e) {  Logger.debug("Error al abrir directorio desde SO : {}", e.getMessage());  }
            }else{
                try {
                    Process procOS=new ProcessBuilder("zenity", "--file-selection", "--directory", "--title="+titulo).start();
                    BufferedReader retOS=new BufferedReader(new InputStreamReader(procOS.getInputStream()));
                    retPath=retOS.readLine();
                } catch (IOException e) {  Logger.debug("Error al abrir directorio desde SO : {}", e.getMessage());  }
            }
            return retPath!=null?retPath:"";
        }

        public static void createFolder(String ruta){
            File path=new File(ruta);
            if(!path.exists()) path.mkdirs();
            path=null;
        }

        private static boolean isWin(){
            return System.getProperty("os.name").toLowerCase().contains("win");
        }
        private static String getHomePath(){
            return System.getProperty("user.home");
        }
        private static String getSeparador(){
            return java.io.File.separator;
        }
        private static String getPaqueteName(){
            return Utilsw.class.getPackageName();
        }
        //********************* working *************************

        public static CfgDB[] loadCFG(String pathFile){
            ArrayList<CfgDB> tmp=new ArrayList<>();
            try (BufferedReader tmpCFG = new BufferedReader(new FileReader(pathFile))){
                String tmpText; int pos=-1;
                while((tmpText=tmpCFG.readLine())!=null){
                    tmpText=tmpText.trim();
                    if(!tmpText.isEmpty() && !tmpText.startsWith("#") && !tmpText.startsWith("//") && tmpText.contains(":")){
                        pos=tmpText.indexOf(":");
                        String clave=tmpText.substring(0,pos).trim();
                        String valor=tmpText.substring(pos+1).trim();
                        tmp.add(new CfgDB(clave, valor));
                    }
                }
                tmpCFG.close();
            } catch (IOException e){  Logger.debug("Error abriendo archivo de configuracion : {}", e.getMessage());  }
            return tmp.toArray(CfgDB[]::new);
        }

        public static void saveCfg(String clave, String valor){
            saveCfg(null, clave, valor, true);
        }
        public static void saveCfg(String ruta, String clave, String valor){
            saveCfg(ruta, clave, valor, true);
        }
        public static void saveCfg(String ruta, String clave, String valor, boolean modo){
            createFolder(cfgw.PATH);
            if(Validate.isEmpty(ruta)) ruta=cfgw.PATH_CONFIG_FILE;
            try (BufferedWriter tmpLog = new BufferedWriter(new FileWriter(ruta, modo))) {
                // mas adelante tengo que agregar busqueda y remplazo del parametro
                if("\n".equals(clave))
                    tmpLog.write("\n"+valor);
                else if("#".equals(clave))
                    tmpLog.write(clave+valor);
                else if(" ".equals(clave))
                    tmpLog.write(valor);
                else
                    tmpLog.write(clave+" : "+valor);
//                Logger.debug("Archivo CFG guardado en ..: {}",cfg.PATH_CONFIG_FILE);
                tmpLog.close();
            } catch (IOException e) {
                Logger.error(e.getMessage(),"Error al guardar CFG... ");
            }
        }// en faltan los demas parametros
       //******************************************************** 
    }

    public final class OS {

        public static final String SymHome="~";

//**************************** Marcado para eliminar *************************//
        public static ProcessBuilder exec(String comando){
            ProcessBuilder pid=null;
            try {
                String nombre, path;
                if(comando.contains(getSeparador()) &&  !comando.contains("."+getSeparador()) && !comando.contains(".."+getSeparador())){
                    int pos=comando.lastIndexOf(getSeparador());
                    nombre=comando.substring(pos+1);
                    path=comando.substring(0, pos); }
                else{
                    nombre=comando;
                    path=getWorkPath(); }
//                Path pathFile = Paths.get(path).normalize();
                File pathFile= new File(path);
                List<String> comandos = new ArrayList<>();
                if (isWindows()) {
                    comandos.add("cmd.exe");
                    comandos.add("/c");
                    comandos.add("start");
                    comandos.add("\"\"");
                    comandos.add(nombre); }
                else {
                    comandos.add("sh");
                    comandos.add("-c");
                    comandos.add("nohup ./" + nombre); }
                pid=new ProcessBuilder(comandos);
                pid.directory(pathFile);
                pid.start();
            }
            catch (Exception e) {
                Logger.error(e, "Error en el lanzador ");}
            return pid;
        }
//****************************************************************************//

        
    ///@return   Devuelve el nombre del paquete
        public static String getPaqueteName(Object obj){
            return obj.getClass().getPackageName();
        }
    ///@return   Devuelve el directorio de Usuario
        public static String getHomePath(){
            return System.getProperty("user.home");
        }
    ///@return   Devuelve el directorio ApppData (solo windows)
        public static String getAppDataPath(){
            return System.getenv("APPDATA");
        }
    /**
     * @return   Devuelve el identificador del S.O.
     *          Windows 11, Windows 10, Windows 8.1, 
     *          Windows 7, Windows Server 2019, Windows Server 2016
     *          Mac OS X, Linux, FreeBSD, OS/2, Solaris, SunOS
     *          AIX, Digital Unix, HP-UX, Irix, MPE/iX, NetWare, OpenVMS
    **/
        public static String getName(){
            return System.getProperty("os.name");
        }
        
    /// @return   Devuelve el directorio de trabajo actual
        public static String getWorkPath(){
            return System.getProperty("user.dir");
        }
    /// @return   Devuelve el separador de archivo del S.O.
        public static String getSeparador(){
            return java.io.File.separator;
        }

        public static boolean isWindows(){
            return getName().toLowerCase().contains("win");
        }

        public static boolean isLinux(){
            return getName().toLowerCase().contains("nux");
        }

        public static boolean isMac(){
            return getName().toLowerCase().contains("mac");
        }

        public static boolean isUnix(){
            return getName().toLowerCase().contains("nix");
        }

        public static boolean isBSD(){
            return getName().toLowerCase().contains("bsd");
        }
        
        public static int scanPortFree(String puerto){
            return scanPortFree(Cast.valuOf(puerto));
        }
        public static int scanPortFree(int puerto){
            boolean flag=true;
            int ret;
            for(ret=puerto; ret<65536; ret++){
                if(isPortFree(ret)){
                    flag=false;
                    break; } }
            if(flag)
                for(ret=puerto-1; ret>1023; ret--){
                    if(isPortFree(ret))
                        break; }
            if(ret>65525 || ret<1024){
                ret=-1;
                Logger.error("Sin puertos disponibles.");
            }
            return ret;
        }
        
        public static int getPortFree(){
            try (ServerSocket ret=new ServerSocket(0)){
                return ret.getLocalPort();
            } catch (Exception e) {
                Logger.error("Sin puertos disponibles.");
                return -1;
            }
        }
        public static boolean isPortFree(String port){
            return isPortFree(Cast.valueof(port));
        }
        public static boolean isPortFree(int port){
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                return true;
            } catch (IOException e) {
                return false;
            }
        }

    }
   
}
