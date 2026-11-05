<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ed" tagdir="/WEB-INF/tags" %>
<%-- Dispositivo Móvil - Consulta de Curso (Figura 3(c)): datos del curso, ediciones, programas y valoraciones --%>
<c:set var="tituloPagina" value="${curso.nombre}"/>
<%@ include file="cabecera.jspf" %>

<main class="container py-3">
    <div class="card detalle" id="detalleCurso">
        <div class="card-body pb-2">
            <h5 class="card-title mb-0"><c:out value="${curso.nombre}"/></h5>
            <c:if test="${not empty instituto}"><small class="text-muted">Instituto: <c:out value="${instituto}"/></small></c:if>
        </div>
        <c:url var="urlImagen" value="/ImagenServlet"><c:param name="nombre" value="${empty curso.imagen ? 'usr.png' : curso.imagen}"/></c:url>
        <img onerror="this.onerror=null; this.src='${ctx}/ImagenServlet?nombre=usr.png'" src="${urlImagen}" class="imagen-detalle" alt="<c:out value='${curso.nombre}'/>">

        <div class="card-body pt-2">
            <c:if test="${not empty curso.url}">
                <a href="<c:out value='${curso.url}'/>" target="_blank" rel="noopener" class="small">Ir al sitio del curso</a>
            </c:if>
            <c:if test="${not empty curso.descripcion}">
                <p class="small text-muted mt-2 mb-1"><c:out value="${curso.descripcion}"/></p>
            </c:if>

            <ul class="list-group list-group-flush">
                <li class="list-group-item">Duración: <c:out value="${curso.duracion}"/></li>
                <li class="list-group-item">Horas: ${curso.cantidadHoras}</li>
                <li class="list-group-item">Créditos: ${curso.creditos}</li>
                <li class="list-group-item">Fecha de registro: ${empty curso.fechaRegistro ? '-' : curso.fechaRegistro.format(fmtFechaCorta)}</li>
                <c:if test="${not empty curso.categorias_toArray}">
                    <li class="list-group-item">Categorías:
                        <c:forEach var="cat" items="${curso.categorias_toArray}" varStatus="st"><c:out value="${cat}"/><c:if test="${not st.last}">, </c:if></c:forEach>
                    </li>
                </c:if>
            </ul>

            <h6>Ediciones del curso:</h6>
            <c:choose>
                <c:when test="${empty ediciones}"><p class="small text-muted mb-0">El curso no tiene ediciones.</p></c:when>
                <c:otherwise>
                    <ul class="list-group list-group-flush" id="edicionesCurso">
                        <c:forEach var="ed" items="${ediciones}">
                            <c:url var="urlEdicion" value="/movil/edicion"><c:param name="nombre" value="${ed.nombre}"/></c:url>
                            <li class="list-group-item"><a href="${urlEdicion}"><c:out value="${ed.nombre}"/></a></li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>

            <h6>Programas de formación:</h6>
            <c:choose>
                <c:when test="${empty curso.programas}"><p class="small text-muted mb-0">El curso no integra programas de formación.</p></c:when>
                <c:otherwise>
                    <ul class="list-group list-group-flush" id="programasCurso">
                        <c:forEach var="prog" items="${curso.programas}">
                            <li class="list-group-item"><c:out value="${prog.nombre}"/></li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>

            <%-- Opcional en la letra: valoraciones del curso realizadas por los estudiantes --%>
            <c:if test="${not empty valoraciones}">
                <h6>Valoraciones:</h6>
                <div id="valoracionesCurso" class="small">
                    <ed:resumenValoraciones v="${valoraciones}"/>
                </div>
            </c:if>
        </div>
    </div>
</main>

<%@ include file="pie.jspf" %>
