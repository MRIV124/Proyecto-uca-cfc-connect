package sv.edu.udb.cfcconnect.cliente.service.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;
import sv.edu.udb.cfcconnect.cliente.dto.ClienteRequest;
import sv.edu.udb.cfcconnect.cliente.dto.ClienteResponse;
import sv.edu.udb.cfcconnect.cliente.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.cliente.service.ClienteService;
import sv.edu.udb.cfcconnect.exception.ReglaNegocioException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ClienteResponse> findAll() {
        return repository.findAll().stream().map(ClienteResponse::fromEntity).toList();
    }

    @Override
    public ClienteResponse findById(Long id) {
        // Antes: repository.findById(id).orElseThrow(NoSuchElementException::new)
        // Ahora: excepcion propia -> el GlobalExceptionHandler la convierte en HTTP 404
        //        con un mensaje claro, en vez de un 500 generico sin explicacion.
        return ClienteResponse.fromEntity(getEntity(id));
    }

    @Override
    public ClienteResponse create(ClienteRequest request) {
        // Regla de negocio: no permitir dos clientes con el mismo correo.
        // Se lanza ReglaNegocioException -> HTTP 409 Conflict.
        repository.findByCorreoIgnoreCase(request.getCorreo()).ifPresent(c -> {
            throw new ReglaNegocioException("Ya existe un cliente registrado con el correo " + request.getCorreo());
        });

        Cliente cliente = new Cliente();
        aplicarDatos(cliente, request);
        return ClienteResponse.fromEntity(repository.save(cliente));
    }

    @Override
    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente cliente = getEntity(id);

        repository.findByCorreoIgnoreCase(request.getCorreo())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new ReglaNegocioException("Ya existe otro cliente con el correo " + request.getCorreo());
                });

        aplicarDatos(cliente, request);
        return ClienteResponse.fromEntity(repository.save(cliente));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private void aplicarDatos(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.getNombre());
        cliente.setCorreo(request.getCorreo());
        cliente.setTelefono(request.getTelefono());
    }

    private Cliente getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", id));
    }
}
