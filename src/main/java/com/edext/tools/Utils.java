package com.edext.tools;
//****************************** Libreria **************************************
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author vdraco
 */
public class Utils {
    

    public final class Fecha{
        
        public static String assignFormato(Date fecha){
            return fecha!=null?(new SimpleDateFormat("dd/MM/yy")).format(fecha):null;
        }
        
        public static String assignFormato(LocalDate fecha){
            return fecha!=null?DateTimeFormatter.ofPattern("dd/MM/yy").format(fecha):null;
        }

        public static boolean validate(String fecha){
            boolean ret=false;
            try{
                LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yy"));
                ret=true;
            } catch (Exception e) {}
            return ret;
        }

        public static boolean isCoherente(Date fecha1, Date fecha2){
            return fecha1.before(fecha2);
        }   
        
        public static boolean isCoherente(LocalDate fecha1, LocalDate fecha2){
            return fecha1.isBefore(fecha2);
        }   
        
    }

    public static LocalDate date2locatDate(Date fecha){
        return fecha!=null?fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate():null;
    }

    public static LocalDate str2LocalDate(String fecha){
        LocalDate ret=null;
        try{
            ret=LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yy"));
        } catch (Exception e) {}
        return ret;
    }

    public static int str2Int(String cadena){
        return Integer.parseInt(cadena);
    }

}
