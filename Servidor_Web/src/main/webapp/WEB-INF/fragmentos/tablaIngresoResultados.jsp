<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Planilla de notas de una edición abierta: datos básicos + estudiantes aceptados con un combo de nota (1 a 12) --%>
<c:choose>
    <c:when test="${not empty errorIngreso}">
        <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
            <h4 style="margin: 0; color: #d35400;">No se pueden ingresar resultados</h4>
            <p style="margin: 5px 0 0 0; color: #e67e22;"><c:out value="${errorIngreso}"/></p>
        </div>
    </c:when>
    <c:otherwise>
        <%@ include file="datosEdicionResultados.jspf" %>

        <c:choose>
            <c:when test="${not empty participantes}">
                <%-- Se anula el estilo global de form (caja de 800px con sombra) para que la planilla ocupe todo el ancho --%>
                <form id="formIngresoResultados" onsubmit="guardarResultados(event, false)"
                      style="max-width: none; padding: 0; border: none; box-shadow: none; background: transparent;">
                    <input type="hidden" name="edicion" value="${fn:escapeXml(edicion.nombre)}">

                    <div style="margin-bottom: 12px; padding: 10px; background: #eaf2f8; border-left: 4px solid #2980b9; border-radius: 4px; display: flex; gap: 25px; flex-wrap: wrap;">
                        <strong style="color: #1f618d;">Estudiantes aceptados: ${participantes.size()}</strong>
                        <strong style="color: #1f618d;">Con nota: <span id="contador-notas">0</span> de ${participantes.size()}</strong>
                    </div>

                    <table id="tabla-ingreso-resultados" style="width: 100%; border-collapse: collapse; text-align: left;">
                        <thead>
                            <tr style="background-color: #2c3e50; color: white;">
                                <th style="padding: 10px; border: 1px solid #ddd; width: 60px; text-align: center;">Foto</th>
                                <th style="padding: 10px; border: 1px solid #ddd;">Estudiante</th>
                                <th style="padding: 10px; border: 1px solid #ddd;">Email</th>
                                <th style="padding: 10px; border: 1px solid #ddd; width: 150px; text-align: center; white-space: nowrap;">Nota (1 a 12)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="ins" items="${participantes}">
                                <tr>
                                    <td style="padding: 10px; border: 1px solid #ddd; background: #fff; text-align: center;">
                                        <img src="ImagenServlet?nombre=${fn:escapeXml(empty ins.estudiante.imagen ? 'usr.png' : ins.estudiante.imagen)}"
                                             alt="Avatar"
                                             style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">
                                    </td>
                                    <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                                        <strong style="color: #2c3e50;"><c:out value="${ins.estudiante.nombre} ${ins.estudiante.apellido}"/></strong><br>
                                        <span style="font-size: 12px; color: #7f8c8d;">@<c:out value="${ins.estudiante.nickname}"/></span>
                                    </td>
                                    <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                                        <c:out value="${ins.estudiante.email}"/>
                                    </td>
                                    <td style="padding: 10px; border: 1px solid #ddd; background: #fff; text-align: center;">
                                        <select name="nota_${fn:escapeXml(ins.estudiante.nickname)}" class="select-nota"
                                                aria-label="Nota de ${fn:escapeXml(ins.estudiante.nombre)} ${fn:escapeXml(ins.estudiante.apellido)}"
                                                onchange="actualizarContadorNotas()"
                                                style="padding: 6px 10px; border-radius: 4px; border: 1px solid #bdc3c7; font-size: 14px; min-width: 100px;">
                                            <option value="">Sin nota</option>
                                            <c:forEach begin="1" end="12" var="n">
                                                <option value="${n}" ${ins.nota == n ? 'selected' : ''}>${n}</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div id="mensaje-ingreso" style="display: none; margin-top: 15px; padding: 12px; border-radius: 4px; font-weight: bold;"></div>

                    <div style="margin-top: 20px; display: flex; gap: 10px; justify-content: flex-end; flex-wrap: wrap;">
                        <button type="submit" id="btnGuardarNotas" class="btn-moderno" style="background-color: #2980b9;">Guardar notas</button>
                        <button type="button" id="btnCerrarEdicion" class="btn-moderno" style="background-color: #8e44ad;" onclick="confirmarCierreEdicion()">Guardar y cerrar edición</button>
                    </div>
                    <p style="margin: 8px 0 0 0; text-align: right; font-size: 12px; color: #7f8c8d;">
                        Para cerrar la edición todos los estudiantes deben tener nota. Después del cierre las notas no se pueden modificar.
                    </p>
                </form>
            </c:when>
            <c:otherwise>
                <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
                    <h4 style="margin: 0; color: #d35400;">Sin estudiantes aceptados</h4>
                    <p style="margin: 5px 0 0 0; color: #e67e22;">
                        Esta edición no tiene inscripciones en estado "Aceptada", por lo que no hay notas para ingresar.
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
