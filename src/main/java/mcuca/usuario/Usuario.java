package mcuca.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import mcuca.establecimiento.Establecimiento;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
public class Usuario implements UserDetails {

	private static final long serialVersionUID = -7245278202840772378L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String dni;

	private String firstName;

	private String lastName;

	private String username;

	private String password;
	
	private Rol rol;
	
	@ManyToMany(targetEntity=Establecimiento.class)
	@JoinTable(name = "establecimiento_usuario", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "establecimiento_id", referencedColumnName = "id"))
	private Set<Establecimiento> establecimientos;

	public Long getId() { return id; }
	public String getDni() { return dni; }
	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	@Override
	public String getPassword() { return this.password; }
	@Override
	public String getUsername() { return this.username; }
	public Rol getRol() { return rol; }
	public Set<Establecimiento> getEstablecimientos() { return establecimientos; }

	public void setDni(String dni) { this.dni = dni; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public void setUsername(String username) { this.username= username; }
	public void setPassword(String password) { this.password = password; }
	public void setRol(Rol rol) { this.rol = rol; }
	public void setEst(Set<Establecimiento> establecimientos) { this.establecimientos = establecimientos; }

	public Usuario() {}
	
	public Usuario(String dni, String firstName, String lastName, String username)
	{
		this.dni = dni;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
	}
	
	public Usuario(String dni, String firstName, String lastName) {
		this(dni, firstName,lastName,firstName);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list=new ArrayList<GrantedAuthority>();
		list.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
		return list;
	}

	@Override
	public boolean isAccountNonExpired() { return true; }
	
	@Override
	public boolean isAccountNonLocked() { return true; }
	
	@Override
	public boolean isCredentialsNonExpired() { return true; }
	
	@Override
	public boolean isEnabled() { return true; }

	@Override
	public String toString() {
		return String.format("User[id=%d, firstName='%s', lastName='%s', username='%s', password='%s']", id,
				firstName, lastName,username,password);
	}

}
