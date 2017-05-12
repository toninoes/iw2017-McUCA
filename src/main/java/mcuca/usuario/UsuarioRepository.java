package mcuca.usuario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public List<Usuario> findByLastNameStartsWithIgnoreCase(String lastName);

	public Usuario findByUsername(String username);
}

