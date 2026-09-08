package sv.edu.udb.cfcconnect.pago.service.implementation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.cliente.domain.Cliente;
import sv.edu.udb.cfcconnect.cliente.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;
import sv.edu.udb.cfcconnect.pago.domain.Pago;
import sv.edu.udb.cfcconnect.pago.dto.PagoRequest;
import sv.edu.udb.cfcconnect.pago.dto.PagoResponse;
import sv.edu.udb.cfcconnect.pago.repository.PagoRepository;
import sv.edu.udb.cfcconnect.pago.service.PagoService;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository repository;
    private final ClienteRepository clienteRepository;

    public PagoServiceImpl(PagoRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<PagoResponse> findAll() {
        return repository.findAll().stream().map(PagoResponse::fromEntity).toList();
    }

    @Override
    public PagoResponse findById(Long id) {
        return PagoResponse.fromEntity(getEntity(id));
    }

    @Override
    public PagoResponse create(PagoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Cliente", request.getClienteId()));

        Pago pago = new Pago();
        pago.setCliente(cliente);

        try {
            pago.setTipoReferencia(Pago.TipoReferencia.valueOf(request.getTipoReferencia().toUpperCase()));
            pago.setMetodo(Pago.Metodo.valueOf(request.getMetodo().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "tipoReferencia debe ser INSCRIPCION/COTIZACION/ESPACIO/CATERING y "
                            + "metodo debe ser EFECTIVO/TARJETA/TRANSFERENCIA/DEPOSITO");
        }

        pago.setReferenciaId(request.getReferenciaId());
        pago.setMonto(request.getMonto());
        pago.setEstado(Pago.Estado.PENDIENTE);
        pago.setFecha(LocalDate.now());

        return PagoResponse.fromEntity(repository.save(pago));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    @Override
    public PagoResponse confirmar(Long id) {
        Pago pago = getEntity(id);
        pago.setEstado(Pago.Estado.PAGADO);
        return PagoResponse.fromEntity(repository.save(pago));
    }

    private Pago getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Pago", id));
    }
}
