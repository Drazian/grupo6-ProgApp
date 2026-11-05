<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- Dispositivo Móvil - Ver Cursos (Figura 3(b)): Instituto o Categoría -> grilla de cursos.
     También es el primer paso de "Ver Ediciones": al tocar un curso se listan sus ediciones. --%>
<c:set var="paraEdiciones" value="${seccion == 'ediciones'}"/>
<c:set var="tituloPagina" value="${paraEdiciones ? 'Ver Ediciones' : 'Ver Cursos'}"/>
<c:set var="rutaLista" value="${paraEdiciones ? '/movil/ediciones' : '/movil/cursos'}"/>
<%@ include file="cabecera.jspf" %>

<main class="container py-3">
    <h4 class="titulo-seccion">${tituloPagina}</h4>

    <!-- Filtros: el estudiante indica el instituto que brinda el curso o la categoría -->
    <div class="d-flex flex-wrap gap-2 mb-2">
        <div class="dropdown">
            <button class="btn btn-primary btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" id="btnInstituto">
                Instituto
            </button>
            <ul class="dropdown-menu" aria-labelledby="btnInstituto">
                <c:forEach var="inst" items="${institutos}">
                    <c:url var="urlInst" value="${rutaLista}"><c:param name="instituto" value="${inst}"/></c:url>
                    <li><a class="dropdown-item ${inst == filtroInstituto ? 'active' : ''}" href="${urlInst}"><c:out value="${inst}"/></a></li>
                </c:forEach>
            </ul>
        </div>
        <div class="dropdown">
            <button class="btn btn-primary btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" id="btnCategoria">
                Categoría
            </button>
            <ul class="dropdown-menu" aria-labelledby="btnCategoria">
                <c:forEach var="cat" items="${categorias}">
                    <c:url var="urlCat" value="${rutaLista}"><c:param name="categoria" value="${cat}"/></c:url>
                    <li><a class="dropdown-item ${cat == filtroCategoria ? 'active' : ''}" href="${urlCat}"><c:out value="${cat}"/></a></li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <p class="small text-muted mb-3" id="descripcionFiltro">
        <c:choose>
            <c:when test="${not empty filtroInstituto}">Cursos del instituto <strong><c:out value="${filtroInstituto}"/></strong> · <a href="${ctx}${rutaLista}">ver todos</a></c:when>
            <c:when test="${not empty filtroCategoria}">Cursos de la categoría <strong><c:out value="${filtroCategoria}"/></strong> · <a href="${ctx}${rutaLista}">ver todos</a></c:when>
            <c:otherwise>Todos los cursos</c:otherwise>
        </c:choose>
        <c:if test="${paraEdiciones}"><br>Elija un curso para ver sus ediciones.</c:if>
    </p>

    <!-- Grilla: foto del curso con su nombre; al tocarla se va al detalle (o a sus ediciones) -->
    <c:choose>
        <c:when test="${empty cursos}">
            <div class="alert alert-light border small">No hay cursos para el filtro elegido.</div>
        </c:when>
        <c:otherwise>
            <div class="row g-3">
                <c:forEach var="curso" items="${cursos}">
                    <c:choose>
                        <c:when test="${paraEdiciones}">
                            <c:url var="urlCurso" value="/movil/ediciones"><c:param name="curso" value="${curso.nombre}"/></c:url>
                        </c:when>
                        <c:otherwise>
                            <c:url var="urlCurso" value="/movil/curso"><c:param name="nombre" value="${curso.nombre}"/></c:url>
                        </c:otherwise>
                    </c:choose>
                    <c:url var="urlImagen" value="/ImagenServlet"><c:param name="nombre" value="${empty curso.imagen ? 'usr.png' : curso.imagen}"/></c:url>
                    <div class="col-12 col-sm-6 col-lg-4">
                        <a class="card h-100 tarjeta-elemento tarjeta-curso" href="${urlCurso}">
                            <img onerror="this.onerror=null; this.src='${ctx}/ImagenServlet?nombre=usr.png'" class="card-img-top" src="${urlImagen}" alt="<c:out value='${curso.nombre}'/>" loading="lazy">
                            <div class="card-body">
                                <c:out value="${curso.nombre}"/>
                                <%-- En "Ver Ediciones" se indica cuántas ediciones tiene el curso: al tocarlo se listan --%>
                                <c:if test="${paraEdiciones}">
                                    <c:set var="cant" value="${cantidadEdiciones[curso.nombre]}"/>
                                    <div class="cantidad-ediciones ${cant == 0 ? 'text-muted' : 'text-primary'}">
                                        <c:choose>
                                            <c:when test="${cant == 0}">Sin ediciones</c:when>
                                            <c:when test="${cant == 1}">1 edición &rsaquo;</c:when>
                                            <c:otherwise>${cant} ediciones &rsaquo;</c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="pie.jspf" %>
