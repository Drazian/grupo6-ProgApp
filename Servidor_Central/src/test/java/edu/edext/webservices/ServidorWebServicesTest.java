package edu.edext.webservices;

import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Publica los Web Services de verdad (servidor HTTP de la JDK) sobre la base H2 de los tests
 * y los consume por HTTP con mensajes SOAP, como lo haria el Servidor Web.
 */
public class ServidorWebServicesTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static String url;

    @BeforeAll
    public static void publicar() throws Exception {
        Fabrica.getInstance().getIControlador().cargarDatosDePrueba();
        int puerto;
        try (ServerSocket libre = new ServerSocket(0)) {
            puerto = libre.getLocalPort();
        }
        url = ServidorWebServices.publicar("http://localhost:" + puerto + "/edext/publicador");
    }

    @AfterAll
    public static void detener() {
        ServidorWebServices.detener();
    }

    @AfterEach
    public void republicarSiHaceFalta() {
        if (!url.equals(ServidorWebServices.getUrlPublicada()))
            ServidorWebServices.publicar(url);
    }

    private static HttpResponse<String> soap(String operacion, String parametros) throws Exception {
        String sobre = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body>"
                + "<ns:" + operacion + " xmlns:ns=\"http://webservices.edext.edu/\">" + parametros + "</ns:" + operacion + ">"
                + "</S:Body></S:Envelope>";
        HttpRequest pedido = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "text/xml; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(sobre, StandardCharsets.UTF_8))
                .build();
        return HTTP.send(pedido, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    @Test
    public void testWsdlPublicado() throws Exception {
        assertTrue(ServidorWebServices.estaPublicado());
        assertEquals(url, ServidorWebServices.getUrlPublicada());
        HttpResponse<String> wsdl = HTTP.send(HttpRequest.newBuilder(URI.create(url + "?wsdl")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, wsdl.statusCode());
        assertTrue(wsdl.body().contains("PublicadorService"));
        assertTrue(wsdl.body().contains("name=\"iniciarSesion\""));
        assertFalse(wsdl.body().contains("name=\"cargarDatosDePrueba\""), "Las operaciones del Administrador no se publican");
    }

    @Test
    public void testConsultasPorSoap() throws Exception {
        HttpResponse<String> institutos = soap("listarInstitutos", "");
        assertEquals(200, institutos.statusCode());
        assertTrue(institutos.body().contains("<nombre>INCO</nombre>"));

        // Nombres con tildes y fechas LocalDate (viajan como yyyy-MM-dd)
        HttpResponse<String> edicion = soap("getEdicion", "<nombre>Seminarios de Resolución de Problemas - 2026</nombre>");
        assertEquals(200, edicion.statusCode());
        assertTrue(edicion.body().contains("<nombre>Seminarios de Resolución de Problemas - 2026</nombre>"));
        assertTrue(edicion.body().matches("(?s).*<fechaInicio>\\d{4}-\\d{2}-\\d{2}</fechaInicio>.*"));
    }

    @Test
    public void testLaPasswordNoViajaYElLoginSeVerificaEnElServidor() throws Exception {
        String nick = "ws-" + UUID.randomUUID().toString().substring(0, 8);
        DtUsuario nuevo = new DtUsuario(nick, null, nick + "@test.com", "Web", "Service", null, new Date(),
                List.of(), TipoUsuario.ESTUDIANTE);
        new Publicador().crearUsuario(nuevo, "clave-segura");

        HttpResponse<String> usuario = soap("obtenerUsuario", "<nicknameOEmail>" + nick + "</nicknameOEmail>");
        assertTrue(usuario.body().contains("<nickname>" + nick + "</nickname>"));
        assertFalse(usuario.body().toLowerCase().contains("password"));
        assertFalse(usuario.body().contains("$2a$"), "No viaja el hash de la contraseña");

        HttpResponse<String> loginOk = soap("iniciarSesion", "<identificador>" + nick + "@test.com</identificador><password>clave-segura</password>");
        assertTrue(loginOk.body().contains("<nickname>" + nick + "</nickname>"));
        HttpResponse<String> loginMal = soap("iniciarSesion", "<identificador>" + nick + "</identificador><password>otra</password>");
        assertFalse(loginMal.body().contains("<return>"));
    }

    @Test
    public void testErrorDelControladorLlegaComoFault() throws Exception {
        HttpResponse<String> duplicado = soap("crearInstituto", "<nombre>INCO</nombre>");
        assertEquals(500, duplicado.statusCode());
        assertTrue(duplicado.body().contains("faultstring"), duplicado.body());
        assertTrue(duplicado.body().contains("INCO"), duplicado.body());
    }

    @Test
    public void testRepublicarYDetener() throws Exception {
        // Publicar con la URL de la configuracion (ws.url de servidor-test.properties) reemplaza la anterior
        String configurada = ServidorWebServices.publicar();
        assertEquals("http://localhost:0/edext/publicador", configurada);
        assertTrue(ServidorWebServices.estaPublicado());

        ServidorWebServices.detener();
        assertFalse(ServidorWebServices.estaPublicado());
        assertNull(ServidorWebServices.getUrlPublicada());
        ServidorWebServices.detener(); // detener dos veces no falla
    }

    @Test
    public void testMainPublicaSinEstacionDeTrabajo() {
        ServidorWebServices.main(new String[0]);
        assertTrue(ServidorWebServices.estaPublicado());
        assertEquals("http://localhost:0/edext/publicador", ServidorWebServices.getUrlPublicada());
    }
}
