package sv.edu.udb.cfcconnect.espacio.service;

import java.util.List;
import sv.edu.udb.cfcconnect.espacio.dto.EspacioRequest;
import sv.edu.udb.cfcconnect.espacio.dto.EspacioResponse;

public interface EspacioService {
    List<EspacioResponse> findAll();
    List<EspacioResponse> findDisponibles();
    EspacioResponse findById(Long id);
    EspacioResponse create(EspacioRequest request);
    EspacioResponse update(Long id, EspacioRequest request);
    void delete(Long id);

    /** Reserva el espacio: caso de fallo por negocio -> EspacioOcupadoException si ya esta ocupado. */
    EspacioResponse reservar(Long id);

    /** Libera el espacio (por ejemplo, al finalizar el evento o cancelar la reserva). */
    EspacioResponse liberar(Long id);
}
