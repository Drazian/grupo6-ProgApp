para evitar contratiempos, he subido al Git en modo monolito, para que sigan trabajando sin inconveniente, pero si quieren compilar, tendran que seguir los siguientes pasos:

Para Compilar se tiene que comentar toda referencia al Central
1 - comentar en el POM la dependencia del central, solo coloquen <!-- antes y --> despues del siguiente bloque

       <dependency>
            <groupId>edu</groupId>
            <artifactId>Servidor_Central</artifactId>
            <version>1.0-SNAPSHOT</version>
       </dependency>
Para que quede parecido a:
<!--        <dependency>
            <groupId>edu</groupId>
            <artifactId>Servidor_Central</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>-->

2 - ee tiene que comentar todos los import del central y descomentar los de SOAP

3 - se tiene que comentar la fabrica del central y descomentar la fabrica del web

4 - se tiene que comentar todos las comandos denl central y descomentar todos los del web

5 - cuando ya no haya servlet con marcas de erros, se procede a realizar u clean and build, siempre teniendo el central en funcionamiento

6 - se lanza con Run el web

el procedimiento de los pasos 2, 3 y 4, se detallaran a continuacion:

Se tiene que compilar secuencialmente, primero un clean and build al Central, luego un Run.
Con el Central en funcionamiento, se hace un clean and build al Web, pero antes se descomenta en el pom la dependencia del central:
<!--        <dependency>
            <groupId>edu</groupId>
            <artifactId>Servidor_Central</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>-->




en todos los servlets para compilar se tiene que comentar/descomentar y en produccion se tiene que realizar la operacion contraria:
    Descomentar:
        edu.edext.logica.*
        edu.edext.datatypes.*
        private IControlador ic = Fabrica.getInstance().getIControlador(); (la linea inmediata a public class)
    Comentar:
        edu.edext.controlador.ws.*
        private IBridgeSOAP ic = Fabrica.getConexion(); (la linea inmediata a public class)
Fabrica:
    Comentar:
        edu.edext.controlador.ws.BridgeSOAPService
        import edu.edext.controlador.ws.IBridgeSOAP
        linea 18 y 19
    Descomentar:
        edu.edext.logica.IBridgeSOAP
        linea 17
Remux:
    Comentar todas las lineas, menos 'package', 'public class{' y '}'
    
BuscadorServlet:
    Instrucciones
        lineas 56 - 60
        lineas 76 - 80
        lineas 99 - 103

CursoServlet:
    Instrucciones
        lineas 307 -313

EdicionServlet:
    Instrucciones
        lineas 219 -223

LoginHelper:
    Instrucciones
        lineas 76 -87
        
ProgramaServlet:
    Instrucciones
        lineas 183 -187

UsuarioServlet:
    Instrucciones en:
        lineas 276 - 284
        lineas 346 - 370
        lineas 565 - 589

