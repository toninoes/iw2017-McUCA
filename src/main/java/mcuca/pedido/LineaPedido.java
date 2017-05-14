package mcuca.pedido;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
	
	private boolean enCocina;
	
	public LineaPedido() {}
	
	public Long getId() { return this.id; }
	public Pedido getPedido() { return this.pedido; }
	public Producto getProducto() { return this.producto; }
	public int getCantidad() { return this.cantidad; }
	public boolean isEnCocina() { return this.enCocina; }
	
	public void setPedido(Pedido pedido) { this.pedido = pedido; }
	public void setProducto(Producto producto) { this.producto = producto; }
	public void setCantidad(int cantidad) { this.cantidad = cantidad; }
	public void setEnCocina(boolean enCocina) { this.enCocina = enCocina; }
	
	@Override
	public String toString() 
	{ 
		return String.format(
				"Pedido %d, Producto %s, Cantidad %d, en cocina: %b", 
				this.pedido.getId(), this.producto.getNombre(), this.cantidad, this.enCocina
		); 
	}

}
