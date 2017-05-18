package mcuca.producto;

import java.util.ArrayList;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import mcuca.pedido.Pedido;
import mcuca.ingrediente.Ingrediente;


@Entity
public class Producto {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String nombre;
	
	private Float precio;
	
	private Float iva;
	
	private String foto; //la url
	
	@ManyToMany(targetEntity=Pedido.class, fetch=FetchType.EAGER)
	@JoinTable(name = "pedido_producto", joinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "pedido_id", referencedColumnName = "id"))
	private Set<Pedido> pedidos;
	
	@ManyToMany(targetEntity=Ingrediente.class, fetch=FetchType.EAGER)
	@JoinTable(name = "producto_ingredientes", joinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "ingrediente_id", referencedColumnName = "id"))
	private ArrayList<Ingrediente> ingredientes;
	
	protected Producto() {
	}
	
	public Producto(String nombre, Float precio, Float iva) {
		this.nombre = nombre;
		this.precio = precio;
		this.iva = iva;
	}

	public Producto(String nombre, Float precio, Float iva, Set<Pedido> pedidos) {
		this.nombre = nombre;
		this.precio = precio;
		this.iva = iva;
		this.pedidos = pedidos;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(Float precio) {
		this.precio = precio;
	}

	public Float getIva() {
		return iva;
	}

	public void setIva(Float iva) {
		this.iva = iva;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public Set<Pedido> getPedidos() {
		return pedidos;
	}
	   
	public void setPedidos(Set<Pedido> pedidos) {
		this.pedidos = pedidos;
	}
	
	@Override
	public String toString() {
		return String.format("%s", nombre);
	}

	public ArrayList<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(ArrayList<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}
	
}
