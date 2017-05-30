package mcuca.producto;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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

@SuppressWarnings("serial")
@SpringView(name = ProductoView.VIEW_NAME)
public class ProductoView extends VerticalLayout implements View {
	
	public static final String VIEW_NAME = "productoView";
	public static Grid<Producto> parrilla;
	private final ProductoRepository almacen;
	private ProductoEditor editor;
	final TextField filtro;
	private final Button agregarNuevoBoton;
	
	@Autowired
	public ProductoView(ProductoRepository almacen, ProductoEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		parrilla = new Grid<>(Producto.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Producto");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Productos");
		titulo.setStyleName("h2");
		addComponent(titulo);
		
		filtro.setPlaceholder("Búsqueda por nombre");
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(filtro);
		acciones.addComponent(agregarNuevoBoton);
		addComponent(acciones);	
		
		parrilla.setWidth("100%");
		parrilla.setColumns("id", "nombre", "precio", "iva", "ingredientes", "foto");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("iva").setCaption("I.V.A.");
		parrilla.getColumn("ingredientes").setCaption("Ingredientes");
		parrilla.getColumn("foto").setCaption("Foto");
		
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
		filtro.addValueChangeListener(e -> listarProductos(e.getValue()));
		
		// Connect selected Producto to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarProducto(e.getValue());
		});

		// Instantiate and edit new Producto the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarProducto(new Producto()));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarProductos(filtro.getValue());
		});

		// Initialize listing
		listarProductos(null);
	}

	void listarProductos(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Producto>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findByNombreStartsWithIgnoreCase(texto));
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}