package mcuca.producto;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ProductoRepository extends CrudRepository<Producto, Long> {
	
	@Cacheable("productos")
	List<Producto> findByNombreStartsWithIgnoreCase(String nombre);
	
	@Cacheable("alergia")
	@Query("Select p from Producto p INNER Join p.ingredientes c  Where c.nombre != :nombre ")
	List<Producto> findByIngrediente(String nombre);


}
