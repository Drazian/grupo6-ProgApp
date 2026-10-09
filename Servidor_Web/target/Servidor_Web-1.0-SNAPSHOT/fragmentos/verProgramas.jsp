<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ver Programas</title>
    </head>
    <body>
        <div class="contenedor-programas">
            <h2>Programas de Formación</h2>
            <p class="descripcion-seccion">Selecciona un programa para consultar sus datos.</p><br>

            <c:choose>
                <c:when test="${not empty listaProgramas}">
                    <ul class="lista-resultados-programas">
                        <c:forEach var="prog" items="${listaProgramas}">
                            <li class="item-programa">
                                <!-- Se accede a prog.nombre del DtPrograma -->
                                <span class="nombre-programa">${prog.getNombre()}</span>
                                <button class="btn-detalle" onclick="cargarDetallePrograma('${prog.getNombre()}')">
                                    Ver detalle
                                </button>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>

                <c:otherwise>
                    <div class="alerta-info">
                        No hay programas de formación registrados actualmente.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>
