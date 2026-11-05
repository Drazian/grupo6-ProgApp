<%@ tag pageEncoding="UTF-8" body-content="empty" description="Resumen de las valoraciones de un curso: promedio y barras por puntaje (requerimiento 7.6, Figura 7)" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ed" tagdir="/WEB-INF/tags" %>
<%@ attribute name="v" required="true" type="java.lang.Object" description="DtValoracionesCurso" %>
<c:choose>
    <c:when test="${empty v or v.cantidad == 0}">
        <p class="sin-valoraciones" style="color: #7f8c8d; font-style: italic; margin: 5px 0;">Este curso todavía no tiene valoraciones.</p>
    </c:when>
    <c:otherwise>
        <div class="resumen-valoraciones" style="display: flex; gap: 30px; align-items: center; flex-wrap: wrap;">
            <!-- Promedio -->
            <div style="text-align: center; min-width: 140px;">
                <div class="promedio-valoracion" style="font-size: 44px; font-weight: bold; color: #2c3e50; line-height: 1.1;">${v.promedio}</div>
                <ed:estrellas valor="${v.promedio}" tamano="22px"/>
                <div style="font-size: 13px; color: #7f8c8d; margin-top: 4px;">
                    ${v.cantidad} ${v.cantidad == 1 ? 'valoración' : 'valoraciones'}
                </div>
            </div>
            <!-- Barras: cuantos estudiantes dieron cada puntaje, de 5 a 1 estrellas -->
            <div style="flex: 1; min-width: 240px;">
                <c:forEach begin="0" end="4" var="i">
                    <c:set var="puntos" value="${5 - i}"/>
                    <div class="fila-resumen" data-puntaje="${puntos}" style="display: flex; align-items: center; gap: 8px; margin: 4px 0; font-size: 13px; color: #34495e;">
                        <span style="width: 70px; white-space: nowrap;">${puntos} ${puntos == 1 ? 'estrella' : 'estrellas'}</span>
                        <span style="flex: 1; height: 12px; background: #ecf0f1; border-radius: 6px; overflow: hidden;"
                              role="img" aria-label="${v.porcentajePorPuntaje[puntos - 1]}% de las valoraciones">
                            <span style="display: block; height: 100%; width: ${v.porcentajePorPuntaje[puntos - 1]}%; background: #f5b301;"></span>
                        </span>
                        <span class="cantidad-puntaje" style="width: 70px; text-align: right; white-space: nowrap;">${v.porcentajePorPuntaje[puntos - 1]}% (${v.cantidadPorPuntaje[puntos - 1]})</span>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:otherwise>
</c:choose>
