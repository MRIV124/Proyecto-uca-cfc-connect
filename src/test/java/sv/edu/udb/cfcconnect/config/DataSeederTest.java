package sv.edu.udb.cfcconnect.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.edu.udb.cfcconnect.rol.domain.Rol;
import sv.edu.udb.cfcconnect.rol.repository.RolRepository;
import sv.edu.udb.cfcconnect.usuario.domain.Usuario;
import sv.edu.udb.cfcconnect.usuario.repository.UsuarioRepository;

/**
 * Pruebas del {@link DataSeeder} relacionadas con la Fase 3.
 *
 * <p>Verifican que una base de datos con datos de la Fase 2 (contrasenas en
 * texto plano) se migra a BCrypt al arrancar, de modo que los usuarios
 * existentes puedan iniciar sesion.</p>
 */
@SpringBootTest
public class DataSeederTest {

    @Autowired
    private DataSeeder dataSeeder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void debeMigrarAContrasenasEnTextoPlano() {
        Rol rol = rolRepository.findByNombreIgnoreCase("ADMIN").orElseThrow();

        Usuario legacy = new Usuario();
        legacy.setNombre("Usuario Legacy");
        legacy.setEmail("legacy@cfc.edu.sv");
        legacy.setPassword("Legacy123*"); // texto plano, como en la Fase 2
        legacy.setRol(rol);
        legacy.setEstado(true);
        usuarioRepository.save(legacy);

        // Se ejecuta el seeder, igual que al arrancar la aplicacion.
        dataSeeder.run();

        Usuario migrado = usuarioRepository.findByEmailIgnoreCase("legacy@cfc.edu.sv").orElseThrow();
        assertTrue(migrado.getPassword().startsWith("$2"), "La contrasena debe quedar cifrada con BCrypt");
        assertTrue(passwordEncoder.matches("Legacy123*", migrado.getPassword()),
                "El hash debe seguir correspondiendo a la contrasena original");

        usuarioRepository.delete(migrado);
    }

    @Test
    void debeMantenerLasContrasenasYaCifradas() {
        // El administrador lo crea el seeder con BCrypt: al volver a correrlo
        // no debe duplicarse ni re-cifrar su contrasena.
        dataSeeder.run();

        long total = usuarioRepository.count();
        dataSeeder.run();

        assertTrue(usuarioRepository.count() == total, "El seeder no debe duplicar usuarios");
    }
}
