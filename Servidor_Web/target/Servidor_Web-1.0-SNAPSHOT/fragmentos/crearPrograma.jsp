<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Crear Programa</title>
    </head>
    <body>
        <h1>Crear Programa</h1>
        
        <form id="formCrearPrograma">
            <label for="nombre">Nombre:</label>
            <input type="text" id="nombre" placeholder="Programita" required><br>

            <label for="desc">Descripcion</label>
            <textarea id="desc" placeholder="Ejemplito" required></textarea><br>

            <label for="fechaInicio">Fecha de Inicio:</label>
            <input type="date" id="fechaInicio" required><br>

            <label for="fechaFin">Fecha de Fin:</label>
            <input type="date" id="fechaFin" required><br>
            
            <button type="button" onclick="crearPrograma()">Guardar</button>
        </form>
    </body>
</html>
