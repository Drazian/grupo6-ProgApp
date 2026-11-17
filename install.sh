#!/bin/bash

CFG_PRJ="$HOME/.edEXT"
CFG_TMP="$CFG_PRJ/temp"
CFG_WRK="$CFG_PRJ/output"
CFG_TP="$CFG_PRJ/tmp"

rDIR_PRJ="${1:-edEXT}"
BIN="bin"
RSC="resources"
TLS="tools"
DIR_PRJ="$HOME/$rDIR_PRJ"
DIR_BIN="$DIR_PRJ/$BIN"
DIR_SHD="$DIR_PRJ/shared"
DIR_JAR="$DIR_PRJ/jar"
DIR_LOG="$DIR_PRJ/logs"
DIR_RSC="$DIR_PRJ/$RSC"
rDIR_RSC="$rDIR_PRJ/$RSC"
DIR_IMG="$DIR_RSC/imagen"
DIR_TLS="$DIR_RSC/$TLS"
rDIR_TLS="$rDIR_RSC/$TLS"
DIR_TMP="$DIR_PRJ/temp"

NODEAMONS=("edEXT_cli" "Servidor" "testport")
NODEAMONS=("edEXT_cli" "Servidor" "testport")
NODEA_JAR=("edEXT.jar" "servidor.jar" "testPort.jar")
NODEA_NOM=("edEXT" "servidor" "testPort")

crear(){
    mkdir -p $CFG_TMP
    mkdir -p $DIR_BIN
    mkdir -p $DIR_LOG
    mkdir -p $DIR_RSC
    mkdir -p $DIR_IMG
    mkdir -p $DIR_TLS
    mkdir -p $DIR_TMP
    mkdir -p $DIR_SHD
    mkdir -p $DIR_JAR
}

makeAppDir() {
    mkdir -p "$1/usr/$BIN" \
             "$1/usr/lib/jvm" \
             "$1/usr/share/icons/hicolor/256x256/apps" \
             "$1/miapp/lib"

    cp $CFG_WRK/$2 "$1/usr/$BIN/$2"
    cp miapp.png "$1/"
    cp miapp.png "$1/usr/share/icons/hicolor/256x256/apps/"

    cat << EOF > "$1/miapp.desktop"
[Desktop Entry]
Name=edEXT $1
Exec=AppRun
Icon=miapp
Type=Application
Categories=Utility;
EOF
    cat << EOF > "$1/AppRun"
#!/bin/bash
HERE="\$(dirname "\$(readlink -f "\${0}")")"
JAR_PATH="\$HERE/usr/bin/$2"
exec java -jar "\$JAR_PATH"$3 "\$@"
EOF
    chmod +x "$1/AppRun"
}

makeAppImage(){
    ARCH=x86_64 ./appimagetool.AppImage "$1/" $2
}

checkDir(){
    if [[ ! -d "$CFG_PRJ" ]]; then
        mkdir -p $CFG_PRJ
    fi
    if [[ ! -d "$DIR_PRJ" ]]; then
        mkdir $DIR_PRJ
    fi
    if [[ -d "$CFG_TMP" ]]; then
        rm -rf "$CFG_TMP"
    fi
    if [[ -d "$CFG_WRK" ]]; then
        rm -rf "$CFG_WRK"
    fi
    if [[ -d "$CFG_TP" ]]; then
        rm -rf "$CFG_TP"
    fi

}

copy(){
    cp -r "centralCli" "$CFG_TMP/"
    cp -r "Servidor_Central" "$CFG_TMP/"
    cp -r "Servidor_Web" "$CFG_TMP/"
    cp -r "Test_Puertos" "$CFG_TMP/"
    cp "deploy" "$CFG_TMP/"
    cp "pom.xml" "$CFG_TMP/"
    cp "tomcat-users.xml" "$CFG_TMP/"
    cp "miapp.png" "$CFG_TMP/"
}

compile(){
    cd $CFG_TMP || exit 1
    mvn clean install -DskipTests
}

renombrar(){
    mv $CFG_WRK/edEXT-jar-with-dependencies.jar $CFG_WRK/edEXT.jar
    mv $CFG_WRK/servidor-jar-with-dependencies.jar $CFG_WRK/servidor.jar
    mv $CFG_WRK/test-port-jar-with-dependencies.jar $CFG_WRK/testPort.jar
}

downloadApp(){
    HOST="https://github.com"
    REPO="AppImage/appimagetool"
    FILE="releases/download/continuous/appimagetool-x86_64.AppImage"
    curl -fL -o appimagetool.AppImage "$HOST/$REPO/$FILE" && chmod +x appimagetool.AppImage
}

crearApp(){
    for((pos=0; pos<${#NODEAMONS[@]}; pos++)); do
        makeAppDir "${NODEAMONS[pos]}" "${NODEA_JAR[pos]}"
        makeAppImage "${NODEAMONS[pos]}" "${NODEA_NOM[pos]}"
        rm -rf "${NODEAMONS[pos]}"
    done
}

moverApp(){
    mv -f "edEXT" $DIR_PRJ
    mv -f "servidor" "$DIR_BIN/servidor"
    cp -f "testPort" "$DIR_SHD/testPort"
    mv -f "testPort" "$DIR_TLS/testPort"
    ln -sf "resources/tools/testPort" "$DIR_PRJ/check-puertos"
}

clearSHD(){
    if [[ -f "$DIR_SHD/web.war" ]]; then
        rm -f "$DIR_SHD/web.war"
    fi
    if [[ -f "$DIR_SHD/deploy" ]]; then
        rm -f "$DIR_SHD/deploy"
    fi
    if [[ -f "$DIR_SHD/testPort" ]]; then
        rm -f "$DIR_SHD/testPort"
    fi
    if [[ -d "$DIR_SHD/apache-tomcat-10.1.61" ]]; then
        rm -rf "$DIR_SHD/apache-tomcat-10.1.61"
    fi
}

checkDir
crear
copy
compile
cd $CFG_TMP || exit 1
renombrar
downloadApp
crearApp
clearSHD
if [[ -f "$DIR_SHD/serverWeb.tar.gz" ]]; then
    rm -f "$DIR_SHD/serverWeb.tar.gz"
fi
moverApp

wget -O tomcat.tar.gz https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.61/bin/apache-tomcat-10.1.61.tar.gz

tar -xzvf tomcat.tar.gz
mv -f "tomcat-users.xml" "apache-tomcat-10.1.61/conf/tomcat-users.xml"
        
mv -f "apache-tomcat-10.1.61" "$DIR_SHD/apache-tomcat-10.1.61"
mv -f "$CFG_WRK/web.war" "$DIR_SHD/web.war"
mv -f "$CFG_WRK/testPort.jar" "$DIR_JAR/testPort.jar"
mv -f "$CFG_WRK/servidor.jar" "$DIR_JAR/servidor.jar"
mv -f "$CFG_WRK/edEXT.jar" "$DIR_JAR/edEXT.jar"
mv -f "miapp.png" "$DIR_PRJ/edEXT.png"
mv -f "deploy" "$DIR_SHD/deploy"

rm appimagetool.AppImage
rm -rf "$CFG_TMP"
rm -rf "$CFG_TP"
rm -rf "$CFG_WRK"



cd $DIR_SHD
tar -czvf "serverWeb.tar.gz" *
cd $DIR_PRJ
clearSHD

clear
#╔╗╚╝║═
echo ""
echo "                   ╔═════════════════════════════════════╗"
echo "                   ║                                     ║"
echo "                   ║         Instalacion completa        ║"
echo "                   ║                edEXT                ║"
echo "                   ║         Plataforma educativa        ║"
echo "                   ║                                     ║"
echo "                   ╚═════════════════════════════════════╝"
echo ""
echo "Para preparar el Servidor Web...."
echo "                         Utilice check-puertos para compartir la carpeta shared"
echo "Para iniciar el Servidor Central..."
echo "                         escriba  ./edEXT start o ./edEXT -h para mas ayuda"
echo "Para iniciar el Servidor Web..."
echo "                   descargue el tar.gz, descomprima y escriba  ./deploy"
echo ""
