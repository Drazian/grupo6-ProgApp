<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Requerimiento 7.3: Listado de tendencias (cursos más visitados) --%>
<div style="max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #e67e22; padding-bottom: 10px; margin-bottom: 8px;">
        Cursos más visitados
    </h2>
    <p style="color: #7f8c8d; font-size: 13px; margin: 0 0 15px 0;">
        Tendencias de edEXT: los cursos que más consultaron los usuarios y visitantes, ordenados por cantidad de visitas.
    </p>

    <c:choose>
        <c:when test="${not empty tendencias}">
            <p id="total-visitas" style="font-size: 13px; color: #34495e; margin: 0 0 12px 0;">
                <strong>${tendencias.size()}</strong> ${tendencias.size() == 1 ? 'curso visitado' : 'cursos visitados'} ·
                <strong>${totalVisitas}</strong> ${totalVisitas == 1 ? 'visita' : 'visitas'} en total
            </p>

            <table id="tabla-tendencias" style="width: 100%; border-collapse: collapse; text-align: left;">
                <thead>
                    <tr style="background-color: #2c3e50; color: white;">
                        <th style="padding: 10px; border: 1px solid #ddd; width: 50px; text-align: center;">#</th>
                        <th style="padding: 10px; border: 1px solid #ddd; width: 64px; text-align: center;">Imagen</th>
                        <th style="padding: 10px; border: 1px solid #ddd;">Curso</th>
                        <th style="padding: 10px; border: 1px solid #ddd; width: 240px;">Visitas</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="t" items="${tendencias}">
                        <tr class="fila-tendencia" data-curso="${fn:escapeXml(t.curso)}" data-visitas="${t.visitas}">
                            <td style="padding: 10px; border: 1px solid #ddd; text-align: center; font-size: 18px; font-weight: bold; color: ${t.posicion == 1 ? '#d4a017' : (t.posicion == 2 ? '#8e9aa6' : (t.posicion == 3 ? '#b87333' : '#7f8c8d'))};">
                                ${t.posicion}
                            </td>
                            <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                <img src="ImagenServlet?nombre=${fn:escapeXml(empty t.imagen ? 'usr.png' : t.imagen)}" alt=""
                                     onerror="this.onerror=null; this.src='ImagenServlet?nombre=usr.png'"
                                     style="width: 48px; height: 36px; object-fit: cover; border-radius: 4px; border: 1px solid #ccc;">
                            </td>
                            <td style="padding: 10px; border: 1px solid #ddd;">
                                <a href="#" data-curso="${fn:escapeXml(t.curso)}" onclick="verDetallesCurso(this.dataset.curso); return false;"
                                   style="color: #2c3e50; font-weight: bold; text-decoration: none;"><c:out value="${t.curso}"/></a>
                                <c:if test="${not empty t.instituto}">
                                    <div style="font-size: 12px; color: #7f8c8d;">Instituto: <c:out value="${t.instituto}"/></div>
                                </c:if>
                            </td>
                            <td style="padding: 10px; border: 1px solid #ddd;">
                                <div style="display: flex; align-items: center; gap: 8px;">
                                    <span style="flex: 1; height: 10px; background: #ecf0f1; border-radius: 5px; overflow: hidden;">
                                        <span style="display: block; height: 100%; width: ${maximoVisitas > 0 ? (t.visitas * 100 / maximoVisitas) : 0}%; background: #e67e22;"></span>
                                    </span>
                                    <strong class="cantidad-visitas" style="white-space: nowrap; color: #2c3e50;">${t.visitas} ${t.visitas == 1 ? 'visita' : 'visitas'}</strong>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <p style="margin: 10px 0 0 0; font-size: 12px; color: #7f8c8d;">
                Se cuenta una visita cada vez que alguien consulta el detalle de un curso, desde el sitio web o desde el móvil.
            </p>
        </c:when>
        <c:otherwise>
            <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
                <h4 style="margin: 0; color: #d35400;">Todavía no hay visitas registradas</h4>
                <p style="margin: 5px 0 0 0; color: #e67e22;">
                    El listado se arma solo a medida que usuarios y visitantes consultan cursos.
                </p>
            </div>
        </c:otherwise>
    </c:choose>
</div>
