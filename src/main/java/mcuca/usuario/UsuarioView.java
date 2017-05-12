package mcuca.usuario;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@SpringView(name = UsuarioView.VIEW_NAME)
public class UsuarioView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "userView";

	private Grid<Usuario> grid;
	private TextField filter;


	private final UsuarioService service;

	@Autowired
	public UsuarioView(UsuarioService service, UsuarioEditor editor) {
		this.service = service;
		this.grid = new Grid<>(Usuario.class);
		this.filter = new TextField();

	}


	@PostConstruct
	void init() {

		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter);

		addComponents(actions, grid);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listUsers(e.getValue()));

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
