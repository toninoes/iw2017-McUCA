package mcuca.establecimiento;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import mcuca.usuario.Usuario;

@Entity
public class Establecimiento {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String nombre;
	
	private String domicilio;
	
	@ManyToMany(targetEntity=Usuario.class)
	@JoinTable(name = "establecimiento_usuario", joinColumns = @JoinColumn(name = "establecimiento_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"))
	private Set<Usuario> usuarios;
	
	public Establecimiento() {
	}
	
	public Establecimiento(String nombre) {
		this.nombre = nombre;
	}

	public Establecimiento(String nombre, String domicilio, Set<Usuario> usuarios) {
		this.nombre = nombre;
		this.domicilio = domicilio;
		this.usuarios = usuarios;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
        this.id = id;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	
	
	public Set<Usuario> getUsuarios() {
		return usuarios;
	}
	   
	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
	@Override
	public String toString() {
		return String.format("%s", nombre);
	}
}
