<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Lista cursos</title>
    </head>
    <body>

        <div class="contenedor-cursos">
            <h3>Resultados de búsqueda - <span class="badge-filtro">${filtroAplicado}</span></h3>

            <c:choose>
                <c:when test="${not empty listaCursos}">
                    <ul class="lista-resultados-cursos">
                        <c:forEach var="curso" items="${listaCursos}">
                            <li class="item-curso">
                                <span class="nombre-curso">${curso}</span>
                                <button class="btn-detalle" onclick="verDetalleCurso('${curso}')">Ver detalle</button>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>

                <c:otherwise>
                    <div class="alerta-info">
                        No se encontraron cursos registrados para este criterio.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        
        
    </body>
</html>
