# edEXT en el laboratorio (3 PCs Linux)

Un script para cada PC. Se corren desde una terminal del escritorio, **sin `sudo`**: lo piden solo cuando hace falta.

| PC | Comando | Qué hace |
|---|---|---|
| **1. Servidor Central** | `./laboratorio/lab-central.sh` | Revisa Java 21 y MySQL. Si hace falta, corre `install.sh`. Arranca el Central (`./edEXT start`), comparte `serverWeb.tar.gz` y muestra el comando para la PC 2 |
| **2. Servidor Web** | `wget -q http://<IP PC 1>:8000/lab-web.sh && bash lab-web.sh <IP PC 1> 8081` | Escribe `~/.edEXT/Web.cfg` con la IP del Central. Hace el `deploy` (Tomcat + `web.war`) y prueba el login de punta a punta |
| **3. Profesor** | `bash lab-cliente.sh <IP PC 2>` (opcional) | Prueba el sitio, la redirección del celular y el login, y abre el navegador en `http://<IP PC 2>:8080/web/` |

## Orden

1. **PC 1:** clonar el repositorio **en Linux** y correr `./laboratorio/lab-central.sh`.
   - Si la copian desde Windows, los scripts pueden quedar con fin de línea CRLF y fallar.
   - Si la base está vacía: en la Estación de Trabajo, menú **Datos de prueba → Cargar datos**.
   - **No cerrar esa ventana**: al cerrarla se apaga el Central.
2. **PC 2:** el comando que muestra `lab-central.sh`, que ya trae la IP y el puerto.
3. **PC 3:** abrir `http://<IP PC 2>:8080/web/`. Desde un celular, la misma dirección abre la app móvil.

## Si algo falla

- **"Usuario o contraseña incorrectos" con datos correctos:** el Web no llega al Central.
  - Revisar `~/.edEXT/Web.cfg` en la PC 2 (`ip : <IP PC 1>`, `puerto : 8081`). Se puede editar en caliente.
  - Revisar que el Central esté arrancado.
- **Puertos que usa cada PC:** el Central usa el **8081** (y el **8000** para compartir los archivos) y el Web el **8080**. Si `ufw` está activo, los scripts abren esos puertos.
- **Si el AppImage no arranca** (falta FUSE), `lab-central.sh` arranca el mismo jar con `java -jar ../jar/servidor.jar` desde `~/edEXT/bin`.
- **Logs:**
  - Central: `~/edEXT/logs/` en la PC 1.
  - Web: `~/edEXT/logs/Web-<fecha>.log` en la PC 2.
  - Tomcat: `~/edEXT/tools/apache-tomcat-*/logs/`.
