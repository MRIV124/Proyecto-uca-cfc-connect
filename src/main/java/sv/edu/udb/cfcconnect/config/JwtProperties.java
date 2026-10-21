package sv.edu.udb.cfcconnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuracion del modulo de seguridad (Fase 3).
 *
 * <p>Se enlazan con el prefijo {@code app.jwt} del archivo
 * {@code application.properties} y permiten cambiar la clave de firma y la
 * duracion del token sin tocar el codigo Java.</p>
 *
 * <p>La clave {@code secret} debe estar codificada en Base64 y decodificar a
 * al menos 32 bytes (256 bits), porque el algoritmo de firma usado es HS256.
 * Puede generarse con {@code openssl rand -base64 32}. En produccion debe
 * sobreescribirse con la variable de entorno {@code APP_JWT_SECRET}.</p>
 */
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** Clave secreta de firma (Base64, minimo 32 bytes decodificados). */
    private String secret;

    /** Duracion de la validez del token, en segundos. */
    private long expirationSeconds = 3600L;

    /** Emisor del token (claim "iss"), para identificar a la aplicacion. */
    private String issuer = "uca-cfc-connect";

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getExpirationSeconds() { return expirationSeconds; }
    public void setExpirationSeconds(long expirationSeconds) { this.expirationSeconds = expirationSeconds; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}
