package sv.edu.udb.cfcconnect.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.edu.udb.cfcconnect.seguridad.domain.UsuarioPrincipal;
import sv.edu.udb.cfcconnect.seguridad.jwt.JwtService;

/**
 * Pruebas del servicio de tokens JWT y del cifrado de contrasenas con BCrypt
 * (Fase 3 - Seguridad).
 */
@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UsuarioPrincipal usuario() {
        return new UsuarioPrincipal(1L, "Administrador CFC", "admin@cfc.edu.sv",
                passwordEncoder.encode("Admin123*"), "ADMIN", true);
    }

    @Test
    void shouldGenerarTokenConCorreoYRol() {
        UsuarioPrincipal usuario = usuario();

        String token = jwtService.generateToken(usuario);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length, "El token debe tener header, payload y firma");
        assertEquals("admin@cfc.edu.sv", jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractRol(token));
    }

    @Test
    void shouldValidarTokenEmitido() {
        UsuarioPrincipal usuario = usuario();
        String token = jwtService.generateToken(usuario);

        assertTrue(jwtService.isTokenValid(token, usuario));
    }

    @Test
    void shouldRechazarTokenAlterado() {
        UsuarioPrincipal usuario = usuario();
        String token = jwtService.generateToken(usuario);

        // Se altera un caracter del PAYLOAD (contenido), no de la firma: de
        // este modo el cambio siempre modifica los bytes y la firma deja de
        // coincidir. (Alterar el ultimo caracter de la firma no es fiable:
        // sus bits de relleno se descartan al decodificar y el token podria
        // seguir siendo valido.)
        String[] partes = token.split("\\.");
        partes[1] = cambiarPrimerCaracter(partes[1]);
        String tokenAlterado = String.join(".", partes);

        assertFalse(jwtService.isTokenValid(tokenAlterado, usuario));
    }

    @Test
    void shouldRechazarFirmaAlterada() {
        UsuarioPrincipal usuario = usuario();
        String token = jwtService.generateToken(usuario);

        // Se altera un caracter del medio de la firma: al no ser el ultimo,
        // todos sus bits forman parte de la firma y el cambio es siempre real.
        String[] partes = token.split("\\.");
        String firma = partes[2];
        int posicion = firma.length() / 2;
        partes[2] = firma.substring(0, posicion)
                + (firma.charAt(posicion) == 'A' ? 'B' : 'A')
                + firma.substring(posicion + 1);

        assertFalse(jwtService.isTokenValid(String.join(".", partes), usuario));
    }

    /** Devuelve la cadena con su primer caracter cambiado por una letra distinta. */
    private String cambiarPrimerCaracter(String valor) {
        char original = valor.charAt(0);
        char nuevo = original == 'A' ? 'B' : 'A';
        return nuevo + valor.substring(1);
    }

    @Test
    void shouldRechazarTokenDeOtroUsuario() {
        UsuarioPrincipal admin = usuario();
        UsuarioPrincipal otro = new UsuarioPrincipal(2L, "Otro", "otro@cfc.edu.sv",
                passwordEncoder.encode("Otrom123*"), "CLIENTE", true);

        String token = jwtService.generateToken(admin);

        assertFalse(jwtService.isTokenValid(token, otro));
    }

    @Test
    void shouldRespetarLaExpiracionConfigurada() {
        UsuarioPrincipal usuario = usuario();

        String token = jwtService.generateToken(usuario);
        Date expiracion = jwtService.extractExpiration(token);

        assertNotNull(expiracion);
        assertTrue(expiracion.after(new Date()), "El token recien emitido debe estar vigente");
        assertEquals(jwtService.getExpirationSeconds(), jwtService.getExpirationSeconds());
        assertTrue(expiracion.getTime() - System.currentTimeMillis() <= jwtService.getExpirationSeconds() * 1000L);
    }

    @Test
    void shouldCifrarContrasenaConBcrypt() {
        String enTextoPlano = "Admin123*";

        String hash = passwordEncoder.encode(enTextoPlano);

        assertNotEquals(enTextoPlano, hash);
        assertTrue(hash.startsWith("$2"), "El hash debe tener el formato de BCrypt");
        assertTrue(passwordEncoder.matches(enTextoPlano, hash));
        assertFalse(passwordEncoder.matches("otra-contrasena", hash));
    }

    @Test
    void shouldGenerarHashDistintoParaLaMismaContrasena() {
        // BCrypt usa una sal aleatoria: dos usuarios con la misma contrasena
        // deben tener hashes distintos.
        String hash1 = passwordEncoder.encode("misma-clave");
        String hash2 = passwordEncoder.encode("misma-clave");

        assertNotEquals(hash1, hash2);
        assertTrue(passwordEncoder.matches("misma-clave", hash1));
        assertTrue(passwordEncoder.matches("misma-clave", hash2));
    }
}
