package edu.edext.presentacion;

import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.tools.Utils;
import edu.edext.tools.cfg;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.tinylog.Logger;

public class AltaCurso extends javax.swing.JInternalFrame {

    private final Utils.Mouse mouse=new Utils.Mouse(); 
    private IControlador control;
    private File imagen;

    public AltaCurso() {
        initComponents();
        cargarDatosIniciales();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        institutoLabel = new javax.swing.JLabel();
        institutoSeleccion = new javax.swing.JComboBox<>();
        descripcionLabel = new javax.swing.JLabel();
        duracionLabel = new javax.swing.JLabel();
        textoDuracion = new javax.swing.JTextField();
        cantidadHorasLabel = new javax.swing.JLabel();
        textoCantidadHoras = new javax.swing.JTextField();
        textoDescripcion = new javax.swing.JTextField();
        creditosLabel = new javax.swing.JLabel();
        urlLabel = new javax.swing.JLabel();
        fechaAltaLabel = new javax.swing.JLabel();
        textoCreditos = new javax.swing.JTextField();
        textoURL = new javax.swing.JTextField();
        previasLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaCategorias = new javax.swing.JList<>();
        buttonGuardar = new javax.swing.JButton();
        buttonCancelar = new javax.swing.JButton();
        nombreLabel = new javax.swing.JLabel();
        textoNombre = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        listaPrevias = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jBfechaAlta = new javax.swing.JButton();
        textoFechaAlta = new javax.swing.JFormattedTextField();
        jDfechaAlta = new com.toedter.calendar.JDateChooser();
        jCUnitDuracion = new javax.swing.JComboBox<>();
        lblImagen = new javax.swing.JLabel();
        btnAgregarImagen = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Alta Curso");
        setMinimumSize(new java.awt.Dimension(635, 500));
        setPreferredSize(new java.awt.Dimension(635, 500));
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

        institutoLabel.setText("Instituto .:");
        institutoLabel.setFocusable(false);
        institutoLabel.setRequestFocusEnabled(false);
        getContentPane().add(institutoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 19, -1, -1));

        institutoSeleccion.setNextFocusableComponent(textoNombre);
        institutoSeleccion.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                institutoSeleccionPopupMenuWillBecomeVisible(evt);
            }
        });
        institutoSeleccion.addActionListener(this::institutoSeleccionActionPerformed);
        getContentPane().add(institutoSeleccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 12, 187, 33));

        descripcionLabel.setText("Descripcion .:");
        descripcionLabel.setFocusable(false);
        descripcionLabel.setRequestFocusEnabled(false);
        getContentPane().add(descripcionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 64, -1, -1));

        duracionLabel.setText("Duracion .:");
        duracionLabel.setFocusable(false);
        duracionLabel.setRequestFocusEnabled(false);
        getContentPane().add(duracionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 106, -1, -1));

        textoDuracion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        textoDuracion.setNextFocusableComponent(textoFechaAlta);
        getContentPane().add(textoDuracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 103, 60, -1));

        cantidadHorasLabel.setText("Cantidad de Horas .:");
        cantidadHorasLabel.setFocusable(false);
        cantidadHorasLabel.setRequestFocusEnabled(false);
        getContentPane().add(cantidadHorasLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 148, -1, -1));

        textoCantidadHoras.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        textoCantidadHoras.setNextFocusableComponent(textoCreditos);
        getContentPane().add(textoCantidadHoras, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 145, 60, -1));

        textoDescripcion.setNextFocusableComponent(textoURL);
        getContentPane().add(textoDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 61, 163, -1));

        creditosLabel.setText("Creditos Asociados .:");
        creditosLabel.setFocusable(false);
        creditosLabel.setRequestFocusEnabled(false);
        getContentPane().add(creditosLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 190, -1, -1));

        urlLabel.setText("URL Asociada .:");
        urlLabel.setFocusable(false);
        urlLabel.setRequestFocusEnabled(false);
        getContentPane().add(urlLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 63, -1, -1));

        fechaAltaLabel.setText("Fecha Alta .:");
        fechaAltaLabel.setFocusable(false);
        fechaAltaLabel.setRequestFocusEnabled(false);
        getContentPane().add(fechaAltaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 233, -1, -1));

        textoCreditos.setNextFocusableComponent(listaCategorias);
        getContentPane().add(textoCreditos, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 187, 75, -1));

        textoURL.setNextFocusableComponent(textoDuracion);
        getContentPane().add(textoURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 60, 210, -1));

        previasLabel.setText("Previas ....: ");
        previasLabel.setFocusable(false);
        previasLabel.setRequestFocusEnabled(false);
        getContentPane().add(previasLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 250, -1, -1));

        listaCategorias.setNextFocusableComponent(listaPrevias);
        listaCategorias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                listaCategoriasFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(listaCategorias);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 95, 210, 140));

        buttonGuardar.setText("Guardar");
        buttonGuardar.addActionListener(this::buttonGuardarActionPerformed);
        getContentPane().add(buttonGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 423, 110, -1));

        buttonCancelar.setText("Cancelar");
        buttonCancelar.addActionListener(this::buttonCancelarActionPerformed);
        getContentPane().add(buttonCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 423, -1, -1));

        nombreLabel.setText("Nombre Curso .:");
        nombreLabel.setFocusable(false);
        nombreLabel.setRequestFocusEnabled(false);
        getContentPane().add(nombreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 22, -1, -1));

        textoNombre.setNextFocusableComponent(textoDescripcion);
        getContentPane().add(textoNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 202, -1));

        listaPrevias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                listaPreviasFocusLost(evt);
            }
        });
        jScrollPane4.setViewportView(listaPrevias);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 245, 230, 165));

        jLabel1.setText("Categorias ..:");
        jLabel1.setFocusable(false);
        jLabel1.setRequestFocusEnabled(false);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, -1, -1));

        jBfechaAlta.setText("......");
        jBfechaAlta.setNextFocusableComponent(textoCantidadHoras);
        jBfechaAlta.addActionListener(this::jBfechaAltaActionPerformed);
        getContentPane().add(jBfechaAlta, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 230, 32, 19));

        textoFechaAlta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        textoFechaAlta.setNextFocusableComponent(jBfechaAlta);
        textoFechaAlta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textoFechaAltaFocusLost(evt);
            }
        });
        textoFechaAlta.addActionListener(this::textoFechaAltaActionPerformed);
        getContentPane().add(textoFechaAlta, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 99, -1));

        jDfechaAlta.setDateFormatString("d M y");
        jDfechaAlta.setFocusable(false);
        jDfechaAlta.setMaxSelectableDate(new java.util.Date(1798776104000L));
        jDfechaAlta.setOpaque(false);
        jDfechaAlta.setRequestFocusEnabled(false);
        jDfechaAlta.setVerifyInputWhenFocusTarget(false);
        jDfechaAlta.addPropertyChangeListener(this::jDfechaAltaPropertyChange);
        getContentPane().add(jDfechaAlta, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 238, 5, 5));

        jCUnitDuracion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "dias", "semanas", "meses", "años" }));
        getContentPane().add(jCUnitDuracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(163, 100, 110, 30));

        lblImagen.setBackground(new java.awt.Color(102, 204, 255));
        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        lblImagen.setFocusable(false);
        lblImagen.setRequestFocusEnabled(false);
        getContentPane().add(lblImagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 160, 160));

        btnAgregarImagen.setText("<< Agregar Imagen");
        btnAgregarImagen.addActionListener(this::btnAgregarImagenActionPerformed);
        getContentPane().add(btnAgregarImagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 330, 160, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void institutoSeleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_institutoSeleccionActionPerformed
        if(institutoSeleccion.getSelectedItem()!=null)
            buttonGuardar.setEnabled(true);
        else
            buttonGuardar.setEnabled(false);
    }//GEN-LAST:event_institutoSeleccionActionPerformed

    private void buttonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_buttonCancelarActionPerformed

    private void buttonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_buttonGuardarActionPerformed

    private void jBfechaAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBfechaAltaActionPerformed
        jDfechaAlta.getCalendarButton().doClick();
    }//GEN-LAST:event_jBfechaAltaActionPerformed

    private void institutoSeleccionPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_institutoSeleccionPopupMenuWillBecomeVisible
        refreshInstitutos();
    }//GEN-LAST:event_institutoSeleccionPopupMenuWillBecomeVisible

    private void listaCategoriasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listaCategoriasFocusLost
        if(listaCategorias.getSelectedValuesList().isEmpty()) refreshCategorias();
    }//GEN-LAST:event_listaCategoriasFocusLost

    private void listaPreviasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listaPreviasFocusLost
        if(listaPrevias.getSelectedValuesList().isEmpty()) refreshPrevias();
    }//GEN-LAST:event_listaPreviasFocusLost

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        mouse.MouseContenedorDragged(evt);
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        mouse.MousePressed(evt);
    }//GEN-LAST:event_formMousePressed

    private void textoFechaAltaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textoFechaAltaFocusLost
        if(!Utils.GUI.isEmpty(textoFechaAlta))
            jDfechaAlta.setDate(Utils.Cast.valudf(textoFechaAlta.getText()));
    }//GEN-LAST:event_textoFechaAltaFocusLost

    private void textoFechaAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textoFechaAltaActionPerformed
        if(!Utils.GUI.isEmpty(textoFechaAlta))
            jDfechaAlta.setDate(Utils.Cast.valudf(textoFechaAlta.getText()));
    }//GEN-LAST:event_textoFechaAltaActionPerformed

    private void jDfechaAltaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDfechaAltaPropertyChange
        if(jDfechaAlta.getDate()!=null)
            textoFechaAlta.setText(Utils.Fecha.assignFormato(jDfechaAlta.getDate()));
    }//GEN-LAST:event_jDfechaAltaPropertyChange

    private void btnAgregarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarImagenActionPerformed
        File tmp=Utils.Imagen.openSO();
        if(tmp!=null){
            imagen=tmp;
            lblImagen.setIcon(
                Utils.Imagen.resize(
                    lblImagen.getWidth(),
                    lblImagen.getHeight(),
                    Utils.Imagen.loadSO(
                        imagen.getAbsolutePath())));
        }
    }//GEN-LAST:event_btnAgregarImagenActionPerformed

    private void cargarDatosIniciales() {
        Utils.GUI.soloNumeros(textoCantidadHoras);
        Utils.GUI.assignFormato(textoFechaAlta);
        Utils.GUI.soloNumeros(textoCreditos);
        Utils.GUI.soloNumeros(textoDuracion);
        refreshInstitutos();
        buttonGuardar.setEnabled(institutoSeleccion.getSelectedIndex() >= 0);
        refreshCategorias();
        refreshPrevias();
    }

    private void refreshInstitutos(){
        control = Fabrica.getInstance().getIControlador();
        try {
            Object previo = institutoSeleccion.getSelectedItem();
            institutoSeleccion.removeAllItems();
            List<DtInstituto> institutos = control.listarInstitutos();
            for (DtInstituto i : institutos)
                institutoSeleccion.addItem(i.getNombre());
            if (previo != null) {
                institutoSeleccion.setSelectedItem(previo);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage(), "Error al cargar institutos ");
            error("Error al cargar Institutos : " + e.getMessage(), "Error");
        }
    }

    private void refreshPrevias(){
        control = Fabrica.getInstance().getIControlador();
        try {
            DefaultListModel<String> modeloLista = new DefaultListModel<>();
            List<String> cursos = control.listarNombresCursos();
            for (String c : cursos) modeloLista.addElement(c);
            listaPrevias.setModel(modeloLista);
        } catch (Exception e) {
            Logger.error(e.getMessage(), "Error al cargar Previas ");
            error("Error al cargar Previas : " + e.getMessage(), "Error");
        }
    }

    private void refreshCategorias(){
        control = Fabrica.getInstance().getIControlador();
        try {
            DefaultListModel<String> listCategorias = new DefaultListModel<>();
            List<DtCategoria> categorias = control.listarCategorias();
            for (DtCategoria c : categorias) listCategorias.addElement(c.getNombre());
            listaCategorias.setModel(listCategorias);
        } catch (Exception e) {
            Logger.error(e.getMessage(), "Error al cargar Categorias ");
            error("Error al cargar Categorias : " + e.getMessage(), "Error");
        }
    }
    
    private void alert(String message){
        alert(message, "Advertencia");
    }
    
    private void alert(String message, String titulo){
        JOptionPane.showMessageDialog(this, message, titulo, JOptionPane.WARNING_MESSAGE);
    }
    
    private void error(String message, String titulo){
        JOptionPane.showMessageDialog(this, message, titulo, JOptionPane.ERROR_MESSAGE);
    }
    
    private void newCurso(){
        textoNombre.setText("");
        textoDescripcion.setText("");
        textoCantidadHoras.setText("");
        textoCreditos.setText("");
        textoDuracion.setText("");
        textoFechaAlta.setText("");
        textoURL.setText("");
        lblImagen.setIcon(null);
        imagen=null;
        cargarDatosIniciales();
    }
    
    private void guardar(){
        String nombre = textoNombre.getText().trim();
        String descripcion = textoDescripcion.getText().trim();
        String duracion = textoDuracion.getText().trim();
        String url = textoURL.getText().trim();
        String horas=textoCantidadHoras.getText().trim();
        String creditos=textoCreditos.getText().trim();
        String fecha=textoFechaAlta.getText();  //  campo formateado no necesita trim()
        String instSeleccionado = (String) institutoSeleccion.getSelectedItem();
        List<String> previas=listaPrevias.getSelectedValuesList(),
                     categorias=listaCategorias.getSelectedValuesList();
        if(Utils.Validate.isEmpty(nombre)){
            alert("Ingrese nombre del curso");
            textoNombre.requestFocus();
            return; }
        if(Utils.Validate.isEmpty(descripcion))
            if(confirm("Desean continuar sin descripcion")!=0){
                textoDescripcion.requestFocus();
                return; }
        if(Utils.Validate.isEmpty(url)){
            alert("Ingrese link del curso");
            textoURL.requestFocus();
            return; }
        if(Utils.Cast.valueof(duracion)<=0){
            alert("Ingrese duracion del curso");
            textoDuracion.requestFocus();
            return; }
        if(!Utils.Validate.isFecha(fecha)){
            alert("Ingrese fecha de alta");
            jDfechaAlta.getCalendarButton().doClick();
            return; }
        if(Utils.Cast.valueof(horas)<=0){
            alert("Ingrese cantidad de horas");
            textoCantidadHoras.requestFocus();
            return; }
        if(Utils.Cast.valueof(creditos)<=0){
            alert("Ingrese creditos del curso");
            textoCreditos.requestFocus();
            return; }
        if(Utils.Validate.isEmpty(categorias))
            if(confirm("Desea continuar sin asignar categoria")!=0)
                return;
        if(Utils.Validate.isEmpty(previas))
            if(confirm("Desea continuar sin asignar previas")!=0)
                return;
        String nombreImagen = cfg.IMAGE_USER;
        if (imagen != null) {
            int dot = imagen.getName().lastIndexOf(".");
            String ext = dot >= 0 ? imagen.getName().substring(dot) : ".png";
            nombreImagen = "Curso." + nombre + ext;
        }
        DtCurso dt = new DtCurso(nombre, descripcion, duracion+" "+(String)jCUnitDuracion.getSelectedItem(), 
                    Utils.Cast.valueof(horas), Utils.Cast.valueof(creditos), url, Utils.Cast.valudf(fecha), 
                    new DtInstituto(instSeleccionado), new HashSet<>(previas), new HashSet<>(categorias), 
                    nombreImagen);
        try {
            control.altaCurso(dt, instSeleccionado);
            if (imagen != null) {
                Utils.Imagen.save(imagen.getAbsolutePath(), "Curso." + nombre);
            }
            Logger.info("El curso '" + nombre + "' fue creado exitosamente.");
            JOptionPane.showMessageDialog(this, "El curso '" + nombre + "' fue creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            newCurso();
        } catch (Exception ex) {
            Logger.error(ex.getMessage(), "Error al crear curso : {}", nombre);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en el Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   private int confirm(String mensaje){
        return JOptionPane.showOptionDialog(this, mensaje, "Confirme acción", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarImagen;
    private javax.swing.JButton buttonCancelar;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JLabel cantidadHorasLabel;
    private javax.swing.JLabel creditosLabel;
    private javax.swing.JLabel descripcionLabel;
    private javax.swing.JLabel duracionLabel;
    private javax.swing.JLabel fechaAltaLabel;
    private javax.swing.JLabel institutoLabel;
    private javax.swing.JComboBox<String> institutoSeleccion;
    private javax.swing.JButton jBfechaAlta;
    private javax.swing.JComboBox<String> jCUnitDuracion;
    private com.toedter.calendar.JDateChooser jDfechaAlta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JList<String> listaCategorias;
    private javax.swing.JList<String> listaPrevias;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JLabel previasLabel;
    private javax.swing.JTextField textoCantidadHoras;
    private javax.swing.JTextField textoCreditos;
    private javax.swing.JTextField textoDescripcion;
    private javax.swing.JTextField textoDuracion;
    private javax.swing.JFormattedTextField textoFechaAlta;
    private javax.swing.JTextField textoNombre;
    private javax.swing.JTextField textoURL;
    private javax.swing.JLabel urlLabel;
    // End of variables declaration//GEN-END:variables
}