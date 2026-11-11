#!/bin/bash

CFG_PRJ="$HOME/.edEXT"
CFG_TMP="$CFG_PRJ/temp"
CFG_WRK="$CFG_PRJ/output"
DIR_PRJ="${1:-$HOME/edEXT}"
DIR_BIN="$DIR_PRJ/bin"
DIR_LOG="$DIR_PRJ/logs"
DIR_RSC="$DIR_PRJ/resources"
DIR_IMG="$DIR_RSC/images"
DIR_TLS="$DIR_RSC/tools"
DIR_TMP="$DIR_PRJ/temp"

crearCFG(){
    mkdir -p $CFG_TMP
}

crear(){
    mkdir $DIR_PRJ
    mkdir $DIR_BIN
    mkdir $DIR_LOG
    mkdir $DIR_RSC
    mkdir $DIR_IMG
    mkdir $DIR_TLS
    mkdir $DIR_TMP
}

makeAppDir() {

    mkdir -p "$1/usr/bin" \
             "$1/usr/lib/jvm" \
             "$1/usr/share/icons/hicolor/256x256/apps" \
             "$1/miapp/lib"

    cp $2 "$1/usr/bin/$2"
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
exec java -jar "\$JAR_PATH" "\$@"
EOF

    chmod +x "$1/AppRun"
}

makeAppImage(){
    ARCH=x86_64 ./appimagetool "$1/" $2
}


if [ ! -d "$CFG_PRJ" ]; then
    crearCFG
else
    rm -rf "$CFG_PRJ"
    crearCFG
fi

if [ ! -d "$DIR_PRJ" ]; then
    crear
else
    rm -rf "$DIR_PRJ"
    crear
fi

cp -r ./* "$CFG_TMP/"
cd $CFG_TMP || exit 1
mvn clean install -DskipTests
#mvn -T 2C -o clean install -DskipTests

#mv $CFG_WRK/edEXT-jar-with-dependencies.jar $CFG_WRK/edEXT.jar
#mv $CFG_WRK/daemon-server-jar-with-dependencies.jar $CFG_WRK/daemon-central.jar
#mv $CFG_WRK/daemon-web-jar-with-dependencies.jar $CFG_WRK/daemon-web.jar
#mv $CFG_WRK/servidor-jar-with-dependencies.jar $CFG_WRK/servidor.jar
#mv $CFG_WRK/test-port-jar-with-dependencies.jar $CFG_WRK/testPort.jar

#makeAppDir "daemoncentral" "daemon-central.jar"
#makeAppDir "daemonweb" "daemon-web.jar"
#makeAppDir "edEXT_cli" "edEXT.jar"
#makeAppDir "servidor" "servidor.jar"
#makeAppDir "testport" "testPort.jar"

#mv $CFG_WRK/edEXT.jar "$CFG_TMP/edEXT_cli/usr/bin"
#mv $CFG_WRK/daemon-central.jar "$CFG_TMP/daemoncentral/usr/bin"
#mv $CFG_WRK/daemon-web.jar "$CFG_TMP/daemonweb/usr/bin"
#mv $CFG_WRK/servidor.jar "$CFG_TMP/servidor/usr/bin"
#mv $CFG_WRK/testPort.jar "$CFG_TMP/testport/usr/bin"

#HOST="https://github.com"
#REPO="AppImage/appimagetool"
#FILE="releases/download/continuous/appimagetool-x86_64.AppImage"
#curl -fL appimagetool.AppImage "$HOST/$REPO/$FILE" && chmod +x appimagetool.AppImage

#makeAppImage "daemoncentral" "daemonCentral"
#makeAppImage "daemonweb" "daemonWeb"
#makeAppImage "edEXT_cli" "edEXT"
#makeAppImage "servidor" "servidor"
#makeAppImage "testport" "testPort"

#wget -O tomcat.tar.gz https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.61/bin/apache-tomcat-10.1.61.tar.gz

