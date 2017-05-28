package mcuca.pedido;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface LineaPedidoRepository extends CrudRepository<LineaPedido, Long> {
	List<LineaPedido> findById(Long id);
	
	@Query("SELECT l from LineaPedido l where l.pedido = :pedido")
	List<LineaPedido> findByPedido(@Param("pedido") Pedido pedido);
}
