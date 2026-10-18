<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div style="background: #fff; border: 1px solid #e0e0e0; border-radius: 6px; padding: 20px;">
    
    <div style="display: flex; gap: 20px; align-items: flex-start; justify-content: space-between; margin-bottom: 20px;">

        <div style="display: flex; gap: 20px; align-items: flex-start;">
            <img src="ImagenServlet?nombre=${empty requestScope.usuarioPerfil.imagen ? 'usr.png' : requestScope.usuarioPerfil.imagen}" 
                 alt="Foto Perfil" 
                 style="width: 90px; height: 90px; border-radius: 50%; object-fit: cover; border: 2px solid #3498db; cursor: pointer;"
                 onclick="event.preventDefault(); cargarMiPerfil('${requestScope.usuarioPerfil.nickname}');">

            <div style="line-height: 1.5;">
                <h2 style="margin: 0; color: #2c3e50; font-size: 22px;">
                    ${requestScope.usuarioPerfil.nombre} ${requestScope.usuarioPerfil.apellido}
                </h2>
                <p style="margin: 2px 0; color: #7f8c8d; font-weight: bold;">@${requestScope.usuarioPerfil.nickname}</p>
                <p style="margin: 2px 0; font-size: 14px;"><b>Correo electronico:</b> ${requestScope.usuarioPerfil.email}</p>
                <p style="margin: 2px 0; font-size: 14px;"><b>Fecha de Nacimiento:</b> ${requestScope.usuarioPerfil.fNacimiento}</p>
                
                <!-- Informacion de institutos para docentes -->
                <c:if test="${requestScope.usuarioPerfil.tipoUsuario == 'DOCENTE'}">
                    <p style="margin: 5px 0 0 0; font-size: 14px;">
                        <b>Institutos a los que pertenece:</b> 
                        <c:choose>
                            <c:when test="${not empty requestScope.usuarioPerfil.institutos}">
                                <c:forEach var="inst" items="${requestScope.usuarioPerfil.institutos}" varStatus="loop">
                                    <span style="background: #e8f4f8; color: #2980b9; padding: 2px 6px; border-radius: 3px; font-size: 12px; font-weight: bold;">${inst}</span>${!loop.last ? ' ' : ''}
                                </c:forEach>
                            </c:when>
                            <c:otherwise><span style="color: #95a5a6; font-style: italic;">Sin institutos asignados</span></c:otherwise>
                        </c:choose>
                    </p>
                </c:if>
            </div>
        </div>

        <!-- ACCIONES (Modificar vs Seguir/Dejar de seguir) -->
        <c:if test="${not empty sessionScope.usuarioLogueado}">
            <div>
                <c:choose>
                    <c:when test="${sessionScope.usuarioLogueado.nickname == requestScope.usuarioPerfil.nickname}">
                        <button type="button" 
                                onclick="cargarModificarUsuario();" 
                                style="background-color: #2ecc71; color: white; border: none; padding: 8px 14px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                            Modificar Perfil
                        </button>
                    </c:when>

                    <c:otherwise>
                        <c:choose>
                            <c:when test="${requestScope.loSigo}">
                                <button type="button" 
                                        onclick="dejarDeSeguirRapido('${requestScope.usuarioPerfil.nickname}')" 
                                        style="background-color: #d9534f; color: white; border: none; padding: 8px 14px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                                    Dejar de seguir
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button type="button" 
                                        onclick="seguirRapido('${requestScope.usuarioPerfil.nickname}')" 
                                        style="background-color: #0275d8; color: white; border: none; padding: 8px 14px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                                    Seguir
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

    </div>

    <hr style="opacity: 0.3; margin: 20px 0;">

    <!-- BARRA DE PESTANAS (TABS) -->
    <div style="border-bottom: 2px solid #ddd; margin-bottom: 15px; display: flex; gap: 5px;">
        <c:choose>
            <c:when test="${requestScope.usuarioPerfil.tipoUsuario == 'DOCENTE'}">
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabEdicionesDocente')" id="btn-tabEdicionesDocente" 
                        style="padding: 10px 15px; border: none; background: #3498db; color: white; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Ediciones / Cursos
                </button>
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabProgramas')" id="btn-tabProgramas" 
                        style="padding: 10px 15px; border: none; background: #e0e0e0; color: #333; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Programas de Formacion
                </button>
            </c:when>
            <c:otherwise>
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabInscripciones')" id="btn-tabInscripciones" 
                        style="padding: 10px 15px; border: none; background: #3498db; color: white; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Inscripciones a Ediciones
                </button>
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabProgramas')" id="btn-tabProgramas" 
                        style="padding: 10px 15px; border: none; background: #e0e0e0; color: #333; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Programas de Formacion
                </button>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- CONTENEDORES DE PESTANAS -->
    <div id="contenedor-tabs-contenido">
        
        <%-- TAB 1: INSCRIPCIONES (ESTUDIANTE) --%>
        <c:if test="${requestScope.usuarioPerfil.tipoUsuario != 'DOCENTE'}">
            <div id="tabInscripciones" class="contenido-tab" style="display: block;">
                <table style="width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e0e0e0;">
                    <thead>
                        <tr style="background: #f8f9fa; border-bottom: 2px solid #ddd; text-align: left;">
                            <th style="padding: 10px;">Edicion / Curso</th>
                            <th style="padding: 10px;">Fecha Inscripcion</th>
                            <th style="padding: 10px; text-align: center;">Estado</th>
                            <th style="padding: 10px; text-align: center;">Accion</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ins" items="${requestScope.listaInscripcionesCompletas}">
                            <%-- REGLA DE NEGOCIO: Si es Rechazada (valorEstado == 3), solo se muestra si es el propio perfil del usuario --%>
                            <c:if test="${ins.valorEstado != 3 or requestScope.esPropioPerfil}">
                                <tr style="border-bottom: 1px solid #eee;">
                                    <td style="padding: 10px;">
                                        <!-- Enlace a Consulta de Edicion -->
                                        <a href="#" onclick="event.preventDefault(); cargarConsultaEdicion('${ins.edicion.nombre}')" 
                                           style="color: #2980b9; font-weight: bold; text-decoration: none;">
                                            ${ins.edicion.nombre}
                                        </a>
                                        <c:if test="${not empty ins.edicion.curso}">
                                            <span style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 3px;">
                                                Curso: 
                                                <!-- Enlace a Consulta de Curso -->
                                                <a href="#" onclick="event.preventDefault(); verDetallesCurso('${ins.edicion.curso.nombre}')" style="color: #34495e; font-weight: bold;">
                                                    ${ins.edicion.curso.nombre}
                                                </a>
                                            </span>
                                        </c:if>
                                    </td>
                                    
                                    <td style="padding: 10px;">${ins.fechaInscripcion}</td>

                                    <!-- Estado de la inscripcion -->
                                    <td style="padding: 10px; text-align: center;">
                                        <c:choose>
                                            <c:when test="${ins.valorEstado == 2}">
                                                <span style="background: #2ecc71; color: white; padding: 3px 8px; border-radius: 3px; font-size: 12px; font-weight: bold;">
                                                    Aceptada
                                                </span>
                                            </c:when>
                                            <c:when test="${ins.valorEstado == 3}">
                                                <span style="background: #e74c3c; color: white; padding: 3px 8px; border-radius: 3px; font-size: 12px; font-weight: bold;">
                                                    Rechazada
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="background: #f39c12; color: white; padding: 3px 8px; border-radius: 3px; font-size: 12px; font-weight: bold;">
                                                    Inscripto
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td style="padding: 10px; text-align: center;">
                                        <button type="button" onclick="cargarConsultaEdicion('${ins.edicion.nombre}')" 
                                                style="background: #17a2b8; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                            Ver Edicion
                                        </button>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        <c:if test="${empty requestScope.listaInscripcionesCompletas}">
                            <tr>
                                <td colspan="4" style="text-align: center; color: #7f8c8d; padding: 15px;">
                                    No hay inscripciones registradas para este estudiante.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </c:if>

        <%-- TAB 2: EDICIONES DICTADAS (DOCENTE) --%>
        <c:if test="${requestScope.usuarioPerfil.tipoUsuario == 'DOCENTE'}">
            <div id="tabEdicionesDocente" class="contenido-tab" style="display: block;">
                <table style="width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e0e0e0;">
                    <thead>
                        <tr style="background: #f8f9fa; border-bottom: 2px solid #ddd; text-align: left;">
                            <th style="padding: 10px;">Edicion / Curso</th>
                            <th style="padding: 10px;">Fecha Inicio</th>
                            <th style="padding: 10px;">Fecha Fin</th>
                            <th style="padding: 10px; text-align: center;">Accion</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ed" items="${requestScope.listaEdicionesDocente}">
                            <tr style="border-bottom: 1px solid #eee;">
                                <td style="padding: 10px;">
                                    <!-- Enlace a la Consulta de Edicion -->
                                    <a href="#" onclick="event.preventDefault(); cargarConsultaEdicion('${ed.nombre}')" 
                                       style="color: #2980b9; font-weight: bold; text-decoration: none;">
                                        🎓 ${ed.nombre}
                                    </a>

                                    <!-- Enlace opcional directo al Curso Asociado -->
                                    <c:if test="${not empty ed.curso}">
                                        <span style="font-size: 12px; color: #7f8c8d; display: block; margin-top: 3px;">
                                            Curso: 
                                            <a href="#" onclick="event.preventDefault(); verDetallesCurso('${ed.curso.nombre}')" 
                                               style="color: #34495e; font-weight: bold; text-decoration: underline;">
                                                ${ed.curso.nombre}
                                            </a>
                                        </span>
                                    </c:if>
                                </td>

                                <td style="padding: 10px;">${ed.fechaInicio}</td>
                                <td style="padding: 10px;">${ed.fechaFin}</td>

                                <td style="padding: 10px; text-align: center; white-space: nowrap;">
                                    <!-- Boton para ver Edicion -->
                                    <button type="button" onclick="cargarConsultaEdicion('${ed.nombre}')" 
                                            style="background: #17a2b8; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px; margin-right: 4px;">
                                        Ver Edicion
                                    </button>

                                    <!-- Boton para ver Curso completo -->
                                    <c:if test="${not empty ed.curso}">
                                        <button type="button" onclick="verDetallesCurso('${ed.curso.nombre}')" 
                                                style="background: #34495e; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                            Ver Curso
                                        </button>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.listaEdicionesDocente}">
                            <tr>
                                <td colspan="4" style="text-align: center; color: #7f8c8d; padding: 15px;">
                                    No hay ediciones asociadas a este docente.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </c:if>

        <%-- TAB 3: PROGRAMAS DE FORMACION --%>
        <div id="tabProgramas" class="contenido-tab" style="display: none;">
            <table style="width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e0e0e0;">
                <thead>
                    <tr style="background: #f8f9fa; border-bottom: 2px solid #ddd; text-align: left;">
                        <th style="padding: 10px;">Programa de Formacion</th>
                        <th style="padding: 10px;">Descripcion</th>
                        <th style="padding: 10px; text-align: center;">Accion</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="prog" items="${requestScope.listaProgramas}">
                        <tr style="border-bottom: 1px solid #eee;">
                            <td style="padding: 10px;">
                                <a href="#" onclick="event.preventDefault(); cargarDetallePrograma('${prog.nombre}')" 
                                   style="color: #8e44ad; font-weight: bold; text-decoration: none;">
                                    ${prog.nombre}
                                </a>
                            </td>
                            <td style="padding: 10px;">${prog.descripcion}</td>
                            <td style="padding: 10px; text-align: center;">
                                <button type="button" onclick="cargarDetallePrograma('${prog.nombre}')" 
                                        style="background: #8e44ad; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                    Ver Programa
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty requestScope.listaProgramas}">
                        <tr>
                            <td colspan="3" style="text-align: center; color: #7f8c8d; padding: 15px;">
                                No hay programas de formacion vinculados.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

    </div>

</div>