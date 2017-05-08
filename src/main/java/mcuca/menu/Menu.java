package mcuca.menu;

import java.util.ArrayList;

import javax.persistence.Entity;

import mcuca.producto.Producto;

@Entity
public class Menu extends Producto {
	
	private String descripcion;
	
	private Boolean esOferta;
	
	private Float descuento;
	
	private ArrayList<Producto> productos;
	
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
	public ArrayList<Producto> getProductos() {
		return productos;
	}
	public void setProductos(ArrayList<Producto> productos) {
		this.productos = productos;
	}
	
	@Override
	public String toString() {
		return String.format("%s", getNombre());
	}
}
