package sv.edu.udb.cfcconnect.service.implementation;

import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.repository.domain.Cliente;
import sv.edu.udb.cfcconnect.service.ClienteService;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Cliente> findAll() {
        return repository.findAll();
    }

    @Override
    public Cliente findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Cliente save(Cliente cliente) {
        return repository.save(cliente);
    }

    @Override
    public Cliente update(Long id, Cliente cliente) {
        return repository.update(id, cliente);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
