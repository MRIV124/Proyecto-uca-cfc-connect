package sv.edu.udb.cfcconnect.config;

import org.springframework.boot.CommandLineRunner;
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

/**
 * Carga datos de prueba al iniciar la aplicacion, para poder probar de
 * inmediato los endpoints (GET /clientes, GET /cursos, etc.) y demostrar
 * que la persistencia JPA + la base de datos (H2/MySQL) estan funcionando
 * de extremo a extremo, sin depender de insertar datos manualmente.
 *
 * Solo inserta datos si las tablas estan vacias, para no duplicar registros
 * en reinicios sucesivos durante el desarrollo.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final ClienteRepository clienteRepository;
    private final CursoRepository cursoRepository;
    private final DiplomadoRepository diplomadoRepository;
    private final EspacioRepository espacioRepository;
    private final ServicioCateringRepository servicioCateringRepository;

    public DataSeeder(RolRepository rolRepository,
                       ClienteRepository clienteRepository,
                       CursoRepository cursoRepository,
                       DiplomadoRepository diplomadoRepository,
                       EspacioRepository espacioRepository,
                       ServicioCateringRepository servicioCateringRepository) {
        this.rolRepository = rolRepository;
        this.clienteRepository = clienteRepository;
        this.cursoRepository = cursoRepository;
        this.diplomadoRepository = diplomadoRepository;
        this.espacioRepository = espacioRepository;
        this.servicioCateringRepository = servicioCateringRepository;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedClientes();
        seedCursos();
        seedDiplomados();
        seedEspacios();
        seedCatering();
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
}
