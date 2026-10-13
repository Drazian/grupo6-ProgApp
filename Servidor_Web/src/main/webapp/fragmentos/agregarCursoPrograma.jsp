<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Agregar Curso a Programa</title>
    </head>
    <body>
        <form id="formAgregarCursoPrograma">
            <div>
                <label for="selectPrograma">Programa de Formacion:</label>
                <select id="selectPrograma" name="nombrePrograma" required>
                    <option value="" disabled selected>-- Seleccione un Programa --</option>
                    <c:forEach var="prog" items="${programas}">
                        <option value="${prog.getNombre()}">${prog.getNombre()}</option>
                    </c:forEach>
                </select>
            </div>
            <br>

            <div>
                <label>Curso:</label>
                
                <!-- Input oculto para que el servlet lo reciba -->
                <input type="hidden" id="selectCurso" name="nombreCurso" required />

                <div class="table-responsive" style="max-height: 300px; overflow-y: auto;">
                    <table class="table align-middle table-hover">
                        <thead>
                            <tr>
                                <th style="width: 70px;">Imagen</th>
                                <th>Nombre</th>
                                <th>Instituto</th>
                                <th class="text-end">Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cur" items="${cursos}">
                                <tr id="fila-curso-${cur.getNombre()}">
                                    <td>
                                        <img src="ImagenServlet?nombre=${empty cur.imagen ? 'usr.png' : cur.imagen}" 
                                             alt="Curso" 
                                             style="width: 40px; height: 40px; object-fit: cover; border-radius: 50%; border: 1px solid #ccc;">
                                    </td>
                                    <td>
                                        <span style="font-weight: 500;">${cur.getNombre()}</span>
                                    </td>
                                    <td>
                                        <span class="text-muted">${cur.getInstituto() != null ? cur.getInstituto().getNombre() : 'Sin instituto'}</span>
                                    </td>
                                    <td class="text-end">
                                        <button type="button" class="btn btn-sm btn-outline-primary btn-seleccionar-curso" data-nombre="${cur.getNombre()}">
                                            Seleccionar
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <br>
                
                <div>
                    <span>Curso seleccionado: </span>
                    <span id="labelCursoSeleccionado" style="font-weight: bold;">Ninguno seleccionado</span>
                </div>
            </div>

            <br>

            <button type="button" onclick="agregarCursoAPrograma()">Agregar Curso</button>
        </form>
        
    </body>
</html>