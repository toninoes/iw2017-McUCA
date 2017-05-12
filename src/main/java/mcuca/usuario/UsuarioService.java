package mcuca.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository repo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Usuario loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario user = repo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}

	public Usuario save(Usuario user) {
		user.setPassword(passwordEncoder.encode(user.getPassword() != null ? user.getPassword() : "default"));
		return repo.save(user);
	}

	public List<Usuario> findByApellidosStartsWithIgnoreCase(String lastName) {
		return repo.findByApellidosStartsWithIgnoreCase(lastName);
	}

	public Usuario findOne(Long arg0) {
		return repo.findOne(arg0);
	}

	public void delete(Usuario arg0) {
		repo.delete(arg0);
	}

	public List<Usuario> findAll() {
		return repo.findAll();
	}

}