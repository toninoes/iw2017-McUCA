package mcuca.mesa;

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


@SuppressWarnings("serial")
@SpringView(name = MesaView.VIEW_NAME)
public class MesaView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "mesaView";

	private final MesaRepository almacen;
	private final MesaEditor editor;
	final Grid<Mesa> parrilla;
	final TextField filtro;
	private final Button agregarNuevoBoton;

	@Autowired
	public MesaView(MesaRepository almacen, MesaEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Mesa.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nueva Mesa");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Mesas");
		titulo.setStyleName("h2");		
		HorizontalLayout acciones = new HorizontalLayout(filtro, agregarNuevoBoton);
		HorizontalLayout contenido = new HorizontalLayout(parrilla, editor);
		VerticalLayout todo = new VerticalLayout(titulo, acciones, contenido);

		editor.setWidth(300, Unit.PIXELS); //
		parrilla.setHeight(420, Unit.PIXELS);
		parrilla.setWidth(1100, Unit.PIXELS);
		parrilla.setColumns("id", "numero", "zona");
		parrilla.getColumn("numero").setCaption("Número");
		parrilla.getColumn("zona").setCaption("Zona");
		
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
		
		addComponent(todo);
	}


	void listarMesas(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Mesa>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findByNumeroStartsWithIgnoreCase(texto));
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
