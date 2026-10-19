<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; margin-bottom: 5px;">Edición: ${edicion.nombre}</h2>
    <h4 style="color: #7f8c8d; margin-top: 0;">Curso Asociado: ${edicion.curso.nombre}</h4>
    
    <!-- FIX 1: Cambiado a edicion.imagen para que cargue la portada correcta -->
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
                                        <!-- FIX 2: Llamada al ImagenServlet en lugar de la carpeta física -->
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
</div>