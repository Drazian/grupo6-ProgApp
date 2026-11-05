<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Acta de una edición cerrada: datos básicos + estudiantes participantes con su nota --%>
<c:choose>
    <c:when test="${not empty errorActa}">
        <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
            <h4 style="margin: 0; color: #d35400;">No se puede mostrar el acta</h4>
            <p style="margin: 5px 0 0 0; color: #e67e22;"><c:out value="${errorActa}"/></p>
        </div>
    </c:when>
    <c:otherwise>
        <!-- Datos básicos de la Edición de Curso -->
        <c:if test="${not empty edicion}">
            <div style="display: flex; gap: 20px; align-items: center; background: #f4f6f7; padding: 15px; border-radius: 6px; margin-bottom: 20px; border: 1px solid #e0e0e0; flex-wrap: wrap;">
                <img src="ImagenServlet?nombre=${fn:escapeXml(empty edicion.imagen ? 'usr.png' : edicion.imagen)}"
                     alt="Portada Edición"
                     style="width: 140px; height: 100px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc;">

                <div style="flex: 1; min-width: 250px; line-height: 1.6;">
                    <h3 style="margin: 0 0 5px 0; color: #2c3e50;">
                        <a href="#" data-edicion="${fn:escapeXml(edicion.nombre)}" onclick="cargarConsultaEdicion(this.dataset.edicion); return false;" style="color: #2c3e50; text-decoration: none;">
                            <c:out value="${edicion.nombre}"/>
                        </a>
                        <span style="background: #8e44ad; color: white; padding: 3px 10px; border-radius: 12px; font-size: 12px; vertical-align: middle; margin-left: 8px;">Cerrada</span>
                    </h3>
                    <div>
                        <a href="#" data-curso="${fn:escapeXml(edicion.curso.nombre)}" onclick="verDetallesCurso(this.dataset.curso); return false;" style="color: #2980b9; font-weight: bold; text-decoration: underline; font-size: 14px;">
                            Ver información del Curso (<c:out value="${edicion.curso.nombre}"/>)
                        </a>
                    </div>
                    <div style="font-size: 13px; color: #34495e; margin-top: 8px; display: flex; gap: 15px; flex-wrap: wrap;">
                        <span><strong>Fecha inicio:</strong> ${edicion.fechaInicio}</span>
                        <span><strong>Fecha fin:</strong> ${edicion.fechaFin}</span>
                        <span><strong>Cupo:</strong> ${empty edicion.cupo or edicion.cupo lt 1 ? 'Sin límite' : edicion.cupo}</span>
                        <span><strong>Fecha de cierre:</strong> ${edicion.fechaCierre}</span>
                    </div>
                    <c:if test="${not empty edicion.docentes}">
                        <div style="font-size: 13px; color: #34495e; margin-top: 4px;">
                            <strong>Docentes:</strong>
                            <c:forEach var="doc" items="${edicion.docentes}" varStatus="st">
                                <c:out value="${doc.nombre} ${doc.apellido}"/><c:if test="${not st.last}">, </c:if>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:if>

        <!-- Acta: estudiantes participantes (inscripción "Aceptada") y su nota -->
        <c:choose>
            <c:when test="${not empty acta}">
                <div style="margin-bottom: 12px; padding: 10px; background: #f5eef8; border-left: 4px solid #8e44ad; border-radius: 4px; display: flex; gap: 25px; flex-wrap: wrap;">
                    <strong style="color: #6c3483;">Estudiantes participantes: ${acta.size()}</strong>
                    <c:if test="${not empty promedioActa}">
                        <strong style="color: #6c3483;">Promedio de la edición: ${promedioActa}</strong>
                    </c:if>
                </div>

                <table id="tabla-acta-edicion" style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead>
                        <tr style="background-color: #2c3e50; color: white;">
                            <th style="padding: 10px; border: 1px solid #ddd; width: 60px; text-align: center;">Foto</th>
                            <th style="padding: 10px; border: 1px solid #ddd;">Estudiante</th>
                            <th style="padding: 10px; border: 1px solid #ddd;">Email</th>
                            <th style="padding: 10px; border: 1px solid #ddd; width: 130px; text-align: center; white-space: nowrap;">Nota (1 a 12)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ins" items="${acta}">
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
                                    <c:choose>
                                        <c:when test="${not empty ins.nota}">
                                            <span style="background: #2c3e50; color: white; padding: 4px 12px; border-radius: 12px; font-size: 14px; font-weight: bold;">${ins.nota}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #95a5a6; font-style: italic; font-size: 13px;">Sin nota</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
                    <h4 style="margin: 0; color: #d35400;">Sin estudiantes participantes</h4>
                    <p style="margin: 5px 0 0 0; color: #e67e22;">
                        Esta edición de curso no tuvo inscripciones en estado "Aceptada".
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
