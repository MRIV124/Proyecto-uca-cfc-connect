package sv.edu.udb.cfcconnect.diplomado.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.diplomado.domain.Diplomado;
import sv.edu.udb.cfcconnect.diplomado.dto.DiplomadoRequest;
import sv.edu.udb.cfcconnect.diplomado.dto.DiplomadoResponse;
import sv.edu.udb.cfcconnect.diplomado.repository.DiplomadoRepository;
import sv.edu.udb.cfcconnect.diplomado.service.DiplomadoService;
import sv.edu.udb.cfcconnect.exception.CupoAgotadoException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

@Service
public class DiplomadoServiceImpl implements DiplomadoService {

    private final DiplomadoRepository repository;

    public DiplomadoServiceImpl(DiplomadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DiplomadoResponse> findAll() {
        return repository.findAll().stream().map(DiplomadoResponse::fromEntity).toList();
    }

    @Override
    public DiplomadoResponse findById(Long id) {
        return DiplomadoResponse.fromEntity(getEntity(id));
    }

    @Override
    public DiplomadoResponse create(DiplomadoRequest request) {
        Diplomado diplomado = new Diplomado();
        diplomado.setNombre(request.getNombre());
        diplomado.setPrecio(request.getPrecio());
        diplomado.setCupo(request.getCupo());
        diplomado.setInscritos(0);
        return DiplomadoResponse.fromEntity(repository.save(diplomado));
    }

    @Override
    public DiplomadoResponse update(Long id, DiplomadoRequest request) {
        Diplomado diplomado = getEntity(id);
        diplomado.setNombre(request.getNombre());
        diplomado.setPrecio(request.getPrecio());
        diplomado.setCupo(request.getCupo());
        return DiplomadoResponse.fromEntity(repository.save(diplomado));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    @Override
    public DiplomadoResponse inscribirParticipante(Long id) {
        Diplomado diplomado = getEntity(id);
        if (!diplomado.tieneCupoDisponible()) {
            throw new CupoAgotadoException(
                    "No es posible inscribir al participante: el diplomado '" + diplomado.getNombre()
                            + "' ya alcanzo su cupo maximo (" + diplomado.getCupo() + ").");
        }
        diplomado.setInscritos(diplomado.getInscritos() + 1);
        return DiplomadoResponse.fromEntity(repository.save(diplomado));
    }

    private Diplomado getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Diplomado", id));
    }
}
