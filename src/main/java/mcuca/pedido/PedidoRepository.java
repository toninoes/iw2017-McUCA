package mcuca.pedido;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {
	
	@Query("Select p from Pedido p INNER Join p.zona c  Where c.nombre = :zona ")
	List<Pedido> findByZona(String zona);
	
}
