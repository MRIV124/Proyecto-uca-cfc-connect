package sv.edu.udb.cfcconnect.curso.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.curso.domain.Curso;
import sv.edu.udb.cfcconnect.curso.dto.CursoRequest;
import sv.edu.udb.cfcconnect.curso.dto.CursoResponse;
import sv.edu.udb.cfcconnect.curso.repository.CursoRepository;
import sv.edu.udb.cfcconnect.curso.service.CursoService;
import sv.edu.udb.cfcconnect.exception.CupoAgotadoException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository repository;

    public CursoServiceImpl(CursoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CursoResponse> findAll() {
        return repository.findAll().stream().map(CursoResponse::fromEntity).toList();
    }

    @Override
    public CursoResponse findById(Long id) {
        return CursoResponse.fromEntity(getEntity(id));
    }

    @Override
    public CursoResponse create(CursoRequest request) {
        Curso curso = new Curso();
        curso.setNombre(request.getNombre());
        curso.setPrecio(request.getPrecio());
        curso.setCupo(request.getCupo());
        curso.setInscritos(0);
        return CursoResponse.fromEntity(repository.save(curso));
    }

    @Override
    public CursoResponse update(Long id, CursoRequest request) {
        Curso curso = getEntity(id);
        curso.setNombre(request.getNombre());
        curso.setPrecio(request.getPrecio());
        curso.setCupo(request.getCupo());
        return CursoResponse.fromEntity(repository.save(curso));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    @Override
    public CursoResponse inscribirParticipante(Long id) {
        Curso curso = getEntity(id);

        // Caso de fallo por negocio: el curso ya alcanzo su cupo maximo.
        // -> CupoAgotadoException, capturada por GlobalExceptionHandler como HTTP 409.
        if (!curso.tieneCupoDisponible()) {
            throw new CupoAgotadoException(
                    "No es posible inscribir al participante: el curso '" + curso.getNombre()
                            + "' ya alcanzo su cupo maximo (" + curso.getCupo() + ").");
        }

        // Caso de exito: hay cupo disponible.
        curso.setInscritos(curso.getInscritos() + 1);
        return CursoResponse.fromEntity(repository.save(curso));
    }

    private Curso getEntity(Long id) {
        // Caso de fallo por recurso inexistente -> ResourceNotFoundException -> HTTP 404.
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Curso", id));
    }
}
