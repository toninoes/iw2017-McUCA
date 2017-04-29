package mcuca;

import org.springframework.data.repository.CrudRepository;

import mcuca.Cliente;

import java.util.List;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {

	List<Cliente> findByApellidosStartsWithIgnoreCase(String apellidos);
}
