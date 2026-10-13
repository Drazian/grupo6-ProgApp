<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${not empty inscripciones}">
        
        <!-- Cabecera de la tabla con el Ordenador (Req. 7.5) -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; background: #f8f9fa; padding: 10px; border-radius: 4px;">
            <strong style="color: #2c3e50;">Inscripciones encontradas: ${inscripciones.size()}</strong>
            
            <div>
                <label for="ordenInscripciones" style="font-weight: bold; margin-right: 10px; color: #34495e;">Ordenar por:</label>
                <select id="ordenInscripciones" onchange="ordenarInscripciones(this.value)" style="padding: 5px; border-radius: 4px;">
                    <option value="fecha">Fecha de Inscripción</option>
                    <option value="prioridad">Prioridad (Mayor a menor)</option>
                </select>
            </div>
        </div>

        <!-- Tabla -->
        <table style="width: 100%; border-collapse: collapse; text-align: left;">
            <thead>
                <tr style="background-color: #34495e; color: white;">
                    <th style="padding: 10px; border: 1px solid #ddd;">Estudiante</th>
                    <th style="padding: 10px; border: 1px solid #ddd;">Fecha Inscr.</th>
                    <th style="padding: 10px; border: 1px solid #ddd;">Prioridad</th>
                    <th style="padding: 10px; border: 1px solid #ddd;">Estado Actual</th>
                    <th style="padding: 10px; border: 1px solid #ddd; text-align: center;">Evaluación</th>
                </tr>
            </thead>
            <tbody id="cuerpoTablaInscripciones">
                <c:forEach var="ins" items="${inscripciones}">
                    <!-- Guardamos data-fecha y data-prioridad ocultos en la fila para que JS pueda ordenarlos -->
                    <tr class="fila-inscripcion" data-fecha="${ins.fechaInscripcion}" data-prioridad="${ins.prioridad}">
                        
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                            <strong>${ins.estudiante.nombre} ${ins.estudiante.apellido}</strong><br>
                            <span style="font-size: 12px; color: #7f8c8d;">@${ins.estudiante.nickname}</span>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">${ins.fechaInscripcion}</td>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                            <span style="background: #e74c3c; color: white; padding: 3px 8px; border-radius: 12px; font-size: 12px; font-weight: bold;">
                                ${ins.prioridad}
                            </span>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                            <!-- Mostramos el estado actual con colores -->
                            <c:choose>
                                <c:when test="${ins.valorEstado == 1}"><span style="color: #f39c12; font-weight: bold;">${ins.estado}</span></c:when>
                                <c:when test="${ins.valorEstado == 2}"><span style="color: #27ae60; font-weight: bold;">${ins.estado}</span></c:when>
                                <c:when test="${ins.valorEstado == 3}"><span style="color: #c0392b; font-weight: bold;">${ins.estado}</span></c:when>
                                <c:otherwise>${ins.estado}</c:otherwise>
                            </c:choose>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ddd; text-align: center; background: #fdfefe;">
                            <!-- Inputs que viajarán al Servlet como "estado_nickname" -->
                            <label style="margin-right: 15px; cursor: pointer;">
                                <input type="radio" name="estado_${ins.estudiante.nickname}" value="2" ${ins.valorEstado == 2 ? 'checked' : ''} required> 
                                <span style="color: #27ae60; font-weight: bold;">Aceptar</span>
                            </label>
                            <label style="cursor: pointer;">
                                <input type="radio" name="estado_${ins.estudiante.nickname}" value="3" ${ins.valorEstado == 3 ? 'checked' : ''} required> 
                                <span style="color: #c0392b; font-weight: bold;">Rechazar</span>
                            </label>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
            <h4 style="margin: 0; color: #d35400;">Sin inscripciones</h4>
            <p style="margin: 5px 0 0 0; color: #e67e22;">No hay estudiantes inscriptos en esta edición todavía.</p>
        </div>
    </c:otherwise>
</c:choose>