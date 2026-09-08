package sv.edu.udb.cfcconnect.catering.service;

import java.util.List;
import sv.edu.udb.cfcconnect.catering.dto.ServicioCateringRequest;
import sv.edu.udb.cfcconnect.catering.dto.ServicioCateringResponse;

public interface ServicioCateringService {
    List<ServicioCateringResponse> findAll();
    ServicioCateringResponse findById(Long id);
    ServicioCateringResponse create(ServicioCateringRequest request);
    ServicioCateringResponse update(Long id, ServicioCateringRequest request);
    void delete(Long id);
}
