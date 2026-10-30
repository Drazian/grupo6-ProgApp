<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div style="max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">Seleccionar Estudiantes</h2>
    
    <div id="mensaje-seleccion" style="display: none; padding: 10px; margin-bottom: 15px; border-radius: 4px;"></div>

    <!-- Filtros Superiores en Cascada -->
    <div style="display: flex; gap: 15px; margin-bottom: 20px;">
        <div style="flex: 1;">
            <label style="font-weight: bold; color: #34495e;">Instituto:</label>
            <select id="instSeleccion" onchange="cargarCursosParaSeleccion(this.value)" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="${fn:escapeXml(inst.nombre)}">${inst.nombre}</option>
                </c:forEach>
            </select>
        </div> 
        <div style="flex: 1;">
            <label style="font-weight: bold; color: #34495e;">Curso:</label>
            <select id="cursoSeleccion" onchange="cargarEdicionesParaSeleccion(this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un instituto</option>
            </select>
        </div>
        <div style="flex: 1;">
            <label style="font-weight: bold; color: #34495e;">Edición:</label>
            <select id="edicionSeleccion" onchange="cargarTablaInscripciones(this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un curso</option>
            </select>
        </div>
    </div>

    <!-- Formulario que envuelve la Tabla para guardar masivamente -->
    <form id="formSeleccionEstudiantes" onsubmit="guardarSeleccionEstudiantes(event)">
        <!-- Input oculto para saber a qué edición estamos mandando la actualización -->
        <input type="hidden" id="edicionOculta" name="edicion" value="">
        
        <!-- Aquí adentro el JS inyectará el fragmento "tablaInscripciones.jsp" -->
        <div id="contenedor-tabla-inscripciones">
            <p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">
                Seleccione Instituto, Curso y Edición para ver las inscripciones.
            </p>
        </div>
        
        <div style="margin-top: 20px; text-align: right;">
            <button type="submit" id="btnGuardarSeleccion" disabled style="background-color: #2ecc71; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold;">
                Guardar Selección
            </button>
        </div>
    </form>
</div>