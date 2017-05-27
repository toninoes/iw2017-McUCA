package mcuca.pedido;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import mcuca.zona.Zona;

import java.util.List;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {
	
	@Query("Select p from Pedido p INNER Join p.zona c  Where c.nombre = :zona ")
	List<Pedido> findByZona(Zona zona);
	//List<Pedido> findByZona(String zona);
	List<Pedido> findById(Long id);
	List<Pedido> findByTipo(Tipo tipo);
}
