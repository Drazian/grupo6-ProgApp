<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Datos básicos de la Edición de Curso -->
<c:if test="${not empty edicion}">
    <div style="display: flex; gap: 20px; align-items: center; background: #f4f6f7; padding: 15px; border-radius: 6px; margin-bottom: 20px; border: 1px solid #e0e0e0; flex-wrap: wrap;">
        <img src="ImagenServlet?nombre=${empty edicion.imagen ? 'usr.png' : edicion.imagen}" 
             alt="Portada Edición" 
             style="width: 140px; height: 100px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc;">
        
        <div style="flex: 1; min-width: 250px; line-height: 1.6;">
            <h3 style="margin: 0 0 5px 0; color: #2c3e50;">
                <a href="#" onclick="cargarConsultaEdicion('${edicion.nombre}'); return false;" style="color: #2c3e50; text-decoration: none;">
                    ${edicion.nombre}
                </a>
            </h3>
            <div>
                <a href="#" onclick="verDetallesCurso('${edicion.curso.nombre}'); return false;" style="color: #2980b9; font-weight: bold; text-decoration: underline; font-size: 14px;">
                    Ver información del Curso (${edicion.curso.nombre})
                </a>
            </div>
            <div style="font-size: 13px; color: #34495e; margin-top: 8px; display: flex; gap: 15px; flex-wrap: wrap;">
                <span><strong>Fecha inicio:</strong> ${edicion.fechaInicio}</span>
                <span><strong>Fecha fin:</strong> ${edicion.fechaFin}</span>
                <span><strong>Cupo:</strong> ${empty edicion.cupo ? 'Sin límite' : edicion.cupo}</span>
            </div>
        </div>
    </div>
</c:if>

<!-- Listado de Inscripciones en estado "Aceptada" -->
<c:choose>
    <c:when test="${not empty inscripcionesAceptadas}">
        <div style="margin-bottom: 12px; padding: 10px; background: #eafaf1; border-left: 4px solid #2ecc71; border-radius: 4px;">
            <strong style="color: #27ae60;">
                Total de estudiantes aceptados en esta edición: ${inscripcionesAceptadas.size()}
            </strong>
        </div>

        <table style="width: 100%; border-collapse: collapse; text-align: left;">
            <thead>
                <tr style="background-color: #2c3e50; color: white;">
                    <th style="padding: 10px; border: 1px solid #ddd; width: 60px; text-align: center;">Foto</th>
                    <th style="padding: 10px; border: 1px solid #ddd;">Estudiante</th>
                    <th style="padding: 10px; border: 1px solid #ddd;">Fecha de Inscripción</th>
                    <th style="padding: 10px; border: 1px solid #ddd; text-align: center;">Estado</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ins" items="${inscripcionesAceptadas}">
                    <tr>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff; text-align: center;">
                            <img src="ImagenServlet?nombre=${empty ins.estudiante.imagen ? 'usr.png' : ins.estudiante.imagen}" 
                                 alt="Avatar" 
                                 style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">
                        </td>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                            <strong style="color: #2c3e50;">${ins.estudiante.nombre} ${ins.estudiante.apellido}</strong><br>
                            <span style="font-size: 12px; color: #7f8c8d;">@${ins.estudiante.nickname}</span>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff;">
                            ${ins.fechaInscripcion}
                        </td>
                        <td style="padding: 10px; border: 1px solid #ddd; background: #fff; text-align: center;">
                            <span style="background: #27ae60; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold;">
                                Aceptada
                            </span>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div style="background: #fdf2e9; padding: 20px; border-left: 4px solid #e67e22; border-radius: 4px;">
            <h4 style="margin: 0; color: #d35400;">Sin estudiantes aceptados</h4>
            <p style="margin: 5px 0 0 0; color: #e67e22;">
                No hay inscripciones en estado "Aceptada" para esta edición de curso.
            </p>
        </div>
    </c:otherwise>
</c:choose>