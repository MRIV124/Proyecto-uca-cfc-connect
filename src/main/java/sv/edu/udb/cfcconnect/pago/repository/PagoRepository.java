package sv.edu.udb.cfcconnect.pago.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.cfcconnect.pago.domain.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByClienteId(Long clienteId);
    List<Pago> findByEstado(Pago.Estado estado);
}
