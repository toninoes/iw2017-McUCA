package mcuca.cliente;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "deprecation", "serial" })
@SpringComponent
@UIScope
public class ClienteEditor extends VerticalLayout {
	

	private final ClienteRepository almacen;

	private Cliente cliente;

	/* Fields to edit properties in Cliente entity */
	Label title = new Label("Nuevo Cliente");
	TextField nombre = new TextField("Nombre");
	TextField apellidos = new TextField("Apellidos");
	TextField domicilio = new TextField("Domicilio");
	TextField telefono = new TextField("Telefono");

	/* Action buttons */	
	Button guardar = new Button(FontAwesome.SAVE);
	Button cancelar = new Button(FontAwesome.TIMES);
	Button borrar = new Button(FontAwesome.ERASER);
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Cliente> binder = new Binder<>(Cliente.class);

	@Autowired
	public ClienteEditor(ClienteRepository almacen) {
		
		this.almacen = almacen;

		addComponents(title, nombre, apellidos, domicilio, telefono, acciones);

		// bind using naming convention
		binder.bindInstanceFields(this);
		/*binder.bind(nombre, "nombre");
		binder.bind(apellidos, "apellidos");
		binder.bind(domicilio, "domicilio");
		binder.forField(telefono)
		  .withConverter(
		    new StringToIntegerConverter("Por favor introduce un número"))
		  .bind("telefono");*/

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(e -> almacen.save(cliente));
		borrar.addClickListener(e -> almacen.delete(cliente));
		cancelar.addClickListener(e -> editarCliente(cliente));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editarCliente(Cliente c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			cliente = almacen.findOne(c.getId());
		}
		else {
			cliente = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(cliente);

		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in nombre field automatically
		nombre.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar
		// is clicked
		guardar.addClickListener(e -> h.onChange());
		borrar.addClickListener(e -> h.onChange());
	}

}