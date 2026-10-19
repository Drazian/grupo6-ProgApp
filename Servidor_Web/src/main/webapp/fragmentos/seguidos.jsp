<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                            <tr class="fila-usuario-social" data-nickname="${usr.nickname.toLowerCase()}" style="border-bottom: 1px solid #eee;">
                                <td style="padding: 8px;">
                                    <strong>${usr.nickname}</strong>
                                    <br>
                                    <small style="color: #666;">${usr.nombre} ${usr.apellido}</small>
                                </td>
                                
                                <td style="padding: 8px; text-align: center;">
                                    <c:choose>
                                        <c:when test="${requestScope.misSeguidos.contains(usr.nickname)}">
                                            <button type="button" 
                                                    onclick="dejarDeSeguirRapido('${usr.nickname}')"
                                                    style="background-color: #d9534f; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                                Dejar de seguir
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" 
                                                    onclick="seguirRapido('${usr.nickname}')"
                                                    style="background-color: #0275d8; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">
                                                Seguir
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td style="padding: 8px; text-align: center;">
                                    <button type="button" 
                                            onclick="verPerfilUsuario('${usr.nickname}')"
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

    <!-- PANEL DERECHO: Detalle del Perfil -->
    <div id="detalle-perfil-usuario" style="flex: 2; background: #fff; padding: 20px; border: 1px solid #ddd; border-radius: 5px; min-height: 350px;">
        <p style="color: #777; font-style: italic; text-align: center; margin-top: 50px;">
            Seleccione un usuario de la lista y haga clic en "Ver perfil" para ver su información.
        </p>
    </div>

</div>