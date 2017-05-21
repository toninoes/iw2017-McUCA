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


@SuppressWarnings("serial")
@SpringView(name = PedidoView.VIEW_NAME)
public class PedidoView extends VerticalLayout implements View {
	
	public static final String VIEW_NAME = "pedidoView";

	private final PedidoRepository almacen;
	private final PedidoEditor editor;
	final Grid<Pedido> parrilla;
	final Grid<LineaPedido> parrilla2;
	final TextField filtro;
	final TextField filtro2;
	private final LineaPedidoRepository almacen2;
	private final LineaPedidoEditor editor2;
	private final Button agregarNuevoBoton;
	private final Button detallePedidoBoton;
	private final Button agregarNuevoBoton2;
	private final Button pedidoBoton;
	
	@Autowired
	public PedidoView(PedidoRepository almacen, PedidoEditor editor, LineaPedidoRepository almacen2, 
			          LineaPedidoEditor editor2) {
		this.almacen = almacen;
		this.editor = editor;
		this.almacen2 = almacen2;
		this.editor2 = editor2;
		this.parrilla = new Grid<>(Pedido.class);
		this.parrilla2 = new Grid<>(LineaPedido.class);
		this.filtro = new TextField();
		this.filtro2 = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Pedido");
		this.detallePedidoBoton = new Button("Ver Detalle Pedido");
		this.agregarNuevoBoton2 = new Button("Nueva Linea de Pedido");
		this.pedidoBoton = new Button("Volver");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Pedidos");
		titulo.setStyleName("h2");		
		
		Label titulo2 = new Label("Linea Pedidos");
		titulo.setStyleName("h2");
		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton, detallePedidoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(titulo, acciones, contenido);

		HorizontalLayout acciones2 = new HorizontalLayout(filtro2, agregarNuevoBoton2, pedidoBoton);
		HorizontalLayout contenido2 = new HorizontalLayout(parrilla2, editor2);
		VerticalLayout todo2 = new VerticalLayout(titulo2, acciones2, contenido2);
		
		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "precio", "abierto", "tipo", "fecha");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("abierto").setCaption("Abierto");
		parrilla.getColumn("tipo").setCaption("Tipo");
		parrilla.getColumn("fecha").setCaption("Fecha");
		
		editor2.setWidth(300, Unit.PIXELS); //
		parrilla2.setHeight(420, Unit.PIXELS);
		parrilla2.setWidth(1100, Unit.PIXELS);
		parrilla2.setColumns("id", "cantidad", "enCocina");
		parrilla2.getColumn("cantidad").setCaption("Cantidad");
		parrilla2.getColumn("enCocina").setCaption("En Cocina");
		
		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por número");

		filtro2.setWidth(300, Unit.PIXELS);
		filtro2.setPlaceholder("Búsqueda por número");
		
		// Hook logic to components

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> listarPedidos(Long.decode(e.getValue()).longValue()));

		filtro2.setValueChangeMode(ValueChangeMode.LAZY);
		filtro2.addValueChangeListener(e -> listarLineaPedidos(Long.decode(e.getValue()).longValue()));
		
		detallePedidoBoton.setVisible(false);
		todo2.setVisible(false);
		
		// Connect selected Pedido to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarPedido(e.getValue());
			editor.setVisible(true);
			detallePedidoBoton.setVisible(true);
		});
		
		// Connect selected LineaPedido to editor or hide if none is selected
		parrilla2.asSingleSelect().addValueChangeListener(e -> {
			editor2.editarLineaPedido(e.getValue());
			editor2.setVisible(true);
		});

		// Instantiate and edit new Pedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarPedido(new Pedido()));
		
		// Instantiate and edit new Pedido the new button is clicked
		agregarNuevoBoton2.addClickListener(e -> editor2.editarLineaPedido(new LineaPedido()));
		
		detallePedidoBoton.addClickListener(e -> {
			editor.setVisible(false);
			editor2.setVisible(false);
			todo.setVisible(false);
			todo2.setVisible(true);
			detallePedidoBoton.setVisible(false);
		});
		
		pedidoBoton.addClickListener(e -> {
			editor.setVisible(false);
			editor2.setVisible(false);
			todo.setVisible(true);
			todo2.setVisible(false);
		});
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarPedidos(Long.decode(filtro.getValue()).longValue());
		});
		
		editor2.setChangeHandler(() -> {
			editor2.setVisible(false);
			listarLineaPedidos(Long.decode(filtro2.getValue()).longValue());
		});
		
		// Initialize listing
		if(!parrilla.getSelectedItems().iterator().hasNext())
			listarPedidos(0);
		else
			listarPedidos(parrilla.asSingleSelect().getValue().getId().longValue());
		
		if(!parrilla2.getSelectedItems().iterator().hasNext())
			listarLineaPedidos(0);
		else
			listarLineaPedidos(parrilla2.asSingleSelect().getValue().getId().longValue());
		
		addComponent(todo);
		addComponent(todo2);
	}


	void listarPedidos(long id) {
		if (id == 0)
			parrilla.setItems((Collection<Pedido>) almacen.findAll());
		else
			parrilla.setItems(almacen.findById(id));
	}
	
	void listarLineaPedidos(long pedido_id) {
		if (pedido_id == 0)
			parrilla2.setItems((Collection<LineaPedido>) almacen2.findAll());
		else 
			parrilla2.setItems(almacen2.findById(pedido_id));
	}
	
	public Grid<Pedido> getParrilla() {
		return parrilla;
	}
	
	public Grid<LineaPedido> getParrilla2() {
		return parrilla2;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
