<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="max-width: 950px; margin: 0 auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
    
    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 0;">
        Información de Usuario
    </h2>

    <div style="margin-bottom: 25px;">
        <label style="font-weight: bold; color: #34495e; display: block; margin-bottom: 8px;">
            Seleccione un usuario para consultar su información:
        </label>
        
        <div style="max-height: 480px; overflow-y: auto; border: 1px solid #ddd; border-radius: 5px;">
            <table class="table" style="width: 100%; border-collapse: collapse; margin: 0; background: #fff;">
                <thead style="position: sticky; top: 0; background: #f2f2f2; z-index: 1;">
                    <tr style="border-bottom: 2px solid #ccc; text-align: left;">
                        <th style="padding: 10px;">Nickname</th>
                        <th style="padding: 10px;">Nombre y Apellido</th>
                        <th style="padding: 10px;">Rol</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="usr" items="${requestScope.todosLosUsuarios}">
                        <tr style="border-bottom: 1px solid #eee;">
                            <td style="padding: 10px;">
                                <a href="#" onclick="event.preventDefault(); cargarDetalleConsultaUsuario('${usr.nickname}')" 
                                   style="color: #2980b9; font-weight: bold; text-decoration: none;">
                                    👤 ${usr.nickname}
                                </a>
                            </td>
                            <td style="padding: 10px;">${usr.nombre} ${usr.apellido}</td>
                            <td style="padding: 10px;">
                                <span style="font-size: 12px; background: #eef2f5; padding: 3px 8px; border-radius: 3px; border: 1px solid #dcdfe3;">
                                    ${usr.tipoUsuario}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty requestScope.todosLosUsuarios}">
                        <tr>
                            <td colspan="3" style="text-align: center; color: #7f8c8d; padding: 15px;">
                                No hay usuarios registrados en el sistema.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Informació-->
    <div id="contenedor-detalle-consulta-usuario" style="background: #fafafa; padding: 20px; border: 1px solid #e0e0e0; border-radius: 6px; min-height: 200px;">
        <p style="color: #7f8c8d; font-style: italic; text-align: center; margin-top: 60px;">
            Haga clic en el nickname de un usuario para desplegar su información completa.
        </p>
    </div>

</div>