package mcuca.menu;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;


public interface MenuRepository extends CrudRepository<Menu, Long> {
	
	@Cacheable("menus")
	
	List<Menu> findByNombreStartsWithIgnoreCase(String nombre);

}
