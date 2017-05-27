package mcuca.pedido;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import mcuca.cierre.CierreCaja;
import mcuca.cierre.CierreCajaRepository;

public class PedidoService {
	
	@Autowired
	private final CierreCajaRepository cierres;
	
	private final PedidoRepository pedidos;
	@Autowired
	public PedidoService(PedidoRepository ped, CierreCajaRepository cierresCaja) 
	{ 
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

}
