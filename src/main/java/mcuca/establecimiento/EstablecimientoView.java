package mcuca.establecimiento;

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
@SpringView(name = EstablecimientoView.VIEW_NAME)
public class EstablecimientoView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "establecimientoView";
	private final EstablecimientoRepository almacen;
	private final EstablecimientoEditor editor;
	final Grid<Establecimiento> parrilla;
	final TextField filtro;
	private final Button agregarNuevoBoton;

	@Autowired
	public EstablecimientoView(EstablecimientoRepository almacen, EstablecimientoEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Establecimiento.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Establecimiento");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Establecimientos");
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
		parrilla.setColumns("id", "nombre", "domicilio");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("domicilio").setCaption("Domicilio");
		
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
		filtro.addValueChangeListener(e -> listarEstablecimientos(e.getValue()));

		// Connect selected Cliente to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarEstablecimiento(e.getValue());
		});

		// Instantiate and edit new Cliente the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarEstablecimiento(new Establecimiento()));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarEstablecimientos(filtro.getValue());
		});

		// Initialize listing
		listarEstablecimientos(null);
	}


	void listarEstablecimientos(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Establecimiento>) almacen.findAll());
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

