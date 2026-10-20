package sv.edu.udb.cfcconnect.cotizacion.service;

import sv.edu.udb.cfcconnect.cotizacion.domain.Cotizacion;
import java.util.List;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionRequest;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionResponse;

public interface CotizacionService {
    List<CotizacionResponse> findAll();
    CotizacionResponse findById(Long id);
    CotizacionResponse create(CotizacionRequest request);
    CotizacionResponse update(Long id, CotizacionRequest request);
    void delete(Long id);
    CotizacionResponse aprobar(Long id);
    CotizacionResponse rechazar(Long id);

    /** Filtra por cliente y/o estado (ambos opcionales; sin filtros devuelve todo). */
    List<CotizacionResponse> buscar(Long clienteId, Cotizacion.Estado estado);
}
