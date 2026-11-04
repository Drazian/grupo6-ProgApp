<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<div style="max-width: 650px; margin: 30px auto; padding: 24px; background-color: #fdf2f2; border-left: 5px solid #e74c3c; border-radius: 6px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">
    <h3 style="margin-top: 0; color: #c0392b;">Ocurrió un error al procesar la solicitud</h3>
    <c:choose>
        <c:when test="${not empty error}">
            <p style="color: #555; font-size: 15px; margin-bottom: 0;"><c:out value="${error}" /></p>
        </c:when>
        <c:otherwise>
            <p style="color: #555; font-size: 15px; margin-bottom: 0;">La operación solicitada no está disponible o los parámetros enviados son inválidos.</p>
        </c:otherwise>
    </c:choose>
</div>

