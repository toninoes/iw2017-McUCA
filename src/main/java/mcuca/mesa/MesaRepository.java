package mcuca.mesa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mcuca.zona.Zona;

import java.util.List;


public interface MesaRepository extends CrudRepository<Mesa, Long> {

	List<Mesa> findByNumeroStartsWithIgnoreCase(String numero);
	
	@Query("Select m from Mesa m where m.zona = :zona")
	List<Mesa> findByZona(@Param("zona") Zona zona);
}