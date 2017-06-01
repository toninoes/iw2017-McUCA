package mcuca.pedido;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mcuca.producto.Producto;

public interface LineaPedidoRepository extends CrudRepository<LineaPedido, Long> {
	List<LineaPedido> findById(Long id);
	
	@Query("SELECT l from LineaPedido l where l.pedido = :pedido")
	List<LineaPedido> findByPedido(@Param("pedido") Pedido pedido);
	
	@Query("SELECT l from LineaPedido l where l.producto = :producto")
	List<LineaPedido> findByProducto(@Param("producto") Producto producto);
	
	@Query("SELECT l from LineaPedido l where l.menu = :menu")
	List<LineaPedido> findByMenu(@Param("menu") mcuca.menu.Menu menu);
}
