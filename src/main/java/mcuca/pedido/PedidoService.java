package mcuca.pedido;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.ui.Notification;

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
			List<LineaPedido> lineasPedido = lps.findByPedido(pedido);
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
				List<LineaPedido> lineasPedido = lps.findByPedido(pedido);
				if(lineasPedido != null) {
					for(LineaPedido lp : lineasPedido)
						lps.delete(lp);
				}
				pedidos.delete(pedido);
			}
		}
	}
	
	public void deletePedido(Pedido p)
	{
		if(p != null)
		{
			List<LineaPedido> lineasPedido = lps.findByPedido(p);
			if(lineasPedido != null) {
				for(LineaPedido lp : lineasPedido)
					lps.delete(lp);
			}
			pedidos.delete(p);
		}
	}
	
	public void mandarComanda(Pedido p, List<LineaPedido> lp)
	{	
		try {
			Document doc = new Document();
			FileOutputStream fichero = new FileOutputStream("comanda.pdf");
			
			PdfWriter.getInstance(doc, fichero).setInitialLeading(40);
			doc.open();
			doc.add(new Paragraph("McUCA - Comanda"));
			doc.add(new Paragraph("Tipo de pedido: " + p.getTipo().toString()));
			
			if(p.getTipo().toString() == "ESTABLECIMIENTO")
			{
				doc.add(new Paragraph("Zona: " + p.getZona().toString()));
				doc.add(new Paragraph("Mesa: " + p.getMesa().toString()));
			}
				
			PdfPTable tabla = new PdfPTable(2);
			tabla.addCell("Producto"); tabla.addCell("Cantidad");
			for(LineaPedido l : lp)
			{
				if(!l.isEnCocina())
				{
					if(l.getProducto() != null){
						tabla.addCell(l.getProducto().toString()); tabla.addCell("" + l.getCantidad());}
						else {
							tabla.addCell(l.getMenu().toString()); tabla.addCell("" + l.getCantidad());}

				}
			}
			doc.add(tabla);
			doc.close();
			Notification.show("Comanda enviada a cocina.");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show("Se produjo un error enviando la comanda.");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show("Se produjo un error enviando la comanda.");
		}
	}
	
	public void cerrarPedido(Pedido p, List<LineaPedido> lp)
	{	
		try {
			Document doc = new Document();
			FileOutputStream fichero = new FileOutputStream("ticket.pdf");
			
			PdfWriter.getInstance(doc, fichero).setInitialLeading(40);
			doc.open();
			doc.add(new Paragraph("McUCA - Cuenta"));
			doc.add(new Paragraph("Le atendió: " + p.getUsuario().getNombre()));
			doc.add(new Paragraph("Tipo de pedido: " + p.getTipo().toString()));
			
			if(p.getTipo().toString() == "ESTABLECIMIENTO")
			{
				doc.add(new Paragraph("Zona: " + p.getZona().toString()));
				doc.add(new Paragraph("Mesa: " + p.getMesa().toString()));
			}
				
			PdfPTable tabla = new PdfPTable(3);
			tabla.addCell("Producto"); tabla.addCell("Cantidad"); tabla.addCell("Precio");
			for(LineaPedido l : lp)
			{
				if(l.getProducto() != null){
					tabla.addCell(l.getProducto().toString()); tabla.addCell("" + l.getCantidad()); tabla.addCell("" + l.getProducto().getPrecio());
				}
				else {
					tabla.addCell(l.getMenu().toString()); tabla.addCell("" + l.getCantidad()); tabla.addCell("" + l.getMenu().getPrecio());
				}
			}
			tabla.addCell("TOTAL A PAGAR:"); tabla.addCell(""); tabla.addCell("" + p.getPrecio());
			doc.add(tabla);
			doc.close();
			Notification.show("Ticket generado.");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show("Se produjo un error enviando la comanda.");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show("Se produjo un error enviando la comanda.");
		}
	}
}
