<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenedor-lista">
    <h2>Resultados para: ${filtroAplicado}</h2>
    <hr>
    
    <ul style="list-style: none; padding: 0;">
        <c:forEach var="curso" items="${listaCursos}">
            <!-- Al hacer clic en un curso, llama a JavaScript para ver sus detalles -->
            <li style="margin-bottom: 10px;">
                <a href="#" onclick="verDetallesCurso('${curso}')" style="font-size: 18px; text-decoration: none; font-weight: bold; color: #337ab7;">
                    📘 ${curso}
                </a>
            </li>
        </c:forEach>
    </ul>

    <c:if test="${empty listaCursos}">
        <p style="color: #666;">No hay cursos registrados para esta selección.</p>
    </c:if>
</div>