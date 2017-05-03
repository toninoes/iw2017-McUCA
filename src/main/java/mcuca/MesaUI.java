package mcuca;

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


@SpringUI(path="/mesa")
public class MesaUI extends UI {

	private static final long serialVersionUID = 1L;

	private final MesaRepository almacen;

	private final MesaEditor editor;

	final Grid<Mesa> parrilla;

	final TextField filtro;

	private final Button agregarNuevoBoton;

	@Autowired
	public MesaUI(MesaRepository almacen, MesaEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Mesa.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nueva Mesa");
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
		parrilla.setColumns("id", "numero");
		parrilla.getColumn("numero").setCaption("Número");
		
		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por número");

		// Hook logic to components

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> listarMesas(e.getValue()));

		// Connect selected Mesa to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarMesa(e.getValue());
		});

		// Instantiate and edit new Mesa the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarMesa(new Mesa("")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarMesas(filtro.getValue());
		});

		// Initialize listing
		listarMesas(null);
	}


	void listarMesas(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Mesa>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findByNumeroStartsWithIgnoreCase(texto));
		}
	}

}
