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
import com.edext.datatypes.TipoUsuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 * @author pipo
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    
    @Test
    @Order(1)
    public void testCargarDatosDePrueba() throws Exception {
        int resultado = assertDoesNotThrow(() -> ic.cargarDatosDePrueba(), 
        "La carga de datos de prueba no debería lanzar una excepción.");
        assertNotEquals(-1, resultado, "El método debería retornar un valor distinto de -1 en caso de éxito.");
    }
    
    @Test
    @Order(2)
    public void testCargarDatosDePruebaDuplicado() throws Exception {
        int resultadoSegundaCarga = assertDoesNotThrow(() -> ic.cargarDatosDePrueba());

        // Si la arquitectura idempotente/autogenerada devuelve 1 (o el código de retorno del controlador)
        assertEquals(1, resultadoSegundaCarga, "La segunda carga fue procesada devolviendo el código 1.");   
    }
    
    @Test
    public void testCrearInstituto() throws Exception{
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
        String nombreInstituto = "Instituto3";
        ic.crearInstituto(nombreInstituto);
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
    
    @Test
    public void testCrearUsuarioEstudiante() throws Exception{
        String nickname = "NICKNAME";
        List<String> institutos = new ArrayList<>();
        Date fecha = new Date();
        DtUsuario usuario = new DtUsuario(nickname, "email", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.ESTUDIANTE);
        ic.crearUsuario(usuario);
        
        List<DtUsuario> usuarios = ic.listarUsuarios();
        assertNotNull(usuarios, "La lista de institutos no debe ser nula");
        
        boolean existeEnLista = usuarios.stream().anyMatch(i -> i.getNickname().equals(nickname));
        
        assertTrue(existeEnLista, "El usuario creado deberia estar en la lista");
    }
    
    @Test
    public void testCrearUsuarioEstudianteDuplicadoPorNickName() throws Exception{
        String nickname = "NICKNAME2";
        List<String> institutos = new ArrayList<>();
        Date fecha = new Date();
        DtUsuario usuario = new DtUsuario(nickname, "email221", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.ESTUDIANTE);
        DtUsuario usuario2 = new DtUsuario(nickname, "email222", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.ESTUDIANTE);
        ic.crearUsuario(usuario);
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.crearUsuario(usuario2);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());
    }
    
    @Test
    public void testCrearUsuarioEstudianteDuplicadoPorCorreo() throws Exception{
        String nickname = "NICKNAME31";
        String nickname2 = "NICKNAME32";
        List<String> institutos = new ArrayList<>();
        Date fecha = new Date();
        DtUsuario usuario = new DtUsuario(nickname, "email333", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.ESTUDIANTE);
        DtUsuario usuario2 = new DtUsuario(nickname2, "email333", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.ESTUDIANTE);
        ic.crearUsuario(usuario);
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.crearUsuario(usuario2);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());
    }
    
    @Test
    public void testCrearUsuarioDocente() throws Exception{
        String nickname = "NICKNAME_DOCENTE";
        List<String> institutos = new ArrayList<>();
        Date fecha = new Date();
        DtUsuario usuario = new DtUsuario(nickname, "email_docente", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.DOCENTE);
        ic.crearUsuario(usuario);
        
        List<DtUsuario> usuarios = ic.listarUsuarios();
        assertNotNull(usuarios, "La lista de institutos no debe ser nula");
        
        boolean existeEnLista = usuarios.stream().anyMatch(i -> i.getNickname().equals(nickname));
        
        assertTrue(existeEnLista, "El usuario creado deberia estar en la lista");
    }
    
    @Test
    public void testCrearUsuarioDocenteInstitutoFalso() throws Exception{
        String nickname = "NICKNAME_DOCENTE2";
        List<String> institutos = new ArrayList<>();
        institutos.add("Instituto manolo");
        
        Date fecha = new Date();
        DtUsuario usuario = new DtUsuario(nickname, "email_docente2", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.DOCENTE);
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.crearUsuario(usuario);}, "No existe el Instituto");        
        assertNotNull(excepcion.getMessage());
    }
    
    @Test
    public void testListarEdicionesPorUsuarioEstudiante() throws Exception{
        String nickname = "roro";
        List<DtEdicion> lista = ic.listarEdicionesPorUsuario(nickname);
        assertNotNull(lista, "La lista no debe ser nula");
    }
    
    @Test
    public void testListarEdicionesPorUsuarioDocente() throws Exception{
        String nickname = "heisenberg";
        List<DtEdicion> lista = ic.listarEdicionesPorUsuario(nickname);
        assertNotNull(lista, "La lista no debe ser nula");
    }
    
    @Test
    public void testListarEdicionesPorUsuarioFallido() throws Exception{
        String nickname = "USUARIO_INEXISTENTE";        
        Exception excepcion = assertThrows(Exception.class, () -> {List<DtEdicion> lista = ic.listarEdicionesPorUsuario(nickname);}, "No existe el usuario");        
        assertNotNull(excepcion.getMessage());
    }
   
    @Test
    public void testAltaCurso() throws Exception {    
        String nombreInstituto = "DISI";
        String nombreCurso = "CURSITO1";

        DtInstituto instituto = ic.listarInstitutos().stream()
            .filter(inst -> inst.getNombre().equalsIgnoreCase(nombreInstituto))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        List<DtCurso> previas = ic.listarCursosPorUsuario("heisenberg");
        
        DtCurso curso = new DtCurso(nombreCurso, "Intro", "4 meses", 60, 10, "http...", new Date(), instituto, previas);
        
        ic.altaCurso(curso, nombreInstituto);
        
        List<DtCurso> cursos = ic.listarCursos();
        assertNotNull(cursos, "La lista de cursos no debe ser nula");
        boolean existeEnLista = cursos.stream().anyMatch(i -> i.getNombre().equals(nombreCurso));
        assertTrue(existeEnLista, "El curso creado deberia estar en la lista");
    }
    
    @Test
    public void testAltaCursoPreviaInexistente() throws Exception {    
        String nombreInstituto = "DISI";
        String nombreCurso = "CURSITO2";

        DtInstituto instituto = ic.listarInstitutos().stream()
            .filter(inst -> inst.getNombre().equalsIgnoreCase(nombreInstituto))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        DtCurso previa = new DtCurso(nombreCurso, "Intro", "4 meses", 60, 10, "http...", new Date(), instituto);
        
        List<DtCurso> previas = ic.listarCursosPorUsuario("heisenberg");
        previas.add(previa);
        
        DtCurso curso = new DtCurso(nombreCurso, "Intro", "4 meses", 60, 10, "http...", new Date(), instituto, previas);
               
        Exception excepcion = assertThrows(Exception.class, () -> {ic.altaCurso(curso, nombreInstituto);}, "No existe la previa");        
        assertNotNull(excepcion.getMessage());
    }
    
    @Test
    public void testAltaCursoDuplicado() throws Exception {    
        String nombreInstituto = "DISI";
        String nombreCurso = "CURSITO3";

        DtInstituto instituto = ic.listarInstitutos().stream()
            .filter(inst -> inst.getNombre().equalsIgnoreCase(nombreInstituto))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        DtCurso curso = new DtCurso(nombreCurso, "Intro", "4 meses", 60, 10, "http...", new Date(), instituto);
        
        ic.altaCurso(curso, nombreInstituto);
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.altaCurso(curso, nombreInstituto);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());
    }

    @Test
    public void testAltaCursoInstitutoInexistente() throws Exception {    
        String nombreInstituto = "DISI";
        String nombreCurso = "CURSITO4";

        DtInstituto instituto = ic.listarInstitutos().stream()
            .filter(inst -> inst.getNombre().equalsIgnoreCase(nombreInstituto))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        DtCurso curso = new DtCurso(nombreCurso, "Intro", "4 meses", 60, 10, "http...", new Date(), instituto);
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.altaCurso(curso, "INSTITUTO_INEXISTENTE33");}, "Instituto inexistente");        
        assertNotNull(excepcion.getMessage());
    }
    
    @Test
    public void testAltaEdicionCurso() throws Exception {
        String nombreInstituto = "DISI";
        String nombreCurso = "MicroBit";
        String nombreEdicion = "MicroBit-2026";
        String nombreDocente = "heisenberg";
        
        Set<String> docentes = new HashSet<>();
        docentes.add(nombreDocente);
        
        DtCurso curso = ic.listarCursos().stream().filter(inst -> inst.getNombre().equalsIgnoreCase(nombreCurso))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        DtEdicion edicion = new DtEdicion(nombreEdicion, 1, LocalDate.now(), LocalDate.now(), LocalDate.now(),curso, docentes);
        
        ic.altaEdicionCurso(nombreCurso, edicion);      
   
        List<DtEdicion> cursos = ic.listarEdicionPorCurso(nombreCurso);
        assertNotNull(cursos, "La lista de ediciones no debe ser nula");
        boolean existeEnLista = cursos.stream().anyMatch(i -> i.getNombre().equals(nombreEdicion));
        assertTrue(existeEnLista, "La edicion creada deberia estar en la lista");
    }
    
    @Test
    public void testAltaEdicionCursoDuplicado() throws Exception {
        String nombreInstituto = "DISI";
        String nombreCurso = "MicroBit";
        String nombreEdicion = "MicroBit-2026-2";
        String nombreDocente = "heisenberg";
        
        Set<String> docentes = new HashSet<>();
        docentes.add(nombreDocente);
        
        DtCurso curso = ic.listarCursos().stream().filter(inst -> inst.getNombre().equalsIgnoreCase(nombreCurso))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        DtEdicion edicion = new DtEdicion(nombreEdicion, 1, LocalDate.now(), LocalDate.now(), LocalDate.now(),curso, docentes);
        
        ic.altaEdicionCurso(nombreCurso, edicion);      
           
        Exception excepcion = assertThrows(Exception.class, () -> {ic.altaEdicionCurso(nombreCurso, edicion);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());
    }
   
    @Test
    public void testAltaEdicionCursoInexistente() throws Exception {
        String nombreInstituto = "DISI";
        String nombreCurso = "MicroBit";
        String nombreEdicion = "MicroBit-2026-3";
        String nombreDocente = "heisenberg";
        
        Set<String> docentes = new HashSet<>();

        docentes.add(nombreDocente);
        
        DtCurso curso = ic.listarCursos().stream().filter(inst -> inst.getNombre().equalsIgnoreCase(nombreCurso))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el instituto: " + nombreInstituto));
        
        DtEdicion edicion = new DtEdicion(nombreEdicion, 1, LocalDate.now(), LocalDate.now(), LocalDate.now(),curso, docentes);  
           
        Exception excepcion = assertThrows(Exception.class, () -> {ic.altaEdicionCurso("CURSO_INEXISTENTE", edicion);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());
    }

    @Test
    public void testListarCursosPorPrograma() throws Exception {
        String nombre = "EFI Ingeniería Mecánica";
        List<DtCurso> cursos = ic.listarCursosPorPrograma(nombre);
        assertNotNull(cursos, "La lista de cursos no debe ser nula");
    }
    
    @Test
    public void testListarCursosPorProgramaFallido() throws Exception {
        String nombre = "CURSO_INEXISTENTE";        
        Exception excepcion = assertThrows(Exception.class, () -> {List<DtCurso> cursos = ic.listarCursosPorPrograma(nombre);}, "No existe");        
        assertNotNull(excepcion.getMessage());
    }    
    
    @Test
    public void testListarCursosPorInstituto() throws Exception {
        String nombre = "DISI";
        List<String> cursos = ic.listarCursosPorInstituto(nombre);
        assertNotNull(cursos, "La lista de cursos no debe ser nula");
    }    
    
    @Test
    public void testListarCursosPorUsuarioFallido() throws Exception {
        String nombre = "USUARIO_INEXISTENTE";        
        Exception excepcion = assertThrows(Exception.class, () -> {List<DtCurso> cursos = ic.listarCursosPorUsuario(nombre);}, "No existe");        
        assertNotNull(excepcion.getMessage());
    } 

    @Test
    public void testListarProgramas() throws Exception {
        List<DtPrograma> cursos = ic.listarProgramas();
        assertNotNull(cursos, "La lista de programas no debe ser nula");
    }  
    
    @Test
    public void testListarProgramasPorUsuarioEstudiante() throws Exception {
        String nombre = "roro";
        List<DtPrograma> cursos = ic.listarProgramasPorUsuario(nombre);
        assertNotNull(cursos, "La lista de programas no debe ser nula");
    }  

    @Test
    public void testListarProgramasPorUsuarioDocente() throws Exception {
        String nombre = "heisenberg";
        List<DtPrograma> cursos = ic.listarProgramasPorUsuario(nombre);
        assertNotNull(cursos, "La lista de programas no debe ser nula");
    }  
    
    @Test
    public void testListarProgramasPorUsuarioFallido() throws Exception {
        String nombre = "USUARIO_INEXISTENTE";        
        Exception excepcion = assertThrows(Exception.class, () -> {List<DtPrograma> cursos = ic.listarProgramasPorUsuario(nombre);}, "No existe");        
        assertNotNull(excepcion.getMessage());
    } 
    
    @Test
    public void testAgregarProgramaCurso() throws Exception {
        String programa = "Formación integral";
        String curso = "MicroBit";
        ic.agregarProgramaCurso(programa, curso);
        
        List<DtCurso> cursos = ic.listarCursosPorPrograma(programa);
        assertNotNull(cursos, "La lista de programas no debe ser nula");
                
        boolean existeEnLista = cursos.stream().anyMatch(i -> i.getNombre().equals(curso));
        
        assertTrue(existeEnLista, "El curso deberia estar en la lista");
    }
    
    @Test
    public void testAgregarProgramaCursoDuplicado() throws Exception {
        String programa = "Formación integral";
        String curso = "Flor del Ceibo";
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.agregarProgramaCurso(programa, curso);}, "Ya existe");        
        assertNotNull(excepcion.getMessage());        
    }
    
    @Test
    public void testAgregarProgramaCursoInexistente() throws Exception {
        String programa = "Formación integral";
        String curso = "CURSO_INEXISTENTE";
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.agregarProgramaCurso(programa, curso);}, "No existe");        
        assertNotNull(excepcion.getMessage());        
    }
    
    @Test
    public void testBuscarPrograma() throws Exception {
        String nombre = "Formación integral";
        DtPrograma programa = ic.buscarPrograma(nombre);
        assertEquals(nombre, programa.getNombre(), "El nombre del programa buscado debe coincidir");
    }
    
    @Test
    public void testBuscarProgramaInexistente() throws Exception {
        String programa = "PROGRAMA_INEXISTENTE";
        Exception excepcion = assertThrows(Exception.class, () -> {ic.buscarPrograma(programa);}, "No existe");        
        assertNotNull(excepcion.getMessage());   
    }

    @Test
    public void testObtenerDatosCurso() throws Exception {
        String nombre = "MicroBit";
        DtConsultaCurso curso = ic.obtenerDatosCurso(nombre);
        assertEquals(nombre, curso.getNombre(), "El nombre del programa buscado debe coincidir");    
    }
    
    @Test
    public void testObtenerDatosCursoInexistente() throws Exception {
        String curso = "CURSO_INEXISTENTE";
        Exception excepcion = assertThrows(Exception.class, () -> {ic.obtenerDatosCurso(curso);}, "No existe");        
        assertNotNull(excepcion.getMessage());      
    }
    
    @Test
    public void testModificarUsuarioDocente() throws Exception {
        String nombre = "heisenberg";
        String nuevoApellido = "Black";
        
        DtUsuario usuarioOriginal = ic.listarUsuarios().stream().filter(inst -> inst.getNickname().equalsIgnoreCase(nombre))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario: " + nombre));        
        
        DtUsuario usuarioModificado = new DtUsuario(
        usuarioOriginal.getNickname(),     // Mantiene nickname
        usuarioOriginal.getEmail(),        // Mantiene email
        usuarioOriginal.getNombre(),       // Mantiene nombre
        nuevoApellido,                     // <--- Apellido cambiado
        usuarioOriginal.getImagen(),       // Mantiene imagen
        usuarioOriginal.getfNacimiento(),  // Mantiene fecha nacimiento
        usuarioOriginal.getInstitutos(),   // Mantiene institutos
        usuarioOriginal.getTipoUsuario()   // Mantiene tipo de usuario
    );
        
        ic.modificarUsuario(usuarioModificado);
        
        DtUsuario usuarioFinal = ic.listarUsuarios().stream().filter(inst -> inst.getNickname().equalsIgnoreCase(nombre))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario: " + nombre)); 
        
        assertEquals(usuarioModificado.getApellido(), usuarioFinal.getApellido() , "El apellido buscado debe coincidir");
        
    }
    
    @Test
    public void testModificarUsuarioInexistente() throws Exception {
        String nickname = "NICKNAME44";
        List<String> institutos = new ArrayList<>();
        Date fecha = new Date();
        DtUsuario usuario = new DtUsuario(nickname, "email", "nombre", "apellido", "imagen", fecha, institutos, TipoUsuario.ESTUDIANTE);
        
        Exception excepcion = assertThrows(Exception.class, () -> {ic.modificarUsuario(usuario);}, "No existe");        
        assertNotNull(excepcion.getMessage());      
    
    }
    
    @Test
    public void testListarDocentes() throws Exception {
        List<String> lista = ic.listarDocentes();
        assertNotNull(lista, "La lista de programas no debe ser nula");
    }
    
    @Test
    public void testNombreCursos() throws Exception {
        List<String> lista = ic.listarNombresCursos();
        assertNotNull(lista, "La lista de programas no debe ser nula");
    }
    
    @Test
    public void testGetEdicion() throws Exception {
        String nombre = "Dalavuelta - 2025";
        DtEdicion edicion = ic.getEdicion(nombre);

        assertEquals(nombre, edicion.getNombre(), "El nombre buscado debe coincidir");
    }
    
    @Test
    public void testBuscarEdicion() throws Exception {
        String nombre = "Dalavuelta - 2025";
        DtEdicion edicion = ic.buscarEdicion(nombre);

        assertEquals(nombre, edicion.getNombre(), "El nombre buscado debe coincidir");
    }
    
    @Test
    public void testObtenerInstitutoPorCurso() throws Exception {
        String curso = "Dalavuelta";
        String instituto = "IMPII";
        
        String resultado = ic.obtenerInstitutoPorCurso(curso);
        
        assertEquals(resultado, instituto, "El instituto buscado debe coincidir");
    }
    
    @Test
    public void testExisteUsuario() throws Exception {
        String nickname = "heisenberg";
        boolean resultado = ic.existeUsuario(nickname);
        assertTrue(resultado, "El usuario deberia estar");
    }

    @Test
    public void testExisteUsuarioInexistente() throws Exception {
        String nickname = "USUARIO_INEXISTENTE";
        boolean resultado = ic.existeUsuario(nickname);
        assertFalse(resultado, "El usuario no deberia estar");
    }
    
    @Test
    public void testExisteCorreo() throws Exception {
        String correo = "heisenberg@gmail.com";
        boolean resultado = ic.existeEmail(correo);
        assertTrue(resultado, "El usuario deberia estar");
    }

    @Test
    public void testExisteCorreoInexistente() throws Exception {
        String correo = "CORREO_INEXISTENTE";
        boolean resultado = ic.existeEmail(correo);
        assertFalse(resultado, "El usuario no deberia estar");
    }

    @Test
    public void testListaEdicionPorCurso() throws Exception {
        String curso = "MicroBit";
        List<String> lista = ic.listaEdicionPorCurso(curso);
        assertNotNull(lista, "La lista no debe ser nula");
    }
 
    
    
    
}
