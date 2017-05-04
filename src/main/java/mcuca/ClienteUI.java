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


@SpringUI(path="/cliente")
public class ClienteUI extends UI {

	private static final long serialVersionUID = 1L;

	private final ClienteRepository almacen;

	private final ClienteEditor editor;

	final Grid<Cliente> parrilla;

	final TextField filtro;

	private final Button agregarNuevoBoton;

	@Autowired
	public ClienteUI(ClienteRepository almacen, ClienteEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Cliente.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Cliente");
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
		parrilla.setColumns("id", "nombre", "apellidos", "domicilio", "telefono");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("apellidos").setCaption("Apellidos");
		parrilla.getColumn("domicilio").setCaption("Domicilio");
		parrilla.getColumn("telefono").setCaption("Telefono");

		filtro.setWidth(300, Unit.PIXELS);
		filtro.setPlaceholder("Búsqueda por apellidos");

		// Hook logic to components

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> listarClientes(e.getValue()));

		// Connect selected Cliente to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarCliente(e.getValue());
		});

		// Instantiate and edit new Cliente the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarCliente(new Cliente("", "", "", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listarClientes(filtro.getValue());
		});

		// Initialize listing
		listarClientes(null);
	}


	void listarClientes(String texto) {
		if (StringUtils.isEmpty(texto)) {
			parrilla.setItems((Collection<Cliente>) almacen.findAll());
		}
		else {
			parrilla.setItems(almacen.findByApellidosStartsWithIgnoreCase(texto));
		}
	}

}
