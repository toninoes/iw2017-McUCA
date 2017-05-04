package mcuca;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Zona {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String nombre;

	private Integer aforo;

	protected Zona() {
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
		return String.format("Zona '%s' Capacidad: %d personas", nombre, aforo);
	}

}
