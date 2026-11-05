<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ed" tagdir="/WEB-INF/tags" %>

<div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; margin-bottom: 15px;">Detalles del Curso: ${curso.nombre}</h2>

    <!-- Requerimiento 7.6: puntaje promedio del curso con 5 estrellas -->
    <c:if test="${not empty valoraciones}">
        <div id="promedio-curso" style="margin: -8px 0 15px 0; display: flex; align-items: center; gap: 8px; color: #34495e;">
            <c:choose>
                <c:when test="${valoraciones.cantidad > 0}">
                    <ed:estrellas valor="${valoraciones.promedio}" tamano="20px"/>
                    <strong>${valoraciones.promedio}</strong>
                    <span style="color: #7f8c8d; font-size: 13px;">(${valoraciones.cantidad} ${valoraciones.cantidad == 1 ? 'valoración' : 'valoraciones'})</span>
                </c:when>
                <c:otherwise>
                    <ed:estrellas valor="0" tamano="20px"/>
                    <span style="color: #7f8c8d; font-size: 13px; font-style: italic;">Sin valoraciones</span>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- Renderizado de la Portada del Curso -->
    <c:choose>
        <c:when test="${not empty curso.imagen}">
            <img src="ImagenServlet?nombre=${curso.imagen}" alt="Portada" style="width: 100%; max-width: 400px; height: 250px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); margin-bottom: 20px;">
        </c:when>
        <c:otherwise>
            <img src="ImagenServlet?nombre=usr.png" alt="Portada" style="width: 100%; max-width: 400px; height: 250px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); margin-bottom: 20px;">
        </c:otherwise>
    </c:choose>

    <hr style="border: 1px solid #eee; margin: 10px 0 20px 0;">

    <div style="margin-bottom: 25px; line-height: 1.6;">
        <p><strong>Descripción:</strong> ${curso.descripcion}</p>
        <p><strong>Instituto:</strong> <span style="color: #c0392b; font-weight: bold;">${institutoDelCurso}</span></p>
        <p><strong>Duración:</strong> ${curso.duracion}</p>
        <p><strong>Cantidad de Horas:</strong> ${curso.cantidadHoras} hrs</p>
        <p><strong>Créditos:</strong> ${curso.creditos}</p>
        <p><strong>URL:</strong> <a href="${curso.url}" target="_blank" style="color: #3498db;">${curso.url}</a></p>
        <p><strong>Fecha de Alta:</strong> ${curso.fechaRegistro}</p>
        <p><strong>Categorías:</strong>  
            <c:choose>
                <c:when test="${not empty curso.categorias_toArray}">
                    <c:forEach var="catStr" items="${curso.categorias_toArray}" varStatus="loop">
                        <span style="background: #f1f2f6; padding: 2px 6px; border-radius: 4px; border: 1px solid #dcdde1; font-size: 13px;">${catStr}</span>${!loop.last ? ' ' : ''}
                    </c:forEach>
                </c:when>
                <c:otherwise><span style="color: #7f8c8d; font-style: italic;">Ninguna</span></c:otherwise>
            </c:choose>
        </p>
    </div>

    <div style="display: flex; gap: 30px; flex-wrap: wrap;">
        <!-- Columna de Ediciones Asociadas -->
        <div style="flex: 1; min-width: 250px;">
            <h3 style="border-bottom: 2px solid #3498db; padding-bottom: 5px; color: #2c3e50;">Ediciones Asociadas</h3>
            <c:choose>
                <c:when test="${not empty curso.ediciones}">
                    <ul style="list-style: none; padding: 0;">
                        <c:forEach var="edicionName" items="${curso.ediciones}">
                            <li style="margin-bottom: 8px;">
                                <a href="#" onclick="cargarConsultaEdicion('${edicionName.getNombre()}')" style="text-decoration: none; color: #2980b9; font-weight: bold;">
                                    <span style="margin-right: 5px;">📘</span> ${edicionName.getNombre()}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p style="color: #7f8c8d; font-style: italic;">No hay ediciones registradas.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Columna de Programas de Formación -->
        <div style="flex: 1; min-width: 250px;">
            <h3 style="border-bottom: 2px solid #9b59b6; padding-bottom: 5px; color: #2c3e50;">Programas de Formación</h3>
            <c:choose>
                <c:when test="${not empty curso.programas}">
                    <ul style="list-style: none; padding: 0;">
                        <c:forEach var="progName" items="${curso.programas}">
                            <li style="margin-bottom: 8px;">
                                <a href="#" onclick="cargarDetallePrograma('${progName.getNombre()}')" style="text-decoration: none; color: #8e44ad; font-weight: bold;">
                                    <span style="margin-right: 5px;">🎓</span> ${progName.getNombre()}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p style="color: #7f8c8d; font-style: italic;">No hay programas asociados.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Requerimiento 7.6: resumen de los puntajes asignados por los estudiantes -->
    <c:if test="${not empty valoraciones}">
        <div id="seccion-valoraciones" style="margin-top: 25px;">
            <h3 style="border-bottom: 2px solid #f5b301; padding-bottom: 5px; color: #2c3e50;">Valoraciones de los Estudiantes</h3>
            <ed:resumenValoraciones v="${valoraciones}"/>
            <c:choose>
                <c:when test="${not empty valoraciones.puntajeEstudiante}">
                    <p style="margin-top: 15px; color: #1e8449;">
                        <strong>Su valoración:</strong> <ed:estrellas valor="${valoraciones.puntajeEstudiante}"/> ${valoraciones.puntajeEstudiante} de 5
                    </p>
                </c:when>
                <c:when test="${valoraciones.participa}">
                    <p style="margin-top: 15px;">
                        <a href="#" id="link-valorar-curso" data-curso="${fn:escapeXml(curso.nombre)}" onclick="cargarFormValorarCurso(this.dataset.curso); return false;"
                           style="color: #d68910; font-weight: bold;">&#9733; Valorar este curso</a>
                    </p>
                </c:when>
            </c:choose>
        </div>
    </c:if>
</div>