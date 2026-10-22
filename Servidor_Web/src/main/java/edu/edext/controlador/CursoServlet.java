package edu.edext.controlador;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.logica.GestorImagenes;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List; 
import java.util.Set;

@WebServlet("/CursoServlet")
@MultipartConfig 
public class CursoServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        if (accion == null){
            accion = "error"; 
        }
              
        switch (accion){
            case "buscarCurso":
                String opcion = request.getParameter("opcion"); // "instituto" o "categoria"
                String nombre = request.getParameter("nombre");

                // Reparar codificación ISO a UTF-8 si Tomcat rompe las tildes
                if (nombre != null && nombre.contains("Ã")) {
                    nombre = new String(nombre.getBytes("ISO-8859-1"), "UTF-8");
                }

                try {
                    List<String> cursos = null;
                    if ("instituto".equalsIgnoreCase(opcion)) {
                        cursos = ic.listarCursosPorInstituto(nombre);
                    } else if ("categoria".equalsIgnoreCase(opcion)) {
                        cursos = ic.listarCursosPorCategoria(nombre);
                        
                        List<String> programas = new ArrayList<>();
                        List<DtPrograma> listaProgramas = ic.listarProgramas();
                        
                        if (listaProgramas != null) {
                                for (DtPrograma programa : listaProgramas) {
                                    Set<String> categoriasPrograma = programa.getCategorias();

                                    if (categoriasPrograma != null) {
                                        boolean perteneceACategoria = false;

                                        for (String catStr : categoriasPrograma) {
                                            if (catStr != null) {
                                                String[] subCategorias = catStr.split("[,\\s]+");
                                                for (String subCat : subCategorias) {
                                                    if (subCat.trim().equalsIgnoreCase(nombre)) {
                                                        perteneceACategoria = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (perteneceACategoria) break;
                                        }
                                        if (perteneceACategoria) {
                                            programas.add(programa.getNombre());
                                        }
                                    }
                                }
                            List<String> programasSafe = new ArrayList<>();
                            for(String p : programas) {
                                programasSafe.add(p.replace("\"", "&quot;").replace("'", "&#39;"));
                            }
                            request.setAttribute("listaProgramas", programasSafe);
                        }
                    }
                    if (cursos != null) {
                        List<String> cursosSafe = new ArrayList<>();
                        for (String c : cursos) {
                            cursosSafe.add(c.replace("\"", "&quot;").replace("'", "&#39;"));
                        }
                        request.setAttribute("listaCursos", cursosSafe);
                    } else {
                        request.setAttribute("listaCursos", cursos);
                    }

                    request.setAttribute("filtroAplicado", opcion + ": " + nombre);
                    request.getRequestDispatcher("fragmentos/listaCursos.jsp").forward(request, response);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().write("<div style='padding:20px;'><h3 style='color:#e74c3c;'>Error al buscar cursos:</h3><p>" + e.getMessage() + "</p></div>");
                }
                break;

            case "listarCategoriasJson": 
                try {
                    List<edu.edext.datatypes.DtCategoria> listaCat = ic.listarCategorias();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < listaCat.size(); i++) {
                        json.append("{\"nombre\":\"").append(listaCat.get(i).getNombre()).append("\"}");
                        if (i < listaCat.size() - 1) {
                            json.append(",");
                        }
                    }
                    json.append("]");

                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener categorías");
                }
                break;

            case "listarInstitutosJSON": 
                try {
                    List<edu.edext.datatypes.DtInstituto> listaInst = ic.listarInstitutos();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < listaInst.size(); i++) {
                        json.append("\"").append(listaInst.get(i).getNombre()).append("\"");
                        if (i < listaInst.size() - 1) {
                            json.append(",");
                        }
                    }
                    json.append("]");

                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener institutos");
                }
                break;
            
                case "verDetallesCurso": 
                String nombreDelCurso = request.getParameter("nombreCurso");
                try {
                    if (nombreDelCurso != null && nombreDelCurso.contains("Ã")) {
                        nombreDelCurso = new String(nombreDelCurso.getBytes("ISO-8859-1"), "UTF-8");
                    }
                    
                    edu.edext.datatypes.DtConsultaCurso dtCurso = ic.obtenerDatosCurso(nombreDelCurso);
                    
                    if(dtCurso == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.setContentType("text/html;charset=UTF-8");
                        response.getWriter().write("<div style='padding:20px;'><h3 style='color:#e74c3c;'>El curso no existe en la base de datos.</h3><p style='color:#7f8c8d;'><b>Debug - Buscando exactamente:</b> [" + nombreDelCurso + "]</p></div>");
                        return;
                    }
                    String institutoDelCurso = "Sin instituto asignado";
                    List<edu.edext.datatypes.DtInstituto> todosLosInstitutos = ic.listarInstitutos();
                    if (todosLosInstitutos != null) {
                        for (edu.edext.datatypes.DtInstituto inst : todosLosInstitutos) {
                            List<String> cursosDeEsteInst = ic.listarCursosPorInstituto(inst.getNombre());
                            if (cursosDeEsteInst != null && cursosDeEsteInst.contains(nombreDelCurso)) {
                                institutoDelCurso = inst.getNombre();
                                break;
                            }
                        }
                    }
                    // Pasamos el instituto como una variable separada al JSP
                    request.setAttribute("institutoDelCurso", institutoDelCurso);
                    // =========================================================================
                    
                    request.setAttribute("curso", dtCurso);
                    request.getRequestDispatcher("fragmentos/consultaCurso.jsp").forward(request, response);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().write("<div style='padding:20px;'><h3 style='color:#e74c3c;'>Error al cargar el curso:</h3><p>" + e.getMessage() + "</p><p style='color:#7f8c8d; font-size:13px;'><i>Debug - String recibido: [" + nombreDelCurso + "]</i></p></div>");
                }
                break;
                
            case "altaCurso": 
                HttpSession session = request.getSession();
                DtUsuario usuarioLogueado = (DtUsuario) session.getAttribute("usuarioLogueado");
                
                if (usuarioLogueado == null || usuarioLogueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los docentes pueden realizar esta acción.");
                    return; 
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.setAttribute("categorias", ic.listarCategorias());
                    List<String> previos = ic.listarNombresCursos();
                    if (previos != null) {
                        List<String> previosSafe = new ArrayList<>();
                        for(String p : previos) {
                            previosSafe.add(p.replace("\"", "&quot;").replace("'", "&#39;"));
                        }
                        request.setAttribute("cursosPrevios", previosSafe);
                    } else {
                        request.setAttribute("cursosPrevios", previos);
                    }

                    request.getRequestDispatcher("fragmentos/altaCurso.jsp").forward(request, response);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().write("<div style='padding:20px;'><h3 style='color:#e74c3c;'>Error al cargar formulario:</h3><p>" + e.getMessage() + "</p></div>");
                }
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String accion = request.getParameter("accion");
        
        if (accion == null){
            accion = "error"; 
        }
        
        switch (accion){
            case "altaCurso": 
                try {
                    String nombreCurso = request.getParameter("nombre");
                    String descripcion = request.getParameter("descripcion");
                    String duracion = request.getParameter("duracion");
                    int cantidadHoras = Integer.parseInt(request.getParameter("cantidadHoras"));
                    int creditos = Integer.parseInt(request.getParameter("creditos"));
                    String url = request.getParameter("url");
                    String nombreInstituto = request.getParameter("instituto");

                    String[] catArray = request.getParameterValues("categorias");
                    Set<String> categorias = catArray != null ? new HashSet<>(Arrays.asList(catArray)) : new HashSet<>();
                    
                    if (categorias.isEmpty()) {
                        throw new Exception("Debe seleccionar al menos una categoría.");
                    }

                    String[] prevArray = request.getParameterValues("previas");
                    Set<String> previas = prevArray != null ? new HashSet<>(Arrays.asList(prevArray)) : new HashSet<>();

                    Date fechaRegistro = new Date(); 

                    String nombreImagen = "usr.png"; 
                    Part filePart = request.getPart("imagen"); 
                    
                    if (filePart != null && filePart.getSize() > 0) {
                        byte[] imgBytes = filePart.getInputStream().readAllBytes();
                        String nombreOriginal = filePart.getSubmittedFileName();
                        String identificadorUnico = "Curso_" + nombreCurso.replaceAll("\\s+", "_"); 
                        nombreImagen = GestorImagenes.guardarImagen(imgBytes, nombreOriginal, identificadorUnico);
                    }

                    DtCurso dtCurso = new DtCurso(nombreCurso, descripcion, duracion, cantidadHoras, creditos, url, 
                            fechaRegistro, new DtInstituto(nombreInstituto), previas, categorias, nombreImagen
                    );

                    ic.altaCurso(dtCurso, nombreInstituto); 

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Curso registrado exitosamente.");

                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Las horas y los créditos deben ser números válidos.");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(e.getMessage());
                }
                break;
        }       
    }
}