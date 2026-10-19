package edu.edext.logica;

import jakarta.persistence.EntityManagerFactory;
import edu.edext.datatypes.DtSessionUsuario;
import edu.edext.datatypes.DtConsultaCurso;
import jakarta.persistence.EntityManager;
import edu.edext.datatypes.DtCategoria;
import edu.edext.datatypes.DtInstituto;
import jakarta.persistence.Persistence;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtCurso;
import edu.edext.tools.Utils;
import edu.edext.tools.Utils.dbData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controlador implements IControlador {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("edext");

//******************************** Institutos ********************************//
    @Override
    public void crearInstituto(String nombre) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
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
    public String obtenerInstitutoPorCurso(String nombreCurso) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT c.instituto.nombre " +
                    "FROM Curso c " +
                    "WHERE c.nombre = :nombreCurso",
                    String.class
            )
            .setParameter("nombreCurso", nombreCurso)
            .getSingleResult();
        }
    }    

//******************************** Categrias *********************************//
    @Override
    public void crearCategoria(String nombre) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Categoria aux = em.find(Categoria.class,nombre); 
            if (aux == null){
                        Categoria aux2 = new Categoria(nombre);
                        em.persist(aux2);
                        em.getTransaction().commit();
            } else {
                throw new Exception("La categoria con el nombre '"+nombre+"' ya existe.");
            }
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    @Override
    public List<DtCategoria> listarCategorias() throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            List<DtCategoria> resultado = new ArrayList<>();
            List<Categoria> listaAux = em.createQuery("SELECT i FROM Categoria i",Categoria.class).getResultList();
            for (Categoria aux: listaAux){
                resultado.add(new DtCategoria(aux.getNombre()));
            }
            return resultado;     
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
//********************************* Usuarios *********************************//
    @Override
    public void modificarUsuario(DtUsuario usuario)throws Exception{
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Usuario usuarioModificado = em.find(Usuario.class, usuario.getNickname());
            if(!usuarioModificado.getApellido().equals(usuario.getApellido()))
                usuarioModificado.setApellido(usuario.getApellido());
            if(!usuarioModificado.getNombre().equals(usuario.getNombre()))
                usuarioModificado.setNombre(usuario.getNombre());
            if(!usuarioModificado.getfNacimiento().equals(usuario.getfNacimiento()))
                usuarioModificado.setfNacimiento(usuario.getfNacimiento());
            if(!usuarioModificado.getImagen().equals(usuario.getImagen()))
                usuarioModificado.setImagen(usuario.getImagen());
            if(!usuarioModificado.getEmail().equals(usuario.getEmail()))
                if(!existeEmail(usuario.getEmail()))
                    usuarioModificado.setEmail(usuario.getEmail());
            
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
            String password=Utils.Crypt.encodeBCrypt(Utils.Crypt.descifrarAES(usuario.getPassword()));
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
                    password,
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
                    password,
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
    public List<DtUsuario> listarUsuarios(){
        try(EntityManager em = emf.createEntityManager()) {
           List <DtUsuario> resultado = new ArrayList<>();
           List<Usuario> listAux = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
           for(Usuario aux: listAux){
               if (aux instanceof Docente){
                  List<String> institutos =  new ArrayList<>();
                  for (Instituto instituto : ((Docente) aux).getInstitutos()) {
                      institutos.add(instituto.getNombre());
                  }
                  resultado.add(new DtUsuario(aux.getNickname(), aux.getPassword(), aux.getEmail(), aux.getNombre(), 
                          aux.getApellido(), aux.getImagen(), aux.getfNacimiento(), 
                          institutos , TipoUsuario.DOCENTE));
               }else{
                   resultado.add(new DtUsuario(aux.getNickname(), aux.getPassword(), aux.getEmail(), aux.getNombre(), 
                          aux.getApellido(), aux.getImagen(), aux.getfNacimiento(), null, TipoUsuario.ESTUDIANTE));
               }
           }
            return resultado;
        }
    }
    @Override
    public List<String> listarDocentes() throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT d.nickname FROM Docente d", String.class).getResultList();
        }
    }
    @Override
    public boolean existeUsuario(String nickname) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            Usuario usuario = em.find(Usuario.class, nickname);
            return usuario != null;
        }
    }
    @Override
    public boolean existeEmail(String email) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            Usuario usuario = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email",
                Usuario.class
            )
            .setParameter("email", email)
            .getResultStream()
            .findFirst()
            .orElse(null);
            return usuario != null;
        }
    }
    @Override
    public DtSessionUsuario validarNick(String nickname, String password) throws Exception{
        DtSessionUsuario ret;
        UsuarioHelper tmp=new UsuarioHelper(emf);
        ret=tmp.validaNick(nickname, password);
        tmp=null;
        return ret;
    }
    @Override
    public DtSessionUsuario validarEmail(String email, String password) throws Exception{
        DtSessionUsuario ret;
        UsuarioHelper tmp=new UsuarioHelper(emf);
        ret=tmp.validarEmail(email, password);
        tmp=null;
        return ret;
    }

//********************************* Cursos ***********************************//
    @Override
    public void altaCurso(DtCurso curso, String nombreInstituto) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Curso existe = em.find(Curso.class, curso.getNombre());
            if (existe != null) {
                throw new Exception("Ya existe un curso registrado con el nombre '" + curso.getNombre() + "'.");
            }
            Instituto instituto = em.find(Instituto.class, nombreInstituto);
            if (instituto == null) {
                throw new Exception("El instituto '" + nombreInstituto + "' no existe en la base de datos.");
            }
            List<Curso> previas=null;
            if(!Utils.Validate.isEmpty(curso.getNamePrevias())){
                previas = new ArrayList<>();
                for (String nombrePrevia : curso.getPrevias_toArray()) {
                    Curso previa = em.find(Curso.class, nombrePrevia);
                    if (previa == null) {
                        throw new Exception("No se encontró el curso previo '" + nombrePrevia + "'.");
                    }
                    previas.add(previa);
                }
            }
            List<Categoria> categoriasAsignadas=null;
            if(!Utils.Validate.isEmpty(curso.getNameCategorias())){
                categoriasAsignadas = new ArrayList<>();
                for (String nombreCat : curso.getCategorias_toArray()) {
                    Categoria cat = em.find(Categoria.class, nombreCat);
                    if (cat == null) {
                        throw new Exception("No se encontró la categoría '" + nombreCat + "'.");
                    }
                    categoriasAsignadas.add(cat);
                }
            }
            Curso nuevoCurso = new Curso(
                    curso.getNombre(),
                    curso.getDescripcion(),
                    curso.getDuracion(),
                    curso.getCantidadHoras(),
                    curso.getCreditos(),
                    curso.getUrl(),
                    curso.getFechaRegistro(),
                    instituto,
                    categoriasAsignadas,
                    previas,
                    curso.getImagen()
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
        try (EntityManager em = emf.createEntityManager()) {
            // Consulta rápida para cargar el JList de "Previas" en la interfaz
            return em.createQuery("SELECT c.nombre FROM Curso c", String.class).getResultList();
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
    public List<String> listarCursosPorCategoria(String nombreCategoria) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c.nombre FROM Curso c WHERE :cat MEMBER OF c.categorias", String.class)
                     .setParameter("cat", nombreCategoria)
                     .getResultList();
        }    
    }
    @Override
    public List<DtCurso> listarCursosPorUsuario(String nickname) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            List<DtCurso> resultado = new ArrayList<>();
            Usuario usuario = em.find(Usuario.class, nickname);
            if (usuario == null) {
                throw new Exception("No existe el usuario '" + nickname + "'.");
            }
            if (usuario instanceof Docente) {
                List<Curso> cursos = em.createQuery(
                    "SELECT DISTINCT e.curso " +
                    "FROM Edicion e " +
                    "JOIN e.docentes d " +
                    "WHERE d.nickname = :nickname",
                    Curso.class
                )
                .setParameter("nickname", nickname)
                .getResultList();
                for (Curso aux : cursos) {
                    Set<String> nombresPrevias = new HashSet<>();
                    if (aux.getPrevias() != null) {
                        for (Curso previa : aux.getPrevias()) {
                            nombresPrevias.add(previa.getNombre());
                        }
                    }
                    Set<String> nombresCategorias = new HashSet<>();
                    if (aux.getCategorias() != null) {
                        for (Categoria categoria : aux.getCategorias()) {
                            nombresCategorias.add(categoria.getNombre());
                        }
                    }
                    resultado.add(
                            new DtCurso(
                                    aux.getNombre(),
                                    aux.getDescripcion(),
                                    aux.getDuracion(),
                                    aux.getCantidadHoras(),
                                    aux.getCreditos(),
                                    aux.getUrl(),
                                    aux.getFechaRegistro(),
                                    new DtInstituto(aux.getInstituto().getNombre()),
                                    nombresPrevias,
                                    nombresCategorias,
                                    aux.getImagen()
                    ));
                }
            }
            return resultado;
        }
    }
    @Override
    public DtConsultaCurso obtenerDatosCurso(String nombreCurso) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            Curso c = em.find(Curso.class, nombreCurso);
            Curso c2=em.find(Curso.class, nombreCurso);
            if (c == null) throw new Exception("El curso no existe.");

            List<Edicion> Ediciones = em.createQuery(
                    "SELECT e FROM Edicion e WHERE e.curso.nombre = :curso", Edicion.class).setParameter("curso", nombreCurso).getResultList();
            List<ProgramaFormacion> Programas = em.createQuery(
                    "SELECT p FROM ProgramaFormacion p JOIN p.cursos c WHERE c.nombre = :curso", ProgramaFormacion.class).setParameter("curso", nombreCurso).getResultList();

            List<DtCurso> previas=new ArrayList<>();                    
            List<DtCategoria> categorias= new ArrayList<>();
            if (c.getCategorias() != null) {
                for (Categoria categoria : c.getCategorias())
                    categorias.add(new DtCategoria(categoria.getNombre()));
            }
            if(c2.getPrevias()!=null)
                for (Curso p : c2.getPrevias())
                    previas.add(new DtCurso(p.getNombre(), p.getDescripcion(), p.getDuracion(), p.getCantidadHoras(), p.getCreditos(), p.getUrl(), p.getFechaRegistro(), new DtInstituto(p.getInstituto().getNombre()), p.getImagen()));
            List<DtEdicion> ediciones=new ArrayList<>();
            for(Edicion ed : Ediciones) {
                List<DtUsuario> docentes=new ArrayList<>();
                if(ed.getDocentes()!=null)
                    for(Docente d : ed.getDocentes())
                        docentes.add(new DtUsuario(d.getNickname(), null, d.getEmail(), d.getNombre(), d.getApellido(), null, null, null, null));
                Curso ec=ed.getCurso();
                ediciones.add(new DtEdicion(
                        ed.getNombre(),
                        ed.getCupo(),
                        ed.getFechaInicio(),
                        ed.getFechaFin(),
                        ed.getFechaPublicacion(),
                        new DtCurso(
                                ec.getNombre(),
                                ec.getDescripcion(),
                                ec.getDuracion(),
                                ec.getCantidadHoras(),
                                ec.getCreditos(),
                                ec.getUrl(),
                                ec.getFechaRegistro(),
                                new DtInstituto(ec.getInstituto().getNombre()),
                                ec.getImagen()),
                        docentes, ed.getImagen()));
            }
            List<DtPrograma> dtProgramas= new ArrayList<>();
            for(ProgramaFormacion pg : Programas){
                Set<String> cursos=new HashSet<>(); 
                if(pg.getCursos()!=null)
                    for (Curso pc : pg.getCursos())
                        cursos.add(pc.getNombre());
                dtProgramas.add(new DtPrograma(pg.getNombre(), pg.getDescripcion(), pg.getFechaRegistro(), pg.getFechaInicio(), pg.getFechaFin(), cursos, pg.getImagen()));
            }
            return new DtConsultaCurso(
                c.getNombre(), c.getDescripcion(), c.getDuracion(),
                c.getCantidadHoras(), c.getCreditos(), c.getUrl(), c.getFechaRegistro(),
                ediciones, 
                    dtProgramas, 
                    categorias, 
                    previas,
                    c.getImagen());
        }
    }
    @Override
    public List<DtCurso> listarCursos() throws Exception{
        EntityManager em = emf.createEntityManager();
        try{
            List<DtCurso> resultado = new ArrayList<>();         
            List<Curso> listaAux = em.createQuery("SELECT i FROM Curso i", Curso.class).getResultList();
            for (Curso aux : listaAux) {
                Set<String> nombresPrevias = new HashSet<>();
                if (aux.getPrevias() != null) {
                    for (Curso previa : aux.getPrevias()) {
                        nombresPrevias.add(previa.getNombre());
                    }
                }
                Set<String> nombresCategorias = new HashSet<>();
                if (aux.getCategorias() != null){
                    for (Categoria categoria : aux.getCategorias()){
                        nombresCategorias.add(categoria.getNombre());
                    }
                }
                resultado.add(
                        new DtCurso(
                                aux.getNombre(),
                                aux.getDescripcion(),
                                aux.getDuracion(),
                                aux.getCantidadHoras(),
                                aux.getCreditos(),
                                aux.getUrl(),
                                aux.getFechaRegistro(),
                                new DtInstituto(aux.getInstituto().getNombre()),
                                nombresPrevias,
                                nombresCategorias,
                                aux.getImagen()
                ));
            }
            return resultado;          
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }

//********************************* Edicion **********************************//
    @Override
    public void altaEdicionCurso(String nombreCurso, edu.edext.datatypes.DtEdicion dt) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Edicion existe = em.find(Edicion.class, dt.getNombre());
            if (existe != null) {
                throw new Exception("Ya existe una Edición de Curso con el nombre: " + dt.getNombre());
            }
            Curso curso = em.find(Curso.class, nombreCurso);
            if (curso == null) {
                throw new Exception("El curso seleccionado no existe en la base de datos.");
            }
            Edicion nuevaEdicion = new Edicion();
            nuevaEdicion.setNombre(dt.getNombre());
            nuevaEdicion.setFechaInicio(dt.getFechaInicio());
            nuevaEdicion.setFechaFin(dt.getFechaFin());
            if (dt.getCupo() != null) {
                nuevaEdicion.setCupo(dt.getCupo());
            }
            nuevaEdicion.setFechaPublicacion(dt.getFechaPublicacion()); 
            nuevaEdicion.setCurso(curso); 
            List<Docente> docentesAsignados = new ArrayList<>();
            for (String nick : dt.getNameDocentes()) {
                Docente d = em.find(Docente.class, nick);
                if (d != null) {
                    docentesAsignados.add(d);
                }
            }
            nuevaEdicion.setDocentes(docentesAsignados);
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
    @Override
    public DtEdicion getEdicion(String nombre) throws Exception{
        DtEdicion ret;
        EdicionHelper seek=new EdicionHelper(emf, nombre);
        try { ret=seek.getEdicion(); }
        catch (Exception e) { ret=null; }
        finally{ seek=null; }
        return ret;
    }
    @Override
    public List<DtEdicion> listarEdicionPorCurso(String curso) throws Exception{
        List<DtEdicion> ret;
        EdicionHelper seek=new EdicionHelper(emf, curso);
        try { ret=seek.getListEditPorCurso(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        finally{ seek=null; }
        return ret;
    }
    @Override
    public List<String> listaEdicionPorCurso(String curso) throws Exception{
        List<String> ret;
        EdicionHelper seek=new EdicionHelper(emf, curso);
        try { ret=seek.listarEdicionPorCurso(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        finally{ seek=null; }
        return ret;        
    }
    @Override
    public List<DtEdicion> listarEdicionesPorUsuario(String nickname) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            List<DtEdicion> resultado = new ArrayList<>();
            Usuario usuario = em.find(Usuario.class, nickname);
            if (usuario == null) {
                throw new Exception("No existe el usuario '" + nickname + "'.");
            }
            List<Edicion> ediciones;
            if (usuario instanceof Docente) {
                ediciones = em.createQuery(
                    "SELECT DISTINCT e " +
                    "FROM Edicion e " +
                    "JOIN e.docentes d " +
                    "WHERE d.nickname = :nickname",
                    Edicion.class
                )
                .setParameter("nickname", nickname)
                .getResultList();

            } else {
                ediciones = em.createQuery(
                    "SELECT ie.edicion " +
                    "FROM InscripcionEdicion ie " +
                    "WHERE ie.estudiante.nickname = :nickname",
                    Edicion.class
                )
                .setParameter("nickname", nickname)
                .getResultList();
            }
            for (Edicion aux : ediciones) {
                Curso curso = aux.getCurso();
                Set<String> nombresPrevias = new HashSet<>();
                if (curso.getPrevias() != null) {
                    for (Curso previa : curso.getPrevias()) {
                        nombresPrevias.add(previa.getNombre());
                    }
                }
                Set<String> nombresCategorias = new HashSet<>();
                if (curso.getPrevias() != null) {
                    for (Categoria categoria : curso.getCategorias()) {
                        nombresCategorias.add(categoria.getNombre());
                    }
                }
                DtCurso dtCurso = new DtCurso(
                        curso.getNombre(),
                        curso.getDescripcion(),
                        curso.getDuracion(),
                        curso.getCantidadHoras(),
                        curso.getCreditos(),
                        curso.getUrl(),
                        curso.getFechaRegistro(),
                        new DtInstituto(curso.getInstituto().getNombre()),
                        nombresPrevias,
                        nombresCategorias,
                        curso.getImagen()
                );
                Set<String> nombresDocentes = new HashSet<>();

                if (aux.getDocentes() != null) {
                    for (Docente docente : aux.getDocentes()) {
                        nombresDocentes.add(docente.getNickname());
                    }
                }

                resultado.add(new DtEdicion(
                    aux.getNombre(),
                    aux.getCupo(),
                    aux.getFechaInicio(),
                    aux.getFechaFin(),
                    aux.getFechaPublicacion(),
                    dtCurso,
                    nombresDocentes
                ));
            }
            return resultado;
        }
    }    
    @Override
    public DtEdicion buscarEdicion(String nombre) throws Exception{
        return getEdicion(nombre);
    }

//******************************* Inscripcion ********************************//
    @Override
    public int setInscripcion(String nomEstudiante, String nomEdicion) throws Exception{
        InscripcionEdicionHelper regInscrip=new InscripcionEdicionHelper(emf, "");
        int ret=-1;
        try {
            ret=regInscrip.inscribirEstudiante(nomEstudiante, nomEdicion);
        } catch (Exception e) {}
        finally{
            regInscrip.kill();
            regInscrip=null; }
        return ret;
    }
    @Override
    public int delInscripcion(String estudiante, String edicion) throws Exception{
        InscripcionEdicionHelper cancel=new InscripcionEdicionHelper(emf, "");
        int ret=-1;
        try {
            ret=cancel.cancelarInscripcion(estudiante, edicion);
        } catch (Exception e) {}
        finally{
            cancel.kill();
            cancel=null; }
        return ret;
    }
    @Override
    public List<String> getEstudiantesInscriptosEdicion(String nombre) throws Exception{
        List<String> ret;
        InscripcionEdicionHelper inscrip=new InscripcionEdicionHelper(emf, nombre);
        try { ret=inscrip.getEstudiantesInscriptos(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        finally{ 
            inscrip.kill();
            inscrip=null; }
        return  ret;
    }
    @Override
    public List<String> getEstudiantesCandidatosEdicion(String nombre) throws Exception{
        List<String> ret;
        InscripcionEdicionHelper candidatos=new InscripcionEdicionHelper(emf, nombre);
        try { ret=candidatos.getEstudiantesCandidatos(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        finally{
            candidatos.kill();
            candidatos=null;
        }
        return ret;
    }

//**************************** ProgramaFormacion *****************************//
    @Override
    public boolean setCrearProgramaFormacion(DtPrograma programa) throws Exception {
        boolean flag=false;
        ProgramaFormacionHelper manage=new ProgramaFormacionHelper(emf, programa);
        try {
            flag=manage.validate();
            if(flag)
                flag=manage.persist(); }
        catch (Exception e) {
            flag=false; }
        finally{
            manage.kill();
            manage=null; }
        return flag;
    }
    @Override
    public void agregarProgramaCurso(String programa, String curso) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ProgramaFormacion auxPrograma = em.find(ProgramaFormacion.class,programa);
            Curso auxCurso = em.find(Curso.class, curso);
            if (auxPrograma == null || auxCurso == null){
                throw new Exception("El Programa de Formacion y el Curso deben de existir");
            }
            if (auxPrograma.getCursos().contains(auxCurso)){
                throw new Exception("El curso '" + curso + "' ya pertenece al programa '" + programa + "'.");
            }
            auxPrograma.agregarCurso(auxCurso);
            em.getTransaction().commit();
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    @Override
    public List<DtPrograma> listarProgramas() throws Exception{
        EntityManager em = emf.createEntityManager();
        try{
            List<DtPrograma> resultado = new ArrayList<>();
            List<ProgramaFormacion> listaAux = em.createQuery("SELECT i FROM ProgramaFormacion i", ProgramaFormacion.class).getResultList();
            for (ProgramaFormacion aux: listaAux){
                resultado.add(new DtPrograma(aux.getNombre(), aux.getDescripcion(),aux.getFechaRegistro(),aux.getFechaInicio(),aux.getFechaFin()));
            }
            return resultado;          
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    @Override
    public List<DtCurso> listarCursosPorPrograma(String nombre) throws Exception{//
        EntityManager em = emf.createEntityManager();
        try{
            ProgramaFormacion programa = em.find(ProgramaFormacion.class, nombre);
            if (programa == null) {
                throw new Exception("El programa '" + nombre + "' no existe.");
            }
            List<DtCurso> resultado = new ArrayList<>();
            for (Curso aux : programa.getCursos()) {
                Set<String> nombresPrevias = new HashSet<>();
                if (aux.getPrevias() != null) {
                    for (Curso previa : aux.getPrevias()) {
                        nombresPrevias.add(previa.getNombre());
                    }
                }
                Set<String> nombresCategorias = new HashSet<>();
                if (aux.getCategorias() != null){
                    for (Categoria categoria : aux.getCategorias()){
                        nombresCategorias.add(categoria.getNombre());
                    }
                }
                resultado.add(new DtCurso(
                        aux.getNombre(),                                    
                        aux.getDescripcion(),                               
                        aux.getDuracion(),                                  
                        aux.getCantidadHoras(),                             
                        aux.getCreditos(),                                  
                        aux.getUrl(),                                       
                        aux.getFechaRegistro(),                             
                        new DtInstituto(aux.getInstituto().getNombre()),    
                        nombresPrevias,
                        nombresCategorias,
                        aux.getImagen()
                ));
            }
            return resultado;
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    @Override
    public List<DtPrograma> listarProgramasPorUsuario(String nickname) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            List<DtPrograma> resultado = new ArrayList<>();
            Usuario usuario = em.find(Usuario.class, nickname);
            if (usuario == null) {
                throw new Exception("No existe el usuario '" + nickname + "'.");
            }
            List<ProgramaFormacion> programas;
            if (usuario instanceof Docente) {
                programas = em.createQuery(
                    "SELECT DISTINCT p " +
                    "FROM ProgramaFormacion p " +
                    "JOIN p.cursos c " +
                    "WHERE c.nombre IN (" +
                    "   SELECT DISTINCT e.curso.nombre " +
                    "   FROM Edicion e " +
                    "   JOIN e.docentes d " +
                    "   WHERE d.nickname = :nickname" +
                    ")",
                    ProgramaFormacion.class
                )
                .setParameter("nickname", nickname)
                .getResultList();
            } else {
                programas = em.createQuery(
                    "SELECT ip.programa " +
                    "FROM InscripcionPrograma ip " +
                    "WHERE ip.estudiante.nickname = :nickname",
                    ProgramaFormacion.class
                )
                .setParameter("nickname", nickname)
                .getResultList();
            }
            for (ProgramaFormacion aux : programas) {
                resultado.add(new DtPrograma(
                    aux.getNombre(),
                    aux.getDescripcion(),
                    aux.getFechaRegistro(),
                    aux.getFechaInicio(),
                    aux.getFechaFin()
                ));
            }
            return resultado;
        }
    }
    @Override
    public DtPrograma buscarPrograma(String nombre) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            ProgramaFormacion programa = em.find(ProgramaFormacion.class, nombre);
            if (programa == null) {
                throw new Exception("No existe ningún programa con el nombre: " + nombre);
            }
            List<DtCurso> dtCursos = new ArrayList<>();
            if (programa.getCursos() != null) {
                for (Curso c : programa.getCursos()) {
                    Set<String> categorias = new HashSet<>(), previas=new HashSet<>();
                    DtInstituto dtInst = null;
                    if (c.getInstituto() != null) {
                        dtInst = new DtInstituto(c.getInstituto().getNombre());
                    }
                    for(Curso previa : c.getPrevias()) previas.add(previa.getNombre());
                    for (Categoria categoria : c.getCategorias()) categorias.add(categoria.getNombre());
                    DtCurso dtc = new DtCurso(
                            c.getNombre(),
                            c.getDescripcion(),
                            c.getDuracion(),
                            c.getCantidadHoras(),
                            c.getCreditos(),
                            c.getUrl(),
                            c.getFechaRegistro(),
                            dtInst,
                            previas,
                            categorias,
                            c.getImagen()
                    );
                    dtCursos.add(dtc);
                }
            }
            //  pero si el DTO maneja localDate y la Entity tambien la maneja
            //  porque hacer  Entity -> localDate -> Object -> localDate -> DTO
            //  serian un casteo innecesario y hasta se podria considerar un antipatron,
            //  porque java ya tiene un fuerte tipado
//            LocalDate fRegistro = parseToLocalDate(programa.getFechaRegistro());
//            LocalDate fInicio   = parseToLocalDate(programa.getFechaInicio());
//            LocalDate fFin      = parseToLocalDate(programa.getFechaFin());
//            return new DtPrograma(
//                programa.getNombre(), 
//                programa.getDescripcion(), 
//                fRegistro, 
//                fInicio, 
//                fFin,
//                dtCursos
//            );
            return new DtPrograma(
                programa.getNombre(), 
                programa.getDescripcion(), 
                programa.getFechaRegistro(),
                programa.getFechaInicio(),
                programa.getFechaFin(),
                dtCursos
            );
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    //Metodo auxiliar para implementar buscarPrograma().
    //Hubiera estado bueno ponerse de acuerdo para usar LocalDate O Date pero no ambas a la vez.
    //Respuesta:  Date esa marcada como obsoleta desde 1997, arrastra fallos internos,
    //            como la total carencia de zonas, es inconsistente e inherentemente inestable
    //            al ser mutable, posee complejidad interna innecesaria y muchos metodos obsoletos
    //            para mitigarlo habia dejado un metodo sobrecargado en la clase libreria
    //            Utils.Cast.valueOf(fecha) la cual convierte Date->localDate y localDate->Date
    //            esta ultima es para facilitar la conversion para los que 
    //            prefieran seguir utilizando algo que lleva obsoleto 29 años
    private java.time.LocalDate parseToLocalDate(Object fecha) {
        if (fecha == null) return null;
        if (fecha instanceof java.time.LocalDate) {
            return (java.time.LocalDate) fecha;
        }
        if (fecha instanceof String) {
            String str = (String) fecha;
            return str.isEmpty() ? null : java.time.LocalDate.parse(str);
        }
        if (fecha instanceof java.util.Date) {
            return ((java.util.Date) fecha).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }

//***************************** Datos de Prueba ******************************//
    @Override
    public int cargarDatosDePrueba() throws Exception{
        cargarDatosDePruebaHelper DatosDePrueba=new cargarDatosDePruebaHelper(emf);
        int ret=-1;
        try { ret=DatosDePrueba.cargar(); }
        catch (Exception e) { ret=-1; }
        finally{
            DatosDePrueba.kill();
            DatosDePrueba=null;
        }
        return ret;
    }

//******************************* Nueva probar *******************************//
    

    @Override 
    public void modificarUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Usuario usuarioModificado = em.find(Usuario.class, usuario.getNickname());
            if (usuarioModificado == null) {
                throw new Exception("El usuario '" + usuario.getNickname() + "' no existe.");
            }

            usuarioModificado.setApellido(usuario.getApellido());
            usuarioModificado.setNombre(usuario.getNombre());
            usuarioModificado.setfNacimiento(usuario.getfNacimiento());

            if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
                String hashPassword = edu.edext.tools.Utils.Crypt.encodeBCrypt(usuario.getPassword().trim());
                usuarioModificado.setPassword(hashPassword);
            }

            if (bytesImagen != null && bytesImagen.length > 0 && nombreOriginal != null) {
                String nombreImagen = GestorImagenes.guardarImagen(bytesImagen, nombreOriginal, usuario.getNickname());
                usuarioModificado.setImagen(nombreImagen);
            } else if (usuario.getImagen() != null) {
                usuarioModificado.setImagen(usuario.getImagen());
            }

            if (usuario.getTipoUsuario() == TipoUsuario.DOCENTE && usuario.getInstitutos() != null) {
                List<Instituto> institutos = new ArrayList<>();
                for (String aux : usuario.getInstitutos()) {
                    Instituto instituto = em.find(Instituto.class, aux);
                    if (instituto != null) {
                        institutos.add(instituto);
                    }
                }
                ((Docente) usuarioModificado).setInstitutos(institutos);
            }

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
    public boolean setCrearProgramaFormacion(DtPrograma programa, byte[] bytesImagen, String nombreOriginal) throws Exception {
    String nombreImagen = programa.getImagen();

            if (bytesImagen != null && bytesImagen.length > 0 && nombreOriginal != null) {
                nombreImagen = GestorImagenes.guardarImagen(bytesImagen, "Programa_" + nombreOriginal, "Programa_" + programa.getNombre());
            }

            DtPrograma programaConImagen = new DtPrograma(
                    programa.getNombre(),
                    programa.getDescripcion(),
                    programa.getFechaRegistro(),
                    programa.getFechaInicio(),
                    programa.getFechaFin(),
                    nombreImagen
            );

            boolean resultado = setCrearProgramaFormacion(programaConImagen);
            return resultado;
        }
    
    
    
    @Override 
    public void crearUsuario(DtUsuario usuario, byte[] bytesImagen, String nombreOriginal) throws Exception {
        String nombreImagen = usuario.getImagen();

        if (existeUsuario(usuario.getNickname())) {
            throw new Exception("Ya existe un usuario con el nickname '" + usuario.getNickname() + "'.");
        }

        if (existeEmail(usuario.getEmail())) {
            throw new Exception("Ya existe un usuario con el correo '" + usuario.getEmail() + "'.");
        }

        if (bytesImagen != null && bytesImagen.length > 0 && nombreOriginal != null) {
            nombreImagen = GestorImagenes.guardarImagen(bytesImagen, nombreOriginal, usuario.getNickname());
        }

        DtUsuario usuarioConImagen = new DtUsuario(
            usuario.getNickname(),
            usuario.getPassword(),
            usuario.getEmail(),
            usuario.getNombre(),
            usuario.getApellido(),
            nombreImagen,
            usuario.getfNacimiento(),
            usuario.getInstitutos(),
            usuario.getTipoUsuario()
        );

        crearUsuario(usuarioConImagen);
    }
    

   
    @Override 
    public void seguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception {
        if (nicknameSeguidor.equalsIgnoreCase(nicknameASeguir)) {
            throw new Exception("Un usuario no puede seguirse a sí mismo.");
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Usuario seguidor = em.find(Usuario.class, nicknameSeguidor);
            Usuario seguido = em.find(Usuario.class, nicknameASeguir);

            if (seguidor == null || seguido == null) {
                throw new Exception("Uno de los usuarios no existe.");
            }

            seguidor.agregarSeguido(seguido);

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
    public void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameADejar) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Usuario seguidor = em.find(Usuario.class, nicknameSeguidor);
            Usuario seguido = em.find(Usuario.class, nicknameADejar);

            if (seguidor == null || seguido == null) {
                throw new Exception("Uno de los usuarios no existe.");
            }

            seguidor.removerSeguido(seguido);

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
    public List<String> listarSeguidos(String nickname) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, nickname);
            List<String> res = new ArrayList<>();
            if (usuario != null && usuario.getSeguidos() != null) {
                for (Usuario u : usuario.getSeguidos()) {
                    res.add(u.getNickname());
                }
            }
            return res;
        } finally {
            em.close();
        }
    }

    @Override 
    public List<String> listarSeguidores(String nickname) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            Usuario usuario = em.find(Usuario.class, nickname);
            List<String> res = new ArrayList<>();
            if (usuario != null && usuario.getSeguidores() != null) {
                for (Usuario u : usuario.getSeguidores()) {
                    res.add(u.getNickname());
                }
            }
            return res;
        } finally {
            em.close();
        }
    }

    @Override 
    public java.util.List<edu.edext.datatypes.DtInscripcionEdicion> getInscripcionesCompletas(String nombreEdicion) throws Exception {
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper(emf, nombreEdicion);
        try {
            return helper.getInscripcionesCompletas();
        } finally {
            helper.kill();
        }
    }

    @Override 
    public void evaluarInscripciones(String nombreEdicion, java.util.Map<String, Integer> evaluaciones) throws Exception {
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper(emf, nombreEdicion);
        try {
            helper.evaluarInscripciones(evaluaciones);
        } finally {
            helper.kill();
        }
    }

    @Override
    public java.util.List<edu.edext.datatypes.DtInscripcionEdicion> getInscripcionesDeEstudiante(String nicknameEstudiante) throws Exception {
        InscripcionEdicionHelper helper = new InscripcionEdicionHelper(emf, nicknameEstudiante);
        try {
            return helper.getInscripcionesDeEstudiante();
        } finally {
            helper.kill();
        }
    }
    
    @Override
    public int setInscripcionPrograma(String nomEstudiante, String nomPrograma) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Estudiante est = em.find(Estudiante.class, nomEstudiante);
            ProgramaFormacion prog = em.find(ProgramaFormacion.class, nomPrograma);

            if (est == null || prog == null) {
                throw new Exception("El estudiante o el programa no existen.");
            }

            List<InscripcionPrograma> existentes = em.createQuery(
                "SELECT ip FROM InscripcionPrograma ip WHERE ip.estudiante.nickname = :est AND ip.programa.nombre = :prog",
                InscripcionPrograma.class)
                .setParameter("est", nomEstudiante)
                .setParameter("prog", nomPrograma)
                .getResultList();

            if (!existentes.isEmpty()) {
                return 1;
            }

            InscripcionPrograma nuevaInscripcion = new InscripcionPrograma(est, prog, java.time.LocalDate.now(), 1);
            em.persist(nuevaInscripcion);

            em.getTransaction().commit();
            return 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }



    
}