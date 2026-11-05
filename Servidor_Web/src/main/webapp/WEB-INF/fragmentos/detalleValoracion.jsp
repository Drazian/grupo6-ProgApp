<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ed" tagdir="/WEB-INF/tags" %>

<%-- Detalle de la valoración de un curso: promedio + resumen + puntaje del estudiante (o selector para valorar) --%>
<c:choose>
    <c:when test="${not empty errorValoracion}">
        <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
            <h4 style="margin: 0; color: #d35400;">No se puede valorar este curso</h4>
            <p style="margin: 5px 0 0 0; color: #e67e22;"><c:out value="${errorValoracion}"/></p>
        </div>
    </c:when>
    <c:otherwise>
        <div style="background: #f4f6f7; padding: 15px; border-radius: 6px; border: 1px solid #e0e0e0; margin-bottom: 20px;">
            <h3 style="margin: 0 0 12px 0; color: #2c3e50;">
                <a href="#" data-curso="${fn:escapeXml(valoraciones.curso)}" onclick="verDetallesCurso(this.dataset.curso); return false;" style="color: #2c3e50; text-decoration: none;">
                    <c:out value="${valoraciones.curso}"/>
                </a>
            </h3>
            <ed:resumenValoraciones v="${valoraciones}"/>
        </div>

        <c:choose>
            <%-- Ya valoró: se muestra su puntaje, no se puede cambiar --%>
            <c:when test="${not empty valoraciones.puntajeEstudiante}">
                <div id="valoracion-propia" style="padding: 15px; background: #eafaf1; border-left: 4px solid #27ae60; border-radius: 4px;">
                    <strong style="color: #1e8449;">Su valoración:</strong>
                    <ed:estrellas valor="${valoraciones.puntajeEstudiante}" tamano="24px"/>
                    <span style="color: #1e8449; font-weight: bold; margin-left: 6px;">${valoraciones.puntajeEstudiante} de 5</span>
                    <p style="margin: 8px 0 0 0; font-size: 13px; color: #27ae60;">Ya valoró este curso. Cada curso se puede valorar una única vez.</p>
                </div>
            </c:when>
            <%-- Todavía no valoró: selector de 1 a 5 estrellas --%>
            <c:otherwise>
                <div id="form-valoracion" data-curso="${fn:escapeXml(valoraciones.curso)}" style="padding: 15px; border: 1px solid #f5d76e; background: #fffdf3; border-radius: 6px;">
                    <strong style="color: #2c3e50;">¿Qué tan satisfecho está con este curso?</strong>
                    <div id="selector-estrellas" role="radiogroup" aria-label="Puntaje de 1 a 5" style="margin: 10px 0 6px 0;">
                        <c:forEach begin="1" end="5" var="n">
                            <button type="button" class="estrella-opcion" data-valor="${n}" role="radio" aria-checked="false"
                                    aria-label="${n} ${n == 1 ? 'estrella' : 'estrellas'}"
                                    onclick="elegirPuntaje(${n})" onmouseover="resaltarEstrellas(${n})" onmouseout="resaltarEstrellas(puntajeElegido)"
                                    onfocus="resaltarEstrellas(${n})" onblur="resaltarEstrellas(puntajeElegido)"
                                    style="background: none; border: none; box-shadow: none; border-radius: 4px; cursor: pointer; font-size: 36px; line-height: 1; padding: 0 2px; color: #d5d8dc; outline-offset: 2px;">&#9733;</button>
                        </c:forEach>
                        <span id="texto-puntaje" style="margin-left: 10px; color: #7f8c8d; font-size: 14px; vertical-align: middle;">Elija un puntaje</span>
                    </div>
                    <div id="mensaje-valoracion" style="display: none; margin-top: 10px; padding: 10px; border-radius: 4px; font-weight: bold;"></div>
                    <div style="margin-top: 12px; text-align: right;">
                        <button type="button" id="btnValorar" class="btn-moderno" style="background-color: #f39c12;" disabled onclick="confirmarValoracion()">Valorar curso</button>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
