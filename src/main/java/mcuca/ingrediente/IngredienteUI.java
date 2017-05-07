package mcuca.ingrediente;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


@SpringUI(path="/ingrediente")
public class IngredienteUI extends UI {

	private static final long serialVersionUID = 1L;

	private final IngredienteRepository almacen;

	private final IngredienteEditor editor;

	final Grid<Ingrediente> parrilla;

	final TextField filtro;

	private final Button agregarNuevoBoton;

	@Autowired
	public IngredienteUI(IngredienteRepository almacen, IngredienteEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Ingrediente.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Ingrediente");
	}

	@Override
	protected void init(VaadinRequest request) {

		Label titulo = new Label("iw2017-McUCA");
		titulo.setStyleName("h1");
		HorizontalLayout cabecera = new HorizontalLayout(titulo);		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(cabecera, acciones, contenido);
		todo.setComponentAlignment(cabecera, Alignment.MIDDLE_CENTER);
		setContent(todo);

		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "nombre", "precio");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("precio").setCaption("Precio");

		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por nombre");

		// Hook logic to components

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> listarIngrediente(e.getValue()));

		// Connect selected Ingrediente to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarIngrediente(e.getValue());
		});

		// Instantiate and edit new Ingrediente the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarIngrediente(new Ingrediente("", 0.0)));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarIngrediente(filtro.getValue());
		});

		// Initialize listing
		listarIngrediente(null);
	}


	void listarIngrediente(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Ingrediente>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findByNombreStartsWithIgnoreCase(texto));
		}
	}

}
