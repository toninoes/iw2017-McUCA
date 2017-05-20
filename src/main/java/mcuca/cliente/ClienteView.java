package mcuca.cliente;

import java.util.Collection;
import javax.annotation.PostConstruct;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.spring.annotation.SpringView;


@SuppressWarnings("serial")
@SpringView(name = ClienteView.VIEW_NAME)
public class ClienteView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "clienteView";
	private final ClienteRepository almacen;
	private final ClienteEditor editor;
	final Grid<Cliente> parrilla;
	final TextField filtro;
	private final Button agregarNuevoBoton;

	@Autowired
	public ClienteView(ClienteRepository almacen, ClienteEditor editor) {
		this.almacen = almacen;
		this.editor = editor;
		this.parrilla = new Grid<>(Cliente.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Cliente");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Clientes");
		titulo.setStyleName("h2");
		addComponent(titulo);		
		
		filtro.setPlaceholder("Búsqueda por apellidos");
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(filtro);
		acciones.addComponent(agregarNuevoBoton);
		addComponent(acciones);	
		
		parrilla.setWidth("100%");
		parrilla.setColumns("id", "nombre", "apellidos", "domicilio", "telefono");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("apellidos").setCaption("Apellidos");
		parrilla.getColumn("domicilio").setCaption("Domicilio");
		parrilla.getColumn("telefono").setCaption("Telefono");
		
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
		filtro.addValueChangeListener(e -> listarClientes(e.getValue()));

		// Connect selected Cliente to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editarCliente(e.getValue());
		});

		// Instantiate and edit new Cliente the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarCliente(new Cliente()));

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

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	public ClienteEditor getEditor() {
		return editor;
	}

	public ClienteRepository getAlmacen() {
		return almacen;
	}

}