package edu.edext.tools;
//****************************** Libreria **************************************
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.format.DateTimeFormatter;
import java.nio.file.StandardCopyOption;
import java.net.URISyntaxException;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import org.mindrot.jbcrypt.BCrypt;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.io.InputStreamReader;
import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import java.io.BufferedReader;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.time.LocalDate;
import java.util.ArrayList;
import java.awt.FileDialog;
import java.io.IOException;
import java.nio.file.Files;
import java.io.InputStream;
import java.nio.file.Path;
import javax.swing.JFrame;
import org.tinylog.Logger;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.io.FileReader;
import java.time.ZoneId;
import java.util.Date;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
/**
 *
 * @author vdraco
 */
public class Utils {
 
    
    private static final String FORMATO_FECHA="dd/MM/yyyy";
    private static final String MASK_FECHA="##/##/####";
    public static final int ALIGN_LEFT=SwingConstants.LEFT;
    public static final int ALIGN_CENTER=SwingConstants.CENTER;
    public static final int ALIGN_RIGHT=SwingConstants.RIGHT;
    public record CfgDB(String clave, String valor) {}
    public record dbData(String clave, String valor) {}
    public static final int PATHFILE=0;
    public static final int NAMEFILE=1;
    public static final int EXTFILE=2;     
    public static final int SAVEINTERNAL=0;
    public static final int SAVEEXTERNAL=1;
            
    
    public static String[] ArrayNull(){
        String[] ret=null;
        return ret;
    }
    
    public final class GUI{

        public static void sizeColumn(JTable obj, int column, int size){
            if(obj!=null)
                obj.getColumnModel().getColumn(column).setPreferredWidth(size);
        }

        public static void sizeColumn(JTable obj, int column, int size, int Alignment){
            if(obj!=null){
                obj.getColumnModel().getColumn(column).setPreferredWidth(size);
                alignment(obj, column, Alignment); }
        }

        public static void alignment(JTable obj, int column, int Alignment){
            if(obj!=null)
                obj.getColumnModel().getColumn(column).setCellRenderer(getAlignCell(Alignment));
        }

        private static DefaultTableCellRenderer getAlignCell(int Alignment){
            DefaultTableCellRenderer tmp=new DefaultTableCellRenderer();
            tmp.setHorizontalAlignment(Alignment);
            return tmp;
        }

        public static DefaultTableModel getModel(JTable obj){
            if(obj!=null)
                return (DefaultTableModel) obj.getModel();
            else return null;
        }

        public static void addRow(JTable obj, Object[] rowData){
            if(obj!=null){
                DefaultTableModel model=getModel(obj);
                if(model!=null){
                    model.addRow(rowData);
                    obj.setModel(model); }
            }
        }
        
        public static DefaultTableModel clear(JTable obj){
            DefaultTableModel model=null;
            if(obj!=null){
                model=getModel(obj);
                if(model!=null)
                    model.setRowCount(0); }
            return model;
        }

        public static void setDisable(JList obj){
            obj.setSelectionModel(new DefaultListSelectionModel(){
                @Override
                public void setSelectionInterval(int index0, int index1){}

                @Override
                public void addSelectionInterval(int index0, int index1){}
            });
        }
        
        
        public static void setDisable(JCheckBox obj){
            obj.setModel(new javax.swing.DefaultButtonModel(){
                @Override
                public void setPressed(boolean b) {}
//                @Override
//                public void setSelected(boolean b) {}
            });
        }
            
        public static void soloNumeros(Component obj){
            soloNumeros(obj, false);
        }
        
        public static void soloNumeros(Component obj, boolean espacios){
            obj.addKeyListener(new java.awt.event.KeyAdapter(){
                @Override
                public void keyTyped(java.awt.event.KeyEvent evt){
                    char c = evt.getKeyChar();
                    if(espacios){
                        if (!Character.isDigit(c) && c != ' ')
                            evt.consume();
                    }else
                        if (!Character.isDigit(c))
                            evt.consume(); } });
        }
        
        public static void soloLetras(Component obj){
            soloLetras(obj, false);
        }
        
        public static void soloLetras(Component obj, boolean espacios){
            obj.addKeyListener(new java.awt.event.KeyAdapter(){
                @Override
                public void keyTyped(java.awt.event.KeyEvent evt){
                    char c = evt.getKeyChar();
                    if(espacios){
                        if (!Character.isLetter(c) && c != ' ')
                            evt.consume();
                    }else
                        if (!Character.isLetter(c))
                            evt.consume(); } });
        }
        
        public static boolean isEmpty(JFormattedTextField obj){
            String txt=obj.getText();
            return txt==null || txt.isBlank() || txt.equals("--/--/----");
        }
        
        public static void assignFormato(JFormattedTextField obj){
            assignFormato(obj, MASK_FECHA);
        }
        
        public static void assignFormato(JFormattedTextField obj, String formato){
            try {
                MaskFormatter tmp = new MaskFormatter(formato);
                tmp.setPlaceholderCharacter('-');
                obj.setFormatterFactory(new DefaultFormatterFactory(tmp));
            } catch (java.text.ParseException e){
                Logger.error(e.getMessage(), "Error al formatear : {} ", obj);
            }
        }

        public static JInternalFrame getContenedor(Component obj){
            if(obj instanceof JInternalFrame)
                return (JInternalFrame) obj;
            else
                return (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, obj);
        }
        
        public static void reSizePadre(Component obj, int width, int height){
            JInternalFrame tmp = getContenedor(obj);
            if(tmp!=null){
                tmp.setSize(new Dimension(width, height));
                tmp.revalidate();
                tmp.repaint();
            }
            tmp=null;
            System.gc();
        }
        
        private static void repaint(Component obj){
            obj.revalidate();
            obj.repaint();
        }
        
    }
    
    public final class Validate{
        
        public static boolean isFecha(String fecha){
            boolean ret=false;
            try{
                LocalDate.parse(fecha, DateTimeFormatter.ofPattern(FORMATO_FECHA.replace("yyyy", "uuuu")).withResolverStyle(java.time.format.ResolverStyle.STRICT));
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
            return fecha1!=null && fecha2!=null && !fecha1.after(fecha2);
        }
        
        public static boolean isCoherente(LocalDate fecha1, LocalDate fecha2){
            return fecha1!=null && fecha2!=null && !fecha1.isAfter(fecha2);
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

        public static String hashPassword(String password){
            return encodeBCrypt(password);
        }

        public static boolean isBCrypt(String passSaved){
            return passSaved!=null && passSaved.matches("^\\$2[aby]?\\$\\d{2}\\$.{53}$");
        }

        public static boolean verifyPassword(String password, String passSaved){
            if(password==null || passSaved==null) return false;
            try {
                if(isBCrypt(passSaved)) return BCrypt.checkpw(password, passSaved);
            } catch (Exception e) { return false; }
            try {
                if(password.equals(descifrarAES(passSaved))) return true;
            } catch (Exception e) { /* no era AES */ }
            return password.equals(passSaved);
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
        private static final int WITH=0;
        private static final int HEIGHT=1;
        public static final int SIZEIMG=128;

    /**
     * @param imagen    Imagen a determinar su contenido
     * @return          Devuelve true o false dependiendo el contendo de la imagen
     */
        public static boolean isEmpty(ImageIcon imagen){
            boolean ret=false;
            if(imagen==null) ret=true;
            else if(imagen.getIconWidth()<1 || imagen.getIconHeight()<1) ret=true;
            return ret;
        }
    /**
     * @param imagen    Imagenen a calcular su relacion de aspecto, ( 4:3, 16:9, 9:16, 4:5, etc )
     * @param num       Tamaño resultante de la imagen
     * @param opcion    Determina si el redimensionado se hara en funcion del ancho o en funcion del alto de la imagen
     *                  si la imagen entra con WITH ( 0 ) se respetara el alto y se calculara el ancho basandose en su ratio de origen
     *                  si la imagen entra con HEIGHT ( 1 ) se respetara el ancho y se calculara el alto basandose en su ratio de origen
     * @return          Devuelve una vector con la siguiente estructura { ancho, alto }
     */
        private static int[] getAspectRatio(ImageIcon imagen, int num, int opcion){
            int[] ret={0,0};
            float escala=getAspectRatio(imagen);
            int with=0, height=0;
            if(opcion==WITH){
                with=num;
                height=Math.round(num/escala);
            }else{
                height=num;
                with=Math.round(num*escala);
            }
            ret[WITH]=with;
            ret[HEIGHT]=height;
            return ret;
        }
    /**
     * @param imagen    Imagen a calcular su relacion de aspecto, ( 4:3, 16:9, 9:16, 4:5, etc )
     * @return          Devuelve la relacion de aspecto de la imagen
     */
        public static float getAspectRatio(ImageIcon imagen){
            return !isEmpty(imagen)?(float)imagen.getIconWidth()/imagen.getIconHeight():0;
        }
    /**
     * @param with      Ancho de la nueva imagen
     *                  si With = 0 se respetara el ancho y se calculara el alto basandose en su ratio de origen
     * @param height    Alto de la nueva imagen
     *                  si Height = 0 se respetara el alto y se calculara el ancho basandose en su ratio de origen
     * @param imagen    Imagen a redimensionar
     * @return          Devuelve la imagen escalada con los nuevas dimensiones
     */
        public static ImageIcon resize(int with, int height, ImageIcon imagen){
            ImageIcon retFoto=null;
            if(with==0 && height==0) with=height=SIZEIMG;
            int[] size ={with, height};
            try {
                if(with==0) size=getAspectRatio(imagen, height, HEIGHT);
                if(height==0) size=getAspectRatio(imagen, with, WITH);
                Image tmpIco2 = imagen.getImage().getScaledInstance(size[WITH], size[HEIGHT], Image.SCALE_SMOOTH);
                retFoto = new ImageIcon(tmpIco2);
            } catch(Exception e){  if(Logger.isDebugEnabled()) Logger.debug("Error al escalar foto ...: {}", e.getMessage());   }       
           return retFoto;
        }    
    /**
     * @param nombre    nombre del archivo contenido en el Jar
     //* @param ruta      Direccion relativa de la imagen al directorio raiz del proyecto
     * @return          Devuelve imagen contenida en el Jar
     */    
       public static ImageIcon loadJVM(String nombre){
            ImageIcon retFoto=null;
            try {
                //retFoto = new ImageIcon(Imagen.class.getClassLoader().getResource(Imagen.class.getPackageName()+"/"+ruta));
                retFoto=new ImageIcon(Imagen.class.getResource("/Images/"+nombre)); }
            catch (Exception e) {  if(Logger.isErrorEnabled()) Logger.error("Error con load JM Foto ...: {} ", e.getMessage());  }
            return retFoto;        
        }
    /**
     * @param ruta      Direccion absoluta de la imagen
     * @return          Devuelve imagen compatible con componentes java
     */
        public static ImageIcon loadSO(String ruta){
            ImageIcon retFoto=null;
            try {
                retFoto=new ImageIcon(new File(ruta).getAbsolutePath()); }
            catch (Exception e) {  if(Logger.isErrorEnabled()) Logger.error("Error con load SO Foto ...: {} ", e.getMessage());  }
            return retFoto;
        }
    /**
     * @param imagen    Direccion url de la imagen
     * @return          Devuelve imagen compatible con componentes java
     */
        public static ImageIcon loadWeb(String imagen){
            ImageIcon retFoto=null;
            try {
                HttpURLConnection conexion = (HttpURLConnection) (new URI(imagen).toURL()).openConnection();
                conexion.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                retFoto=new ImageIcon(ImageIO.read(conexion.getInputStream()));
            } catch (IOException | URISyntaxException e) {  Logger.error("Error con load WWW Foto ...: {}", e.getMessage());  }
            return retFoto;
        }
        
        public static InputStream toStream(String ruta) throws IOException {
            return Files.newInputStream(Path.of(cfg.pathImagen()+OS.getSeparador()+ruta));
        }
        
    /// @return      Devuelve el objeto que representa la imagen seleccionada
        public static File openJVM() {
            File retFile=null;
            JFileChooser buscar=new JFileChooser();
            buscar.setFileFilter(new FileNameExtensionFilter("Imagenes (JPG, PNG)", "jpg", "jpeg", "png"));
            if((buscar.showOpenDialog(null))==JFileChooser.APPROVE_OPTION){
                try {
                    retFile=buscar.getSelectedFile();
                } catch(Exception e) {  Logger.error("Error al abrir foto: {}", e.getMessage());  }
            }
            return retFile;
        }
    /// @return      Devuelve el objeto que representa la imagen seleccionada
        public static File openSO(){
            File retFile=null;
            FileDialog tmpPath = new FileDialog((Frame) null, "Selecciona una Imagen (JPG, PNG)", FileDialog.LOAD);
            if(OS.isWin()) tmpPath.setFile("*.jpg;*.jpeg;*.png");
            else{
                tmpPath.setFilenameFilter((dir, name) -> {
                    String lowerCaseName = name.toLowerCase();
                    return lowerCaseName.endsWith(".jpg") || 
                           lowerCaseName.endsWith(".jpeg") || 
                           lowerCaseName.endsWith(".png"); });
            }
            tmpPath.setVisible(true);
            try {
                retFile=tmpPath.getDirectory()!=null && tmpPath.getFile()!=null?new File(tmpPath.getDirectory(), tmpPath.getFile()):null;
            } catch (Exception e) {  Logger.error("Error al abrir foto: {}", e.getMessage());  }
            return retFile;
        }
        
//        public static void save(String ruta, String nombre){
//            String path=ruta+(ruta.endsWith(OS.getSeparador())?"":OS.getSeparador())+nombre;
//            if(!Disk.isExist(path)) Disk.createFolder(path);
//            try (InputStream tmp = Imagen.class.getResourceAsStream("/Images/usr.png")) {
//                if(tmp!= null)
//                    Files.copy(tmp, Path.of(path), StandardCopyOption.REPLACE_EXISTING);
//                Logger.info("Imagen guardada en ..: {}", ruta+(ruta.endsWith(OS.getSeparador())?"":OS.getSeparador())+nombre);
//            } catch(IOException e) {
//                Logger.error(e.getMessage(), "Error al escribir la Imagen en disco: "); }
//            path=null;
//        }
//                    || pathOrigen.endsWith("#@.png") 
//                    || pathOrigen.endsWith("#@.jpeg")
//                    || pathOrigen.endsWith("#@.jpg")){
        public static void extractImage(String nombre){
            save("#@."+cfg.PATH_INTERNAL+nombre, nombre);
        }
                    
        
        

        public static void save(String path, String nombre){
            String Origen=path.endsWith(Utils.OS.getSeparador())?path.substring(0, path.length()-1):path,
                   ext=!path.endsWith(Utils.OS.getSeparador())?path.substring(path.lastIndexOf(".")):"",
                   Destino=cfg.pathImagen()+Utils.OS.getSeparador()+nombre+ext;
                   if(Destino.contains(ext+ext)) Destino=Destino.substring(0, Destino.length()-ext.length());
            Disk.createFolder(cfg.pathImagen());
            try {
                if(path.endsWith(cfg.IMAGE_USER) || path.startsWith("#@.")){
                    if(path.startsWith("#@."))
                        Origen=Origen.substring(3);
                    else
                        Origen=cfg.PATH_INTERNAL+cfg.IMAGE_USER;
                    try (InputStream tmp = Imagen.class.getResourceAsStream(Origen)) {
                        if(tmp!=null){
                            Files.copy(tmp, Path.of(Destino), StandardCopyOption.REPLACE_EXISTING);
                            Logger.info("Imagen de '{}', guardada en .: {}", nombre, Destino);
                        }
                    }
                }else{
                    File destino=new File(Destino);
                    Files.copy(Path.of(Origen), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Logger.info("Imagen de '{}', guardada en .: {}", nombre, Destino);
                }
            } catch(IOException e) {
                Logger.error(e.getMessage(), "Error al escribir la Imagen en disco: ");
            }
        }   
        
        public static boolean save(InputStream imagen, String nombre, String contenido){
            //String path=OS.getWorkPath()+OS.getSeparador()+"imagen",
            String path=cfg.pathImagen(),
                   //file=nombre+"."+contenido.substring(contenido.lastIndexOf("/")+1),
                   nameFile=path+OS.getSeparador()+nombre;
            long ret=0;
            try {
                if(!Disk.isExist(path)) Disk.createFolder(path);
                ret=Files.copy(imagen, Path.of(nameFile), StandardCopyOption.REPLACE_EXISTING);
                Logger.info("Imagen guardada en ..: {}", nameFile);
            } catch (IOException e) {
                Logger.error(e.getMessage(), "Error al escribir el Imagen en disco: "); }
            return ret!=0;
        }
        
    }
    
    public final class Disk {

        
    /**
     * @param ruta      Ruta a evaluar
     * @return          Devuelve la existencia del archivo/directorio
     */
        public static boolean isExist(String ruta){
            return (new File(ruta)).exists();
        }
    /**
     * @param path      Ruta a ser analizada
     * @return          Devuelve un vector donde:
     *                  [PATHFILE] o [0] contiene la direccion
     *                  [NAMEFILE] o [1] contiene el nombre
     *                  [EXTFILE] o [2] contiene la extension
     */    
        public static String[] splitPath(String path){
            int posS=path.lastIndexOf(File.separator), posE=path.lastIndexOf(".");
            if(posS==-1) posS=path.length()-1;
            if(posE==-1) posE=path.length();
            String[] ret={path.substring(0, posS+1), path.substring(posS+1, posE), path.substring(posE)};
            return ret;
        }
    /**
     * @param file      Archivo a convertir
     * @return          Devuelve una imagen compatible con java
     */
        public static ImageIcon toImageIcon(File file) {
            return file!=null ? new ImageIcon(file.getAbsolutePath()): null;
        }    
    /// @return      Devuelve el objeto que representa la imagen seleccionada
        public static File openJVM() {
            File retFile=null;
            JFileChooser buscar=new JFileChooser();
            if((buscar.showOpenDialog(null))==JFileChooser.APPROVE_OPTION){
                try {
                    retFile=buscar.getSelectedFile();
                } catch(Exception e) {  Logger.error("Error al abrir foto: {}", e.getMessage());  }
            }
            return retFile;
        }

        public static File openFileOS(){
            return openFileOS("");
        }
        public static File openFileOS(String filtro){
            int pos=0;
            if(filtro!=null)
                if((pos=filtro.lastIndexOf("."))==-1)
                    filtro="*.*";
                else
                    filtro=filtro.substring(pos);
            else
                filtro="*.*";
            return openFileOS(filtro, "Selecciona un archivo");
        }
    /**
     * @param filtro    Extension de los archivos que unicamente se mostraran
     *                  la extension debe tener un punto al comienzo
     *                  si se omite el punto se mostrara todos los archivos
     * @param titulo    Titulo de ventana
     * @return          Devuelve el objeto que representa el archivo seleccionado
     */    
        public static File openFileOS(String filtro, String titulo){
            File retFile=null;
            Logger.debug(filtro);
            FileDialog tmpPath = new FileDialog((Frame) null, titulo, FileDialog.LOAD);
            if(isWin())
                if(!filtro.contains("."))
                    tmpPath.setFile("*.*");
                else
                    tmpPath.setFile("*"+filtro);
            else if(filtro.contains(".") && !"*.*".equals(filtro))
                    tmpPath.setFilenameFilter((dir, name) -> {
                        String lowerCaseName = name.toLowerCase();
                        return lowerCaseName.endsWith(filtro); });
            tmpPath.setVisible(true);
            try {
                retFile=tmpPath.getDirectory()!=null && tmpPath.getFile()!=null?new File(tmpPath.getDirectory(), tmpPath.getFile()):null;
            } catch (Exception e) {  Logger.error("Error al abrir archivo desde SO : {}", e.getMessage());  }
            return retFile;
        }
    /**
     * @return      Devuelve la ruta del directorio seleccionado
     */
        public static String openPathJVM(){
            String retPath="";
            JFileChooser tmpPath=new JFileChooser();
            tmpPath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            tmpPath.setAcceptAllFileFilterUsed(false);
            if(tmpPath.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
                if(tmpPath!=null)
                    retPath=(tmpPath.getSelectedFile()).getAbsolutePath();
            return retPath;
        }

        public static String openPathOS(){
            return openPathOS(null);
        }
    /**
     * @param titulo    Titulo de la selector de directorios
     * @return          Devuelve la ruta del directorio seleccionado
     */    
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
        }
        public static void save(File file){
            save(file, "", "");
        }
        public static void save(File file, String nombre){
            save(file, "", nombre);
        }
    /**
     * @param file      Archivo a guardar
     * @param nombre    Nombre de archivo guardado
     * @param ruta      Directorio donde se guardara el archivo
     */
        public static void save(File file, String ruta, String nombre){
            if(ruta.isEmpty()) ruta=getHomePath()+getSeparador()+"."+"edEXT"+getSeparador()+"imagenes";//getPaqueteName();
            try {
                File path=new File(ruta);
                if(!path.exists()) path.mkdirs();
                String[] tmp=splitPath(file.getAbsolutePath());
                if(nombre.isEmpty()) nombre=tmp[NAMEFILE];
                File tmpFile=new File(path, nombre+tmp[EXTFILE]);
                Files.copy(file.toPath(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Logger.debug("Archivo guardado en ..: {}", tmpFile.getAbsolutePath());
            } catch(IOException e){  Logger.error("Error al guardar Archivo ..: " + e.getMessage());  }
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
            return Utils.class.getPackageName();
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
            } catch (IOException e){  Logger.debug("Error abriendo archivo de configuracion : {}", e.getMessage());  }
            return tmp.toArray(CfgDB[]::new);
        }


    public static void saveCfgPath(String clave, String valor){
        saveCfgPath(clave, valor, true);
    }
    
    public static void saveCfgPath(String clave, String valor, boolean modo){
        createFolder(cfg.PATH);
        try (BufferedWriter tmpLog = new BufferedWriter(new FileWriter(cfg.PATH_FILE, modo))) {
            // mas adelante tengo que agregar busqueda y remplazo del parametro
            if("\n".equals(clave))
                tmpLog.write("\n"+valor);
            else if("#".equals(clave))
                tmpLog.write(clave+valor);
            else
                tmpLog.write(clave+" : "+valor);
            Logger.debug("Archivo CFG guardado en ..: {}",cfg.PATH_FILE);
        } catch (IOException e) {
            Logger.error(e.getMessage(),"Error al guardar CFG... ");
        }
    }// en faltan los demas parametros
        
       //******************************************************** 

    }

    public final class OS {

        public static final String SymHome="~";
        
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

        public static boolean isWin(){
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

    }
    
    public static class Mouse {
        private int mfX;
        private int mfY;

        public void MousePressed(java.awt.event.MouseEvent evt){
            mfX = evt.getX();
            mfY = evt.getY();
        }

        public void MouseDragged(MouseEvent evt){
            JFrame Obj= evt.getSource() instanceof JFrame ? (JFrame)evt.getSource() : (JFrame)SwingUtilities.getWindowAncestor(evt.getComponent());
            Point point = MouseInfo.getPointerInfo().getLocation();
            Obj.setLocation(point.x - mfX, point.y - mfY);
        }

        public void MouseContenedorDragged(MouseEvent evt){
            //JInternalFrame Obj= evt.getSource() instanceof JInternalFrame ? (JInternalFrame)evt.getSource() : (JInternalFrame)SwingUtilities.getWindowAncestor(evt.getComponent());
            
            
            Point point = MouseInfo.getPointerInfo().getLocation();
            GUI.getContenedor(evt.getComponent()).setLocation(point.x - mfX, point.y - mfY);
        }
    }
}