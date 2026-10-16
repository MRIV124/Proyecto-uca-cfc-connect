package sv.edu.udb.cfcconnect.cotizacion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.cotizacion.domain.Cotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    List<Cotizacion> findByClienteId(Long clienteId);

    List<Cotizacion> findByEstado(Cotizacion.Estado estado);
    List<Cotizacion> findByClienteIdAndEstado(Long clienteId, Cotizacion.Estado estado);
}
