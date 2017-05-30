package mcuca.pedido;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mcuca.cliente.Cliente;
import mcuca.establecimiento.Establecimiento;
import mcuca.zona.Zona;

import java.util.Date;
import java.util.List;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {
	
	@Query("Select p from Pedido p where p.zona = :zona ")
	List<Pedido> findByZona(@Param("zona")Zona zona);
	//List<Pedido> findByZona(String zona);
	List<Pedido> findByTipo(Tipo tipo);
	
	@Query("SELECT p from Pedido p where p.cliente = :cliente")
	List<Pedido> findByCliente(@Param("cliente") Cliente cliente);
	
	@Query("Select p from Pedido p where p.fecha > :fecha and p.usuario in (Select u from Usuario u where u.establecimiento = :establecimiento)")
	List<Pedido> findByEstablecimiento(@Param("establecimiento")Establecimiento establecimiento, @Param("fecha") Date date);
}
