package sv.edu.udb.cfcconnect.catering.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.catering.domain.ServicioCatering;
import sv.edu.udb.cfcconnect.catering.dto.ServicioCateringRequest;
import sv.edu.udb.cfcconnect.catering.dto.ServicioCateringResponse;
import sv.edu.udb.cfcconnect.catering.repository.ServicioCateringRepository;
import sv.edu.udb.cfcconnect.catering.service.ServicioCateringService;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

@Service
public class ServicioCateringServiceImpl implements ServicioCateringService {

    private final ServicioCateringRepository repository;

    public ServicioCateringServiceImpl(ServicioCateringRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ServicioCateringResponse> findAll() {
        return repository.findAll().stream().map(ServicioCateringResponse::fromEntity).toList();
    }

    @Override
    public ServicioCateringResponse findById(Long id) {
        return ServicioCateringResponse.fromEntity(getEntity(id));
    }

    @Override
    public ServicioCateringResponse create(ServicioCateringRequest request) {
        ServicioCatering servicio = new ServicioCatering();
        servicio.setNombre(request.getNombre());
        servicio.setPrecioPorPersona(request.getPrecioPorPersona());
        return ServicioCateringResponse.fromEntity(repository.save(servicio));
    }

    @Override
    public ServicioCateringResponse update(Long id, ServicioCateringRequest request) {
        ServicioCatering servicio = getEntity(id);
        servicio.setNombre(request.getNombre());
        servicio.setPrecioPorPersona(request.getPrecioPorPersona());
        return ServicioCateringResponse.fromEntity(repository.save(servicio));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private ServicioCatering getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ServicioCatering", id));
    }
}
