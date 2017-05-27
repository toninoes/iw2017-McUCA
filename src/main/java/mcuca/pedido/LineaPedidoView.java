package mcuca.pedido;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


@SpringView(name = LineaPedidoView.VIEW_NAME)
public class LineaPedidoView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_NAME = "lineaPedidoView";

	public static Grid<LineaPedido> parrilla;
	
	private LineaPedidoRepository almacen;
	private LineaPedidoEditor editor;
	
	private TextField filtro;
	private Button agregarNuevoBoton;
	private final Button pedidoBoton;
	
	@Autowired
	public LineaPedidoView(LineaPedidoRepository almacen, LineaPedidoEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		parrilla = new Grid<LineaPedido>(LineaPedido.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nueva Linea Pedido");
		this.pedidoBoton = new Button("Volver");
	}
	
	@PostConstruct
	void init() {
		Label titulo = new Label("Líneas de pedido");
		titulo.setStyleName("h2");
		addComponent(titulo);		
		
		filtro.setPlaceholder("Búsqueda por número");
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(filtro);
		acciones.addComponent(agregarNuevoBoton);
		acciones.addComponent(pedidoBoton);
		addComponent(acciones);	
		
		parrilla.setWidth("100%");
		parrilla.setColumns("id", "cantidad", "precio", "producto", "enCocina");
		parrilla.getColumn("cantidad").setCaption("Cantidad");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("producto").setCaption("Producto");
		parrilla.getColumn("enCocina").setCaption("En cocina");
		
		editor.setWidth("100%");
		
		HorizontalLayout contenido = new HorizontalLayout();
		Responsive.makeResponsive(contenido);
		contenido.setSpacing(false);
		contenido.setMargin(false);
		contenido.setSizeFull();
		
		contenido.addComponent(parrilla);
		contenido.addComponent(editor);
		contenido.setExpandRatio(parrilla, 0.7f);
		contenido.setExpandRatio(editor, 0.3f);
		addComponent(contenido);

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> {
			listarLineaPedidos();
		});

		// Connect selected LineaPedido to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarLineaPedido(e.getValue());
		});

		// Instantiate and edit new LineaPedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarLineaPedido(new LineaPedido()));

		pedidoBoton.addClickListener(e -> {
			getUI().getNavigator().navigateTo(PedidoView.VIEW_NAME);
		});
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarLineaPedidos();
		});
		
		// Initialize listing
		listarLineaPedidos();
		/*
		Label titulo = new Label("Lineas Pedido");
		titulo.setStyleName("h2");		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton, pedidoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(titulo, acciones, contenido);

		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "cantidad", "precio", "producto", "enCocina");
		parrilla.getColumn("cantidad").setCaption("Cantidad");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("producto").setCaption("Producto");
		parrilla.getColumn("enCocina").setCaption("En cocina");
		
		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por número");

		// Hook logic to components

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> {
			listarLineaPedidos();
		});

		// Connect selected LineaPedido to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarLineaPedido(e.getValue());
		});

		// Instantiate and edit new LineaPedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarLineaPedido(new LineaPedido()));

		pedidoBoton.addClickListener(e -> {
			getUI().getNavigator().navigateTo(PedidoView.VIEW_NAME);
		});
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarLineaPedidos();
		});

		// Initialize listing
		listarLineaPedidos();
		
		addComponent(todo);*/
	}


	void listarLineaPedidos() {
		if (PedidoView.pedido_id == 0)
			parrilla.setItems((Collection<LineaPedido>) almacen.findById(-1L));
		else
			parrilla.setItems((Collection<LineaPedido>) almacen.findById(PedidoView.pedido_id));
			//parrilla.setItems((Collection<LineaPedido>) almacen.findByPedidoId(PedidoView.pedido_id));
	}
	
	public Grid<LineaPedido> getParrilla() {
		return parrilla;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
	}
}
