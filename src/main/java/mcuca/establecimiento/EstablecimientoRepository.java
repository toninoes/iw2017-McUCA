package mcuca.establecimiento;

import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface EstablecimientoRepository extends CrudRepository<Establecimiento, Long> {

	List<Establecimiento> findByNombreStartsWithIgnoreCase(String nombre);
	
}
