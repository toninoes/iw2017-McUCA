package mcuca.pedido;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import mcuca.MainScreen;

@SpringView(name = PedidoView.VIEW_NAME)
public class PedidoView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_NAME = "pedidoView";
	
	public static long pedido_id = 0;
	public static long cliente_id = 0;
	public static Grid<Pedido> parrilla;
	
	private static PedidoRepository almacen;
	private final PedidoEditor editor;
	final TextField filtro;
	private final Button agregarNuevoBoton;
	private final Button detallePedidoBoton;
	
	@Autowired
	public PedidoView(PedidoRepository alm, PedidoEditor editor, LineaPedidoRepository almacen2, 
			          LineaPedidoEditor editor2) {
		almacen = alm;
		this.editor = editor;
		parrilla = new Grid<>(Pedido.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Pedido");
		this.detallePedidoBoton = new Button("Lineas de Pedido");
	}

	@PostConstruct
	void init() {
		
		Label titulo = new Label("Pedidos");
		titulo.setStyleName("h2");		
		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton, detallePedidoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(titulo, acciones, contenido);

		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "nombre", "precio", "abierto", "tipo", "fecha");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("abierto").setCaption("Abierto");
		parrilla.getColumn("tipo").setCaption("Tipo");
		parrilla.getColumn("fecha").setCaption("Fecha");
				
		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por número");
		
		// Hook logic to components
		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> {
			if(cliente_id == 0 && filtro.getValue().equals(""))
				listarPedidos(0);
			else if(cliente_id != 0)
				listarPedidos(cliente_id, Tipo.DOMICILIO);
			else
				listarPedidos(Long.decode(filtro.getValue()).longValue());
		});
		
		detallePedidoBoton.setVisible(false);
		
		// Connect selected Pedido to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarPedido(e.getValue());
			editor.setVisible(true);
			detallePedidoBoton.setVisible(true);
		});

		// Instantiate and edit new Pedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarPedido(new Pedido()));
		
		detallePedidoBoton.addClickListener(e -> {
			pedido_id = Long.valueOf(editor.binder.getBean().getId()).longValue();
			getUI().getNavigator().navigateTo(LineaPedidoView.VIEW_NAME);
			editor.setVisible(false);
		});
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			if(cliente_id == 0 && filtro.getValue().equals(""))
				listarPedidos(0);
			else if(cliente_id != 0)
				listarPedidos(cliente_id, Tipo.DOMICILIO);
			else
				listarPedidos(Long.decode(filtro.getValue()).longValue());
		});
		
		// Initialize listing
		if(cliente_id == 0)
			listarPedidos(0);
		else
			listarPedidos(cliente_id, Tipo.DOMICILIO);
		
		// Pedido reset view
		MainScreen.navigationBar.addAttachListener(e -> {
			cliente_id = 0;
			pedido_id = 0;
			listarPedidos(0);
		});
		
		addComponent(todo);
	}

	public static void listarPedidos(long id) {
		if(almacen != null)
			if(id == 0)
				parrilla.setItems((Collection<Pedido>) almacen.findAll());
			else
				parrilla.setItems((Collection<Pedido>) almacen.findById(id));
	}
	
	public static void listarPedidos(Long id, Tipo tipo) {
		cliente_id = id.longValue();
		if(tipo == Tipo.DOMICILIO)
			parrilla.setItems((Collection<Pedido>) almacen.findByTipo(Tipo.DOMICILIO));
		else
			parrilla.setItems((Collection<Pedido>) almacen.findById(-1L));
	}
	
	public Grid<Pedido> getParrilla() {
		return parrilla;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}