package mcuca.pedido;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import mcuca.cliente.Cliente;
import mcuca.producto.Producto;
import mcuca.usuario.Usuario;
import mcuca.zona.Zona;


@Entity
public class Pedido {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String nombre;
	
	private Float precio;
	
	private Boolean abierto;
	
	private Tipo tipo;
	
	private Date hora;
	
	@ManyToOne
	private Zona zona;
	
	@ManyToOne
	private Cliente cliente;
	
	@ManyToOne
	private Usuario usuario;

	@ManyToMany(targetEntity=Producto.class)
	@JoinTable(name = "pedido_producto", joinColumns = @JoinColumn(name = "pedido_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "producto_id", referencedColumnName = "id"))
	private Set<Producto> productos;
	
	protected Pedido() {
	}

	public Pedido(String nombre, Float precio, Tipo tipo, Set<Producto> productos) {
		this.nombre = nombre;
		this.precio = precio;
		this.tipo = tipo;
		this.productos = productos;
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

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(Float precio) {
		this.precio = precio;
	}

	public Boolean getAbierto() {
		return abierto;
	}

	public void setAbierto(Boolean abierto) {
		this.abierto = abierto;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}
	
	public Set<Producto> getProductos() {
		return productos;
	}
	   
	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public String toString() {
		return String.format("%s", nombre);
	}
}
