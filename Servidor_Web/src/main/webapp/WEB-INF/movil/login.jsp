<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Dispositivo Móvil - CU Inicio de Sesión (Figura 2: ejemplo Signin de Bootstrap) --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>edEXT Móvil - Iniciar sesión</title>
    <link rel="icon" type="image/svg+xml" href="${ctx}/movil/static/img/logo-edext.svg?v=${applicationScope.versionRecursosMovil}">
    <link rel="stylesheet" href="${ctx}/movil/static/css/bootstrap.min.css?v=${applicationScope.versionRecursosMovil}">
    <link rel="stylesheet" href="${ctx}/movil/static/css/movil.css?v=${applicationScope.versionRecursosMovil}">
</head>
<body class="movil-login">
    <main class="form-signin">
        <form method="post" action="${ctx}/movil/login" novalidate id="formLogin">
            <h2>Iniciar sesión</h2>

            <c:if test="${not empty errorLogin}">
                <div class="alert alert-danger py-2 small" role="alert"><c:out value="${errorLogin}"/></div>
            </c:if>
            <div id="errorCliente" class="alert alert-warning py-2 small d-none" role="alert"></div>

            <label for="identificador" class="visually-hidden">Nickname o dirección de correo</label>
            <input type="text" id="identificador" name="identificador" class="form-control" placeholder="Nickname o dirección de correo"
                   value="<c:out value='${identificador}'/>" required autofocus autocomplete="username" autocapitalize="none">

            <label for="password" class="visually-hidden">Contraseña</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="Contraseña"
                   required autocomplete="current-password">

            <div class="form-check mb-3 small">
                <input class="form-check-input" type="checkbox" value="si" id="recordarme" name="recordarme">
                <label class="form-check-label" for="recordarme">Recordarme</label>
            </div>

            <button class="btn btn-primary btn-login w-100" type="submit">Login</button>
        </form>
    </main>

    <script>
        // Validación en el navegador (7.5): los dos campos son obligatorios
        document.getElementById('formLogin').addEventListener('submit', function (e) {
            const id = document.getElementById('identificador');
            const pass = document.getElementById('password');
            const aviso = document.getElementById('errorCliente');
            const faltan = [];
            if (!id.value.trim()) faltan.push('el nickname o correo');
            if (!pass.value) faltan.push('la contraseña');
            if (faltan.length) {
                e.preventDefault();
                aviso.textContent = 'Ingrese ' + faltan.join(' y ') + '.';
                aviso.classList.remove('d-none');
                (id.value.trim() ? pass : id).focus();
            }
        });
    </script>
</body>
</html>
