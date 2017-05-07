package mcuca.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class Usuario implements UserDetails {

	private static final long serialVersionUID = -7245278202840772378L;
	
	@Id
	@GeneratedValue
	private Long id;

	private String firstName;

	private String lastName;
	
	private String username;

	private String password;
	
	public Long getId() { return id; }
	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	@Override
	public String getPassword() { return this.password; }
	@Override
	public String getUsername() { return this.username; }

	public void setFirstName(String firstName) { this.firstName = firstName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public void setUsername(String username) { this.username= username; }
	public void setPassword(String password) { this.password = password; }
	
	public Usuario() {}
	public Usuario(String firstName, String lastName, String username)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
	}
	public Usuario(String firstName, String lastName) {
		this(firstName,lastName,firstName);
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
