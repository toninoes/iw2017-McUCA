package mcuca.usuario;

public enum Rol {
	GERENTE("Gerente", 1), 
	ENCARGADO("Encargado", 2), 
	CAMARERO("Camarero", 3);
	
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