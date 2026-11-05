<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- Dispositivo Móvil - Consulta de Edición de curso: grilla con las ediciones del curso elegido --%>
<c:set var="tituloPagina" value="Ediciones de ${curso.nombre}"/>
<%@ include file="cabecera.jspf" %>

<main class="container py-3">
    <h4 class="titulo-seccion">Ver Ediciones</h4>
    <p class="small mb-3">
        <a href="${ctx}/movil/ediciones">&larr; Elegir otro curso</a><br>
        Ediciones del curso <strong><c:out value="${curso.nombre}"/></strong>
    </p>

    <c:choose>
        <c:when test="${empty ediciones}">
            <div class="alert alert-light border small">El curso no tiene ediciones.</div>
        </c:when>
        <c:otherwise>
            <div class="row g-3">
                <c:forEach var="edicion" items="${ediciones}">
                    <c:url var="urlEdicion" value="/movil/edicion"><c:param name="nombre" value="${edicion.nombre}"/></c:url>
                    <c:url var="urlImagen" value="/ImagenServlet"><c:param name="nombre" value="${empty edicion.imagen ? 'usr.png' : edicion.imagen}"/></c:url>
                    <div class="col-12 col-sm-6 col-lg-4">
                        <a class="card h-100 tarjeta-elemento tarjeta-edicion" href="${urlEdicion}">
                            <img onerror="this.onerror=null; this.src='${ctx}/ImagenServlet?nombre=usr.png'" class="card-img-top" src="${urlImagen}" alt="<c:out value='${edicion.nombre}'/>" loading="lazy">
                            <div class="card-body">
                                <c:out value="${edicion.nombre}"/>
                                <div class="text-muted" style="font-size: 12px;">
                                    ${empty edicion.fechaInicio ? '' : edicion.fechaInicio.format(fmtFecha)} al ${empty edicion.fechaFin ? '' : edicion.fechaFin.format(fmtFecha)}
                                </div>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="pie.jspf" %>
