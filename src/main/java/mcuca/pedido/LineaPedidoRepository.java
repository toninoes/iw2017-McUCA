package mcuca.pedido;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LineaPedidoRepository extends CrudRepository<LineaPedido, Long> {
	List<LineaPedido> findById(Long id);
	//List<LineaPedido> findByPedidoId(Long pedido_id);
}
