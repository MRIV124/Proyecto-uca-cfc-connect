package sv.edu.udb.cfcconnect.cliente.service;

import java.util.List;
import sv.edu.udb.cfcconnect.cliente.dto.ClienteRequest;
import sv.edu.udb.cfcconnect.cliente.dto.ClienteResponse;

public interface ClienteService {
    List<ClienteResponse> findAll();
    ClienteResponse findById(Long id);
    ClienteResponse create(ClienteRequest request);
    ClienteResponse update(Long id, ClienteRequest request);
    void delete(Long id);
}
