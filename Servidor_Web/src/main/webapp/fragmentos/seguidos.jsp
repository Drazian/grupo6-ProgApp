<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<h2>Ingrese nickname para buscar</h2>

<div style="display: flex; gap: 20px; align-items: flex-start;">

    <!-- PANEL IZQUIERDO: Buscador + Lista Scrolleable de Usuarios -->
    <div style="flex: 1; max-width: 450px; background: #f9f9f9; padding: 15px; border: 1px solid #ddd; border-radius: 5px;">
        
        <!-- Buscador de Nickname en tiempo real -->
        <div style="margin-bottom: 15px;">
            <input type="text" id="filtroNickname" placeholder="Buscar por nickname..." 
                   onkeyup="filtrarUsuariosRedSocial()" 
                   style="width: 100%; padding: 6px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 3px;">
        </div>
 
        <!-- CONTENEDOR SCROLLEABLE (Límite aproximado de 10 usuarios visibles) -->
        <div style="max-height: 480px; overflow-y: auto; border: 1px solid #e0e0e0; border-radius: 3px;">
            <table id="tablaUsuariosSocial" style="width: 100%; border-collapse: collapse; background: #fff;">
                <thead style="position: sticky; top: 0; background: #eee; z-index: 1;">
                    <tr style="border-bottom: 2px solid #ccc; text-align: left;">
                        <th style="padding: 8px;">Usuario</th>
                        <th style="padding: 8px; text-align: center;">Acción</th>
                        <th style="padding: 8px; text-align: center;">Perfil</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="usr" items="${requestScope.todosLosUsuarios}">
                        <c:if test="${usr.nickname != sessionScope.usuarioLogueado.nickname}">
                            <tr class="fila-usuario-social" data-nickname="${fn:escapeXml(usr.nickname.toLowerCase())}" style="border-bottom: 1px solid #eee;">
                                <td style="padding: 8px;">
                                    <strong>${usr.nickname}</strong>
                                    <br>
                                    <small style="color: #666;">${usr.nombre} ${usr.apellido}</small>
                                </td>
                                
                                <td style="padding: 8px; text-align: center;">
                                    <!-- IMPORTANTE: El botón ahora tiene un ID dinámico (btn-social-...) -->
                                    <c:choose>
                                        <c:when test="${requestScope.misSeguidos.contains(usr.nickname)}">
                                            <button type="button" id="btn-social-${usr.nickname}"
                                                    data-nickname="${fn:escapeXml(usr.nickname)}" onclick="toggleSeguirUsuario(this, this.dataset.nickname, 'dejarSeguir')"
                                                    style="background-color: #d9534f; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px; transition: 0.2s;">
                                                Dejar de seguir
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" id="btn-social-${usr.nickname}"
                                                    data-nickname="${fn:escapeXml(usr.nickname)}" onclick="toggleSeguirUsuario(this, this.dataset.nickname, 'seguir')"
                                                    style="background-color: #0275d8; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px; transition: 0.2s;">
                                                Seguir
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td style="padding: 8px; text-align: center;">
                                    <button type="button" 
                                            data-nickname="${fn:escapeXml(usr.nickname)}" onclick="verPerfilUsuario(this.dataset.nickname)"
                                            style="background-color: #5bc0de; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                        Ver perfil
                                    </button>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- PANEL DERECHO: MIS LISTAS Y DETALLE DEL PERFIL -->
    <div style="flex: 2; display: flex; flex-direction: column; gap: 20px;">
        
        <!-- CAJAS SUPERIORES: SEGUIDORES Y SEGUIDOS (Siempre Visibles) -->
        <div style="display: flex; gap: 15px;">
            <!-- Caja Seguidores -->
            <div style="flex: 1; background: #fff; padding: 15px; border: 1px solid #ddd; border-radius: 5px;">
                <h4 style="margin-top: 0; color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 5px;">Mis Seguidores</h4>
                <ul id="lista-mis-seguidores-social" style="list-style: none; padding: 0; margin: 0; max-height: 150px; overflow-y: auto; font-size: 13px;">
                    <c:forEach var="seguidor" items="${requestScope.misSeguidores}">
                         <li data-nickname="${fn:escapeXml(seguidor)}" onclick="abrirPerfilDesdeRedSocial(this.dataset.nickname)" title="Ver perfil" style="padding: 6px 0; border-bottom: 1px solid #eee; cursor: pointer; color: #2980b9;">👤 <strong>${seguidor}</strong></li>
                    </c:forEach>
                    <c:if test="${empty requestScope.misSeguidores}">
                         <li style="color: #999; font-style: italic;">Aún no tienes seguidores.</li>
                    </c:if>
                </ul>
            </div>

            <!-- Caja Seguidos -->
            <div style="flex: 1; background: #fff; padding: 15px; border: 1px solid #ddd; border-radius: 5px;">
                <h4 style="margin: 0; color: #2c3e50; border-bottom: 2px solid #2ecc71; padding-bottom: 5px; display: flex; justify-content: space-between;">
                    Mis Seguidos
                </h4>
                <ul id="lista-mis-seguidos-social" style="list-style: none; padding: 0; margin: 0; max-height: 150px; overflow-y: auto; font-size: 13px;">
                    <c:forEach var="seguido" items="${requestScope.misSeguidos}">
                         <li id="social-seguido-${seguido}" style="padding: 6px 0; border-bottom: 1px solid #eee; display: flex; justify-content: space-between;">
                             <span data-nickname="${fn:escapeXml(seguido)}" onclick="abrirPerfilDesdeRedSocial(this.dataset.nickname)" title="Ver perfil" style="cursor: pointer; color: #2980b9;">👤 <strong>${seguido}</strong></span>
                             <!-- Botoncito "X" para dejar de seguir desde la lista -->
                             <span data-nickname="${fn:escapeXml(seguido)}" onclick="toggleSeguirUsuario(document.getElementById('btn-social-' + this.dataset.nickname), this.dataset.nickname, 'dejarSeguir')" style="color: #e74c3c; cursor: pointer; font-weight: bold;" title="Dejar de seguir">X</span>
                         </li>
                    </c:forEach>
                    <c:if test="${empty requestScope.misSeguidos}">
                         <li id="social-sin-seguidos" style="color: #999; font-style: italic;">No sigues a nadie.</li>
                    </c:if>
                </ul>
            </div>
        </div>

        <!-- CONTENEDOR PARA EL PERFIL (Se carga al dar click a "Ver Perfil") -->
        <div id="detalle-perfil-usuario" style="background: #fff; padding: 20px; border: 1px solid #ddd; border-radius: 5px; min-height: 250px;">
            <p style="color: #777; font-style: italic; text-align: center; margin-top: 50px;">
                Seleccione un usuario de la lista y haga clic en "Ver perfil" para ver su información aquí abajo.
            </p>
        </div>

    </div>
</div>