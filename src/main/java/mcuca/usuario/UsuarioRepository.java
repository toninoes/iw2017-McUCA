package mcuca.usuario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public List<Usuario> findByApellidosStartsWithIgnoreCase(String apellidos);

	public Usuario findByUsername(String username);
}

