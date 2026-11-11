package edu.edext.logica;
//**************************** Capa Logica *************************************
import edu.edext.entitys.Usuario;
import edu.edext.entitys.Docente;
import edu.edext.entitys.InscripcionEdicion;
import edu.edext.entitys.Estudiante;
import edu.edext.entitys.InscripcionEdicionID;
import edu.edext.entitys.Edicion;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;
 
/**
 *
 * @author vdraco
 */
public class InscripcionEdicionHelper {
    private final EntityManagerFactory DB;
    private String nombre;
    private DtEdicion DTO;
    private EntityManager db;
    private EntityTransaction hiloTransaction;
    
    public InscripcionEdicionHelper(EntityManagerFactory obj, String nombre){
        DB=obj;
        this.nombre=nombre;
    }
    
    
//    public List<String> getEstudiantesInscriptos(){
//        return getInscriptos();
//    }
    public List<DtUsuario> getEstudiantesInscriptos(){
        return toArrayDTO(toArrayEntity(getInscriptos()));
    }

    public List<DtUsuario> getEstudiantesCandidatos(){
        return toArrayDTO(toArrayEntity(getCandidatos()));
    }

    public List<String> getListEstudiantesInscriptos(){
        return getInscriptos();
    }

    public List<String> getListEstudiantesCandidatos(){
        return getCandidatos();
    }

    private List<DtUsuario> toArrayDTO(List<Usuario> entity){
        List<DtUsuario> ret=new ArrayList<>();
        for (Usuario en : entity)
            if(en instanceof Estudiante)
                ret.add(new DtUsuario(en.getNickname(), null, en.getEmail(), en.getNombre(),
                        en.getApellido(), en.getImagen(), en.getfNacimiento(), new ArrayList<>(), TipoUsuario.ESTUDIANTE));
        return ret;
    }
    
    public void kill(){
        if(db!=null && db.isOpen()) db.close();
        hiloTransaction=null;
        DTO=null;
        db=null;
    }
    
    
    //***************************** pipeline ***********************************
    
    private List<String> getInscriptos(){
        db=DB.createEntityManager();
        List<String> ret;
        try { ret=db.createQuery("SELECT i.estudiante.nickname FROM InscripcionEdicion i WHERE i.edicion.nombre = :nombreEdicion", 
                    String.class)
                    .setParameter("nombreEdicion", nombre)
                    .getResultList(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        db.close();
        db=null;
        return ret;
    }
    
    private List<String> getCandidatos(){
        db=DB.createEntityManager();
        List<String> ret;
        try { ret=db.createQuery("SELECT e.nickname FROM Estudiante e WHERE e.nickname NOT IN (" +
                                 "SELECT i.estudiante.nickname FROM InscripcionEdicion i WHERE i.edicion.nombre = :nombreEdicion" +")", 
                    String.class)
                    .setParameter("nombreEdicion", nombre)
                    .getResultList();
        } catch (Exception e) { ret=new ArrayList<>(); }
        db.close();
        db=null;
        return ret;
    }
    
    private List<Usuario> toArrayEntity(List<String> dto){
        db=DB.createEntityManager();
        List<Usuario> ret=new ArrayList<>();
        for(String usuario : dto)
            ret.add(db.find(Usuario.class, usuario));
        return ret;
    }




    public int inscribirEstudiante(String nomEstudiante, String nomEdicion, String videoURL){ // incompleta falta aislacion y modularizacion
        int ret=-1;
        db=DB.createEntityManager();
        try {
            db.getTransaction().begin();
            Estudiante estudiante = db.find(Estudiante.class, nomEstudiante);
            Edicion edicion = db.find(Edicion.class, nomEdicion);
            if(estudiante==null){ db.getTransaction().rollback(); return 1; }
            if (edicion==null){ db.getTransaction().rollback(); return 2; }
            if (edicion.getFechaFin()!=null && edicion.getFechaFin().isBefore(LocalDate.now())){
                db.getTransaction().rollback();
                return 4; // la edicion ya finalizo
            }
            InscripcionEdicionID idDual = new InscripcionEdicionID(nomEstudiante, nomEdicion);
            InscripcionEdicion isExist = db.find(InscripcionEdicion.class, idDual);
            if(isExist==null){
                InscripcionEdicion newInscripcion=new InscripcionEdicion(estudiante, edicion, LocalDate.now(), INSCRIPTO, videoURL);
                db.persist(newInscripcion);
                db.getTransaction().commit();
                Logger.info("Se Inscribio el Estudiante {} en la edicion {}", nomEstudiante, nomEdicion);
                ret=0;
            } else {
                db.getTransaction().rollback();
                ret=3; // ya inscripto
            }
        } catch (Exception e) {
            if(db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error("Error al Inscribir al estudiante {} en la edicion {}", nomEstudiante, nomEdicion);
        } finally {
            db.close();
        }
        return ret;
    }
 
    // 0 exito
    // 1 estudiante no existe
    // 2 edicion no existe
    // 3 ya estaba inscripto
    // 4 la edicion ya finalizo
    // -1 error
    public static final int INSCRIPTO=1, ACEPTADO=2, RECHAZADO=3;

    /**
     * Inscripción a edición (letra 6.1): si el estudiante ya está inscripto, puede modificar los datos
     * de su inscripción (el video de motivación). Se permite mientras el docente no la haya evaluado
     * (sigue en estado Inscripto) y la edición no haya terminado ni esté cerrada. La fecha de inscripción no cambia.
     * @param videoURL nuevo video; vacío o null lo quita
     * @return 0 éxito, 1 no está inscripto, 2 la edición no existe, 3 ya fue evaluada (Aceptado o Rechazado),
     *         4 la edición ya finalizó o está cerrada, -1 error
     */
    public int modificarInscripcion(String nomEstudiante, String nomEdicion, String videoURL){
        int ret=-1;
        db=DB.createEntityManager();
        try {
            db.getTransaction().begin();
            Edicion edicion = db.find(Edicion.class, nomEdicion);
            if (edicion==null){ db.getTransaction().rollback(); return 2; }
            InscripcionEdicion inscripcion = db.find(InscripcionEdicion.class, new InscripcionEdicionID(nomEstudiante, nomEdicion));
            if (inscripcion==null){ db.getTransaction().rollback(); return 1; }
            if (edicion.isCerrada() || (edicion.getFechaFin()!=null && edicion.getFechaFin().isBefore(LocalDate.now()))){
                db.getTransaction().rollback();
                return 4;
            }
            if (inscripcion.getEstado()!=INSCRIPTO){ db.getTransaction().rollback(); return 3; }
            inscripcion.setVideoURL(videoURL==null || videoURL.isBlank() ? null : videoURL.trim());
            db.getTransaction().commit();
            Logger.info("El estudiante {} modificó su inscripción a la edición {}", nomEstudiante, nomEdicion);
            ret=0;
        } catch (Exception e) {
            if(db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error(e, "Error al modificar la inscripción del estudiante {} a la edición {}", nomEstudiante, nomEdicion);
            ret=-1;
        } finally {
            db.close();
            db=null;
        }
        return ret;
    }

    public int cancelarInscripcion(String nomEstudiante, String nomEdicion){ // incompleta falta aislacion y modularizacion
        int ret=-1;
        db=DB.createEntityManager();
        try {
            db.getTransaction().begin();
            InscripcionEdicionID idDual = new InscripcionEdicionID(nomEstudiante, nomEdicion);
//            InscripcionEdicion isExist = db.find(InscripcionEdicion.class, idDual);
            InscripcionEdicion inscripcion = db.find(InscripcionEdicion.class, idDual);
//            if(isExist!=null){
//                db.remove(isExist);
//                db.getTransaction().commit();
//                Logger.info("Se Cancelo la Incripcion del Estudiante {} a la Edicion {}", nomEstudiante, nomEdicion);
//                ret=0;
//            }
            if (inscripcion == null) {
                db.getTransaction().rollback();
                return 1;
            }
            // Eliminar la inscripción sin importar el estado en que se encuentre
            db.remove(inscripcion);
            db.getTransaction().commit();

            Logger.info("Se canceló la inscripción del estudiante {} a la edición {}", nomEstudiante, nomEdicion);
            ret = 0;

        } catch (Exception e) {
            if(db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error("Error al Cancelar la Inscripcion del estudiante {} a la edicion {}", nomEstudiante, nomEdicion);
            ret=-1;
        } finally{
	  if (db != null && db.isOpen()) {
            db.close();
          }
          db = null;
        }
        
        return ret;
    }
    
  // =========================================================================
    // NUEVOS MÉTODOS PARA EL CASO DE USO "SELECCIONAR ESTUDIANTES"
    // =========================================================================

    /**
     * Calcula la prioridad de un estudiante según la fórmula ipr * 0.5
     */
    private static class CupoExcedidoException extends RuntimeException {
        CupoExcedidoException(String m){ super(m); }
    }

    private double calcularPrioridad(String nicknameEstudiante, String nombreCurso) {
        EntityManager emAux = DB.createEntityManager();
        try {
            // Cuenta inscripciones previas rechazadas (estado=3) a ediciones finalizadas del MISMO curso
            Long ipr = emAux.createQuery(
                "SELECT COUNT(i) FROM InscripcionEdicion i " +
                "WHERE i.estudiante.nickname = :estudiante " +
                "AND i.edicion.curso.nombre = :curso " +
                "AND i.estado = 3 " + 
                "AND i.edicion.fechaFin < CURRENT_DATE", Long.class)
                .setParameter("estudiante", nicknameEstudiante)
                .setParameter("curso", nombreCurso)
                .getSingleResult();
            
            return (ipr != null ? ipr : 0) * 0.5;
        } catch (Exception e) {
            return 0.0;
        } finally {
            emAux.close();
        }
    }

    /**
     * Retorna todas las inscripciones a esta edición, ya convertidas a Dt y con la prioridad calculada.
     */
    public List<DtInscripcionEdicion> getInscripcionesCompletas() {
        db = DB.createEntityManager();
        List<DtInscripcionEdicion> ret = new ArrayList<>();
        try {
            List<InscripcionEdicion> inscripciones = db.createQuery(
                "SELECT i FROM InscripcionEdicion i WHERE i.edicion.nombre = :nombreEdicion",
                InscripcionEdicion.class)
                .setParameter("nombreEdicion", this.nombre)
                .getResultList();

            for (InscripcionEdicion i : inscripciones) {
                Estudiante e = i.getEstudiante();
                DtUsuario dtEst = new DtUsuario(e.getNickname(), e.getPassword(), e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, TipoUsuario.ESTUDIANTE);

                Edicion ed = i.getEdicion();
                
                DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), "usr.png");
//                DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), null, null, 0, 0, null, null, null, null, null, "usr.png");
                
                
                DtEdicion dtEd = new DtEdicion(ed.getNombre(), ed.getCupo(), ed.getFechaInicio(), ed.getFechaFin(), ed.getFechaPublicacion(), dtCurso, "usr.png");
                dtEd.setFechaCierre(ed.getFechaCierre());

                // Cálculo en caliente del requerimiento 7.5
                double prioridad = calcularPrioridad(e.getNickname(), ed.getCurso().getNombre());

                // Se pasa i.getVideoURL() como 6to argumento al DTO
                DtInscripcionEdicion dtIns = new DtInscripcionEdicion(dtEst, dtEd, i.getFechaInscripcion(), i.getEstado(), prioridad, i.getVideoURL());
                dtIns.setNota(i.getNota());
                ret.add(dtIns);
            }
        } catch (Exception e) {
            Logger.error("Error al obtener inscripciones completas: " + e.getMessage());
        } finally {
            db.close();
            db=null;
        }
        return ret;
    }

    /**
     * Aplica el dictamen del docente (Aceptado/Rechazado) masivamente a la lista de estudiantes.
     * @param evaluaciones Map donde Key = Nickname del Estudiante, Value = Nuevo Estado (2 o 3)
     */
    public void evaluarInscripciones(Map<String, Integer> evaluaciones) throws Exception {
        db = DB.createEntityManager();
        try {
            db.getTransaction().begin();
            
            Edicion edicionObj = db.find(Edicion.class, this.nombre);
            if (edicionObj == null) throw new Exception("La edición '" + this.nombre + "' ya no existe.");
            if (edicionObj.isCerrada())
                throw new IllegalArgumentException("La edición '" + this.nombre + "' ya fue cerrada: no se pueden modificar sus inscripciones.");

            for (Map.Entry<String, Integer> entry : evaluaciones.entrySet()) {
                String nicknameEstudiante = entry.getKey();
                Integer nuevoEstado = entry.getValue(); 
                if (nuevoEstado == null || (nuevoEstado != ACEPTADO && nuevoEstado != RECHAZADO))
                    throw new IllegalArgumentException("Estado inválido para " + nicknameEstudiante);

                InscripcionEdicionID idDual = new InscripcionEdicionID(nicknameEstudiante, this.nombre);
                InscripcionEdicion inscripcion = db.find(InscripcionEdicion.class, idDual);

                if (inscripcion != null) {
                    inscripcion.setEstado(nuevoEstado);
                }
            }
            db.flush();
            Integer cupo = edicionObj.getCupo();
            if (cupo != null && cupo > 0) {
                Long aceptados = db.createQuery(
                    "SELECT COUNT(i) FROM InscripcionEdicion i WHERE i.edicion.nombre = :ed AND i.estado = :est", Long.class)
                    .setParameter("ed", this.nombre).setParameter("est", ACEPTADO).getSingleResult();
                if (aceptados > cupo)
                    throw new CupoExcedidoException("Se superó el cupo de la edición (" + cupo + "). Aceptados: " + aceptados + ".");
            }
            db.getTransaction().commit();
            Logger.info("Evaluación de inscripciones completada para la edición: " + this.nombre);
        } catch (CupoExcedidoException | IllegalArgumentException e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error(e,"Error al evaluar inscripciones: ");
            throw new Exception("Ocurrió un error al guardar las evaluaciones de los estudiantes.");
        } finally {
            db.close();
            db = null;
        }
    }
    
    public List<DtInscripcionEdicion> getInscripcionesDeEstudiante() {
        db = DB.createEntityManager();
        List<DtInscripcionEdicion> ret = new ArrayList<>();
        try {
            List<InscripcionEdicion> inscripciones = db.createQuery(
                "SELECT i FROM InscripcionEdicion i WHERE i.estudiante.nickname = :nickname ORDER BY i.fechaInscripcion DESC",
                InscripcionEdicion.class)
                .setParameter("nickname", this.nombre)
                .getResultList();

            for (InscripcionEdicion i : inscripciones) {
                Estudiante e = i.getEstudiante();
                DtUsuario dtEst = new DtUsuario(e.getNickname(), e.getPassword(), e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, TipoUsuario.ESTUDIANTE);

                Edicion ed = i.getEdicion();
                String imgCurso = (ed.getCurso() != null && ed.getCurso().getImagen() != null && !ed.getCurso().getImagen().isEmpty()) 
                        ? ed.getCurso().getImagen() : "usr.png";
                DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), imgCurso);

                String imgEdicion = (ed.getImagen() != null && !ed.getImagen().isEmpty()) 
                        ? ed.getImagen() : "usr.png";
                DtEdicion dtEd = new DtEdicion(ed.getNombre(), ed.getCupo(), ed.getFechaInicio(), ed.getFechaFin(), ed.getFechaPublicacion(), dtCurso, imgEdicion);
                dtEd.setFechaCierre(ed.getFechaCierre());

                DtInscripcionEdicion dtIns = new DtInscripcionEdicion(dtEst, dtEd, i.getFechaInscripcion(), i.getEstado(), 0.0, i.getVideoURL());
                dtIns.setNota(i.getNota());
                ret.add(dtIns);
            }
        } catch (Exception e) {
            Logger.error("Error al obtener inscripciones del estudiante " + this.nombre + ": " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
            db = null;
        }
        return ret;
    }

    /**
     * CU "Ver resultados de evaluaciones en ediciones de cursos" (Docente).
     * Devuelve el acta de esta edición: los estudiantes participantes (inscripción "Aceptada")
     * con la nota obtenida, ordenados por apellido y nombre.
     * Solo se permite si la edición existe, está cerrada y el docente está asignado a ella.
     */
    public List<DtInscripcionEdicion> getActaEdicion(String nicknameDocente) throws Exception {
        db = DB.createEntityManager();
        try {
            Edicion ed = getEdicionDelDocente(nicknameDocente);
            if (!ed.isCerrada())
                throw new IllegalArgumentException("La edición '" + this.nombre + "' todavía no fue cerrada, por lo que sus resultados no son definitivos.");
            return toDtParticipantes(ed, getAceptadas());
        } finally {
            if (db != null && db.isOpen()) db.close();
            db = null;
        }
    }

    /**
     * CU "Ingresar resultados de evaluaciones de estudiantes en ediciones de cursos" (Docente).
     * Devuelve los estudiantes aceptados en esta edición con la nota que tengan cargada hasta el momento.
     * Solo se permite si la edición existe, el docente está asignado a ella y todavía no fue cerrada.
     */
    public List<DtInscripcionEdicion> getParticipantesParaEvaluar(String nicknameDocente) throws Exception {
        db = DB.createEntityManager();
        try {
            Edicion ed = getEdicionDelDocente(nicknameDocente);
            if (ed.isCerrada())
                throw new IllegalArgumentException("La edición '" + this.nombre + "' ya fue cerrada: las notas no se pueden modificar.");
            return toDtParticipantes(ed, getAceptadas());
        } finally {
            if (db != null && db.isOpen()) db.close();
            db = null;
        }
    }

    /**
     * CU "Ingresar resultados de evaluaciones de estudiantes en ediciones de cursos" (Docente).
     * Registra las notas (escala 1 al 12) de los estudiantes aceptados en esta edición.
     * @param notas         Key = nickname del estudiante, Value = nota (null borra la nota cargada)
     * @param cerrarEdicion si es true, además cierra la edición (fecha de cierre = hoy). Para cerrar,
     *                      todos los estudiantes aceptados deben tener nota; después ya no se pueden modificar.
     */
    public void ingresarResultados(String nicknameDocente, Map<String, Integer> notas, boolean cerrarEdicion) throws Exception {
        db = DB.createEntityManager();
        try {
            db.getTransaction().begin();

            Edicion ed = getEdicionDelDocente(nicknameDocente);
            if (ed.isCerrada())
                throw new IllegalArgumentException("La edición '" + this.nombre + "' ya fue cerrada: las notas no se pueden modificar.");

            Map<String, InscripcionEdicion> aceptadas = new java.util.LinkedHashMap<>();
            for (InscripcionEdicion i : getAceptadas())
                aceptadas.put(i.getEstudiante().getNickname(), i);

            if (notas != null) {
                for (Map.Entry<String, Integer> entry : notas.entrySet()) {
                    InscripcionEdicion inscripcion = aceptadas.get(entry.getKey());
                    if (inscripcion == null)
                        throw new IllegalArgumentException("El estudiante '" + entry.getKey() + "' no está aceptado en la edición '" + this.nombre + "'.");
                    Integer nota = entry.getValue();
                    if (nota != null && (nota < 1 || nota > 12))
                        throw new IllegalArgumentException("La nota de '" + entry.getKey() + "' debe estar entre 1 y 12.");
                    inscripcion.setNota(nota);
                }
            }

            if (cerrarEdicion) {
                if (aceptadas.isEmpty())
                    throw new IllegalArgumentException("La edición '" + this.nombre + "' no tiene estudiantes aceptados para evaluar.");
                List<String> sinNota = new ArrayList<>();
                for (InscripcionEdicion i : aceptadas.values())
                    if (i.getNota() == null) sinNota.add(i.getEstudiante().getNickname());
                if (!sinNota.isEmpty())
                    throw new IllegalArgumentException("Para cerrar la edición todos los estudiantes deben tener nota. Falta la nota de: " + String.join(", ", sinNota) + ".");
                ed.setFechaCierre(LocalDate.now());
            }

            db.getTransaction().commit();
            Logger.info("Resultados ingresados para la edición " + this.nombre + (cerrarEdicion ? " (edición cerrada)" : ""));
        } catch (IllegalArgumentException e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error(e, "Error al ingresar resultados: ");
            throw new Exception("Ocurrió un error al guardar las notas de los estudiantes.");
        } finally {
            if (db != null && db.isOpen()) db.close();
            db = null;
        }
    }

    //********************* resultados de evaluaciones *************************
    // Busca esta edición (con el EntityManager abierto) y verifica que el docente esté asignado a ella.
    private Edicion getEdicionDelDocente(String nicknameDocente) {
        Edicion ed = db.find(Edicion.class, this.nombre);
        if (ed == null)
            throw new IllegalArgumentException("La edición '" + this.nombre + "' no existe.");

        boolean esDocenteDeLaEdicion = false;
        if (ed.getDocentes() != null)
            for (Docente d : ed.getDocentes())
                if (d.getNickname().equals(nicknameDocente)) esDocenteDeLaEdicion = true;
        if (!esDocenteDeLaEdicion)
            throw new IllegalArgumentException("Usted no es docente de la edición '" + this.nombre + "'.");
        return ed;
    }

    // Inscripciones "Aceptada" de esta edición (los participantes), ordenadas por apellido y nombre.
    private List<InscripcionEdicion> getAceptadas() {
        return db.createQuery(
            "SELECT i FROM InscripcionEdicion i WHERE i.edicion.nombre = :ed AND i.estado = :est " +
            "ORDER BY i.estudiante.apellido, i.estudiante.nombre", InscripcionEdicion.class)
            .setParameter("ed", this.nombre)
            .setParameter("est", ACEPTADO)
            .getResultList();
    }

    private List<DtInscripcionEdicion> toDtParticipantes(Edicion ed, List<InscripcionEdicion> participantes) {
        DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), ed.getCurso().getImagen());
        DtEdicion dtEd = new DtEdicion(ed.getNombre(), ed.getCupo(), ed.getFechaInicio(), ed.getFechaFin(), ed.getFechaPublicacion(), dtCurso, ed.getImagen());
        dtEd.setFechaCierre(ed.getFechaCierre());

        List<DtInscripcionEdicion> ret = new ArrayList<>();
        for (InscripcionEdicion i : participantes) {
            Estudiante e = i.getEstudiante();
            DtUsuario dtEst = new DtUsuario(e.getNickname(), null, e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, TipoUsuario.ESTUDIANTE);
            DtInscripcionEdicion dtIns = new DtInscripcionEdicion(dtEst, dtEd, i.getFechaInscripcion(), i.getEstado(), 0.0, i.getVideoURL());
            dtIns.setNota(i.getNota());
            ret.add(dtIns);
        }
        return ret;
    }

    /**
     * CU "Listar resultados de evaluaciones en ediciones de cursos" (Estudiante).
     * Devuelve todas las inscripciones del estudiante (this.nombre = nickname) a ediciones de cursos cerradas,
     * con la nota y el resultado de la evaluacion (Aprobado / No aprobado), de la edicion cerrada mas reciente a la mas vieja.
     * Las inscripciones que no fueron aceptadas no tienen evaluacion: nota y resultado quedan en null.
     */
    public List<DtInscripcionEdicion> getResultadosEvaluacionesDeEstudiante() {
        db = DB.createEntityManager();
        try {
            Estudiante e = db.find(Estudiante.class, this.nombre);
            if (e == null)
                throw new IllegalArgumentException("Solo los estudiantes pueden consultar sus resultados de evaluaciones.");

            List<InscripcionEdicion> inscripciones = db.createQuery(
                "SELECT i FROM InscripcionEdicion i WHERE i.estudiante.nickname = :nick AND i.edicion.fechaCierre IS NOT NULL " +
                "ORDER BY i.edicion.fechaCierre DESC, i.edicion.nombre", InscripcionEdicion.class)
                .setParameter("nick", this.nombre)
                .getResultList();

            DtUsuario dtEst = new DtUsuario(e.getNickname(), null, e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, TipoUsuario.ESTUDIANTE);
            List<DtInscripcionEdicion> ret = new ArrayList<>();
            for (InscripcionEdicion i : inscripciones) {
                Edicion ed = i.getEdicion();
                String imgCurso = (ed.getCurso().getImagen() != null && !ed.getCurso().getImagen().isEmpty()) ? ed.getCurso().getImagen() : "usr.png";
                String imgEdicion = (ed.getImagen() != null && !ed.getImagen().isEmpty()) ? ed.getImagen() : "usr.png";
                DtEdicion dtEd = new DtEdicion(ed.getNombre(), ed.getCupo(), ed.getFechaInicio(), ed.getFechaFin(), ed.getFechaPublicacion(),
                        new DtCurso(ed.getCurso().getNombre(), imgCurso), imgEdicion);
                dtEd.setFechaCierre(ed.getFechaCierre());

                DtInscripcionEdicion dtIns = new DtInscripcionEdicion(dtEst, dtEd, i.getFechaInscripcion(), i.getEstado(), 0.0, i.getVideoURL());
                if (i.getEstado() == ACEPTADO && i.getNota() != null) {
                    dtIns.setNota(i.getNota());
                    dtIns.setResultado(i.getNota() >= ActaEstudioHelper.NOTA_MINIMA_APROBACION ? "Aprobado" : "No aprobado");
                }
                ret.add(dtIns);
            }
            return ret;
        } finally {
            if (db != null && db.isOpen()) db.close();
            db = null;
        }
    }
}
