/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.edext.logica;

import com.edext.datatypes.DtConsultaCurso;
import com.edext.datatypes.DtCurso;
import com.edext.datatypes.DtEdicion;
import com.edext.datatypes.DtInstituto;
import com.edext.datatypes.DtPrograma;
import com.edext.datatypes.DtUsuario;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author pipo
 */
public class ControladorTest {
    
    private IControlador ic;
    
    public ControladorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        ic = Fabrica.getInstance().getIControlador();
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of crearInstituto method, of class Controlador.
     */
//    @org.junit.jupiter.api.Test
//    public void testCrearInstituto() throws Exception {
//        System.out.println("crearInstituto");
//        String nombre = "1";
//        Controlador instance = new Controlador();
//        instance.crearInstituto(nombre);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

 
    @Test
    public void testCrearInstitutoExitoso() throws Exception{
        String nombreInstituto = "Instituto1";
        ic.crearInstituto(nombreInstituto);
        
        List<DtInstituto> institutos = ic.listarInstitutos();
        
        assertNotNull(institutos, "La lista de institutos no debe ser nula");
        
        boolean existeEnLista = institutos.stream().anyMatch(i -> i.getNombre().equals(nombreInstituto));
        
        assertTrue(existeEnLista, "El instituto creado deberia estar en la lista");
        
    }
    
    @Test
    public void testCrearInstitutoDuplicado() throws Exception{
        String nombreInstituto = "Instituto2";
        
        ic.crearInstituto(nombreInstituto);
  
        Exception excepcion = assertThrows(Exception.class, () -> {ic.crearInstituto(nombreInstituto);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());
    }
    
    @Test
    public void testEliminarInstitutoBasico() throws Exception{
        String nombreInstituto = "Instituto1";
                
        ic.eliminarInstituto(nombreInstituto);
        
        List<DtInstituto> institutos = ic.listarInstitutos();
        
        assertNotNull(institutos, "La lista de institutos no debe ser nula");
    
        boolean existeEnLista = institutos.stream().anyMatch(i -> i.getNombre().equals(nombreInstituto));
        
        assertFalse(existeEnLista, "El instituto creado deberia estar en la lista");
    
    }
    
    @Test
    public void testEliminarInstitutoFallido() throws Exception{
        String nombreInstituto = "InstitutoFallido";
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.eliminarInstituto(nombreInstituto);}, "Instituto inexistente");
        assertNotNull(excepcion.getMessage());
    }
    
    
}
