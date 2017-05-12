package mcuca.usuario;

public enum Rol {
	GERENTE("Gerente", 0), 
	ENCARGADO("Encargado", 1), 
	CAMARERO("Camarero", 2);
	
	private final String cargo;
	private final int id;
	
	Rol(String cargo, int id) {
		this.cargo = cargo;
		this.id = id;
	}

	public String getCargo() {
		return cargo;
	}

	public int getId() {
		return id;
	}
}