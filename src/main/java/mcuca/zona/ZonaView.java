package mcuca.zona;

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

@SpringView(name = ZonaView.VIEW_NAME)
public class ZonaView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "zonaView";
	private static final long serialVersionUID = 1L;

	private final ZonaRepository almacen;
	
	

	private final ZonaEditor editor;

	final Grid<Zona> parrilla;

	final TextField filtro;

	private final Button agregarNuevoBoton;

	@Autowired
	public ZonaView(ZonaRepository almacen, ZonaEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Zona.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nueva Zona");
	}

	@PostConstruct
	protected void init() {

		Label titulo = new Label("Zonas");
		titulo.setStyleName("h2");
		addComponent(titulo);		
		
		filtro.setPlaceholder("Búsqueda");
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(filtro);
		acciones.addComponent(agregarNuevoBoton);
		addComponent(acciones);	
		
		parrilla.setWidth("100%");
		parrilla.setColumns("id", "nombre", "aforo", "establecimiento");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("aforo").setCaption("Aforo");
		parrilla.getColumn("establecimiento").setCaption("Establecimiento");
		
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
		filtro.addValueChangeListener(e -> listarZonas(e.getValue()));

		// Connect selected Cliente to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarZona(e.getValue());
		});

		// Instantiate and edit new Cliente the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarZona(new Zona()));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarZonas(filtro.getValue());
		});

		// Initialize listing
		listarZonas(null);		
	}


	void listarZonas(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Zona>) almacen.findAll());
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
