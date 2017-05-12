package mcuca.establecimiento;

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
		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(acciones, contenido);

		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "nombre", "domicilio");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("domicilio").setCaption("Domicilio");

		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por nombre");

		// Hook logic to components

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
		
		addComponent(todo);
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

