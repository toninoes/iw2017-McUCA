package mcuca.producto;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import mcuca.ingrediente.Ingrediente;


@Entity
public class Producto {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length = 32)
	private String nombre;
	
	@Column(length = 5)
	private Double precio;
	
	@Column(length = 5)
	private Double iva;
	
	@Column(length = 255)
	private String foto; //la url
	
	//@ManyToMany(targetEntity=Pedido.class, fetch=FetchType.EAGER)
	//@JoinTable(name = "pedido_producto", joinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "pedido_id", referencedColumnName = "id"))
	//private Set<Pedido> pedidos;
	
	@ManyToMany(targetEntity=Ingrediente.class, fetch=FetchType.EAGER)
	@JoinTable(name = "producto_ingredientes", joinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "ingrediente_id", referencedColumnName = "id"))
	private Set<Ingrediente> ingredientes;
	
	protected Producto() {
	}
	
	public Producto(String nombre, Double precio, Double iva, String foto, Set<Ingrediente> ingredientes) {
		this.nombre = nombre;
		this.precio = precio;
		this.iva = iva;
		this.foto = foto;
		this.ingredientes = ingredientes;
	}

	public Long getId() {
		return id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public Set<Ingrediente> getIngredientes() {
		return ingredientes;
	}
	   
	public void setIngredientes(Set<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}
	
	@Override
	public String toString() {
		return String.format("%s", nombre);
	}
}