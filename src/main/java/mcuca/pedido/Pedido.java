package mcuca.pedido;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import mcuca.cliente.Cliente;
import mcuca.mesa.Mesa;
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
	private Date fecha;
	
	@ManyToOne
	private Zona zona;
	
	@ManyToOne
	private Mesa mesa;
	
	@ManyToOne
	private Cliente cliente;
	
	@ManyToOne
	private Usuario usuario;
		
	protected Pedido() {
	}

	public Pedido(String nombre, Float precio, Boolean abierto, Tipo tipo, Date fecha) { //Set<Producto> productos) {
		this.nombre = nombre;
		this.precio = precio;
		this.abierto = abierto;
		this.tipo = tipo;
		this.fecha = fecha;
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public Long getClienteId() {
		return cliente.getId();
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}
	
	public Zona getZona() {
		return zona;
	}
	
	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}
	
	public Mesa getMesa() {
		return mesa;
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
