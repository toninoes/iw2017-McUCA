package mcuca.menu;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import mcuca.producto.Producto;

@Entity
public class Menu{
	
	private String descripcion;
	
	private Boolean esOferta;
	
	private float descuento;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String nombre;
	
	private float precio;
	
	private float iva;
	
	private String foto;
	
	@ManyToMany(targetEntity=Producto.class, fetch=FetchType.EAGER)
	//@JoinTable(name = "menu_producto", joinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"))
	private Set<Producto> productos;	
	
	@ManyToMany(targetEntity=Menu.class, fetch=FetchType.EAGER)
	@JoinTable(name = "oferta_menu", joinColumns = @JoinColumn(name = "oferta_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id"))
	private Set<Menu> menus;
	
	protected Menu() {
	}

	public Menu(String nombre, float precio, float iva, Boolean esOferta, float descuento, String descripcion, String foto ) {
		this.nombre = nombre;
		this.precio = precio;
		this.iva = iva;
		this.descripcion = descripcion;
		this.foto = foto;
		this.descuento = descuento;
		this.esOferta = esOferta;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public float getIva() {
		return iva;
	}

	public void setIva(float iva) {
		this.iva = iva;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
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
	public float getDescuento() {
		return descuento;
	}
	public void setDescuento(float descuento) {
		this.descuento = descuento;
	}
	public Set<Producto> getProductos() {
		return productos;
	}
	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}
	
	public Long getId() {
		return id;
	}

	
	@Override
	public String toString() {
		return String.format("%s", getNombre());
	}
}
