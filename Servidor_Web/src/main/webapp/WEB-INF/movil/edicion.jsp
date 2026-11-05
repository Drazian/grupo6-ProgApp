<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- Dispositivo Móvil - Consulta de Edición de curso: datos de la edición y docentes asignados --%>
<c:set var="tituloPagina" value="${edicion.nombre}"/>
<%@ include file="cabecera.jspf" %>

<main class="container py-3">
    <div class="card detalle" id="detalleEdicion">
        <div class="card-body pb-2">
            <h5 class="card-title mb-0"><c:out value="${edicion.nombre}"/></h5>
            <c:url var="urlCurso" value="/movil/curso"><c:param name="nombre" value="${edicion.curso.nombre}"/></c:url>
            <small class="text-muted">Curso: <a href="${urlCurso}"><c:out value="${edicion.curso.nombre}"/></a></small>
        </div>
        <c:url var="urlImagen" value="/ImagenServlet"><c:param name="nombre" value="${empty edicion.imagen ? 'usr.png' : edicion.imagen}"/></c:url>
        <img onerror="this.onerror=null; this.src='${ctx}/ImagenServlet?nombre=usr.png'" src="${urlImagen}" class="imagen-detalle" alt="<c:out value='${edicion.nombre}'/>">

        <div class="card-body pt-2">
            <ul class="list-group list-group-flush">
                <li class="list-group-item">Fecha de inicio: ${empty edicion.fechaInicio ? '-' : edicion.fechaInicio.format(fmtFecha)}</li>
                <li class="list-group-item">Fecha de fin: ${empty edicion.fechaFin ? '-' : edicion.fechaFin.format(fmtFecha)}</li>
                <li class="list-group-item">Fecha de publicación: ${empty edicion.fechaPublicacion ? '-' : edicion.fechaPublicacion.format(fmtFecha)}</li>
                <li class="list-group-item">Cupo: ${empty edicion.cupo or edicion.cupo lt 1 ? 'Sin límite' : edicion.cupo}</li>
                <li class="list-group-item">Estado: ${edicion.cerrada ? 'Cerrada' : 'Abierta'}</li>
            </ul>

            <h6>Docentes asignados:</h6>
            <c:choose>
                <c:when test="${empty edicion.docentes}"><p class="small text-muted mb-0">La edición no tiene docentes asignados.</p></c:when>
                <c:otherwise>
                    <ul class="list-group list-group-flush" id="docentesEdicion">
                        <c:forEach var="doc" items="${edicion.docentes}">
                            <c:url var="urlFoto" value="/ImagenServlet"><c:param name="nombre" value="${empty doc.imagen ? 'usr.png' : doc.imagen}"/></c:url>
                            <li class="list-group-item d-flex align-items-center gap-2">
                                <img onerror="this.onerror=null; this.src='${ctx}/ImagenServlet?nombre=usr.png'" src="${urlFoto}" class="foto-docente" alt="">
                                <span><c:out value="${doc.nombre} ${doc.apellido}"/>
                                    <span class="text-muted">(<c:out value="${doc.nickname}"/>)</span></span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<%@ include file="pie.jspf" %>
