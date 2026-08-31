package com.edext.tools;
//************************* Capa Presentacion **********************************
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author vdraco
 */
public class CreaProgFormaUtils {

    public void assignFormato(JFormattedTextField obj){
        try {
            MaskFormatter tmp = new MaskFormatter("##/##/##");
            tmp.setPlaceholderCharacter('-');
            obj.setFormatterFactory(new DefaultFormatterFactory(tmp));
        } catch (java.text.ParseException e){
            System.err.println("Error al formatear.....");
        }
    }    

//    public int confirm(JFrame Obj){
//        JButton botonSi = new JButton("         SI         ");
//        botonSi.setBackground(new Color(140, 6, 6)); // rojo pastel
//        botonSi.setForeground(Color.BLACK);
//        botonSi.setFont(new Font("Arial", Font.BOLD, 12));
//        JButton botonNo = new JButton("         NO         ");
//        botonNo.setBackground(new Color(41, 102, 49));  // verde pastel
//        botonNo.setForeground(Color.WHITE);
//        botonNo.setFont(new Font("Arial", Font.BOLD, 12));
//        Object[] opciones = { botonSi, botonNo };
//        botonSi=null;
//        botonNo=null;
//        return JOptionPane.showOptionDialog(
//                Obj, "La descripción se encuentra vacía,  ¿Desea continuar?", "Confirme acción", 
//                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);
//    }
    
//    public boolean inputValidate(LocalDate fecha1, LocalDate fecha2, JFormattedTextField obj){
//        boolean flag=true;
//        if(fecha1!=null){
//            if(fecha2!=null)
//                flag=Utils.Fecha.isCoherente(fecha2, fecha1);
//            obj.setText(Utils.Fecha.assignFormato(fecha1));
////            if(flag) alarm(false, titulo);
////            else alarm(true, titulo);
//        }
//        return flag;
//    }

    
}