package sv.edu.udb.cfcconnect.inscripcion.service.implementation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;
import sv.edu.udb.cfcconnect.cliente.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.curso.domain.Curso;
import sv.edu.udb.cfcconnect.curso.repository.CursoRepository;
import sv.edu.udb.cfcconnect.exception.CupoAgotadoException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;
import sv.edu.udb.cfcconnect.inscripcion.domain.Inscripcion;
import sv.edu.udb.cfcconnect.inscripcion.dto.InscripcionRequest;
import sv.edu.udb.cfcconnect.inscripcion.dto.InscripcionResponse;
import sv.edu.udb.cfcconnect.inscripcion.repository.InscripcionRepository;
import sv.edu.udb.cfcconnect.inscripcion.service.InscripcionService;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository repository;
    private final ClienteRepository clienteRepository;
    private final CursoRepository cursoRepository;

    public InscripcionServiceImpl(InscripcionRepository repository,
                                   ClienteRepository clienteRepository,
                                   CursoRepository cursoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    public List<InscripcionResponse> findAll() {
        return repository.findAll().stream().map(InscripcionResponse::fromEntity).toList();
    }

    @Override
    public InscripcionResponse findById(Long id) {
        return InscripcionResponse.fromEntity(getEntity(id));
    }

    @Override
    public InscripcionResponse create(InscripcionRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.getClienteId()));

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> ResourceNotFoundException.of("Curso", request.getCursoId()));

        // Caso de fallo por negocio: el curso ya alcanzo su cupo maximo.
        if (!curso.tieneCupoDisponible()) {
            throw new CupoAgotadoException(
                    "No es posible inscribir a " + cliente.getNombre() + ": el curso '" + curso.getNombre()
                            + "' no tiene cupo disponible.");
        }

        // Caso de exito: se crea la inscripcion y se incrementa el contador de inscritos.
        curso.setInscritos(curso.getInscritos() + 1);
        cursoRepository.save(curso);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCliente(cliente);
        inscripcion.setCurso(curso);
        inscripcion.setFecha(LocalDate.now());
        inscripcion.setEstado(Inscripcion.Estado.PENDIENTE);

        return InscripcionResponse.fromEntity(repository.save(inscripcion));
    }

    @Override
    public InscripcionResponse update(Long id, InscripcionRequest request) {
        Inscripcion inscripcion = getEntity(id);

        // Regla de negocio: una inscripcion cancelada o finalizada ya no deberia editarse.
        if (inscripcion.getEstado() == Inscripcion.Estado.CANCELADA
                || inscripcion.getEstado() == Inscripcion.Estado.FINALIZADA) {
            throw new IllegalArgumentException(
                    "No se puede editar una inscripcion en estado " + inscripcion.getEstado().name() + ".");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.getClienteId()));

        Curso cursoNuevo = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> ResourceNotFoundException.of("Curso", request.getCursoId()));

        Curso cursoAnterior = inscripcion.getCurso();

        // Si se esta cambiando el curso, hay que liberar el cupo del curso anterior
        // y validar/tomar cupo en el nuevo (mismo control que en create()).
        if (!cursoAnterior.getId().equals(cursoNuevo.getId())) {
            if (!cursoNuevo.tieneCupoDisponible()) {
                throw new CupoAgotadoException(
                        "No es posible mover la inscripcion: el curso '" + cursoNuevo.getNombre()
                                + "' no tiene cupo disponible.");
            }
            cursoAnterior.setInscritos(Math.max(0, cursoAnterior.getInscritos() - 1));
            cursoRepository.save(cursoAnterior);

            cursoNuevo.setInscritos(cursoNuevo.getInscritos() + 1);
            cursoRepository.save(cursoNuevo);
        }

        inscripcion.setCliente(cliente);
        inscripcion.setCurso(cursoNuevo);
        // El estado y la fecha original se conservan; para el estado existe /estado.

        return InscripcionResponse.fromEntity(repository.save(inscripcion));
    }

    @Override
    public InscripcionResponse cambiarEstado(Long id, String estado) {
        Inscripcion inscripcion = getEntity(id);
        try {
            inscripcion.setEstado(Inscripcion.Estado.valueOf(estado.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Estado invalido. Valores permitidos: PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA");
        }
        return InscripcionResponse.fromEntity(repository.save(inscripcion));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private Inscripcion getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Inscripcion", id));
    }
}
