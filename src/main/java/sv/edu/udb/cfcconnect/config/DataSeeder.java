package sv.edu.udb.cfcconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sv.edu.udb.cfcconnect.catering.domain.ServicioCatering;
import sv.edu.udb.cfcconnect.catering.repository.ServicioCateringRepository;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;
import sv.edu.udb.cfcconnect.cliente.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.curso.domain.Curso;
import sv.edu.udb.cfcconnect.curso.repository.CursoRepository;
import sv.edu.udb.cfcconnect.diplomado.domain.Diplomado;
import sv.edu.udb.cfcconnect.diplomado.repository.DiplomadoRepository;
import sv.edu.udb.cfcconnect.espacio.domain.Espacio;
import sv.edu.udb.cfcconnect.espacio.repository.EspacioRepository;
import sv.edu.udb.cfcconnect.rol.domain.Rol;
import sv.edu.udb.cfcconnect.rol.repository.RolRepository;
import sv.edu.udb.cfcconnect.usuario.domain.Usuario;
import sv.edu.udb.cfcconnect.usuario.repository.UsuarioRepository;

/**
 * Carga datos de prueba al iniciar la aplicacion, para poder probar de
 * inmediato los endpoints (GET /clientes, GET /cursos, etc.) y demostrar
 * que la persistencia JPA + la base de datos (H2/MySQL) estan funcionando
 * de extremo a extremo, sin depender de insertar datos manualmente.
 *
 * Solo inserta datos si las tablas estan vacias, para no duplicar registros
 * en reinicios sucesivos durante el desarrollo.
 *
 * En la Fase 3 tambien crea los usuarios de prueba (con la contrasena cifrada
 * con BCrypt) para poder iniciar sesion en POST /auth/login y obtener un
 * token JWT.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    /** Prefijo de los hashes BCrypt (por ejemplo "$2a$10$..."). */
    private static final String PREFIJO_BCRYPT = "$2";

    private final RolRepository rolRepository;
    private final ClienteRepository clienteRepository;
    private final CursoRepository cursoRepository;
    private final DiplomadoRepository diplomadoRepository;
    private final EspacioRepository espacioRepository;
    private final ServicioCateringRepository servicioCateringRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.email:admin@cfc.edu.sv}")
    private String adminEmail;

    @Value("${app.security.admin.password:Admin123*}")
    private String adminPassword;

    @Value("${app.security.recepcionista.email:recepcion@cfc.edu.sv}")
    private String recepcionistaEmail;

    @Value("${app.security.recepcionista.password:Recepcion123*}")
    private String recepcionistaPassword;

    public DataSeeder(RolRepository rolRepository,
                       ClienteRepository clienteRepository,
                       CursoRepository cursoRepository,
                       DiplomadoRepository diplomadoRepository,
                       EspacioRepository espacioRepository,
                       ServicioCateringRepository servicioCateringRepository,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.clienteRepository = clienteRepository;
        this.cursoRepository = cursoRepository;
        this.diplomadoRepository = diplomadoRepository;
        this.espacioRepository = espacioRepository;
        this.servicioCateringRepository = servicioCateringRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedClientes();
        seedCursos();
        seedDiplomados();
        seedEspacios();
        seedCatering();
        seedUsuarios();
        migrarContrasenasLegacy();
    }

    private void seedRoles() {
        if (rolRepository.count() > 0) return;
        for (String nombre : new String[]{"ADMIN", "RECEPCIONISTA", "CLIENTE", "CONTABILIDAD"}) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rolRepository.save(rol);
        }
    }

    private void seedClientes() {
        if (clienteRepository.count() > 0) return;

        Cliente c1 = new Cliente();
        c1.setNombre("Juan Perez");
        c1.setCorreo("juan@gmail.com");
        c1.setTelefono("7777-1111");
        clienteRepository.save(c1);

        Cliente c2 = new Cliente();
        c2.setNombre("Ana Lopez");
        c2.setCorreo("ana@gmail.com");
        c2.setTelefono("7777-2222");
        clienteRepository.save(c2);
    }

    private void seedCursos() {
        if (cursoRepository.count() > 0) return;

        Curso c1 = new Curso();
        c1.setNombre("Spring Boot");
        c1.setPrecio(120.0);
        c1.setCupo(30);
        c1.setInscritos(0);
        cursoRepository.save(c1);

        Curso c2 = new Curso();
        c2.setNombre("React");
        c2.setPrecio(150.0);
        c2.setCupo(25);
        c2.setInscritos(25); // sin cupo, para probar CupoAgotadoException
        cursoRepository.save(c2);
    }

    private void seedDiplomados() {
        if (diplomadoRepository.count() > 0) return;

        Diplomado d1 = new Diplomado();
        d1.setNombre("Gerencia de Proyectos");
        d1.setPrecio(350.0);
        d1.setCupo(20);
        d1.setInscritos(4);
        diplomadoRepository.save(d1);
    }

    private void seedEspacios() {
        if (espacioRepository.count() > 0) return;

        Espacio e1 = new Espacio();
        e1.setNombre("Auditorio Principal");
        e1.setTipo("Auditorio");
        e1.setCapacidad(200);
        e1.setPrecio(250.0);
        e1.setEquipamiento("Proyector, sonido");
        e1.setDisponible(true);
        espacioRepository.save(e1);

        Espacio e2 = new Espacio();
        e2.setNombre("Sala de Reuniones A");
        e2.setTipo("Sala de reuniones");
        e2.setCapacidad(12);
        e2.setPrecio(40.0);
        e2.setEquipamiento("TV, pizarra");
        e2.setDisponible(true);
        espacioRepository.save(e2);
    }

    private void seedCatering() {
        if (servicioCateringRepository.count() > 0) return;

        for (Object[] datos : new Object[][]{
                {"Coffee Break", 4.5},
                {"Almuerzo", 8.0},
                {"Refrigerio", 3.0}
        }) {
            ServicioCatering s = new ServicioCatering();
            s.setNombre((String) datos[0]);
            s.setPrecioPorPersona((Double) datos[1]);
            servicioCateringRepository.save(s);
        }
    }

    /**
     * Crea los usuarios de prueba con la contrasena cifrada con BCrypt, para
     * poder iniciar sesion en {@code POST /auth/login}.
     *
     * <p>Se comprueba el correo (y no solo si la tabla esta vacia) para
     * garantizar siempre un administrador con el que entrar, incluso en una
     * base de datos MySQL que ya tenga usuarios de la Fase 2.</p>
     */
    private void seedUsuarios() {
        crearUsuarioSiNoExiste("Administrador CFC", adminEmail, adminPassword, "ADMIN");
        crearUsuarioSiNoExiste("Recepcion CFC", recepcionistaEmail, recepcionistaPassword, "RECEPCIONISTA");
    }

    private void crearUsuarioSiNoExiste(String nombre, String email, String password, String rolNombre) {
        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) return;

        Rol rol = rolRepository.findByNombreIgnoreCase(rolNombre)
                .orElseThrow(() -> new IllegalStateException("No se encontro el rol " + rolNombre));

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setEstado(true);
        usuarioRepository.save(usuario);
    }

    /**
     * Migra a BCrypt las contrasenas que quedaron guardadas en texto plano en
     * la Fase 2 (por ejemplo, en una base de datos MySQL con datos previos).
     *
     * <p>Sin esta migracion esos usuarios no podrian iniciar sesion, porque el
     * login compara contra el hash BCrypt. Solo se tocan las contrasenas que
     * aun no estan cifradas (las que no empiezan por {@code $2}).</p>
     */
    private void migrarContrasenasLegacy() {
        for (Usuario usuario : usuarioRepository.findAll()) {
            String password = usuario.getPassword();
            if (password == null || password.startsWith(PREFIJO_BCRYPT)) continue;

            usuario.setPassword(passwordEncoder.encode(password));
            usuarioRepository.save(usuario);
        }
    }
}
