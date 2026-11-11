package edu.edext;

import java.util.List;
import org.tinylog.Logger;
import edu.edext.tools.cfg;
import java.io.IOException;
import java.util.ArrayList;
import edu.edext.tools.utils;
import java.net.ServerSocket;
import javax.swing.UIManager;
import javax.swing.JTextField;
import java.awt.event.KeyEvent;
import edu.edext.tools.utils.Mouse;
import javax.swing.UnsupportedLookAndFeelException;
import static javax.swing.SwingUtilities.invokeLater;

/**
 *
 * @author vdraco
 */
public class Test extends javax.swing.JFrame {
//    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Test.class.getName());
    private final String SHARED="Compartir en :", NOSHARED="No Compartir :";
    private final List<ServerSocket> listSockets=new ArrayList<>();
    private final int limitMin=1024, limitMax=65535;
    private final Mouse mouse=new Mouse();
    private ServerSocket denyPort=null;
    enum status{ OFF, BUSY, FREE; }
    private String sharedPort=null;
    private boolean reposo=true;
    ProcessBuilder proc;
    Process pid;

    // 0 al 1023      Puertos Reservados
    // 1024 al 49151  Puertos Registrados
    // 49152 al 65535 Puertos Libres
    /**
     * Creates new form Test
     */
    public Test() {
        initComponents();
        utils.GUI.soloNumeros(jTPort);
        utils.GUI.soloNumeros(jTDeny1);
        utils.GUI.soloNumeros(jTDeny2);
        statusIcon(status.OFF);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTPort = new javax.swing.JTextField();
        jBCheck = new javax.swing.JButton();
        jLStatus = new javax.swing.JLabel();
        jTDeny2 = new javax.swing.JTextField();
        jTDeny1 = new javax.swing.JTextField();
        jTBDeny = new javax.swing.JToggleButton();
        jTPath = new javax.swing.JTextField();
        jBFile = new javax.swing.JButton();
        jBShared = new javax.swing.JButton();
        jBAssign = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(250, 290));
        setPreferredSize(new java.awt.Dimension(250, 290));
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTPort.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTPort.addCaretListener(this::jTPortCaretUpdate);
        jTPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTPortFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTPortFocusLost(evt);
            }
        });
        jTPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTPortKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTPortKeyReleased(evt);
            }
        });
        getContentPane().add(jTPort, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 34, 65, -1));

        jBCheck.setText("Verificar");
        jBCheck.addActionListener(this::jBCheckActionPerformed);
        getContentPane().add(jBCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 28, 88, -1));

        jLStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 30, 36, 36));

        jTDeny2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTDeny2.setText("1024");
        jTDeny2.addCaretListener(this::jTDeny2CaretUpdate);
        jTDeny2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTDeny2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTDeny2FocusLost(evt);
            }
        });
        jTDeny2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDeny2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTDeny2KeyReleased(evt);
            }
        });
        getContentPane().add(jTDeny2, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 150, 65, -1));

        jTDeny1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTDeny1.setText("1024");
        jTDeny1.addCaretListener(this::jTDeny1CaretUpdate);
        jTDeny1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTDeny1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTDeny1FocusLost(evt);
            }
        });
        jTDeny1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTDeny1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTDeny1KeyReleased(evt);
            }
        });
        getContentPane().add(jTDeny1, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 110, 65, -1));

        jTBDeny.setText("Denegar");
        jTBDeny.addActionListener(this::jTBDenyActionPerformed);
        getContentPane().add(jTBDeny, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 110, 88, 64));

        jTPath.addCaretListener(this::jTPathCaretUpdate);
        getContentPane().add(jTPath, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 180, -1));

        jBFile.setText("...");
        jBFile.addActionListener(this::jBFileActionPerformed);
        getContentPane().add(jBFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 190, 30, 20));

        jBShared.setEnabled(false);
        jBShared.addActionListener(this::jBSharedActionPerformed);
        getContentPane().add(jBShared, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 209, 30));

        jBAssign.setText("↓↓↓");
        jBAssign.setEnabled(false);
        jBAssign.addActionListener(this::jBAssignActionPerformed);
        getContentPane().add(jBAssign, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 70, 170, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTPortKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTPortKeyPressed
        utils.GUI.numericSlider(jTPort, evt, limitMin, limitMax);
    }//GEN-LAST:event_jTPortKeyPressed

    private void jTDeny2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDeny2KeyPressed
        utils.GUI.numericSlider(jTDeny2, evt, limitMin, limitMax);
    }//GEN-LAST:event_jTDeny2KeyPressed

    private void jTDeny1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDeny1KeyPressed
        utils.GUI.numericSlider(jTDeny1, evt, limitMin, limitMax);
    }//GEN-LAST:event_jTDeny1KeyPressed

    private void jBCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCheckActionPerformed
        check();
    }//GEN-LAST:event_jBCheckActionPerformed

    private void jTPortCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTPortCaretUpdate
        if(!reposo) statusIcon(status.OFF);
    }//GEN-LAST:event_jTPortCaretUpdate

    private void jTPortFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTPortFocusGained
        validateNumero(jTPort);
        if(!reposo) statusIcon(status.OFF);
        jTPort.selectAll();
    }//GEN-LAST:event_jTPortFocusGained

    private void jTPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTPortFocusLost
        validateNumero(jTPort);
        if(!reposo) statusIcon(status.OFF);
    }//GEN-LAST:event_jTPortFocusLost

    private void jTBDenyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBDenyActionPerformed
        try {
            denySocket(); }
        catch (IOException ex) { Logger.error(ex); }
    }//GEN-LAST:event_jTBDenyActionPerformed

    private void jTDeny1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDeny1FocusLost
        validateNumero(jTDeny1);
    }//GEN-LAST:event_jTDeny1FocusLost

    private void jTDeny2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDeny2FocusLost
        validateNumero(jTDeny2);
    }//GEN-LAST:event_jTDeny2FocusLost

    private void jBFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBFileActionPerformed
        String path=utils.Disk.openPathOS();
        if(!utils.Validate.isEmpty(path))
            jTPath.setText(path);
    }//GEN-LAST:event_jBFileActionPerformed

    private void jBSharedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSharedActionPerformed
        if(jBShared.getText().contains(SHARED)){
            jTPath.setEditable(false);
            jBFile.setEnabled(false);
            shared();
            jBShared.setText(NOSHARED+sharedPort); }
        else{
            jBShared.setText("");
            finnaly();
            sharedPort=null;
            jBShared.setEnabled(false);
            jTPath.setEditable(true);
            jBFile.setEnabled(true); }
    }//GEN-LAST:event_jBSharedActionPerformed

    private void jBAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAssignActionPerformed
        sharedPort=jTPort.getText();
        jBShared.setText(SHARED+sharedPort);
        if(utils.Validate.isEmpty(jTPath.getText()))
            jBShared.setEnabled(false);
        else
            jBShared.setEnabled(true);
    }//GEN-LAST:event_jBAssignActionPerformed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        mouse.MousePressed(evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        mouse.MouseDragged(evt);
    }//GEN-LAST:event_formMouseDragged

    private void jTPathCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTPathCaretUpdate
        if(!utils.Validate.isEmpty(jTPath.getText()) && sharedPort!=null)
            jBShared.setEnabled(true);
        else
            jBShared.setEnabled(false);
    }//GEN-LAST:event_jTPathCaretUpdate

    private void jTDeny1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDeny1FocusGained
        jTDeny1.selectAll();
    }//GEN-LAST:event_jTDeny1FocusGained

    private void jTDeny2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTDeny2FocusGained
        jTDeny2.selectAll();
    }//GEN-LAST:event_jTDeny2FocusGained

    private void jTPortKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTPortKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER || evt.getKeyCode()==KeyEvent.VK_SPACE)
            check();
    }//GEN-LAST:event_jTPortKeyReleased

    private void jTDeny1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDeny1KeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER || evt.getKeyCode()==KeyEvent.VK_SPACE)
            jTBDeny.doClick();
    }//GEN-LAST:event_jTDeny1KeyReleased

    private void jTDeny2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTDeny2KeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER || evt.getKeyCode()==KeyEvent.VK_SPACE)
            jTBDeny.doClick();
    }//GEN-LAST:event_jTDeny2KeyReleased

    private void jTDeny1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTDeny1CaretUpdate
        if(utils.Cast.valueof(jTDeny2.getText())<utils.Cast.valueof(jTDeny1.getText()))
            invokeLater(() -> jTDeny2.setText(jTDeny1.getText()));

    }//GEN-LAST:event_jTDeny1CaretUpdate

    private void jTDeny2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTDeny2CaretUpdate
        if(utils.Cast.valueof(jTDeny2.getText())<utils.Cast.valueof(jTDeny1.getText()))
            invokeLater(() -> jTDeny1.setText(jTDeny2.getText()));        
    }//GEN-LAST:event_jTDeny2CaretUpdate

    private void check(){
        if(!utils.Validate.isEmpty(jTPort.getText())){
            if(utils.OS.isPortFree(jTPort.getText())){
                statusIcon(status.FREE);
                jBAssign.setEnabled(true); }
            else{
                jBAssign.setEnabled(false);
                statusIcon(status.BUSY); } }
    }
    
    private void validateNumero(JTextField obj){
        int value=utils.Cast.valueof(obj.getText());
        if(value<limitMin) obj.setText(utils.Cast.valueOf(limitMin));
        else if(value>limitMax) obj.setText(utils.Cast.valueOf(limitMax));

    }
    
    private void denySocket() throws IOException{
        ServerSocket tmp;
        String Inicio=jTDeny1.getText(), Fin=jTDeny2.getText();
        int inicio, fin;
        if(utils.Validate.isEmpty(Inicio)) jTDeny1.setText("1024");
        if(utils.Validate.isEmpty(Fin)) jTDeny2.setText("1024");
        if(jTBDeny.isSelected()) jTBDeny.setText("Liberar");
        else jTBDeny.setText("Denegar");
        if(jTBDeny.isSelected())
            if(Inicio.equals(Fin))
                denyPort=utils.OS.denyPort(Inicio);
            else{
                inicio=utils.Cast.valueof(Inicio);
                fin=utils.Cast.valueof(Fin);
                for(int p=inicio; p<fin+1; p++)
                    if((tmp=utils.OS.denyPort(p))!=null)
                        listSockets.add(tmp); }
        else
            if(denyPort!=null){
                denyPort.close();
                denyPort=null; }
            else{
                for(ServerSocket socket : listSockets)
                    socket.close();
                listSockets.clear(); }
    }
    
    private void statusIcon(status status){
        if(status==status.OFF){
            reposo=true;
            assignIcon("/imagen/neutral.png"); }
        else if(status==status.FREE){
            reposo=false;
            assignIcon("/imagen/free.png"); }
        else if(status==status.BUSY){
            reposo=false;
            assignIcon("/imagen/busy.png"); }
    }
    
    private void assignIcon(String imagen){
        jLStatus.setIcon(utils.Imagen.resize(jLStatus.getWidth(), jLStatus.getHeight(), utils.Imagen.loadJVM(imagen)));
    }
    
    private void shared(){
        String path=cfg.PATH, puerto=sharedPort, union=" -d ", comando="python3 -m http.server ";
        comando=comando+puerto+union+jTPath.getText().trim();
        try {
            proc=new ProcessBuilder(comando.split(" "));
            proc.inheritIO();
            pid=proc.start(); }
        catch (IOException e) {
            System.err.println("Error al compartir archivos.");
            Logger.error(e, "Error al compartir archivos "); } 
    }
    
    private void finnaly(){
        pid.destroy();
        proc=null;
        pid=null;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            Logger.error(ex);
        }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Test().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBAssign;
    private javax.swing.JButton jBCheck;
    private javax.swing.JButton jBFile;
    private javax.swing.JButton jBShared;
    private javax.swing.JLabel jLStatus;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jTBDeny;
    private javax.swing.JTextField jTDeny1;
    private javax.swing.JTextField jTDeny2;
    private javax.swing.JTextField jTPath;
    private javax.swing.JTextField jTPort;
    // End of variables declaration//GEN-END:variables
}
