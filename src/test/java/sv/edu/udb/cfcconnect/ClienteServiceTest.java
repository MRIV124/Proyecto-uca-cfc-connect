package sv.edu.udb.cfcconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sv.edu.udb.cfcconnect.cliente.dto.ClienteResponse;
import sv.edu.udb.cfcconnect.cliente.service.ClienteService;

@SpringBootTest
public class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Test
    void shouldReturnClienteSeedadoPorId() {
        ClienteResponse cliente = clienteService.findById(1L);

        assertEquals("Juan Perez", cliente.getNombre());
        assertEquals("juan@gmail.com", cliente.getCorreo());
    }

    @Test
    void shouldListarTodosLosClientes() {
        var clientes = clienteService.findAll();

        assertFalse(clientes.isEmpty());
        assertEquals(2, clientes.size());
    }
}