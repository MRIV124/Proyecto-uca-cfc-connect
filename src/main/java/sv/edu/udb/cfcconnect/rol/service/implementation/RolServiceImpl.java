package sv.edu.udb.cfcconnect.rol.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;
import sv.edu.udb.cfcconnect.rol.domain.Rol;
import sv.edu.udb.cfcconnect.rol.dto.RolRequest;
import sv.edu.udb.cfcconnect.rol.dto.RolResponse;
import sv.edu.udb.cfcconnect.rol.repository.RolRepository;
import sv.edu.udb.cfcconnect.rol.service.RolService;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    public RolServiceImpl(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RolResponse> findAll() {
        return repository.findAll().stream().map(RolResponse::fromEntity).toList();
    }

    @Override
    public RolResponse findById(Long id) {
        return RolResponse.fromEntity(getEntity(id));
    }

    @Override
    public RolResponse create(RolRequest request) {
        Rol rol = new Rol();
        rol.setNombre(request.getNombre());
        return RolResponse.fromEntity(repository.save(rol));
    }

    @Override
    public RolResponse update(Long id, RolRequest request) {
        Rol rol = getEntity(id);
        rol.setNombre(request.getNombre());
        return RolResponse.fromEntity(repository.save(rol));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private Rol getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Rol", id));
    }
}
