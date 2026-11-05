<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- Dispositivo Móvil - Mensaje (curso o edición inexistente, error al consultar) --%>
<%@ include file="cabecera.jspf" %>

<main class="container py-4">
    <div class="alert alert-warning" role="alert"><c:out value="${mensaje}"/></div>
    <a href="${ctx}/movil/cursos" class="btn btn-outline-primary btn-sm">Ver Cursos</a>
</main>

<%@ include file="pie.jspf" %>
