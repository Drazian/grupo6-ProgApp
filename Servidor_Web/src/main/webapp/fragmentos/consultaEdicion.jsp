<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; margin-bottom: 5px;">Edición: ${edicion.nombre}</h2>
    <h4 style="color: #7f8c8d; margin-top: 0;">Curso Asociado: ${edicion.curso.nombre}</h4>
    
    <img src="ImagenServlet?nombre=${edicion.imagen}" alt="Portada" style="width: 100%; max-width: 400px; height: 250px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); margin-bottom: 20px;">
    
    <hr style="border: 1px solid #eee; margin: 20px 0;">

    <div style="display: flex; gap: 30px; flex-wrap: wrap;">
        <!-- Columna de Datos de la Edición -->
        <div style="flex: 1; min-width: 250px;">
            <h3 style="border-bottom: 2px solid #3498db; padding-bottom: 5px;">Datos Generales</h3>
            <ul style="list-style: none; padding: 0; line-height: 2;">
                <li><strong>Fecha de Publicación:</strong> ${edicion.fechaPublicacion}</li>
                <li><strong>Fecha de Inicio:</strong> ${edicion.fechaInicio}</li>
                <li><strong>Fecha de Fin:</strong> ${edicion.fechaFin}</li>
                <li><strong>Cupo Máximo:</strong> ${empty edicion.cupo ? 'Sin límite' : edicion.cupo}</li>
            </ul>
        </div> 

        <!-- Columna de Docentes con Imagen -->
        <div style="flex: 1; min-width: 300px;">
            <h3 style="border-bottom: 2px solid #2ecc71; padding-bottom: 5px;">Docentes Asignados</h3>
            
            <c:choose>
                <c:when test="${not empty edicion.docentes}">
                    <div style="display: flex; flex-direction: column; gap: 15px; margin-top: 15px;">
                        <c:forEach var="docente" items="${edicion.docentes}">
                            <div style="display: flex; align-items: center; background: #f8f9fa; padding: 10px; border-radius: 6px; border: 1px solid #e9ecef;">
                                
                                <c:choose>
                                    <c:when test="${not empty docente.imagen}">
                                        <img src="ImagenServlet?nombre=${docente.imagen}" alt="Foto" style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover; margin-right: 15px; border: 2px solid #ddd;">
                                    </c:when>
                                    <c:otherwise>
                                        <div style="width: 50px; height: 50px; border-radius: 50%; background: #bdc3c7; color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 20px; margin-right: 15px;">
                                            ${docente.nombre.substring(0,1)}${docente.apellido.substring(0,1)}
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                
                                <div>
                                    <strong style="display: block; color: #2c3e50;">${docente.nombre} ${docente.apellido}</strong>
                                    <span style="font-size: 13px; color: #7f8c8d;">@${docente.nickname}</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #7f8c8d; font-style: italic;">No hay docentes asignados.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div style="margin-top: 30px; border-top: 2px solid #ecf0f1; padding-top: 15px;">
        <h4 style="color: #2c3e50; margin-bottom: 15px;">Estudiantes Aceptados en esta Edición</h4>
        
        <c:choose>
            <c:when test="${not empty inscripcionesAceptadas}">
                <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 13px;">
                    <thead>
                        <tr style="background-color: #34495e; color: white;">
                            <th style="padding: 8px; border: 1px solid #ddd; width: 40px; text-align: center;">Foto</th>
                            <th style="padding: 8px; border: 1px solid #ddd;">Estudiante</th>
                            <th style="padding: 8px; border: 1px solid #ddd;">Fecha de Inscripción</th>
                            <th style="padding: 8px; border: 1px solid #ddd; text-align: center;">Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ins" items="${inscripcionesAceptadas}">
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd; background: #fff; text-align: center;">
                                    <img src="ImagenServlet?nombre=${empty ins.estudiante.imagen ? 'usr.png' : ins.estudiante.imagen}" 
                                         alt="Avatar" 
                                         style="width: 35px; height: 35px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">
                                </td>
                                <td style="padding: 8px; border: 1px solid #ddd; background: #fff;">
                                    <strong style="color: #2c3e50;">${ins.estudiante.nombre} ${ins.estudiante.apellido}</strong><br>
                                    <span style="font-size: 11px; color: #7f8c8d;">@${ins.estudiante.nickname}</span>
                                </td>
                                <td style="padding: 8px; border: 1px solid #ddd; background: #fff;">
                                    ${ins.fechaInscripcion}
                                </td>
                                <td style="padding: 8px; border: 1px solid #ddd; background: #fff; text-align: center;">
                                    <span style="background: #27ae60; color: white; padding: 3px 8px; border-radius: 10px; font-size: 11px; font-weight: bold;">
                                        Aceptada
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div style="background: #fdf2e9; padding: 15px; border-left: 4px solid #e67e22; border-radius: 4px;">
                    <p style="margin: 0; color: #d35400; font-size: 13px;">No hay estudiantes aceptados en esta edición por el momento.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>