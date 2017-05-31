package mcuca.menu;

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

import mcuca.producto.ProductoRepository;


@SuppressWarnings("serial")
@SpringView(name = MenuView.VIEW_NAME)
public class MenuView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "menuView";
	private final MenuRepository almacen;
	private final MenuEditor editor;
	final Grid<Menu> parrilla;
	final TextField filtro;
	private final Button agregarNuevoBoton;
	private final ProductoRepository repoProd;

	@Autowired
	public MenuView(MenuRepository almacen, MenuEditor editor, ProductoRepository repoProd) {
		this.almacen = almacen;
		this.editor = editor;
		this.repoProd = repoProd;
		this.parrilla = new Grid<>(Menu.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Menu");
	}
	
	@PostConstruct
	void init() {
		Label titulo = new Label("Menus");
		titulo.setStyleName("h2");
		addComponent(titulo);		
		
		filtro.setPlaceholder("Búsqueda por Nombre");
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(filtro);
		acciones.addComponent(agregarNuevoBoton);
		addComponent(acciones);	
		
		parrilla.setWidth("100%");
		parrilla.setColumns("id", "nombre", "descripcion", "descuento", "precio", "productos", "iva", "esOferta", "menus");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("descripcion").setCaption("Descripcion");
		parrilla.getColumn("descuento").setCaption("Descuento");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("productos").setCaption("Productos");
		parrilla.getColumn("iva").setCaption("IVA");
		parrilla.getColumn("esOferta").setCaption("Oferta");
		parrilla.getColumn("menus").setCaption("Menu en oferta");

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
		filtro.addValueChangeListener(e -> listarMenus(e.getValue()));

		// Connect selected Menu to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.productos.select();
			editor.editarMenu(e.getValue());
		});

		// Instantiate and edit new Menu the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarMenu(new Menu()));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarMenus(filtro.getValue());
		});

		// Initialize listing
		listarMenus(null);		
	}

	void listarMenus(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Menu>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findByNombreStartsWithIgnoreCase(texto));
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	public MenuEditor getEditor() {
		return editor;
	}

	public MenuRepository getAlmacen() {
		return almacen;
	}

	public ProductoRepository getRepoProd() {
		return repoProd;
	}


}

