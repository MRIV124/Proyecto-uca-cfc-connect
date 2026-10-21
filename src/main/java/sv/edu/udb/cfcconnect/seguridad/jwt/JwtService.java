package sv.edu.udb.cfcconnect.seguridad.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import sv.edu.udb.cfcconnect.config.JwtProperties;
import sv.edu.udb.cfcconnect.seguridad.domain.UsuarioPrincipal;

/**
 * Servicio responsable de emitir y validar los tokens JWT (Fase 3).
 *
 * <p>El token se firma con HMAC-SHA256 (HS256) usando la clave secreta
 * configurada en {@code app.jwt.secret}. Los claims generados son:</p>
 * <ul>
 *   <li>{@code sub}: correo del usuario (identificador de la sesion).</li>
 *   <li>{@code rol}: nombre del rol (ADMIN, RECEPCIONISTA, CLIENTE, CONTABILIDAD).</li>
 *   <li>{@code iat}: fecha de emision.</li>
 *   <li>{@code exp}: fecha de expiracion (calculada con {@code app.jwt.expiration-seconds}).</li>
 *   <li>{@code iss}: emisor de la aplicacion.</li>
 * </ul>
 */
@Service
public class JwtService {

    /** Claim donde se guarda el rol del usuario autenticado. */
    public static final String CLAIM_ROL = "rol";

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = buildKey(properties.getSecret());
    }

    /**
     * Genera un token firmado para el usuario indicado.
     *
     * @param userDetails usuario autenticado (se usa el correo y el rol).
     * @return token JWT en formato compacto.
     */
    public String generateToken(UserDetails userDetails) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMillis());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(CLAIM_ROL, rolDe(userDetails))
                .issuer(properties.getIssuer())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extrae el correo (subject) del token.
     *
     * @throws JwtException si el token es invalido o ha expirado.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /** Extrae el rol (claim {@code rol}) del token. */
    public String extractRol(String token) {
        return extractClaims(token).get(CLAIM_ROL, String.class);
    }

    /** Extrae la fecha de expiracion del token. */
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    /**
     * Valida el token: firma correcta, no expirado y el mismo usuario al que
     * se emitio.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject() != null
                    && claims.getSubject().equalsIgnoreCase(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /** Duracion configurada de la validez del token, en segundos. */
    public long getExpirationSeconds() {
        return properties.getExpirationSeconds();
    }

    /** Momento en que expira el token recien emitido. */
    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + expirationMillis());
    }

    private Claims extractClaims(String token) {
        JwtParserBuilder parser = Jwts.parser().verifyWith(signingKey);
        if (properties.getIssuer() != null && !properties.getIssuer().isBlank()) {
            parser = parser.requireIssuer(properties.getIssuer());
        }
        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private long expirationMillis() {
        return properties.getExpirationSeconds() * 1000L;
    }

    private String rolDe(UserDetails userDetails) {
        if (userDetails instanceof UsuarioPrincipal principal) {
            return principal.getRol();
        }
        return userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .findFirst()
                .orElse("CLIENTE");
    }

    /**
     * Construye la clave de firma. Acepta una clave en Base64 (formato
     * recomendado) o, como alternativa, un texto plano de al menos 32 bytes.
     */
    private SecretKey buildKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar 'app.jwt.secret' (clave Base64 de 32 bytes o mas).");
        }
        byte[] bytes = decodificar(secret);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "La clave 'app.jwt.secret' debe tener al menos 32 bytes para firmar con HS256.");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    /** Decodifica la clave en Base64; si no es posible, usa sus bytes en UTF-8. */
    private byte[] decodificar(String secret) {
        try {
            byte[] base64 = Decoders.BASE64.decode(secret);
            if (base64.length >= 32) {
                return base64;
            }
        } catch (IllegalArgumentException ex) {
            // No es una cadena Base64 valida: se usa como texto plano.
        }
        return secret.getBytes(StandardCharsets.UTF_8);
    }
}
