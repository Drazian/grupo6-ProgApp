<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="background: #fff; border: 1px solid #e0e0e0; border-radius: 6px; padding: 20px;">
    
    <div style="display: flex; gap: 20px; align-items: flex-start; justify-content: space-between; margin-bottom: 20px;">

        <div style="display: flex; gap: 20px; align-items: flex-start;">
            <img src="ImagenServlet?nombre=${empty requestScope.usuarioPerfil.imagen ? 'usr.png' : requestScope.usuarioPerfil.imagen}" 
                 alt="Foto Perfil" 
                 style="width: 90px; height: 90px; border-radius: 50%; object-fit: cover; border: 2px solid #3498db;">

            <div style="line-height: 1.5;">
                <h2 style="margin: 0; color: #2c3e50; font-size: 22px;">
                    ${requestScope.usuarioPerfil.nombre} ${requestScope.usuarioPerfil.apellido}
                </h2>
                <p style="margin: 2px 0; color: #7f8c8d; font-weight: bold;">@${requestScope.usuarioPerfil.nickname}</p>
                <p style="margin: 2px 0; font-size: 14px;"><b>Correo electrónico:</b> ${requestScope.usuarioPerfil.email}</p>
                <p style="margin: 2px 0; font-size: 14px;"><b>Fecha de Nacimiento:</b> ${requestScope.usuarioPerfil.fNacimiento}</p>
                
                <!-- Si es docente muestra sus institutos -->
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

    <!-- BARRA DE PESTAÑAS (TABS) -->
    <div style="border-bottom: 2px solid #ddd; margin-bottom: 15px; display: flex; gap: 5px;">
        <c:choose>
            <c:when test="${requestScope.usuarioPerfil.tipoUsuario == 'DOCENTE'}">
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabEdicionesDocente')" id="btn-tabEdicionesDocente" 
                        style="padding: 10px 15px; border: none; background: #3498db; color: white; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Ediciones / Cursos
                </button>
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabProgramas')" id="btn-tabProgramas" 
                        style="padding: 10px 15px; border: none; background: #e0e0e0; color: #333; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Programas de Formación
                </button>
            </c:when>
            <c:otherwise>
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabInscripciones')" id="btn-tabInscripciones" 
                        style="padding: 10px 15px; border: none; background: #3498db; color: white; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Inscripciones a Ediciones
                </button>
                <button type="button" class="tab-btn" onclick="cambiarPestana('tabProgramas')" id="btn-tabProgramas" 
                        style="padding: 10px 15px; border: none; background: #e0e0e0; color: #333; border-radius: 4px 4px 0 0; cursor: pointer; font-weight: bold;">
                    Programas de Formación
                </button>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- CONTENEDORES CON TABLAS DE DATOS -->
    <div id="contenedor-tabs-contenido">
        
        <%-- TAB 1: INSCRIPCIONES (ESTUDIANTE) --%>
        <c:if test="${requestScope.usuarioPerfil.tipoUsuario != 'DOCENTE'}">
            <div id="tabInscripciones" class="contenido-tab" style="display: block;">
                <table style="width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e0e0e0;">
                    <thead>
                        <tr style="background: #f8f9fa; border-bottom: 2px solid #ddd; text-align: left;">
                            <th style="padding: 10px;">Edición de Curso</th>
                            <th style="padding: 10px;">Fecha Inicio</th>
                            <th style="padding: 10px;">Fecha Fin</th>
                            <th style="padding: 10px; text-align: center;">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ed" items="${requestScope.listaInscripciones}">
                            <tr style="border-bottom: 1px solid #eee;">
                                <td style="padding: 10px;">
                                    <a href="#" onclick="event.preventDefault(); cargarConsultaEdicion('${ed.nombre}')" 
                                       style="color: #2980b9; font-weight: bold; text-decoration: none;">
                                        📘 ${ed.nombre}
                                    </a>
                                </td>
                                <td style="padding: 10px;">${ed.fechaInicio}</td>
                                <td style="padding: 10px;">${ed.fechaFin}</td>
                                <td style="padding: 10px; text-align: center;">
                                    <button type="button" onclick="cargarConsultaEdicion('${ed.nombre}')" 
                                            style="background: #17a2b8; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                        Ver Edición
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requestScope.listaInscripciones}">
                            <tr>
                                <td colspan="4" style="text-align: center; color: #7f8c8d; padding: 15px;">
                                    No hay ediciones/inscripciones registradas para este estudiante.
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
                            <th style="padding: 10px;">Nombre Edición</th>
                            <th style="padding: 10px;">Fecha Inicio</th>
                            <th style="padding: 10px;">Fecha Fin</th>
                            <th style="padding: 10px; text-align: center;">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ed" items="${requestScope.listaEdicionesDocente}">
                            <tr style="border-bottom: 1px solid #eee;">
                                <td style="padding: 10px;">
                                    <a href="#" onclick="event.preventDefault(); cargarConsultaEdicion('${ed.nombre}')" 
                                       style="color: #2980b9; font-weight: bold; text-decoration: none;">
                                        🎓 ${ed.nombre}
                                    </a>
                                </td>
                                <td style="padding: 10px;">${ed.fechaInicio}</td>
                                <td style="padding: 10px;">${ed.fechaFin}</td>
                                <td style="padding: 10px; text-align: center;">
                                    <button type="button" onclick="cargarConsultaEdicion('${ed.nombre}')" 
                                            style="background: #17a2b8; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                        Ver Consulta
                                    </button>
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

        <%-- TAB 3: PROGRAMAS DE FORMACIÓN--%>
        <div id="tabProgramas" class="contenido-tab" style="display: none;">
            <table style="width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e0e0e0;">
                <thead>
                    <tr style="background: #f8f9fa; border-bottom: 2px solid #ddd; text-align: left;">
                        <th style="padding: 10px;">Programa de Formación</th>
                        <th style="padding: 10px;">Descripción</th>
                        <th style="padding: 10px; text-align: center;">Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="prog" items="${requestScope.listaProgramas}">
                        <tr style="border-bottom: 1px solid #eee;">
                            <td style="padding: 10px;">
                                <a href="#" onclick="event.preventDefault(); cargarDetallePrograma('${prog.nombre}')" 
                                   style="color: #8e44ad; font-weight: bold; text-decoration: none;">
                                    📜 ${prog.nombre}
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
                                No hay programas de formación vinculados.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

    </div>

</div>