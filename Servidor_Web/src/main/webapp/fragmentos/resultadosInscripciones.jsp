<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div style="max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin-bottom: 20px; flex-wrap: wrap; gap: 10px;">
        <h2 style="color: #2c3e50; margin: 0;">Resultados de mis Inscripciones a Ediciones</h2>
        
        <!-- Filtro rápido por estado en cliente -->
        <c:if test="${not empty misInscripciones}">
            <div>
                <label for="filtroEstadoMisInscripciones" style="font-weight: bold; color: #34495e; margin-right: 8px;">Filtrar por estado:</label>
                <select id="filtroEstadoMisInscripciones" onchange="filtrarMisInscripciones(this.value)" style="padding: 6px 10px; border-radius: 4px; border: 1px solid #bdc3c7;">
                    <option value="TODOS">Todos los estados (${misInscripciones.size()})</option>
                    <option value="1">Pendientes (Inscripto)</option>
                    <option value="2">Aceptadas</option>
                    <option value="3">Rechazadas</option>
                </select>
            </div>
        </c:if>
    </div>
 
    <c:choose>
        <c:when test="${not empty misInscripciones}">
            <table style="width: 100%; border-collapse: collapse; text-align: left;">
                <thead>
                    <tr style="background-color: #34495e; color: white;">
                        <th style="padding: 10px; border: 1px solid #ddd; width: 70px; text-align: center;">Portada</th>
                        <th style="padding: 10px; border: 1px solid #ddd;">Edición de Curso</th>
                        <th style="padding: 10px; border: 1px solid #ddd;">Curso Asociado</th>
                        <th style="padding: 10px; border: 1px solid #ddd;">Período Edición</th>
                        <th style="padding: 10px; border: 1px solid #ddd;">Fecha Inscripción</th>
                        <th style="padding: 10px; border: 1px solid #ddd; text-align: center;">Estado</th>
                    </tr>
                </thead>
                <tbody id="tablaMisInscripcionesBody">
                    <c:forEach var="ins" items="${misInscripciones}">
                        <tr class="fila-mi-inscripcion" data-estado="${fn:escapeXml(ins.estado)}">
                            <!-- Imagen de la Edición (Req. 7.2) -->
                            <td style="padding: 10px; border: 1px solid #ddd; background: #fff; text-align: center;">
                                <img src="ImagenServlet?nombre=${empty ins.edicion.imagen ? 'usr.png' : ins.edicion.imagen}" 
                                     alt="Portada" 
                                     style="width: 55px; height: 40px; object-fit: cover; border-radius: 4px; border: 1px solid #ccc;">
                            </td>

                            <!-- Link navegable a Consulta de Edición (Req. 7.1) -->
                            <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                                <a href="#" data-edicion="${fn:escapeXml(ins.edicion.nombre)}" onclick="cargarConsultaEdicion(this.dataset.edicion); return false;" style="color: #2980b9; font-weight: bold; text-decoration: none;">
                                    📘 ${ins.edicion.nombre}
                                </a>
                            </td>

                            <!-- Link navegable a Consulta de Curso (Req. 7.1) -->
                            <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                                <a href="#" data-curso="${fn:escapeXml(ins.edicion.curso.nombre)}" onclick="verDetallesCurso(this.dataset.curso); return false;" style="color: #34495e; text-decoration: underline;">
                                    ${ins.edicion.curso.nombre}
                                </a>
                            </td>

                            <!-- Fechas de la Edición -->
                            <td style="padding: 10px; border: 1px solid #ddd; background: #fff; font-size: 13px; color: #555;">
                                ${ins.edicion.fechaInicio} al ${ins.edicion.fechaFin}
                            </td>

                            <!-- Fecha en que se inscribió el estudiante -->
                            <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                                ${ins.fechaInscripcion}
                            </td>

                            <!-- Estado de la Inscripción (Tabla 1: Inscripto, Aceptada, Rechazada) -->
                            <td style="padding: 10px; border: 1px solid #ddd; background: #fff; text-align: center;">
                                <c:choose>
                                    <c:when test="${ins.estado == 1}">
                                        <span style="background: #f39c12; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold;">
                                            Inscripto
                                        </span>
                                    </c:when>
                                    <c:when test="${ins.estado == 2}">
                                        <span style="background: #27ae60; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold;">
                                            Aceptada
                                        </span>
                                    </c:when>
                                    <c:when test="${ins.estado == 3}">
                                        <span style="background: #c0392b; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold;">
                                            Rechazada
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="background: #7f8c8d; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px;">
                                            ${nombresEstado[ins.estado]}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
                <h4 style="margin: 0; color: #d35400;">No tienes inscripciones registradas</h4>
                <p style="margin: 5px 0 15px 0; color: #e67e22;">
                    Aún no te has inscripto a ninguna edición de curso en la plataforma.
                </p>
                <button type="button" onclick="cargarFormInscripcion()" style="background: #3498db; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                    Inscribirme a una Edición
                </button>
            </div>
        </c:otherwise>
    </c:choose>
</div>