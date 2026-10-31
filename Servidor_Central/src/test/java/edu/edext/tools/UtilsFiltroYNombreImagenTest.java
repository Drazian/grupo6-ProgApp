package edu.edext.tools;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * soloNumeros/soloLetras tambien filtran el texto pegado o asignado con setText, y Imagen.save
 * guarda el archivo con el mismo nombre que el formulario registra en la base.
 */
public class UtilsFiltroYNombreImagenTest {

    @Test
    public void testSoloNumerosFiltraTextoPegado() throws Exception {
        JTextField sinEspacios = new JTextField();
        Utils.GUI.soloNumeros(sinEspacios);
        sinEspacios.setText("12a 3-4");
        assertEquals("1234", sinEspacios.getText());
        sinEspacios.setCaretPosition(sinEspacios.getText().length());
        sinEspacios.replaceSelection("x5"); // como un Ctrl+V al final del campo
        assertEquals("12345", sinEspacios.getText());

        JTextField conEspacios = new JTextField();
        Utils.GUI.soloNumeros(conEspacios, true);
        conEspacios.setText("09 12-34");
        assertEquals("09 1234", conEspacios.getText());

        // Texto null (setText(null)) no rompe el filtro
        ((AbstractDocument) conEspacios.getDocument()).replace(0, conEspacios.getText().length(), null, null);
        assertEquals("", conEspacios.getText());
    }

    @Test
    public void testSoloLetrasFiltraTextoPegado() throws Exception {
        JTextField campo = new JTextField();
        Utils.GUI.soloLetras(campo);
        campo.setText("Ana 2 María");
        assertEquals("AnaMaría", campo.getText());
        campo.getDocument().insertString(0, "x9", null);
        assertEquals("xAnaMaría", campo.getText());

        JTextArea conEspacios = new JTextArea();
        Utils.GUI.soloLetras(conEspacios, true);
        conEspacios.setText("Juan Pérez 3");
        assertEquals("Juan Pérez ", conEspacios.getText());
    }

    @Test
    public void testComponentesSinDocumentoFiltrableNoFallan() {
        // Un componente que no es de texto solo recibe el KeyListener
        JLabel etiqueta = new JLabel();
        Utils.GUI.soloNumeros(etiqueta);
        assertEquals(1, etiqueta.getKeyListeners().length);

        // Un JTextComponent cuyo documento no es AbstractDocument tampoco falla (no se puede filtrar)
        JTextField sinDocumentoFiltrable = org.mockito.Mockito.mock(JTextField.class);
        org.mockito.Mockito.when(sinDocumentoFiltrable.getDocument()).thenReturn(org.mockito.Mockito.mock(javax.swing.text.Document.class));
        assertDoesNotThrow(() -> Utils.GUI.soloNumeros(sinDocumentoFiltrable));

        // Con PlainDocument (el de JTextField) si se filtra
        JTextField campo = new JTextField(new PlainDocument(), "", 0);
        Utils.GUI.soloNumeros(campo);
        campo.setText("a1");
        assertEquals("1", campo.getText());
    }

    @Test
    public void testExtension() {
        assertEquals(".jpg", Utils.Imagen.extension("dir" + File.separator + "foto.jpg"));
        assertEquals(".png", Utils.Imagen.extension("juan.perez" + File.separator + "foto"));
    }

    @Test
    public void testSaveUsaElNombreRegistrado() throws Exception {
        File origen = new File("target", "origen-" + UUID.randomUUID() + ".png");
        Files.writeString(origen.toPath(), "imagen");
        String carpeta = cfg.pathImagen();
        String base = "juan-" + UUID.randomUUID().toString().substring(0, 8);

        // Nombre sin extension: se agrega la del archivo
        Utils.Imagen.save(origen.getPath(), base);
        assertTrue(new File(carpeta, base + ".png").exists());

        // Nombre que ya termina en la extension (un nickname "juan.png" registrado como "juan.png.png"):
        // se respeta tal cual, igual que en la base
        Utils.Imagen.save(origen.getPath(), base + ".png.png");
        assertTrue(new File(carpeta, base + ".png.png").exists());
    }
}
