<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; margin-bottom: 15px;">Detalles del Curso: ${curso.nombre}</h2>
    
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
        <p><strong>Duración:</strong> ${curso.duracion}</p>
        <p><strong>Cantidad de Horas:</strong> ${curso.cantidadHoras} hrs</p>
        <p><strong>Créditos:</strong> ${curso.creditos}</p>
        <p><strong>URL:</strong> <a href="${curso.url}" target="_blank" style="color: #3498db;">${curso.url}</a></p>
        <p><strong>Fecha de Alta:</strong> ${curso.fechaRegistro}</p>
        <p><strong>Categorías:</strong> 
            <c:choose>
                <c:when test="${not empty curso.categorias}">
                    <c:forEach var="cat" items="${curso.categorias}" varStatus="loop">
                        ${cat}${!loop.last ? ', ' : ''}
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
                                <a href="#" onclick="cargarConsultaEdicion('${edicionName}')" style="text-decoration: none; color: #2980b9; font-weight: bold;">
                                    <span style="margin-right: 5px;">📘</span> ${edicionName}
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
                                <a href="#" onclick="cargarDetallePrograma('${progName}')" style="text-decoration: none; color: #8e44ad; font-weight: bold;">
                                    <span style="margin-right: 5px;">🎓</span> ${progName}
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
</div>