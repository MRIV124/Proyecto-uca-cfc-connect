package sv.edu.udb.cfcconnect.diplomado.service;

import java.util.List;
import sv.edu.udb.cfcconnect.diplomado.dto.DiplomadoRequest;
import sv.edu.udb.cfcconnect.diplomado.dto.DiplomadoResponse;

public interface DiplomadoService {
    List<DiplomadoResponse> findAll();
    DiplomadoResponse findById(Long id);
    DiplomadoResponse create(DiplomadoRequest request);
    DiplomadoResponse update(Long id, DiplomadoRequest request);
    void delete(Long id);
    DiplomadoResponse inscribirParticipante(Long id);
}
