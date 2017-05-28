package mcuca.pedido;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import mcuca.cierre.CierreCaja;
import mcuca.cierre.CierreCajaRepository;
import mcuca.cliente.Cliente;
import mcuca.zona.Zona;

public class PedidoService {
	
	@Autowired
	private final CierreCajaRepository cierres;
	private final LineaPedidoRepository lps;
	private final PedidoRepository pedidos;
	@Autowired
	public PedidoService(PedidoRepository ped, CierreCajaRepository cierresCaja, LineaPedidoRepository lp) 
	{ 
		this.lps = lp;
		this.pedidos = ped; 
		this.cierres = cierresCaja;
	}
	
	public float getRecaudacion()
	{
		if(cierres.findLast().isEmpty())
			return 0.0f;
		else
		{
			CierreCaja cierre = cierres.findLast().get(0);
			List<Pedido> recaudacion = pedidos.findByCierre(cierre.getFechaCierre());
			float cantidad = 0;
			for(Pedido pedido : recaudacion)
				cantidad += pedido.getPrecio();
			return cantidad;
		}
	}
	
	public void deletePedidosbyZona(Zona zona)
	{
		List<Pedido> pedidosZona = pedidos.findByZona(zona);
		for(Pedido pedido : pedidosZona)
		{
			Set<LineaPedido> lineasPedido = pedido.getLineasPedido();
			for(LineaPedido lp : lineasPedido)
				lps.delete(lp);
			pedidos.delete(pedido);
		}
	}
	
	public void deletePedidosByCliente(Cliente cliente)
	{
		List<Pedido> pedidosZona = pedidos.findByCliente(cliente);
		if(pedidosZona != null)
		{
			for(Pedido pedido : pedidosZona)
			{
				Set<LineaPedido> lineasPedido = pedido.getLineasPedido();
				if(lineasPedido != null) {
					for(LineaPedido lp : lineasPedido)
						lps.delete(lp);
				}
				pedidos.delete(pedido);
			}
		}
	}
}
