#!/bin/bash
# =============================================================================
#  edEXT - PC del SERVIDOR CENTRAL (laboratorio, Linux)
#
#  Uso, desde una terminal del escritorio y en la carpeta del repositorio:
#     ./laboratorio/lab-central.sh              arranca el Central (si no esta instalado, corre install.sh)
#     ./laboratorio/lab-central.sh --instalar   vuelve a correr install.sh (BORRA ~/edEXT y lo crea de nuevo)
#
#  Revisa Java y MySQL, instala si hace falta, abre los puertos en el firewall,
#  arranca el Central (./edEXT start), comparte serverWeb.tar.gz y lab-web.sh
#  para la PC del Servidor Web y muestra el comando que hay que escribir alla.
#  No correr con sudo: lo pide solo cuando hace falta.
# =============================================================================
REPO="$(cd "$(dirname "$0")/.." && pwd)"
PRJ="$HOME/edEXT"
CFG="$HOME/.edEXT"
PUERTO_COMPARTIR=8000

ok()     { echo -e "  \e[32m✔\e[0m $*"; }
mal()    { echo -e "  \e[31m✘\e[0m $*"; }
aviso()  { echo -e "  \e[33m!\e[0m $*"; }
titulo() { echo; echo -e "\e[1m== $*\e[0m"; }
# valor <clave (regex)> <archivo>: lee "clave : valor" (edEXT.cfg) o "clave=valor" (database.properties)
valor()  { grep -E "^[[:space:]]*$1[[:space:]]*[:=]" "$2" 2>/dev/null | head -1 | sed -E "s/^[[:space:]]*$1[[:space:]]*[:=][[:space:]]*//" | tr -d '\r'; }
escuchando()   { ss -ltn 2>/dev/null | grep -qE "[:.]$1[[:space:]]"; }
central_vivo() { pgrep -f "java .*servidor\.jar" > /dev/null; }
abrir_puerto() {
    command -v ufw > /dev/null || return 0
    if sudo ufw status 2>/dev/null | grep -qiE "Status: active|Estado: activo"; then
        sudo ufw allow "$1/tcp" > /dev/null && ok "firewall: puerto $1 abierto" || aviso "no pude abrir el puerto $1 en el firewall (sudo ufw allow $1/tcp)"
    fi
}

[ "$EUID" -eq 0 ] && aviso "Estás corriendo como root: el Central queda instalado en /root. Mejor correrlo sin sudo."

# ----------------------------------------------------------------------------- Java
titulo "Java"
if ! command -v java > /dev/null; then mal "No está Java. Instalar con: sudo apt install -y openjdk-21-jdk"; exit 1; fi
JV=$(java -version 2>&1 | head -1 | sed -E 's/.*version "([0-9]+).*/\1/')
if [ "$JV" -ge 21 ] 2>/dev/null; then ok "Java $JV"; else mal "Java $JV: hace falta Java 21 o más (sudo apt install -y openjdk-21-jdk)"; exit 1; fi

# ----------------------------------------------------------------------------- MySQL
titulo "Base de datos"
PROPS="$CFG/database.properties"
if [ -f "$PROPS" ]; then
    URL=$(valor 'db\.url' "$PROPS"); USR=$(valor 'db\.usuario' "$PROPS"); CLAVE_BD=$(valor 'db\.password' "$PROPS"); ORIGEN="~/.edEXT/database.properties"
else  # primera vez: el Central usa lo de persistence.xml (y despues crea database.properties)
    PX="$REPO/Servidor_Central/src/main/resources/META-INF/persistence.xml"
    URL=$(grep -oP 'jdbc.url" value="\K[^"]*' "$PX" | sed 's/&amp;/\&/g'); USR=$(grep -oP 'jdbc.user" value="\K[^"]*' "$PX"); CLAVE_BD=$(grep -oP 'jdbc.password" value="\K[^"]*' "$PX"); ORIGEN="persistence.xml"
fi
HOST_BD=$(echo "$URL" | sed -E 's#^jdbc:[a-z]+://([^:/?]+).*#\1#')
PUERTO_BD=$(echo "$URL" | sed -nE 's#^jdbc:[a-z]+://[^:/?]+:([0-9]+).*#\1#p'); PUERTO_BD=${PUERTO_BD:-3306}
BD=$(echo "$URL" | sed -E 's#^jdbc:[a-z]+://[^/]+/([^?]+).*#\1#')
[ "$HOST_BD" = "localhost" ] && HOST_BD=127.0.0.1
if [ "$HOST_BD" = "127.0.0.1" ] && ! escuchando "$PUERTO_BD"; then
    aviso "MySQL no está andando: lo inicio (puede pedir la contraseña de sudo)"
    sudo systemctl start mysql 2>/dev/null || sudo service mysql start 2>/dev/null || sudo systemctl start mariadb 2>/dev/null
    for i in $(seq 1 15); do escuchando "$PUERTO_BD" && break; sleep 1; done
fi
sql() { MYSQL_PWD="$CLAVE_BD" mysql --protocol=TCP -h "$HOST_BD" -P "$PUERTO_BD" -u "$USR" -N -B "$@"; }
if command -v mysql > /dev/null; then
    if sql -e "SELECT 1" > /dev/null 2>&1; then ok "MySQL en $HOST_BD:$PUERTO_BD, usuario '$USR', base '$BD' (configuración: $ORIGEN)"
    else
        mal "No se puede entrar a MySQL en $HOST_BD:$PUERTO_BD con el usuario '$USR' (configuración: $ORIGEN)"
        echo "     Revisar usuario y contraseña en ~/.edEXT/database.properties (db.usuario / db.password) o crear ese usuario en MySQL."
        exit 1
    fi
else aviso "No está el comando 'mysql': no puedo probar la conexión, sigo igual."; fi

# ----------------------------------------------------------------------------- Instalacion
titulo "Instalación"
# Sin FUSE los AppImage (edEXT, servidor, appimagetool) no montan: se ejecutan extrayendose
if [ ! -e /dev/fuse ] || { ! command -v fusermount3 > /dev/null && ! command -v fusermount > /dev/null; }; then
    export APPIMAGE_EXTRACT_AND_RUN=1
    aviso "Sin FUSE: los AppImage se van a ejecutar extrayéndose (APPIMAGE_EXTRACT_AND_RUN=1)."
fi
if [ "$1" = "--instalar" ] || [ ! -f "$PRJ/jar/servidor.jar" ]; then
    if [ -d "$PRJ" ]; then
        aviso "install.sh BORRA $PRJ (imágenes subidas y logs) y lo vuelve a crear."
        read -r -p "     ¿Seguir? [s/N] " r; [[ "$r" =~ ^[sS]$ ]] || exit 1
        if central_vivo; then pkill -f "java .*servidor\.jar"; pkill -f "edEXT\.jar start"; sleep 2; fi
    fi
    FALTA=""; for c in mvn curl wget; do command -v $c > /dev/null || FALTA="$FALTA $c"; done
    FALTA=${FALTA/mvn/maven}
    if [ -n "$FALTA" ]; then aviso "Instalo lo que falta:$FALTA"; sudo apt-get install -y $FALTA || { sudo apt-get update && sudo apt-get install -y $FALTA; } || { mal "No se pudo instalar:$FALTA"; exit 1; }; fi
    echo "     Corriendo install.sh (compila todo y descarga Tomcat; tarda unos minutos y necesita internet)..."
    (cd "$REPO" && bash ./install.sh) 2>&1 | tee "$HOME/edEXT-install.log"
    if [ -f "$PRJ/jar/servidor.jar" ] && [ -f "$PRJ/shared/serverWeb.tar.gz" ]; then ok "install.sh terminó (log: ~/edEXT-install.log)"
    else mal "install.sh no generó ~/edEXT/jar/servidor.jar o ~/edEXT/shared/serverWeb.tar.gz: ver ~/edEXT-install.log"; exit 1; fi
else ok "ya instalado en $PRJ (para reinstalar: --instalar)"; fi

# ----------------------------------------------------------------------------- Central
titulo "Servidor Central"
if central_vivo; then ok "el Central ya estaba corriendo"
else
    [ -z "$DISPLAY" ] && aviso "No hay pantalla (DISPLAY vacío): la Estación de Trabajo necesita el escritorio. Correr el script desde una terminal del escritorio."
    mkdir -p "$PRJ/logs" "$PRJ/bin"
    if [ -x "$PRJ/edEXT" ]; then (cd "$PRJ" && nohup ./edEXT start > "$PRJ/logs/lab-central.out" 2>&1 &); fi
    for i in $(seq 1 20); do central_vivo && break; sleep 1; done
    if ! central_vivo; then   # el AppImage no arranco: el mismo jar, desde la misma carpeta (bin)
        aviso "./edEXT start no levantó el servidor: lo arranco con java -jar ../jar/servidor.jar desde ~/edEXT/bin"
        (cd "$PRJ/bin" && nohup java -jar ../jar/servidor.jar > "$PRJ/logs/lab-central.out" 2>&1 &)
    fi
fi
PUERTO=8081
for i in $(seq 1 120); do
    P=$(valor puerto "$CFG/edEXT.cfg"); PUERTO=${P:-8081}
    escuchando "$PUERTO" && break
    [ "$i" -gt 5 ] && ! central_vivo && break
    sleep 1
done
if escuchando "$PUERTO" && curl -s -m 5 "http://127.0.0.1:$PUERTO/servidor_central?wsdl" | grep -q "definitions"; then
    ok "Web Service publicado en el puerto $PUERTO (http://<esta PC>:$PUERTO/servidor_central)"
else
    mal "El Central no publicó el Web Service. Últimas líneas:"
    tail -5 "$PRJ/logs/lab-central.out" 2>/dev/null | sed 's/^/     /'
    ULT=$(ls -t "$PRJ"/logs/Central-*.log 2>/dev/null | head -1); [ -n "$ULT" ] && grep -iE "error|exception" "$ULT" | tail -3 | cut -c1-200 | sed 's/^/     /'
    exit 1
fi
abrir_puerto "$PUERTO"
aviso "No cierren la ventana de la Estación de Trabajo: al cerrarla se apaga el Central."
if command -v mysql > /dev/null; then
    N=$(sql -e "SELECT COUNT(*) FROM \`$BD\`.Usuario" 2>/dev/null)
    if [ -n "$N" ] && [ "$N" -gt 0 ] 2>/dev/null; then ok "la base '$BD' tiene datos ($N usuarios)"
    else aviso "La base está vacía: en la Estación de Trabajo, menú \"Datos de prueba\" → \"Cargar datos\" → Sí."; fi
fi

# ----------------------------------------------------------------------------- Compartir
titulo "Para la PC del Servidor Web"
IPS=$(hostname -I 2>/dev/null | tr ' ' '\n' | grep -E '^[0-9]+\.' | grep -vE '^(127\.|172\.17\.)')
IP=$(echo "$IPS" | head -1); IP=${IP:-<IP de esta PC>}
COMPARTIDO=0
if [ -f "$PRJ/shared/serverWeb.tar.gz" ]; then
    cp -f "$REPO/laboratorio/lab-web.sh" "$REPO/laboratorio/lab-cliente.sh" "$PRJ/shared/" 2>/dev/null
    if command -v python3 > /dev/null; then
        if ! curl -s -m 3 -o /dev/null -f "http://127.0.0.1:$PUERTO_COMPARTIR/lab-web.sh"; then
            (cd "$PRJ/shared" && nohup python3 -m http.server "$PUERTO_COMPARTIR" > /dev/null 2>&1 &)
            sleep 1
        fi
        if curl -s -m 3 -o /dev/null -f "http://127.0.0.1:$PUERTO_COMPARTIR/serverWeb.tar.gz"; then
            COMPARTIDO=1; abrir_puerto "$PUERTO_COMPARTIR"
            ok "compartiendo ~/edEXT/shared en http://$IP:$PUERTO_COMPARTIR/ (serverWeb.tar.gz y lab-web.sh)"
        fi
    fi
fi
[ "$(echo "$IPS" | wc -l)" -gt 1 ] && aviso "Esta PC tiene varias IP: $(echo $IPS). Usar la de la red del laboratorio."
echo
echo "  En la PC del SERVIDOR WEB, abrir una terminal y escribir:"
if [ "$COMPARTIDO" = 1 ]; then
    echo -e "     \e[1mwget -q http://$IP:$PUERTO_COMPARTIR/lab-web.sh && bash lab-web.sh $IP $PUERTO\e[0m"
    echo "  (o, si en esa PC está el repositorio clonado:  ./laboratorio/lab-web.sh $IP $PUERTO)"
else
    echo "     1. Copiar ~/edEXT/shared/serverWeb.tar.gz a esa PC (pendrive o carpeta compartida)"
    echo -e "     2. \e[1m./laboratorio/lab-web.sh $IP $PUERTO\e[0m   (desde la carpeta donde quedó serverWeb.tar.gz o el repositorio)"
fi
echo
