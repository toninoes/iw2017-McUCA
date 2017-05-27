package mcuca.cierre;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import mcuca.establecimiento.Establecimiento;

@Entity
public class CierreCaja {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private Establecimiento establecimiento;
	
	private Date fechaCierre;
	
	private float recaudacion;
	
	public CierreCaja(float recaudacion) {
		this.recaudacion = recaudacion;
		this.fechaCierre = new Date();
	}
	
	public Long getId() { return this.id; }
	public Establecimiento getEstablecimiento() { return this.establecimiento; }
	public Date getFechaCierre() { return this.fechaCierre; }
	public float getRecaudacion() { return this.recaudacion; }
	
	public void setEstablecimiento(Establecimiento establecimiento) { this.establecimiento = establecimiento; }
	public void setFechaCierre(Date fecha) { this.fechaCierre = fecha; }
	public void setRecaudacion(float recaudacion) { this.recaudacion = recaudacion; }

}
