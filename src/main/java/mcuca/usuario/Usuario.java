package mcuca.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import mcuca.establecimiento.Establecimiento;

@SuppressWarnings("serial")
@Entity
public class Usuario implements UserDetails{
	@Id
	@GeneratedValue
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

	protected Usuario() {
	}
	
	public Usuario(String dni, String nombre, String apellidos, String username, Rol rol) {
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.username = username;
		this.rol = rol;
		this.setPassword(dni);
	}
	
	public Usuario(String dni, String nombre, String apellidos, String username, Rol rol, Set<Establecimiento> establecimientos) {
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.username = username;
		this.rol = rol;
		this.establecimientos = establecimientos;
	}

	public Usuario(String nombre, String apellidos, String username) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.username = username;
	}

	public Usuario(String nombre, String apellidos) {
		this(nombre, apellidos, nombre);
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

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public void setUsername(String username) {
		this.username= username;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return String.format("Usuario[id=%d, nombre='%s', apellidos='%s', username='%s', password='%s']", id,
				nombre, apellidos, username, password);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list=new ArrayList<GrantedAuthority>();
		//list.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
		if (this.rol.getId() == 0) 
			list.add(new SimpleGrantedAuthority("GERENTE"));
		else if (this.rol.getId() == 1)
			list.add(new SimpleGrantedAuthority("ENCARGADO"));
		else if (this.rol.getId() == 2)
			list.add(new SimpleGrantedAuthority("CAMARERO"));
		
		return list;

	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
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
	   
	public void setEstablecimientos(Set<Establecimiento> establecimientos) {
		this.establecimientos = establecimientos;
	}

}