#!/bin/bash
# =============================================================================
#  edEXT - PC del PROFESOR / CLIENTE (laboratorio)
#
#  Uso:  bash lab-cliente.sh <IP de la PC del Servidor Web> [puerto, por defecto 8080]
#
#  Prueba desde esta PC que el sitio responde, que un celular va a la app movil
#  y que se puede iniciar sesion; despues abre el sitio en el navegador.
# =============================================================================
IPW="$1"; PW="${2:-8080}"
if [ -z "$IPW" ]; then echo "Uso: $0 <IP de la PC del Servidor Web> [puerto, por defecto 8080]"; exit 1; fi
B="http://$IPW:$PW/web"

ok()    { echo -e "  \e[32m✔\e[0m $*"; }
mal()   { echo -e "  \e[31m✘\e[0m $*"; }
aviso() { echo -e "  \e[33m!\e[0m $*"; }

if ! command -v curl > /dev/null; then
    aviso "No está curl (sudo apt-get install -y curl): no puedo probar, abro el sitio en el navegador."
    command -v xdg-open > /dev/null && xdg-open "$B/" > /dev/null 2>&1 &
    echo "  Sitio: $B/"; exit 0
fi
echo; echo -e "\e[1m== Prueba desde esta PC contra $B/\e[0m"
C=$(curl -s -o /dev/null -m 10 -w '%{http_code}' "$B/")
if [ "$C" = "200" ]; then ok "el sitio responde"
else
    mal "No llego a $B/ (respuesta: $C)"
    echo "     - ¿La IP es la de la PC del Servidor Web? ¿Están en la misma red?"
    echo "     - En la PC del Web: ./lab-web.sh <IP del Central>  (abre el puerto $PW si hay firewall)"
    exit 1
fi
UA='Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1'
[[ "$(curl -s -o /dev/null -m 10 -A "$UA" -w '%{http_code} %{redirect_url}' "$B/")" == "302 "*"/web/movil/" ]] && ok "desde un celular, $B/ lleva a la app móvil" || aviso "la redirección de celulares no respondió como se esperaba"
CJ=$(mktemp); curl -s -c "$CJ" -o /dev/null -m 10 "$B/"
L=$(curl -s -b "$CJ" -m 20 -X POST -d 'accion=login&identificador=chechi&password=1234' "$B/LoginServlet"); rm -f "$CJ"
if [ "$L" = "OK" ]; then ok "inicio de sesión (chechi): el Web llega al Central y a la base"
else aviso "chechi no pudo iniciar sesión (\"$L\"): revisar Web.cfg en la PC del Web (lab-web.sh) o cargar los datos de prueba en la Estación de Trabajo"; fi

echo; echo -e "  Sitio: \e[1m$B/\e[0m   (desde un celular, la misma dirección abre la app móvil)"; echo
if [ -n "$DISPLAY$WAYLAND_DISPLAY" ] && command -v xdg-open > /dev/null; then xdg-open "$B/" > /dev/null 2>&1 & fi
