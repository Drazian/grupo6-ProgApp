package edu.edext.logica;

import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtConsultaCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtCurso;
import jakarta.jws.WebService;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vdraco
 */
//@WebService(endpointInterface = "edu.edext.logica.IBridgeSOAP")
@WebService(
    serviceName = "IBridgeSOAPService", 
    endpointInterface = "edu.edext.logica.IBridgeSOAP"
)
public class BridgeSOAP implements IBridgeSOAP{
    private final IControlador bridge=Fabrica.getInstance().getIControlador();

//******************************** Institutos ********************************//
    @Override
    public List<DtInstituto> listarInstitutos() throws Exception {
        return bridge.listarInstitutos();
    }
    @Override
    public String obtenerInstitutoPorCurso(String nombreCurso) throws Exception{
        return bridge.obtenerInstitutoPorCurso(nombreCurso);
    }

//******************************** Categrias *********************************//
    @Override
    public List<DtCategoria> listarCategorias() throws Exception {
        return bridge.listarCategorias();
    }

//********************************* Usuarios *********************************//
    @Override
    public void crearUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception {
        bridge.crearUsuario(usuario, bytesImagen, nombreOriginal);
    }
    @Override
    public boolean existeUsuario(String nickname) throws Exception {
        return bridge.existeUsuario(nickname);
    }
    @Override
    public boolean existeEmail(String email) throws Exception {
        return bridge.existeEmail(email);
    }
    @Override
    public void modificarUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception {
        bridge.modificarUsuario(usuario, bytesImagen, nombreOriginal);
    }
    @Override
    public List<DtUsuario> listarUsuarios() throws Exception {
        return bridge.listarUsuarios();
    }
    @Override
    public List<String> listarDocentes() throws Exception {
        return bridge.listarDocentes();
    }
    @Override
    public DtUsuario obtenerUsuario(String nicknameOEmail) throws Exception{
        return bridge.obtenerUsuario(nicknameOEmail);
    }
    @Override
    public DtUsuario validarNick(String nickname, String password) throws Exception {
        return bridge.validarNick(nickname, password);
    }
    @Override
    public DtUsuario validarEmail(String email, String password) throws Exception {
        return bridge.validarEmail(email, password);
    }
    @Override
    public void seguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception {
        bridge.seguirUsuario(nicknameSeguidor, nicknameASeguir);
    }
    @Override
    public void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameADejar) throws Exception {
        bridge.dejarDeSeguirUsuario(nicknameSeguidor, nicknameADejar);
    }
    @Override
    public List<String> listarSeguidos(String nickname) throws Exception {
        return bridge.listarSeguidos(nickname);
    }
    @Override
    public List<String> listarSeguidores(String nickname) throws Exception {
        return bridge.listarSeguidores(nickname);
    }
    @Override
    public void agregarDocenteAEdicion(String nombreEdicion, String nicknameDocente) throws Exception {
        bridge.agregarDocenteAEdicion(nombreEdicion, nicknameDocente);
    }
    @Override
    public void quitarDocenteDeEdicion(String nombreEdicion, String nicknameDocente) throws Exception {
        bridge.quitarDocenteDeEdicion(nombreEdicion, nicknameDocente);
    }

//********************************* Cursos ***********************************//
    @Override
    public void altaCurso(DtCurso curso, String nombreInstituto) throws Exception {
        bridge.altaCurso(curso, nombreInstituto);
    }
    @Override
    public DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception {
        return bridge.obtenerDatosCurso(nombreCurso);
    }
    @Override
    public List<String> listarNombresCursos() throws Exception {
        return bridge.listarNombresCursos();
    }
    @Override
    public List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception {
        return bridge.listarCursosPorInstituto(nombreInstituto);
    }
    @Override
    public List<String> listarCursosPorCategoria(String nombreInstituto) throws Exception {
        return bridge.listarCursosPorCategoria(nombreInstituto);
    }
    @Override
    public List<DtCurso> listarCursos() throws Exception {
        return bridge.listarCursos();
    }

//********************************* Edicion **********************************//
    @Override
    public void altaEdicionCurso(String nombreCurso, DtEdicion dt) throws Exception {
        bridge.altaEdicionCurso(nombreCurso, dt);
    }
    @Override
    public DtEdicion getEdicion(String nombre) throws Exception {
        return bridge.getEdicion(nombre);
    }
    @Override
    public List<String> listaEdicionPorCurso(String curso) throws Exception {
        return bridge.listaEdicionPorCurso(curso);
    }
    @Override
    public List<DtEdicion> listarEdicionesPorUsuario(String nickname) throws Exception {
        return bridge.listarEdicionesPorUsuario(nickname);
    }

//******************************* Inscripcion ********************************//
//    @Override
//    public int setInscripcion(String estudiante, String edicion) throws Exception {
//        return bridge.setInscripcion(estudiante, edicion);
//    }
    @Override
    public int setInscripcion(String nomEstudiante, String nomEdicion, String videoURL) throws Exception{
        return bridge.setInscripcion(nomEstudiante, nomEdicion, videoURL);
    }
    @Override
    public int delInscripcion(String estudiante, String edicion) throws Exception{
        return bridge.delInscripcion(estudiante, edicion);
    }
    @Override
    public List<DtInscripcionEdicion> getInscripcionesCompletas(String nombreEdicion) throws Exception {
        return bridge.getInscripcionesCompletas(nombreEdicion);
    }
    @Override
    public void evaluarInscripciones(String nombreEdicion, Map<String, Integer> evaluaciones) throws Exception {
        bridge.evaluarInscripciones(nombreEdicion, evaluaciones);
    }
    @Override
    public List<DtInscripcionEdicion> getInscripcionesDeEstudiante(String nicknameEstudiante) throws Exception {
        return bridge.getInscripcionesDeEstudiante(nicknameEstudiante);
    }
    @Override
    public List<String> listarEdicionesCerradasDeDocente(String nicknameDocente, String nombreCurso) throws Exception {
        return bridge.listarEdicionesCerradasDeDocente(nicknameDocente, nombreCurso);
    }
    @Override
    public List<DtInscripcionEdicion> getActaEdicion(String nombreEdicion, String nicknameDocente) throws Exception {
        return bridge.getActaEdicion(nombreEdicion, nicknameDocente);
    }
    @Override
    public List<String> listarEdicionesAbiertasDeDocente(String nicknameDocente, String nombreCurso) throws Exception {
        return bridge.listarEdicionesAbiertasDeDocente(nicknameDocente, nombreCurso);
    }
    @Override
    public List<DtInscripcionEdicion> getParticipantesParaEvaluar(String nombreEdicion, String nicknameDocente) throws Exception {
        return bridge.getParticipantesParaEvaluar(nombreEdicion, nicknameDocente);
    }
    @Override
    public void ingresarResultados(String nombreEdicion, String nicknameDocente, Map<String, Integer> notas, boolean cerrarEdicion) throws Exception {
        bridge.ingresarResultados(nombreEdicion, nicknameDocente, notas, cerrarEdicion);
    }
    @Override
    public List<DtInscripcionEdicion> listarResultadosEvaluaciones(String nicknameEstudiante) throws Exception {
        return bridge.listarResultadosEvaluaciones(nicknameEstudiante);
    }
//******************************* Valoraciones *******************************//
    @Override
    public List<String> listarCursosParaValorar(String nicknameEstudiante) throws Exception {
        return bridge.listarCursosParaValorar(nicknameEstudiante);
    }
    @Override
    public edu.edext.datatypes.DtValoracionesCurso getValoracionesCurso(String nombreCurso, String nicknameEstudiante) throws Exception {
        return bridge.getValoracionesCurso(nombreCurso, nicknameEstudiante);
    }
    @Override
    public void valorarCurso(String nicknameEstudiante, String nombreCurso, int puntaje) throws Exception {
        bridge.valorarCurso(nicknameEstudiante, nombreCurso, puntaje);
    }
    @Override
    public List<edu.edext.datatypes.DtValoracionesCurso> listarResumenesValoraciones() throws Exception {
        return bridge.listarResumenesValoraciones();
    }
//****************************** Acta de Estudio *****************************//
    @Override
    public edu.edext.datatypes.DtActaEstudio getActaEstudio(String nicknameEstudiante, String nombrePrograma) throws Exception {
        return bridge.getActaEstudio(nicknameEstudiante, nombrePrograma);
    }
//************************** Listado de tendencias (7.3) **********************//
    @Override
    public boolean registrarVisitaCurso(String nombreCurso) throws Exception {
        return bridge.registrarVisitaCurso(nombreCurso);
    }
    @Override
    public List<edu.edext.datatypes.DtCursoVisitas> listarCursosMasVisitados() throws Exception {
        return bridge.listarCursosMasVisitados();
    }
    @Override
    public int setInscripcionPrograma(String nomEstudiante, String nomPrograma) throws Exception {
        return bridge.setInscripcionPrograma(nomEstudiante, nomPrograma);
    }
    @Override
    public void agregarProgramaCurso(String programa, String curso) throws Exception {
        bridge.agregarProgramaCurso(programa, curso);
    }
    @Override
    public DtPrograma buscarPrograma(String nombre) throws Exception {
        return bridge.buscarPrograma(nombre);
    }
    @Override
    public List<DtPrograma> listarProgramasPorUsuario(String nickname) throws Exception {
        return bridge.listarProgramasPorUsuario(nickname);
    }
    @Override
    public List<DtPrograma> listarProgramas() throws Exception {
        return bridge.listarProgramas();
    }

//**************************** ProgramaFormacion *****************************//
    @Override
    public boolean setCrearProgramaFormacion(DtPrograma programa, byte[] bytesImagen, String nombreOriginal) throws Exception {
        return bridge.setCrearProgramaFormacion(programa, bytesImagen, nombreOriginal);
    }
    
}
