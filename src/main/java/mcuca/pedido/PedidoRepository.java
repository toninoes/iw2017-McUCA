package mcuca.pedido;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mcuca.zona.Zona;

import java.util.Date;
import java.util.List;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {
	
	@Query("Select p from Pedido p where p.zona = :zona ")
	List<Pedido> findByZona(@Param("zona")Zona zona);
	//List<Pedido> findByZona(String zona);
	List<Pedido> findById(Long id);
	List<Pedido> findByTipo(Tipo tipo);
	
	@Query("Select p from Pedido p where p.fecha between :date and current_date")
	List<Pedido> findByCierre(@Param("date") Date date);
}
