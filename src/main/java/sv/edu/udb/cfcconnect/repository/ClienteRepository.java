package sv.edu.udb.cfcconnect.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;
import sv.edu.udb.cfcconnect.repository.domain.Cliente;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteRepository {

    private List<Cliente> clientes;

    @PostConstruct
    public void init() {

        clientes = new ArrayList<>();

        clientes.add(
                Cliente.builder()
                        .id(1L)
                        .nombre("Juan Pérez")
                        .correo("juan@gmail.com")
                        .telefono("7777-1111")
                        .build()
        );

        clientes.add(
                Cliente.builder()
                        .id(2L)
                        .nombre("Ana López")
                        .correo("ana@gmail.com")
                        .telefono("7777-2222")
                        .build()
        );
    }

    public List<Cliente> findAll() {
        return clientes;
    }

    public Cliente findById(Long id) {

        return clientes.stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente no encontrado con ID: " + id
                        )
                );
    }

    public Cliente save(Cliente cliente) {

        Long nuevoId = clientes.stream()
                .mapToLong(Cliente::getId)
                .max()
                .orElse(0L) + 1;

        cliente.setId(nuevoId);

        clientes.add(cliente);

        return cliente;
    }

    public Cliente update(Long id, Cliente clienteActualizado) {

        Cliente cliente = findById(id);

        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setCorreo(clienteActualizado.getCorreo());
        cliente.setTelefono(clienteActualizado.getTelefono());

        return cliente;
    }

    public void delete(Long id) {

        Cliente cliente = findById(id);

        clientes.remove(cliente);
    }
}
