package mcuca.pedido;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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

@SpringView(name = LineaPedidoView.VIEW_NAME)
public class LineaPedidoView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_NAME = "lineaPedidoView";

	private LineaPedidoRepository almacen;
	private LineaPedidoEditor editor;
	private Grid<LineaPedido> parrilla;
	private TextField filtro;
	private Button agregarNuevoBoton;

	@Autowired
	public LineaPedidoView(LineaPedidoRepository almacen, LineaPedidoEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<LineaPedido>(LineaPedido.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Pedido");
	}
	
	@PostConstruct
	void init() {
		Label titulo = new Label("Pedido");
		titulo.setStyleName("h2");		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(titulo, acciones, contenido);

		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "cantidad", "enCocina");
		parrilla.getColumn("cantidad").setCaption("Cantidad");
		parrilla.getColumn("enCocina").setCaption("En cocina");
		
		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por número");

		// Hook logic to components

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> listarLineaPedidos(Long.decode(e.getValue())));

		// Connect selected LineaPedido to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarLineaPedido(e.getValue());
		});

		// Instantiate and edit new LineaPedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarLineaPedido(new LineaPedido()));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarLineaPedidos(Long.decode(filtro.getValue()));
		});

		// Initialize listing
		listarLineaPedidos(null);
		
		addComponent(todo);
	}


	void listarLineaPedidos(Long id) {
		if (StringUtils.isEmpty(id)) {
			parrilla.setItems((Collection<LineaPedido>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findById(id));
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
	}


}
