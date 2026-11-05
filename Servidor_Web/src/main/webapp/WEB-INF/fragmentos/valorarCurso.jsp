<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- CU: Valorar un Curso (Estudiante) --%>
<div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #f5b301; padding-bottom: 10px;">
        Valorar un Curso
    </h2>
    <p style="color: #7f8c8d; font-size: 13px; margin: 10px 0 15px 0;">
        Se listan los cursos en los que usted participa (inscripción aceptada en alguna edición).
        Cada curso se puede valorar <strong>una única vez</strong>.
    </p>

    <c:choose>
        <c:when test="${empty cursosParticipa}">
            <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
                <h4 style="margin: 0; color: #d35400;">Todavía no participa en ningún curso</h4>
                <p style="margin: 5px 0 0 0; color: #e67e22;">
                    Podrá valorar un curso cuando tenga una inscripción aceptada en alguna de sus ediciones.
                </p>
            </div>
        </c:when>
        <c:otherwise>
            <div style="margin-bottom: 20px;">
                <label for="cursoValorar" style="font-weight: bold; color: #34495e;">Curso:</label>
                <select id="cursoValorar" onchange="cargarDetalleValoracion(this.value)" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                    <option value="">Seleccione un Curso...</option>
                    <c:forEach var="curso" items="${cursosParticipa}">
                        <option value="${fn:escapeXml(curso)}" ${curso == cursoPreseleccionado ? 'selected' : ''}><c:out value="${curso}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div id="contenedor-valoracion">
                <p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 30px;">
                    Seleccione un curso para ver su puntaje promedio y asignarle su valoración.
                </p>
            </div>
        </c:otherwise>
    </c:choose>
</div>
