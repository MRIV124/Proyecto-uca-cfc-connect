package sv.edu.udb.cfcconnect.espacio.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.espacio.domain.Espacio;
import sv.edu.udb.cfcconnect.espacio.dto.EspacioRequest;
import sv.edu.udb.cfcconnect.espacio.dto.EspacioResponse;
import sv.edu.udb.cfcconnect.espacio.repository.EspacioRepository;
import sv.edu.udb.cfcconnect.espacio.service.EspacioService;
import sv.edu.udb.cfcconnect.exception.EspacioOcupadoException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

@Service
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository repository;

    public EspacioServiceImpl(EspacioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EspacioResponse> findAll() {
        return repository.findAll().stream().map(EspacioResponse::fromEntity).toList();
    }

    @Override
    public List<EspacioResponse> findDisponibles() {
        return repository.findByDisponibleTrue().stream().map(EspacioResponse::fromEntity).toList();
    }

    @Override
    public EspacioResponse findById(Long id) {
        return EspacioResponse.fromEntity(getEntity(id));
    }

    @Override
    public EspacioResponse create(EspacioRequest request) {
        Espacio espacio = new Espacio();
        aplicarDatos(espacio, request);
        return EspacioResponse.fromEntity(repository.save(espacio));
    }

    @Override
    public EspacioResponse update(Long id, EspacioRequest request) {
        Espacio espacio = getEntity(id);
        aplicarDatos(espacio, request);
        return EspacioResponse.fromEntity(repository.save(espacio));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    @Override
    public EspacioResponse reservar(Long id) {
        Espacio espacio = getEntity(id);
        // Caso de fallo por solapamiento: el espacio ya esta ocupado en este momento.
        if (!Boolean.TRUE.equals(espacio.getDisponible())) {
            throw new EspacioOcupadoException(
                    "El espacio '" + espacio.getNombre() + "' ya se encuentra ocupado/reservado.");
        }
        // Caso de exito: se marca como no disponible.
        espacio.setDisponible(false);
        return EspacioResponse.fromEntity(repository.save(espacio));
    }

    @Override
    public EspacioResponse liberar(Long id) {
        Espacio espacio = getEntity(id);
        espacio.setDisponible(true);
        return EspacioResponse.fromEntity(repository.save(espacio));
    }

    private void aplicarDatos(Espacio espacio, EspacioRequest request) {
        espacio.setNombre(request.getNombre());
        espacio.setTipo(request.getTipo());
        espacio.setCapacidad(request.getCapacidad());
        espacio.setPrecio(request.getPrecio());
        espacio.setEquipamiento(request.getEquipamiento());
        espacio.setDisponible(request.getDisponible() == null || request.getDisponible());
    }

    private Espacio getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Espacio", id));
    }
}
