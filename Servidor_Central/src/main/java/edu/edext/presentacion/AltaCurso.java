/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package edu.edext.presentacion;

import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class AltaCurso extends javax.swing.JInternalFrame {

    private IControlador control;

    public AltaCurso() {
        initComponents();
        
        // 1. Inicializar la conexión con la lógica
        Fabrica fabrica = Fabrica.getInstance();
        control = fabrica.getIControlador();
        
        // 2. Cargar los datos de la BD a la vista
        cargarDatosIniciales();
    }
    
    private void cargarDatosIniciales() {
        try {
            // Cargar Institutos en el ComboBox
            institutoSeleccion.removeAllItems();
            List<DtInstituto> institutos = control.listarInstitutos();
            for (DtInstituto i : institutos) {
                institutoSeleccion.addItem(i.getNombre());
            }

            // Cargar Cursos existentes en la Lista de Previas
            DefaultListModel<String> modeloLista = new DefaultListModel<>();
            List<String> cursos = control.listarNombresCursos();
            for (String c : cursos) {
                modeloLista.addElement(c);
            }
            listaPrevias.setModel(modeloLista);

            DefaultListModel<String> listCategorias = new DefaultListModel<>();
            List<DtCategoria> categorias = control.listarCategorias();
            for (DtCategoria c : categorias) listCategorias.addElement(c.getNombre());
            listaCategorias.setModel(listCategorias);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        textoFechaAlta = new javax.swing.JTextField();
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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Alta Curso");
        setMinimumSize(new java.awt.Dimension(690, 464));
        setPreferredSize(new java.awt.Dimension(650, 464));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        institutoLabel.setText("Instituto:");
        getContentPane().add(institutoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 19, -1, -1));

        institutoSeleccion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        institutoSeleccion.addActionListener(this::institutoSeleccionActionPerformed);
        getContentPane().add(institutoSeleccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 12, 187, 33));

        descripcionLabel.setText("Descripcion:");
        getContentPane().add(descripcionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 64, -1, -1));

        duracionLabel.setText("Duracion:");
        getContentPane().add(duracionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 106, -1, -1));

        textoDuracion.addActionListener(this::textoDuracionActionPerformed);
        getContentPane().add(textoDuracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 103, 182, -1));

        cantidadHorasLabel.setText("Cantidad de Horas:");
        getContentPane().add(cantidadHorasLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 148, -1, -1));
        getContentPane().add(textoCantidadHoras, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 145, 118, -1));

        textoDescripcion.addActionListener(this::textoDescripcionActionPerformed);
        getContentPane().add(textoDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 61, 163, -1));

        creditosLabel.setText("Creditos Asociados:");
        getContentPane().add(creditosLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 190, -1, -1));

        urlLabel.setText("URL Asociada:");
        getContentPane().add(urlLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(291, 84, -1, -1));

        fechaAltaLabel.setText("Fecha Alta:");
        getContentPane().add(fechaAltaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 120, -1, -1));
        getContentPane().add(textoCreditos, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 187, 108, -1));
        getContentPane().add(textoURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(397, 81, 210, -1));
        getContentPane().add(textoFechaAlta, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 120, 90, -1));

        previasLabel.setText("Previas del Curso: ");
        getContentPane().add(previasLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, -1, -1));

        listaCategorias.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listaCategorias);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 229, 170, 179));

        buttonGuardar.setText("Guardar");
        buttonGuardar.addActionListener(this::buttonGuardarActionPerformed);
        getContentPane().add(buttonGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(426, 387, -1, -1));

        buttonCancelar.setText("Cancelar");
        buttonCancelar.addActionListener(this::buttonCancelarActionPerformed);
        getContentPane().add(buttonCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(528, 387, -1, -1));

        nombreLabel.setText("Nombre Curso:");
        getContentPane().add(nombreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(291, 42, -1, -1));
        getContentPane().add(textoNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 40, 202, -1));

        listaPrevias.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(listaPrevias);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 160, 190, 204));

        jLabel1.setText("Categorias:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 229, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void institutoSeleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_institutoSeleccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_institutoSeleccionActionPerformed

    private void textoDuracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textoDuracionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textoDuracionActionPerformed

    private void textoDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textoDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textoDescripcionActionPerformed

    private void buttonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_buttonCancelarActionPerformed

    private void buttonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGuardarActionPerformed
        try {
            // Validaciones básicas
            String nombre = textoNombre.getText().trim();
            String descripcion = textoDescripcion.getText().trim();
            String duracion = textoDuracion.getText().trim();
            String url = textoURL.getText().trim();
            String instSeleccionado = (String) institutoSeleccion.getSelectedItem();

            if (nombre.isEmpty() || descripcion.isEmpty() || duracion.isEmpty() || url.isEmpty() || instSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parseo de números
            int horas = Integer.parseInt(textoCantidadHoras.getText().trim());
            int creditos = Integer.parseInt(textoCreditos.getText().trim());

            // Parseo de fecha (espera formato dd/MM/yyyy)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaAlta = sdf.parse(textoFechaAlta.getText().trim());

            // Obtener previas seleccionadas
            List<String> previasSeleccionadas = listaCategorias.getSelectedValuesList();

            // Armar Datatype y enviar a la lógica
            DtCurso dt = new DtCurso(nombre, descripcion, duracion, horas, creditos, url, fechaAlta, new DtInstituto(instSeleccionado), new HashSet<>(previasSeleccionadas));
            control.altaCurso(dt, instSeleccionado);

            JOptionPane.showMessageDialog(this, "El curso '" + nombre + "' fue creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Cerrar la ventana tras el éxito
            this.dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad de Horas y Créditos deben ser valores numéricos enteros.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice dd/MM/yyyy.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en el Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonGuardarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelar;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JLabel cantidadHorasLabel;
    private javax.swing.JLabel creditosLabel;
    private javax.swing.JLabel descripcionLabel;
    private javax.swing.JLabel duracionLabel;
    private javax.swing.JLabel fechaAltaLabel;
    private javax.swing.JLabel institutoLabel;
    private javax.swing.JComboBox<String> institutoSeleccion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList<String> listaCategorias;
    private javax.swing.JList<String> listaPrevias;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JLabel previasLabel;
    private javax.swing.JTextField textoCantidadHoras;
    private javax.swing.JTextField textoCreditos;
    private javax.swing.JTextField textoDescripcion;
    private javax.swing.JTextField textoDuracion;
    private javax.swing.JTextField textoFechaAlta;
    private javax.swing.JTextField textoNombre;
    private javax.swing.JTextField textoURL;
    private javax.swing.JLabel urlLabel;
    // End of variables declaration//GEN-END:variables
}