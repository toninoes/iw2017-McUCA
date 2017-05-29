package mcuca.pedido;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import mcuca.menu.Menu;
import mcuca.producto.Producto;

@Entity
public class LineaPedido {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private Pedido pedido;
	
	@ManyToOne
	private Producto producto;
	
	private int cantidad;
	
	@ManyToOne
	private Menu menu;
	
	private boolean enCocina;
	
	//private Long pedidoId;
	
	protected LineaPedido() {}
	
	public LineaPedido(int cantidad, Producto producto, Pedido pedido, boolean enCocina, Menu menu) {
		this.cantidad = cantidad;
		this.producto = producto;
		this.pedido = pedido;
		this.enCocina = enCocina;
		this.menu = menu;
	}
	
	public Long getId() { return this.id; }
	public Pedido getPedido() { return this.pedido; }
	public Producto getProducto() { return this.producto; }
	public int getCantidad() { return this.cantidad; }
	public boolean isEnCocina() { return this.enCocina; }
	public Menu getMenu(){return this.menu;}

	//public Long getPedidoId() { return this.pedidoId; }
	
	public void setPedido(Pedido pedido) { this.pedido = pedido; }
	public void setProducto(Producto producto) { this.producto = producto; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
	public void setEnCocina(boolean enCocina) { this.enCocina = enCocina; }
	public void setMenu(Menu menu){this.menu = menu;}

	//public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
	
	@Override
	public String toString() 
	{ 
		return String.format(
				"Pedido %d, Producto %s, Cantidad %d, en cocina: %b, Menu %s",
				/*this.pedido.getId(),*/ this.producto.getNombre(), this.cantidad, this.enCocina, this.menu.getNombre()
		); 
	}

}
