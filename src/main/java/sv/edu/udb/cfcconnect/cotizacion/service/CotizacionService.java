package sv.edu.udb.cfcconnect.cotizacion.service;

import java.util.List;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionRequest;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionResponse;

public interface CotizacionService {
    List<CotizacionResponse> findAll();
    CotizacionResponse findById(Long id);
    CotizacionResponse create(CotizacionRequest request);
    void delete(Long id);
    CotizacionResponse aprobar(Long id);
    CotizacionResponse rechazar(Long id);
}
