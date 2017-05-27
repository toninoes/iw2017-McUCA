package mcuca.cierre;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CierreCajaRepository extends CrudRepository<CierreCaja, Long> {

	@Query("Select c from CierreCaja c order by c.id desc")
	List<CierreCaja> findLast();
}
