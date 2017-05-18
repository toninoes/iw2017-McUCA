package mcuca.ingrediente;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface IngredienteRepository extends CrudRepository<Ingrediente, Long> {
	
	@Cacheable("ingredientes")
	List<Ingrediente> findByNombreStartsWithIgnoreCase(String nombre);
	
}
