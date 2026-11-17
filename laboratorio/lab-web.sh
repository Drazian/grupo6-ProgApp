#!/bin/bash
# =============================================================================
#  edEXT - PC del SERVIDOR WEB (laboratorio, Linux)
#
#  Uso:  ./lab-web.sh <IP de la PC del Central> [puerto del Central, por defecto 8081]
#        (lab-central.sh muestra el comando exacto, con la IP y el puerto)
#
#  Escribe ~/.edEXT/Web.cfg con la IP del Central (sin eso el login dice
#  "Usuario o contraseña incorrectos"), despliega el Web con el deploy de
#  serverWeb.tar.gz si todavia no esta, abre el puerto 8080 en el firewall y
#  prueba de punta a punta (Web -> Central -> base). No correr con sudo.
# =============================================================================
IPC="$1"; PC="${2:-8081}"
if [ -z "$IPC" ]; then echo "Uso: $0 <IP de la PC del Servidor Central> [puerto del Central, por defecto 8081]"; exit 1; fi
PRJ="$HOME/edEXT"
CFG="$HOME/.edEXT"
WEB=8080
DIR_SCRIPT="$(cd "$(dirname "$0")" && pwd)"

ok()     { echo -e "  \e[32m✔\e[0m $*"; }
mal()    { echo -e "  \e[31m✘\e[0m $*"; }
aviso()  { echo -e "  \e[33m!\e[0m $*"; }
titulo() { echo; echo -e "\e[1m== $*\e[0m"; }
codigo() { curl -s -o /dev/null -m 10 -w '%{http_code}' "$@"; }
web_ok() { [ "$(codigo "http://127.0.0.1:$WEB/web/")" = "200" ]; }
esperar_web() { for i in $(seq 1 "${1:-60}"); do web_ok && return 0; sleep 1; done; return 1; }
abrir_puerto() {
    command -v ufw > /dev/null || return 0
    if sudo ufw status 2>/dev/null | grep -qiE "Status: active|Estado: activo"; then
        sudo ufw allow "$1/tcp" > /dev/null && ok "firewall: puerto $1 abierto" || aviso "no pude abrir el puerto $1 en el firewall (sudo ufw allow $1/tcp)"
    fi
}
java_home() {
    [ -n "$JAVA_HOME" ] && return
    if [ -d /usr/lib/jvm/java-21-openjdk-amd64 ]; then export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
    elif command -v java > /dev/null; then export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"; fi
}

[ "$EUID" -eq 0 ] && aviso "Estás corriendo como root: Web.cfg queda en /root/.edEXT. Mejor correrlo sin sudo (con el mismo usuario que arranca Tomcat)."
if ! command -v curl > /dev/null; then   # Ubuntu de escritorio no lo trae (el deploy tambien lo instala)
    aviso "Instalo curl (puede pedir la contraseña de sudo)"
    sudo apt-get install -y curl > /dev/null 2>&1 || { sudo apt-get update > /dev/null 2>&1; sudo apt-get install -y curl > /dev/null; } \
        || { mal "No se pudo instalar curl: sudo apt-get update && sudo apt-get install -y curl"; exit 1; }
fi

# ----------------------------------------------------------------------------- Central
titulo "Conexión con el Servidor Central ($IPC:$PC)"
if curl -s -m 8 "http://$IPC:$PC/servidor_central?wsdl" | grep -q "definitions"; then
    CENTRAL=1; ok "desde esta PC se llega al Web Service del Central"
else
    CENTRAL=0; mal "No se llega a http://$IPC:$PC/servidor_central"
    echo "     - ¿El Central está arrancado? (en su PC: ./laboratorio/lab-central.sh)"
    echo "     - ¿La IP y el puerto son los que mostró lab-central.sh?"
    echo "     - ¿Firewall en la PC del Central? (sudo ufw allow $PC/tcp)"
    echo "     Sigo con la instalación del Web; al final se vuelve a probar."
fi

# ----------------------------------------------------------------------------- Web.cfg
titulo "Configuración del Web (~/.edEXT/Web.cfg)"
mkdir -p "$CFG"
[ -f "$CFG/Web.cfg" ] && cp -f "$CFG/Web.cfg" "$CFG/Web.cfg.anterior"
printf '#IP del Web Service\nip : %s\n\n#Puerto del Web Service\npuerto : %s\n\n' "$IPC" "$PC" > "$CFG/Web.cfg"
ok "ip : $IPC   puerto : $PC   (el Web lo relee en cada pedido: no hace falta reiniciar)"

# ----------------------------------------------------------------------------- Tomcat + web.war
titulo "Servidor Web (Tomcat en el puerto $WEB)"
TOMCAT=$(ls -d "$PRJ"/tools/apache-tomcat-* 2>/dev/null | head -1)
if web_ok; then ok "el Web ya está desplegado y andando"
elif [ -n "$TOMCAT" ]; then
    # ya se habia hecho el deploy: arrancar Tomcat (desde ~/edEXT/shared, asi el log del Web queda en ~/edEXT/logs)
    java_home; mkdir -p "$PRJ/shared"
    if ! pgrep -f "org.apache.catalina.startup.Bootstrap" > /dev/null; then (cd "$PRJ/shared" && "$TOMCAT/bin/startup.sh" > /dev/null); fi
    if ! esperar_web 60 && [ -f "$PRJ/web.war" ]; then
        for i in $(seq 1 30); do [ "$(codigo "http://127.0.0.1:$WEB/")" != "000" ] && break; sleep 1; done
        curl -s -u admin:admin "http://127.0.0.1:$WEB/manager/text/deploy?path=/web&update=true" --upload-file "$PRJ/web.war"; echo
        esperar_web 30
    fi
    web_ok && ok "Tomcat arrancado con el Web desplegado" || mal "Tomcat no responde en /web/ (ver $TOMCAT/logs/catalina.out)"
else
    # primera vez: serverWeb.tar.gz (de la carpeta actual, la del script, Descargas... o del Central si lo comparte)
    TAR=""
    for d in "$PWD" "$DIR_SCRIPT" "$HOME" "$HOME/Descargas" "$HOME/Downloads" "$HOME/Escritorio" "$HOME/Desktop" "$PRJ"; do
        [ -f "$d/serverWeb.tar.gz" ] && { TAR="$d/serverWeb.tar.gz"; break; }
    done
    mkdir -p "$PRJ"
    if [ -z "$TAR" ]; then
        echo "     No encontré serverWeb.tar.gz en esta PC: lo pido al Central (http://$IPC:8000/)..."
        if curl -s -f -m 120 -o "$PRJ/serverWeb.tar.gz" "http://$IPC:8000/serverWeb.tar.gz"; then TAR="$PRJ/serverWeb.tar.gz"; ok "serverWeb.tar.gz descargado del Central"
        else rm -f "$PRJ/serverWeb.tar.gz"; mal "No hay serverWeb.tar.gz: copiarlo de ~/edEXT/shared/ de la PC del Central a esta carpeta y volver a correr el script"; exit 1; fi
    else ok "uso $TAR"; fi
    # el deploy (de install.sh) toma los archivos de la carpeta actual: se descomprime en ~/edEXT (queda ~/edEXT/shared)
    tar -xzf "$TAR" -C "$PRJ" || { mal "No se pudo descomprimir $TAR"; exit 1; }
    echo "     Corriendo deploy (instala Java 21 con sudo apt, arranca Tomcat y despliega web.war)..."
    (cd "$PRJ/shared" && bash ./deploy) 2>&1 | grep -vE "^(Get|Hit|Ign):|^Reading|^Building|^WARNING: apt|%|Dload|--:--" | tail -6 | sed 's/^/     /'
    esperar_web 60 && ok "Web desplegado" || mal "El Web no responde en http://127.0.0.1:$WEB/web/ (ver ~/edEXT/tools/apache-tomcat-*/logs/catalina.out)"
fi
abrir_puerto "$WEB"

# ----------------------------------------------------------------------------- Prueba de punta a punta
titulo "Prueba de punta a punta"
[ "$CENTRAL" = 0 ] && curl -s -m 8 "http://$IPC:$PC/servidor_central?wsdl" | grep -q "definitions" && CENTRAL=1
web_ok && ok "http://127.0.0.1:$WEB/web/ responde" || mal "http://127.0.0.1:$WEB/web/ no responde"
CJ=$(mktemp); curl -s -c "$CJ" -o /dev/null -m 10 "http://127.0.0.1:$WEB/web/"
LOGIN=$(curl -s -b "$CJ" -m 20 -X POST -d 'accion=login&identificador=chechi&password=1234' "http://127.0.0.1:$WEB/web/LoginServlet"); rm -f "$CJ"
if [ "$LOGIN" = "OK" ]; then ok "login de chechi: el Web habla con el Central y la base tiene los datos de prueba"
elif [ "$CENTRAL" = 1 ]; then aviso "El Central responde, pero chechi no inicia sesión: ¿se cargaron los datos de prueba? (Estación de Trabajo: \"Datos de prueba\" → \"Cargar datos\")"
else mal "El Web no llega al Central ($IPC:$PC): revisar la IP, que el Central esté arrancado y el firewall de esa PC. Después volver a correr este script."; fi
UA='Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1'
[[ "$(curl -s -o /dev/null -m 10 -A "$UA" -w '%{http_code} %{redirect_url}' "http://127.0.0.1:$WEB/web/")" == "302 "*"/web/movil/" ]] && ok "un celular que entra a /web/ va a /web/movil/" || aviso "la redirección de celulares a /web/movil/ no respondió como se esperaba"

IP=$(hostname -I 2>/dev/null | tr ' ' '\n' | grep -E '^[0-9]+\.' | grep -vE '^(127\.|172\.17\.)' | head -1)
echo
echo -e "  El profesor entra desde su PC (o celular) a:  \e[1mhttp://${IP:-<IP de esta PC>}:$WEB/web/\e[0m"
echo "  Para probar desde la PC del profesor:  bash lab-cliente.sh ${IP:-<IP de esta PC>}"
[ -f "$DIR_SCRIPT/lab-cliente.sh" ] || echo "  (lab-cliente.sh se baja del Central:  wget -q http://$IPC:8000/lab-cliente.sh)"
echo
