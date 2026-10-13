package edu.edext.controlador;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtInstituto;
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
import jakarta.servlet.http.Part; // FIX: Para interceptar el archivo binario
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

                try {
                    List<String> cursos = null;
                    if ("instituto".equalsIgnoreCase(opcion)) {
                        cursos = ic.listarCursosPorInstituto(nombre);
                    } else if ("categoria".equalsIgnoreCase(opcion)) {
                        cursos = ic.listarCursosPorCategoria(nombre);
                    }

                    request.setAttribute("listaCursos", cursos);
                    request.setAttribute("filtroAplicado", opcion + ": " + nombre);
                    request.getRequestDispatcher("fragmentos/listaCursos.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
                }
                break;

            case "listarCategoriasJson": 
                try {
                    // Obtenemos la lista de categorías
                    List<edu.edext.datatypes.DtCategoria> listaCat = ic.listarCategorias();
                    
                    // Armamos un JSON manualmente
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

            case "verDetallesCurso": 
                try {
                    String nombreDelCurso = request.getParameter("nombreCurso");
                    
                    // Llamamos a la lógica que extrae todos los datos, ediciones y programas
                    edu.edext.datatypes.DtConsultaCurso dtCurso = ic.obtenerDatosCurso(nombreDelCurso);
                    
                    // Lo inyectamos en el request con la clave "curso"
                    request.setAttribute("curso", dtCurso);
                    
                    // Despachamos el fragmento de detalles
                    request.getRequestDispatcher("fragmentos/consultaCurso.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
                }
                break;
                
            case "altaCurso": 
                // 1. Validar seguridad: Solo docentes pueden dar de alta un curso
                HttpSession session = request.getSession();
                DtUsuario usuarioLogueado = (DtUsuario) session.getAttribute("usuarioLogueado");
                
                if (usuarioLogueado == null || usuarioLogueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los docentes pueden realizar esta acción.");
                    return; // Cortamos la ejecución aquí
                }

                try {
                    // 2. Cargar datos dinámicos para los <select> del formulario
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.setAttribute("categorias", ic.listarCategorias());
                    request.setAttribute("cursosPrevios", ic.listarNombresCursos());

                    // 3. Despachar el fragmento HTML
                    request.getRequestDispatcher("fragmentos/altaCurso.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
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
            accion = "error"; //Opcion por defecto si no se indica nada.
        }
        
        switch (accion){
            case "altaCurso": 
                try {
                    // 1. Extraer los datos básicos
                    String nombreCurso = request.getParameter("nombre");
                    String descripcion = request.getParameter("descripcion");
                    String duracion = request.getParameter("duracion");
                    int cantidadHoras = Integer.parseInt(request.getParameter("cantidadHoras"));
                    int creditos = Integer.parseInt(request.getParameter("creditos"));
                    String url = request.getParameter("url");
                    String nombreInstituto = request.getParameter("instituto");

                    // 2. Extraer selecciones múltiples
                    String[] catArray = request.getParameterValues("categorias");
                    Set<String> categorias = catArray != null ? new HashSet<>(Arrays.asList(catArray)) : new HashSet<>();
                    
                    if (categorias.isEmpty()) {
                        throw new Exception("Debe seleccionar al menos una categoría.");
                    }

                    String[] prevArray = request.getParameterValues("previas");
                    Set<String> previas = prevArray != null ? new HashSet<>(Arrays.asList(prevArray)) : new HashSet<>();

                    // 3. Generar la fecha actual del sistema
                    Date fechaRegistro = new Date(); 

                    // --- FIX IMAGEN: 4. Capturar y guardar la imagen del curso ---
                    String nombreImagen = "usr.png"; // Imagen genérica por defecto
                    Part filePart = request.getPart("imagen"); // Asegúrate de que el input en tu JSP se llame name="imagen"
                    
                    if (filePart != null && filePart.getSize() > 0) {
                        byte[] imgBytes = filePart.getInputStream().readAllBytes();
                        String nombreOriginal = filePart.getSubmittedFileName();
                        
                        // Limpiamos los espacios del nombre del curso para usarlo como identificador del archivo
                        String identificadorUnico = "Curso_" + nombreCurso.replaceAll("\\s+", "_"); 
                        
                        // Guardamos físicamente la imagen y obtenemos el nombre del archivo final
                        nombreImagen = GestorImagenes.guardarImagen(imgBytes, nombreOriginal, identificadorUnico);
                    }

                    // 5. Armar el Datatype
                    // ATENCIÓN: Se añadió 'nombreImagen' al final del constructor.
                    // Recuerda agregarlo también en DtCurso.java y Curso.java (como hicimos con DtEdicion)
                    DtCurso dtCurso = new DtCurso(
                            nombreCurso, descripcion, duracion, cantidadHoras, creditos, url, 
                            fechaRegistro, new DtInstituto(nombreInstituto), null, categorias, previas, nombreImagen
                    );

                    // 6. Llamar a la lógica del controlador
                    ic.altaCurso(dtCurso, nombreInstituto); 

                    // 7. Enviar mensaje de éxito
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
