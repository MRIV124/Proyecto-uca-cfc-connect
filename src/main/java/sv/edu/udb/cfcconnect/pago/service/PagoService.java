package sv.edu.udb.cfcconnect.pago.service;

import sv.edu.udb.cfcconnect.pago.domain.Pago;
import java.util.List;
import sv.edu.udb.cfcconnect.pago.dto.PagoRequest;
import sv.edu.udb.cfcconnect.pago.dto.PagoResponse;

public interface PagoService {
    List<PagoResponse> findAll();
    PagoResponse findById(Long id);
    PagoResponse create(PagoRequest request);
    PagoResponse update(Long id, PagoRequest request);
    void delete(Long id);
    PagoResponse confirmar(Long id);

    /** Filtra por cliente y/o estado (ambos opcionales; sin filtros devuelve todo). */
    List<PagoResponse> buscar(Long clienteId, Pago.Estado estado);
}
