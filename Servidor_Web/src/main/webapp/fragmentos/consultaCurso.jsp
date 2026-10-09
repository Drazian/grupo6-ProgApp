<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenedor-consulta">
    <h2>Detalles del Curso: ${curso.nombre}</h2>
    <hr>

    <div style="background: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
        <p><strong>Descripción:</strong> ${curso.descripcion}</p>
        <p><strong>Duración:</strong> ${curso.duracion}</p>
        <p><strong>Cantidad de Horas:</strong> ${curso.cantidadHoras} hrs</p>
        <p><strong>Créditos:</strong> ${curso.creditos}</p>
        <p><strong>URL:</strong> <a href="${curso.url}" target="_blank">${curso.url}</a></p>
        <p><strong>Fecha de Alta:</strong> ${curso.fechaRegistro}</p>
        <p><strong>Categorías:</strong>
            <c:forEach var="cat" items="${curso.categorias}" varStatus="status">
                ${cat}${!status.last ? ', ' : ''}
            </c:forEach>
        </p>
    </div>

    <div style="display: flex; gap: 40px;">
        <!-- Lista de Ediciones -->
        <div>
            <h3>Ediciones Asociadas</h3>
            <ul>
                <c:forEach var="ed" items="${curso.ediciones}">
                    <li><a href="#" onclick="verDetalleEdicion('${ed}')">${ed}</a></li>
                </c:forEach>
                <c:if test="${empty curso.ediciones}">
                    <li style="color: #999;">No hay ediciones registradas.</li>
                </c:if>
            </ul>
        </div>

        <!-- Lista de Programas -->
        <div>
            <h3>Programas de Formación</h3>
            <ul>
                <c:forEach var="prog" items="${curso.programas}">
                    <li><a href="#" onclick="verDetallePrograma('${prog}')">${prog}</a></li>
                </c:forEach>
                <c:if test="${empty curso.programas}">
                    <li style="color: #999;">No hay programas asociados.</li>
                </c:if>
            </ul>
        </div>
    </div>
</div>