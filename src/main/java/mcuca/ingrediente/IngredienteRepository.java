package mcuca.ingrediente;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IngredienteRepository extends CrudRepository<Ingrediente, Long> {

	List<Ingrediente> findByNombreStartsWithIgnoreCase(String nombre);
}
