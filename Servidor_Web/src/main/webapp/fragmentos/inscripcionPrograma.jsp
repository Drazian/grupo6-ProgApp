<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div style="max-width: 600px; margin: 20px auto; background: #fff; padding: 25px; border-radius: 8px; border: 1px solid #e0e0e0; box-shadow: 0 2px 5px rgba(0,0,0,0.05);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #8e44ad; padding-bottom: 10px; margin-top: 0;">
        Inscripción a Programa de Formación
    </h2> 

    <div id="mensaje-inscripcion-prog" style="display: none; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-weight: bold;"></div>

    <form id="formInscripcionPrograma" onsubmit="inscribirPrograma(event)">
        <div style="margin-bottom: 20px;">
            <label style="display: block; font-weight: bold; color: #34495e; margin-bottom: 8px;">
                Seleccione el Programa de Formación:
            </label>
            <select name="programa" id="selectProgramaInsc" required style="width: 100%; padding: 10px; border-radius: 4px; border: 1px solid #ccc; font-size: 14px;">
                <option value="">-- Seleccionar Programa --</option>
                <c:forEach var="prog" items="${requestScope.listaProgramas}">
                    <option value="${fn:escapeXml(prog.nombre)}">${prog.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <div style="display: flex; gap: 10px; justify-content: flex-end;">
            <button type="button" onclick="event.preventDefault(); cargarConsultaUsuario();" style="background: #95a5a6; color: white; border: none; padding: 10px 18px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                Cancelar
            </button>
            <button type="submit" style="background: #8e44ad; color: white; border: none; padding: 10px 18px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                Confirmar Inscripción
            </button>
        </div>
    </form>
</div>