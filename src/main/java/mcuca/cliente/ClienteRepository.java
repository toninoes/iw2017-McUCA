package mcuca.cliente;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {

	List<Cliente> findByApellidosStartsWithIgnoreCase(String apellidos);
	@Query("Select c from Cliente c where c.telefono like %:telefono%")
	List<Cliente> findByTelefono(@Param("telefono") String telefono);
}
