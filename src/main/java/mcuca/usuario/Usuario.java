package mcuca.usuario;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import mcuca.establecimiento.Establecimiento;

@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String dni;
	
	private String nombre;
	
	private String apellidos;
	
	private String username;
	
	private String password;
	
	private Rol rol;
	
	@ManyToMany(targetEntity=Establecimiento.class)
	@JoinTable(name = "establecimiento_usuario", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "establecimiento_id", referencedColumnName = "id"))
	private Set<Establecimiento> establecimientos;
	
	public Usuario() {	
	}
	
	public Usuario(String dni) {	
		this.dni = dni;
	}


	public Usuario(String dni, String nombre, String apellidos, String username, String password, Rol rol, Set<Establecimiento> establecimientos) {
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.username = username;
		this.password = password;
		this.rol = rol;
		this.establecimientos = establecimientos;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
        this.id = id;
    }

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}
	

	public Set<Establecimiento> getEstablecimientos() {
		return establecimientos;
	}
	   
	public void setEst(Set<Establecimiento> establecimientos) {
		this.establecimientos = establecimientos;
	}
	
	@Override
	public String toString() {
		return String.format("%s", username);
	}
	
}

