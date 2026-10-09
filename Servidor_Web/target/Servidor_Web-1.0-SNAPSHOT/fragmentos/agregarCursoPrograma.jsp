<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Agregar Curso a Programa</title>
    </head>
    <body>
        <form id="formAgregarCursoPrograma">
        <div>
            <label for="selectPrograma">Programa de Formacion:</label>
            <select id="selectPrograma" name="nombrePrograma" required>
                <option value="" disabled selected>-- Seleccione un Programa --</option>
                <c:forEach var="prog" items="${programas}">
                    <option value="${prog.getNombre()}">${prog.getNombre()}</option>
                </c:forEach>
            </select>
        </div>
        <br>
        <div>
            <label for="selectCurso">Curso:</label>
            <select id="selectCurso" name="nombreCurso" required>
                <option value="" disabled selected>-- Seleccione un Curso --</option>
                <c:forEach var="cur" items="${cursos}">
                    <option value="${cur.getNombre()}">${cur.getNombre()}</option>
                </c:forEach>
            </select>
        </div>

        <br>

        <button type="button" onclick="agregarCursoAPrograma()">Agregar Curso</button>
    </form>
        
        
        
    </body>
</html>
