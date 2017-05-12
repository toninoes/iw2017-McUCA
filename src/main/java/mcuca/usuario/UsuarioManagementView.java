package mcuca.usuario;

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
@SpringView(name = UsuarioManagementView.VIEW_NAME)
public class UsuarioManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "userManagementView";

	private Grid<Usuario> grid;
	private TextField filter;
	private Button addNewBtn;

	private UsuarioEditor editor;


	private final UsuarioService service;

	@Autowired
	public UsuarioManagementView(UsuarioService service, UsuarioEditor editor) {
		this.service = service;
		this.editor = editor;
		this.grid = new Grid<>(Usuario.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New user");

	}


	@PostConstruct
	void init() {

		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);

		addComponents(actions, grid, editor);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listUsers(e.getValue()));

		// Connect selected Usuario to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editUser(e.getValue());
		});

		// Instantiate and edit new Usuario the new button is clicked
		addNewBtn.addClickListener(e -> editor.editUser(new Usuario("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listUsers(filter.getValue());
		});

		// Initialize listing
		listUsers(null);

	}

	private void listUsers(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(service.findAll());
		} else {
			grid.setItems(service.findByLastNameStartsWithIgnoreCase(filterText));
		}
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
