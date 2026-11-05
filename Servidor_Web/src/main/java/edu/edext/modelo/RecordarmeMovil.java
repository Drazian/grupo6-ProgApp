package edu.edext.modelo;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * "Recordarme" del inicio de sesión del Dispositivo Móvil (requerimiento 7.1, Figura 2).
 * La cookie guarda el nickname y el vencimiento, firmados con HMAC-SHA256; nunca la contraseña.
 * La clave se genera al levantar el servidor: si el servidor se reinicia, hay que volver a iniciar sesión.
 */
public final class RecordarmeMovil {
    public static final String COOKIE = "edext_movil_recordarme";
    public static final int DURACION_SEGUNDOS = 30 * 24 * 60 * 60;   // 30 días

    private static final byte[] CLAVE = new byte[32];
    static {
        new SecureRandom().nextBytes(CLAVE);
    }

    private RecordarmeMovil() {}

    /** Token "nickname(base64).vencimiento.firma(base64)". */
    public static String crearToken(String nickname) {
        long vence = Instant.now().getEpochSecond() + DURACION_SEGUNDOS;
        String datos = codificar(nickname.getBytes(StandardCharsets.UTF_8)) + "." + vence;
        return datos + "." + codificar(firmar(datos));
    }

    /** Devuelve el nickname si el token es válido y no venció; si no, null. */
    public static String nicknameDelToken(String token) {
        if (token == null) return null;
        String[] partes = token.split("\\.");
        if (partes.length != 3) return null;
        try {
            String datos = partes[0] + "." + partes[1];
            byte[] recibida = Base64.getUrlDecoder().decode(partes[2]);
            if (!MessageDigest.isEqual(firmar(datos), recibida)) return null;
            if (Instant.now().getEpochSecond() > Long.parseLong(partes[1])) return null;
            return new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {   // base64 o número mal formado
            return null;
        }
    }

    private static byte[] firmar(String datos) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(CLAVE, "HmacSHA256"));
            return mac.doFinal(datos.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo firmar el token de Recordarme", e);
        }
    }

    private static String codificar(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
