package mcuca.zona;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mcuca.establecimiento.Establecimiento;


import java.util.List;

public interface ZonaRepository extends CrudRepository<Zona, Long> {

	List<Zona> findByNombreStartsWithIgnoreCase(String nombre);
	
	@Query("Select z from Zona z where z.establecimiento = :establecimiento")
	List<Zona> findByEstablecimiento(@Param("establecimiento") Establecimiento establecimiento);
}