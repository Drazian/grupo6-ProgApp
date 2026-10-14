<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="display: flex; gap: 20px; align-items: flex-start; margin-bottom: 20px;">
    <!-- Imagen de perfil -->
    <img src="ImagenServlet?nombre=${empty requestScope.usuarioPerfil.imagen ? 'usr.png' : requestScope.usuarioPerfil.imagen}" 
         alt="Foto Perfil" 
         style="width: 85px; height: 85px; border-radius: 50%; object-fit: cover; border: 2px solid #ccc;">

    <div style="flex: 1;">
        <h2 style="margin: 0; font-size: 22px;">${requestScope.usuarioPerfil.nombre} ${requestScope.usuarioPerfil.apellido}</h2>
        <p style="margin: 3px 0; color: #555;">${requestScope.usuarioPerfil.nickname} / ${requestScope.usuarioPerfil.email}</p>
        <span style="font-size: 12px; background: #eee; padding: 2px 6px; border-radius: 3px; border: 1px solid #ddd;">
            Rol: ${requestScope.usuarioPerfil.tipoUsuario}
        </span>
    </div>

    <!-- Botón Seguir / Dejar de seguir -->
    <div>
        <c:choose>
            <c:when test="${requestScope.loSigo}">
                <button type="button" onclick="dejarDeSeguirRapido('${requestScope.usuarioPerfil.nickname}')" 
                        style="background-color: #d9534f; color: white; border: none; padding: 8px 14px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                    Dejar de seguir
                </button>
            </c:when>
            <c:otherwise>
                <button type="button" onclick="seguirRapido('${requestScope.usuarioPerfil.nickname}')" 
                        style="background-color: #0275d8; color: white; border: none; padding: 8px 14px; border-radius: 4px; cursor: pointer; font-weight: bold;">
                    Seguir
                </button>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<hr style="opacity: 0.3; margin: 15px 0;">

<div style="display: flex; gap: 20px;">
    <!-- Información General del Perfil -->
    <div style="flex: 2;">
        <h3 style="border-bottom: 2px solid #0275d8; padding-bottom: 5px; margin-top: 0;">Información General</h3>
        <p><b>Nickname:</b> ${requestScope.usuarioPerfil.nickname}</p>
        <p><b>Nombre:</b> ${requestScope.usuarioPerfil.nombre}</p>
        <p><b>Apellido:</b> ${requestScope.usuarioPerfil.apellido}</p>
        <p><b>Correo electrónico:</b> ${requestScope.usuarioPerfil.email}</p>
        <p><b>Fecha de Nacimiento:</b> ${requestScope.usuarioPerfil.fNacimiento}</p>
    </div>

    <!-- Paneles de Red Social al costado (Seguidores y Seguidos) -->
    <div style="flex: 1; display: flex; flex-direction: column; gap: 15px;">
        
        <!-- Caja 1: Seguidores -->
        <div style="background: #fcfcfc; border: 1px solid #e0e0e0; padding: 10px; border-radius: 4px;">
            <h4 style="margin-top: 0; border-bottom: 1px solid #ddd; padding-bottom: 5px; color: #333;">
                Seguidores (${requestScope.seguidoresPerfil.size()})
            </h4>
            <ul style="list-style: none; padding: 0; margin: 0; max-height: 120px; overflow-y: auto;">
                <c:forEach var="seguidor" items="${requestScope.seguidoresPerfil}">
                    <li style="padding: 4px 0; border-bottom: 1px dashed #eee; font-size: 13px;">
                        👤 <b>${seguidor}</b>
                    </li>
                </c:forEach>
                <c:if test="${empty requestScope.seguidoresPerfil}">
                    <li style="font-size: 12px; color: #888;">Sin seguidores aún</li>
                </c:if>
            </ul>
        </div>

        <!-- Caja 2: Seguidos -->
        <div style="background: #fcfcfc; border: 1px solid #e0e0e0; padding: 10px; border-radius: 4px;">
            <h4 style="margin-top: 0; border-bottom: 1px solid #ddd; padding-bottom: 5px; color: #333;">
                Seguidos (${requestScope.seguidosPerfil.size()})
            </h4>
            <ul style="list-style: none; padding: 0; margin: 0; max-height: 120px; overflow-y: auto;">
                <c:forEach var="seguido" items="${requestScope.seguidosPerfil}">
                    <li style="padding: 4px 0; border-bottom: 1px dashed #eee; font-size: 13px;">
                        ➡️ <b>${seguido}</b>
                    </li>
                </c:forEach>
                <c:if test="${empty requestScope.seguidosPerfil}">
                    <li style="font-size: 12px; color: #888;">No sigue a ningún usuario</li>
                </c:if>
            </ul>
        </div>

    </div>
</div>