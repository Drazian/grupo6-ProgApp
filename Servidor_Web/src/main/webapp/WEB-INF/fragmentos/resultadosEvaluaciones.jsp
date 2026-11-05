<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- CU: Listar resultados de evaluaciones en ediciones de cursos (Estudiante) --%>
<div style="max-width: 1000px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #16a085; padding-bottom: 10px; margin-bottom: 8px;">
        Resultados de mis Evaluaciones
    </h2>
    <p style="color: #7f8c8d; font-size: 13px; margin: 0 0 15px 0;">
        Inscripciones a ediciones de cursos <strong>cerradas</strong> y el resultado de su evaluación (nota en la escala 1 a 12).
    </p>

    <c:choose>
        <c:when test="${not empty resultadosEvaluaciones}">
            <!-- Resumen -->
            <div id="resumen-evaluaciones" style="display: flex; gap: 12px; margin-bottom: 18px; flex-wrap: wrap;">
                <div style="flex: 1; min-width: 150px; background: #f4f6f7; border-radius: 6px; padding: 12px; text-align: center;">
                    <div style="font-size: 26px; font-weight: bold; color: #2c3e50;">${resultadosEvaluaciones.size()}</div>
                    <div style="font-size: 12px; color: #7f8c8d;">Ediciones cerradas</div>
                </div>
                <div style="flex: 1; min-width: 150px; background: #f4f6f7; border-radius: 6px; padding: 12px; text-align: center;">
                    <div style="font-size: 26px; font-weight: bold; color: #2c3e50;">${cantidadEvaluadas}</div>
                    <div style="font-size: 12px; color: #7f8c8d;">Evaluaciones</div>
                </div>
                <div style="flex: 1; min-width: 150px; background: #eafaf1; border-radius: 6px; padding: 12px; text-align: center;">
                    <div style="font-size: 26px; font-weight: bold; color: #1e8449;">${cantidadAprobadas}</div>
                    <div style="font-size: 12px; color: #1e8449;">Aprobadas</div>
                </div>
                <div style="flex: 1; min-width: 150px; background: #e8f6f3; border-radius: 6px; padding: 12px; text-align: center;">
                    <div style="font-size: 26px; font-weight: bold; color: #117864;">${empty promedioNotas ? '-' : promedioNotas}</div>
                    <div style="font-size: 12px; color: #117864;">Promedio de notas</div>
                </div>
            </div>

            <div style="overflow-x: auto;">
                <table id="tabla-resultados-evaluaciones" style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead>
                        <tr style="background-color: #34495e; color: white;">
                            <th style="padding: 10px; border: 1px solid #ddd; width: 64px; text-align: center;">Portada</th>
                            <th style="padding: 10px; border: 1px solid #ddd;">Edición / Curso</th>
                            <th style="padding: 10px; border: 1px solid #ddd; white-space: nowrap;">Fecha Inscripción</th>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: center;">Inscripción</th>
                            <th style="padding: 10px; border: 1px solid #ddd; white-space: nowrap;">Fecha de Cierre</th>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: center;">Nota</th>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: center;">Resultado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ins" items="${resultadosEvaluaciones}">
                            <tr class="fila-resultado-evaluacion" data-edicion="${fn:escapeXml(ins.edicion.nombre)}">
                                <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                    <img src="ImagenServlet?nombre=${fn:escapeXml(empty ins.edicion.imagen ? 'usr.png' : ins.edicion.imagen)}" alt="Portada"
                                         style="width: 48px; height: 36px; object-fit: cover; border-radius: 4px; border: 1px solid #ccc;">
                                </td>
                                <td style="padding: 10px; border: 1px solid #ddd;">
                                    <a href="#" data-edicion="${fn:escapeXml(ins.edicion.nombre)}" onclick="cargarConsultaEdicion(this.dataset.edicion); return false;"
                                       style="color: #2c3e50; font-weight: bold; text-decoration: none;"><c:out value="${ins.edicion.nombre}"/></a><br>
                                    <a href="#" data-curso="${fn:escapeXml(ins.edicion.curso.nombre)}" onclick="verDetallesCurso(this.dataset.curso); return false;"
                                       style="font-size: 12px; color: #2980b9;"><c:out value="${ins.edicion.curso.nombre}"/></a>
                                </td>
                                <td style="padding: 10px; border: 1px solid #ddd; white-space: nowrap;">${ins.fechaInscripcion}</td>
                                <td style="padding: 10px; border: 1px solid #ddd; text-align: center; white-space: nowrap;">
                                    <c:choose>
                                        <c:when test="${ins.estado == 2}"><span style="color: #27ae60; font-weight: bold;">Aceptada</span></c:when>
                                        <c:when test="${ins.estado == 3}"><span style="color: #c0392b; font-weight: bold;">Rechazada</span></c:when>
                                        <c:when test="${ins.estado == 1}"><span style="color: #f39c12; font-weight: bold;">Inscripto</span></c:when>
                                        <c:otherwise><span style="color: #7f8c8d;">No inscripto</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="padding: 10px; border: 1px solid #ddd; white-space: nowrap;">${ins.edicion.fechaCierre}</td>
                                <td style="padding: 10px; border: 1px solid #ddd; text-align: center;">
                                    <c:choose>
                                        <c:when test="${not empty ins.nota}">
                                            <span class="nota-evaluacion" style="background: #2c3e50; color: white; padding: 4px 12px; border-radius: 12px; font-weight: bold;">${ins.nota}</span>
                                        </c:when>
                                        <c:otherwise><span style="color: #95a5a6;">-</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="resultado-evaluacion" style="padding: 10px; border: 1px solid #ddd; text-align: center; white-space: nowrap;">
                                    <c:choose>
                                        <c:when test="${ins.resultado == 'Aprobado'}">
                                            <span style="background: #27ae60; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold;">Aprobado</span>
                                        </c:when>
                                        <c:when test="${ins.resultado == 'No aprobado'}">
                                            <span style="background: #c0392b; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold;">No aprobado</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #95a5a6; font-style: italic; font-size: 13px;" title="Solo se evalúa a los estudiantes con la inscripción aceptada">Sin evaluación</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <p style="margin: 10px 0 0 0; font-size: 12px; color: #7f8c8d;">
                "Sin evaluación": la inscripción no fue aceptada, por lo que el docente no ingresó una nota.
                Para el acta de un programa de formación, use <em>Mi Perfil → Tramitar Acta de Estudio</em>.
            </p>
        </c:when>
        <c:otherwise>
            <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
                <h4 style="margin: 0; color: #d35400;">Todavía no hay resultados</h4>
                <p style="margin: 5px 0 0 0; color: #e67e22;">
                    No tiene inscripciones a ediciones de cursos cerradas. Los resultados aparecen cuando el docente cierra la edición.
                </p>
            </div>
        </c:otherwise>
    </c:choose>
</div>
