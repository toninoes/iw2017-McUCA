package mcuca.zona;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import mcuca.establecimiento.Establecimiento;


@Entity
public class Zona {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String nombre;

	private Integer aforo;
	
	@ManyToOne
	private Establecimiento establecimiento;

	protected Zona() {
	}
	
	public Zona(String nombre, String aforo) {
		this.nombre = nombre;
		
		if (aforo == "")
			this.aforo = null;
	}

	public Zona(String nombre, Integer aforo) {
		this.nombre = nombre;
		this.aforo = aforo;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Integer getAforo() {
		return aforo;
	}
	
	public void setAforo(Integer aforo) {
		this.aforo = aforo;
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", nombre, getEstablecimiento());
	}
	
	public Establecimiento getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(Establecimiento establecimiento) {
		this.establecimiento = establecimiento;
	}

}
