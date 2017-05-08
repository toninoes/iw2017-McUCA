package mcuca.pedido;

public enum Tipo {
	LLEVAR("Llevar", 1), 
	DOMICILIO("Domicilio", 2), 
	ESTABLECIMIENTO("Establecimiento", 3);
	
	private final String tipo_pedido;
	private final int id;
	
	Tipo(String tipo_pedido, int id) {
		this.tipo_pedido = tipo_pedido;
		this.id = id;
	}

	public String getTipoPedido() {
		return tipo_pedido;
	}

	public int getId() {
		return id;
	}
}
