package sv.edu.udb.cfcconnect.cotizacion.service.implementation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;
import sv.edu.udb.cfcconnect.cliente.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.cotizacion.domain.Cotizacion;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionRequest;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionResponse;
import sv.edu.udb.cfcconnect.cotizacion.repository.CotizacionRepository;
import sv.edu.udb.cfcconnect.cotizacion.service.CotizacionService;
import sv.edu.udb.cfcconnect.exception.ReglaNegocioException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository repository;
    private final ClienteRepository clienteRepository;

    public CotizacionServiceImpl(CotizacionRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<CotizacionResponse> findAll() {
        return repository.findAll().stream().map(CotizacionResponse::fromEntity).toList();
    }

    @Override
    public CotizacionResponse findById(Long id) {
        return CotizacionResponse.fromEntity(getEntity(id));
    }

    @Override
    public CotizacionResponse create(CotizacionRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.getClienteId()));

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(cliente);
        cotizacion.setTipo(parseTipo(request.getTipo()));
        cotizacion.setDescripcion(request.getDescripcion());
        cotizacion.setTotal(request.getTotal());
        cotizacion.setEstado(Cotizacion.Estado.PENDIENTE);
        cotizacion.setFecha(LocalDate.now());

        return CotizacionResponse.fromEntity(repository.save(cotizacion));
    }

    @Override
    public CotizacionResponse update(Long id, CotizacionRequest request) {
        Cotizacion cotizacion = getEntity(id);

        // Regla de negocio: una cotizacion ya resuelta (aprobada o rechazada) no deberia
        // poder editarse; si se necesita cambiar algo, se crea una nueva cotizacion.
        if (cotizacion.getEstado() == Cotizacion.Estado.APROBADA
                || cotizacion.getEstado() == Cotizacion.Estado.RECHAZADA) {
            throw new ReglaNegocioException(
                    "No se puede editar una cotizacion que ya fue " + cotizacion.getEstado().name().toLowerCase() + ".");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.getClienteId()));

        cotizacion.setCliente(cliente);
        cotizacion.setTipo(parseTipo(request.getTipo()));
        cotizacion.setDescripcion(request.getDescripcion());
        cotizacion.setTotal(request.getTotal());
        // El estado y la fecha de creacion no se modifican en un update; para eso
        // existen los endpoints especificos /aprobar y /rechazar.

        return CotizacionResponse.fromEntity(repository.save(cotizacion));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    @Override
    public CotizacionResponse aprobar(Long id) {
        Cotizacion cotizacion = getEntity(id);
        // Regla de negocio: solo se puede aprobar una cotizacion pendiente o en proceso.
        if (cotizacion.getEstado() == Cotizacion.Estado.RECHAZADA) {
            throw new ReglaNegocioException("No se puede aprobar una cotizacion que ya fue rechazada.");
        }
        cotizacion.setEstado(Cotizacion.Estado.APROBADA);
        return CotizacionResponse.fromEntity(repository.save(cotizacion));
    }

    @Override
    public CotizacionResponse rechazar(Long id) {
        Cotizacion cotizacion = getEntity(id);
        if (cotizacion.getEstado() == Cotizacion.Estado.APROBADA) {
            throw new ReglaNegocioException("No se puede rechazar una cotizacion que ya fue aprobada.");
        }
        cotizacion.setEstado(Cotizacion.Estado.RECHAZADA);
        return CotizacionResponse.fromEntity(repository.save(cotizacion));
    }

    private Cotizacion.Tipo parseTipo(String tipo) {
        try {
            return Cotizacion.Tipo.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Tipo invalido. Valores permitidos: CURSO, DIPLOMADO, ESPACIO, CATERING, COMBINADO");
        }
    }

    private Cotizacion getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Cotizacion", id));
    }
}
