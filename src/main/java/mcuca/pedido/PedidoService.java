package mcuca.pedido;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class PedidoService {
	
	
	private final PedidoRepository pedidos;
	@Autowired
	public PedidoService(PedidoRepository ped) { this.pedidos = ped; }
	
	public float getRecaudacion()
	{
		List<Pedido> recaudacion = pedidos.findByCierre();
		float cantidad = 0;
		for(Pedido pedido : recaudacion)
			cantidad += pedido.getPrecio();
		return cantidad;
	}

}
