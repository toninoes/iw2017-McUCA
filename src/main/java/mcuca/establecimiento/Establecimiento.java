package mcuca.establecimiento;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Establecimiento {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String nombre;
	
	private String domicilio;
	
	
	public Establecimiento() {
	}
	
	public Establecimiento(String nombre) {
		this.nombre = nombre;
	}

	public Establecimiento(String nombre, String domicilio) {
		this.nombre = nombre;
		this.domicilio = domicilio;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
        this.id = id;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	
	
	@Override
	public String toString() {
		return String.format("%s", nombre);
	}
}
