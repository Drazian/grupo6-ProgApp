<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pruebita</title>
    </head>
    <body>
        <h1>Institutos</h1>
        <ul>
            <c:forEach var="i" items="${institutos}">
                <li>${i.getNombre()}</li>
            </c:forEach>
        </ul>
        
        <h1>Agregar nuevo instituto</h1>
        <label for="nombre">Nombre:</label>
        <input type="text" id="nombre" placeholder="Ejemplito">
        <button type="button" onclick="crearInstituto()">Crear</button>
            
    </body>
</html>
