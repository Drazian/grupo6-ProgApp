package com.edext.logica;
import com.edext.datatypes.DTPrograma;
import com.edext.datatypes.DtInstituto;
import com.edext.datatypes.DtUsuario;
import com.edext.datatypes.DtCurso;
import com.edext.datatypes.DtEdicionCurso;
import com.edext.datatypes.TipoUsuario;
import com.edext.persistencia.Docente;
import com.edext.persistencia.Estudiante;
import com.edext.persistencia.Instituto;
import com.edext.persistencia.Usuario;
import com.edext.persistencia.Curso;
import com.edext.persistencia.EdicionCurso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;

public class Controlador implements IControlador {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("edext");
    
    @Override
    public void modificarUsuario(DtUsuario usuario)throws Exception{
        EntityManager em = emf.createEntityManager();
        
        try{
            em.getTransaction().begin();
            
            Usuario usuarioModificado = em.find(Usuario.class, usuario.getNickname());
            usuarioModificado.setApellido(usuario.getApellido());
            usuarioModificado.setNombre(usuario.getNombre());
            usuarioModificado.setfNacimiento(usuario.getfNacimiento());
            usuarioModificado.setImagen(usuario.getImagen());
            
            if (usuario.getTipoUsuario() == TipoUsuario.DOCENTE) {
                List<Instituto> institutos = new ArrayList<>();
                
                for(String aux: usuario.getInstitutos()){
                    Instituto instituto = em.find(Instituto.class,aux);
                    institutos.add(instituto);
                }
                        
                ((Docente) usuarioModificado).setInstitutos(institutos);
            
            }
            em.getTransaction().commit();
        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;   
        }finally{
            em.close();
        }
    }
    @Override
    public void crearUsuario(DtUsuario usuario) throws Exception{
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Usuario aux = em.find(Usuario.class, usuario.getNickname());
            
            if (aux != null) {
                throw new Exception(
                    "Ya existe un usuario con el nickname '" 
                    + usuario.getNickname() + "'.");
            }
            Usuario auxEmail = em.createQuery(
            "SELECT u FROM Usuario u WHERE u.email = :email",
            Usuario.class).setParameter("email", usuario.getEmail()).getResultStream().findFirst().orElse(null);  
            
            if (auxEmail != null) {
                throw new Exception(
                    "Ya existe un usuario con el Email '" 
                    + usuario.getEmail() + "'.");
            }
            
            Usuario nuevoUsuario;
            
            if (usuario.getTipoUsuario() == TipoUsuario.DOCENTE) {
                List<Instituto> institutos = new ArrayList<>();

                for (String nombre : usuario.getInstitutos()) {
                    Instituto instituto = em.find(Instituto.class, nombre);
                    
                    if (instituto == null) {
                        throw new Exception("No existe el instituto '" + nombre + "'.");
                    }
                    
                    institutos.add(instituto);
                }
                nuevoUsuario = new Docente(
                    usuario.getNickname(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getfNacimiento(),
                    usuario.getImagen(),
                    institutos
                );
            } else {
                nuevoUsuario = new Estudiante(
                    usuario.getNickname(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getfNacimiento(),
                    usuario.getImagen()
                );
            }
            em.persist(nuevoUsuario);
            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }
    
    @Override
    public void crearInstituto(String nombre) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            //Verifico primero si existe un Instituto con ese nombre (aux), de no ser asi lo creo. De existir, envio exception.
            Instituto aux = em.find(Instituto.class,nombre); 
            if (aux == null){
                        Instituto aux2 = new Instituto(nombre);
                        em.persist(aux2);
                        em.getTransaction().commit();
            } else {
                throw new Exception("El instituto con el nombre '"+nombre+"' ya existe.");
            }
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
    @Override
    public void eliminarInstituto(String nombre) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Instituto aux = em.find(Instituto.class, nombre);
            
            if (aux == null){
                throw new Exception ("No se encontró el Instituto con el nombre '"+nombre+"'.");
            }
            
            em.remove(aux);
            
            em.getTransaction().commit();        
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
    @Override
    public List<DtUsuario> listarUsuarios(){
        EntityManager em = emf.createEntityManager();
        
        try{
           List <DtUsuario> resultado = new ArrayList<>();
           List<Usuario> listAux = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
           
           for(Usuario aux: listAux){
               
               if (aux instanceof Docente){
                  List<String> institutos =  new ArrayList<>();
                  
                  for (Instituto instituto : ((Docente) aux).getInstitutos()) {
                      institutos.add(instituto.getNombre());
                  }
                  resultado.add(new DtUsuario(aux.getNickname(), aux.getEmail(), aux.getNombre(), 
                          aux.getApellido(), aux.getImagen(), aux.getfNacimiento(), 
                          institutos , TipoUsuario.DOCENTE));
                  
               }else{
                   resultado.add(new DtUsuario(aux.getNickname(), aux.getEmail(), aux.getNombre(), 
                          aux.getApellido(), aux.getImagen(), aux.getfNacimiento(), null, TipoUsuario.ESTUDIANTE));
               }
               
           }
            return resultado;
        }finally {
            em.close();
        }
    }
    
    @Override
    public List<DtInstituto> listarInstitutos() throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            List<DtInstituto> resultado = new ArrayList<>();
            
            List<Instituto> listaAux = em.createQuery("SELECT i FROM Instituto i",Instituto.class).getResultList();
            for (Instituto aux: listaAux){
                resultado.add(new DtInstituto(aux.getNombre()));
            }
            return resultado;     
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
    
    
    
    @Override
    public boolean existeUsuario(String nickname) throws Exception {
        EntityManager em = emf.createEntityManager();

        try {
            Usuario usuario = em.find(Usuario.class, nickname);
            return usuario != null;

        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean existeEmail(String email) throws Exception {
        EntityManager em = emf.createEntityManager();

        try {
            Usuario usuario = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email",
                Usuario.class
            )
            .setParameter("email", email)
            .getResultStream()
            .findFirst()
            .orElse(null);

            return usuario != null;

        } finally {
            em.close();
        }
    }

    
    @Override
    public void altaCurso(DtCurso curso, String nombreInstituto) throws Exception {
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();

            // 1. Validar que el curso no exista previamente
            Curso existe = em.find(Curso.class, curso.getNombre());
            if (existe != null) {
                throw new Exception("Ya existe un curso registrado con el nombre '" + curso.getNombre() + "'.");
            }

            // 2. Obtener el Instituto seleccionado
            Instituto instituto = em.find(Instituto.class, nombreInstituto);
            if (instituto == null) {
                throw new Exception("El instituto '" + nombreInstituto + "' no existe en la base de datos.");
            }

            // 3. Resolver los cursos previos
            List<Curso> previas = new ArrayList<>();
            if (curso.getPrevias() != null && !curso.getPrevias().isEmpty()) {
                for (String nombrePrevia : curso.getPrevias()) {
                    Curso previa = em.find(Curso.class, nombrePrevia);
                    if (previa == null) {
                        throw new Exception("No se encontró el curso previo '" + nombrePrevia + "'.");
                    }
                    previas.add(previa);
                }
            }

            // 4. Crear la entidad y persistir
            Curso nuevoCurso = new Curso(
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getDuracion(),
                curso.getCantidadHoras(),
                curso.getCreditos(),
                curso.getUrl(),
                curso.getFechaRegistro(),
                instituto,
                previas
            );

            em.persist(nuevoCurso);
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<String> listarNombresCursos() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            // Consulta rápida para cargar el JList de "Previas" en la interfaz
            return em.createQuery("SELECT c.nombre FROM Curso c", String.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<String> listarCursosPorInstituto(String nombreInstituto) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c.nombre FROM Curso c WHERE c.instituto.nombre = :inst", String.class)
                     .setParameter("inst", nombreInstituto)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public com.edext.datatypes.DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            Curso c = em.find(Curso.class, nombreCurso);
            if (c == null) throw new Exception("El curso no existe.");

            // Buscar las ediciones asociadas a este curso
            List<String> ediciones = em.createQuery("SELECT e.nombre FROM EdicionCurso e WHERE e.curso.nombre = :curso", String.class)
                                       .setParameter("curso", nombreCurso)
                                       .getResultList();

            // TODO: Cuando mapees la entidad ProgramaFormacion, haz la consulta real aquí.
            List<String> programas = new java.util.ArrayList<>(); 

            return new com.edext.datatypes.DtConsultaCurso(
                c.getNombre(), c.getDescripcion(), c.getDuracion(),
                c.getCantidadHoras(), c.getCreditos(), c.getUrl(), c.getFechaRegistro(),
                ediciones, programas
            );
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<String> listarDocentes() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT d.nickname FROM Docente d", String.class).getResultList();
        } finally {
            em.close();
        }
    }

@Override
    public void altaEdicionCurso(String nombreCurso, com.edext.datatypes.DtEdicionCurso dt) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Verificar unicidad del nombre de la edición
            EdicionCurso existe = em.find(EdicionCurso.class, dt.getNombre());
            if (existe != null) {
                throw new Exception("Ya existe una Edición de Curso con el nombre: " + dt.getNombre());
            }

            // 2. Buscar el curso padre al que pertenece esta edición
            Curso curso = em.find(Curso.class, nombreCurso);
            if (curso == null) {
                throw new Exception("El curso seleccionado no existe en la base de datos.");
            }

            // 3. Crear la nueva edición y pasarle los datos del Datatype
            EdicionCurso nuevaEdicion = new EdicionCurso();
            nuevaEdicion.setNombre(dt.getNombre());
            nuevaEdicion.setFechaInicio(dt.getFechaInicio());
            nuevaEdicion.setFechaFin(dt.getFechaFin());
            
            if (dt.getCupo() != null) {
                nuevaEdicion.setCupo(dt.getCupo());
            }
            
            // Guardamos la fecha y vinculamos el curso
            nuevaEdicion.setFechaPublicacion(dt.getFechaPublicacion()); 
            nuevaEdicion.setCurso(curso); 

            // 4. Buscar los docentes en la base de datos y asignarlos
            java.util.List<Docente> docentesAsignados = new java.util.ArrayList<>();
            for (String nick : dt.getDocentes()) {
                Docente d = em.find(Docente.class, nick);
                if (d != null) {
                    docentesAsignados.add(d);
                }
            }
            
            // Asignamos la lista de docentes a la entidad
            nuevaEdicion.setDocentes(docentesAsignados);

            // 5. Persistir en la base de datos
            em.persist(nuevaEdicion);
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }    
    public boolean setCrearProgramaFormacion(DTPrograma programa) throws Exception {
        boolean flag=false;
        CreaPograFormaHelper manage=new CreaPograFormaHelper(emf, programa);
        try {
            flag=manage.validate();
            if(flag)
                flag=manage.persist();
        } catch (Exception e) {
            flag=false;
        }finally{
            manage.kill();
            manage=null;
        }
        return flag;
    }
}
