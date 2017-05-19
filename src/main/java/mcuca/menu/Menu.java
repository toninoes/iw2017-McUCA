package mcuca.menu;

import java.util.ArrayList;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import mcuca.producto.Producto;

@Entity
public class Menu extends Producto {
	
	private String descripcion;
	
	private Boolean esOferta;
	
	private Float descuento;
	
	@ManyToMany(targetEntity=Producto.class, fetch=FetchType.EAGER)
	@JoinTable(name = "menu_producto", joinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"))
	private Set<Producto> productos;
	
	protected Menu() {
	}

	public Menu(String nombre, Float precio, Float iva) {
		super(nombre, precio, iva);
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Boolean getEsOferta() {
		return esOferta;
	}
	public void setEsOferta(Boolean esOferta) {
		this.esOferta = esOferta;
	}
	public Float getDescuento() {
		return descuento;
	}
	public void setDescuento(Float descuento) {
		this.descuento = descuento;
	}
	public Set<Producto> getProductos() {
		return productos;
	}
	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}
	
	@Override
	public String toString() {
		return String.format("%s", getNombre());
	}
}
