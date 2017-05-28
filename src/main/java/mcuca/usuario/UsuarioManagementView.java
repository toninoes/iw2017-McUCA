package mcuca.usuario;

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
@SpringView(name = UsuarioManagementView.VIEW_NAME)
public class UsuarioManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "usuarioManagementView";

	private Grid<Usuario> parrilla;
	private TextField filtro;
	private Button agregarNuevoBoton;

	private UsuarioEditor editor;


	private final UsuarioService service;

	@Autowired
	public UsuarioManagementView(UsuarioService service, UsuarioEditor editor) {
		this.service = service;
		this.editor = editor;
		this.parrilla = new Grid<>(Usuario.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Usuario");

	}


	@PostConstruct
	void init() {
		Label titulo = new Label("Usuarios");
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
		parrilla.setColumns("id", "nombre", "apellidos");
		parrilla.getColumn("apellidos").setCaption("Apellidos");

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
		filtro.addValueChangeListener(e -> listUsers(e.getValue()));

		// Connect selected Usuario to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			editor.editUser(e.getValue());
		});

		// Instantiate and edit new Cliente the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editUser(new Usuario("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listUsers(filtro.getValue());
		});

		// Initialize listing
		listUsers(null);

	}

	private void listUsers(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			parrilla.setItems(service.findAll());
		} else {
			parrilla.setItems(service.findByApellidosStartsWithIgnoreCase(filterText));
		}
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
