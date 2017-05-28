package mcuca.usuario;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.establecimiento.Establecimiento;
import mcuca.establecimiento.EstablecimientoRepository;
import mcuca.mesa.Mesa;
import mcuca.security.SecurityUtils;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class UsuarioEditor extends VerticalLayout {

	private final UsuarioService servicio;

	private Usuario usuario;
	private final EstablecimientoRepository repoEst;
	private Binder<Usuario> binder = new Binder<>(Usuario.class);


	/* Fields to edit properties in Usuario entity */
	TextField dni = new TextField("Dni");
	TextField nombre = new TextField("Nombre");
	TextField apellidos = new TextField("Apellidos");
	TextField username = new TextField("Username");
	TextField password = new TextField("Password");
	NativeSelect<Rol> select = new NativeSelect<>("Rol");
	NativeSelect<Establecimiento> establecimiento = new NativeSelect<>("Establecimiento");



	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");

	/* Layout for buttons */
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);


	@Autowired
	public UsuarioEditor(UsuarioService servicio, EstablecimientoRepository repoEst) {
		this.servicio = servicio;
		this.repoEst = repoEst;
		select.setItems(Rol.class.getEnumConstants());
		establecimiento.setItems((Collection<Establecimiento>) repoEst.findAll());



		addComponents(dni, nombre, apellidos, username, password, select, establecimiento, acciones);

		// bind using naming convention
		binder.bindInstanceFields(this);


		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(this::salvar);
		borrar.addClickListener(e -> servicio.delete(usuario));
		cancelar.addClickListener(e -> editUser(usuario));
		setVisible(false);

		// Solo borra el admin
		borrar.setEnabled(SecurityUtils.hasRole("GERENTE"));
	}

	public interface ChangeHandler {

		void onChange();
	}
	
	public void salvar(ClickEvent e) {
		binder.setBean(usuario);
		usuario.setDni(dni.getValue());
		usuario.setNombre(nombre.getValue());
		usuario.setApellidos(apellidos.getValue());
		usuario.setPassword(password.getValue());
		usuario.setRol(select.getValue());
		usuario.setEstablecimiento(establecimiento.getValue());
		servicio.save(usuario);
	}

	public final void editUser(Usuario c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			usuario = servicio.findOne(c.getId());
		}
		else {
			usuario = c;
		}
		cancelar.setVisible(persisted);

		// Bind usuario properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(usuario);

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