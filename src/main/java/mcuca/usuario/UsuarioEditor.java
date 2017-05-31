package mcuca.usuario;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.establecimiento.Establecimiento;
import mcuca.establecimiento.EstablecimientoRepository;
import mcuca.security.SecurityUtils;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class UsuarioEditor extends VerticalLayout {

	private final UsuarioService servicio;

	private Usuario usuario;
	
	@Autowired
	private final EstablecimientoRepository repoEst;
	
	private Binder<Usuario> binder = new Binder<>(Usuario.class);

	/* Fields to edit properties in Usuario entity */
	TextField dni = new TextField("Dni");
	TextField nombre = new TextField("Nombre");
	TextField apellidos = new TextField("Apellidos");
	TextField username = new TextField("Username");
	TextField password = new TextField("Password");
	NativeSelect<Rol> select = new NativeSelect<>("Rol");
	ComboBox<Establecimiento> establecimiento = new ComboBox<>("Establecimiento");

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

		dni.setMaxLength(9);
		nombre.setMaxLength(32);
		apellidos.setMaxLength(64);
		username.setMaxLength(32);
		password.setMaxLength(255);
		
		addComponents(dni, nombre, apellidos, username, password, select, establecimiento, acciones);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.forField(dni)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena de 9 caracteres", 9, 9))
		.bind(Usuario::getDni, Usuario::setDni);
		
		binder.forField(nombre)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 32))
		.bind(Usuario::getNombre, Usuario::setNombre);
		
		binder.forField(apellidos)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 64))
		.bind(Usuario::getApellidos, Usuario::setApellidos);
		
		binder.forField(username)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 64))
		.bind(Usuario::getUsername, Usuario::setUsername);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(this::salvar);
		guardar.addClickListener(e -> {
			if(binder.isValid()){
				binder.setBean(usuario);
				usuario.setDni(dni.getValue());
				usuario.setNombre(nombre.getValue());
				usuario.setApellidos(apellidos.getValue());
				
				if(password.isEmpty())
					usuario.setPassword(dni.getValue());
				else
					usuario.setPassword(password.getValue());
				
				usuario.setRol(select.getValue());
				usuario.setEstablecimiento(establecimiento.getValue());
				servicio.save(usuario);
			}else
				mostrarNotificacion(new Notification("Algunos campos del formulario deben corregirse"));
		});
		borrar.addClickListener(e -> servicio.delete(usuario));
		cancelar.addClickListener(e -> editUser(usuario));		
	
		setVisible(false);

		// Solo borra el admin
		borrar.setEnabled(SecurityUtils.hasRole("GERENTE"));
	}

	private void mostrarNotificacion(Notification notification) {
        notification.setDelayMsec(1500);
        notification.show(Page.getCurrent());
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

		select.setSelectedItem(usuario.getRol());
				
		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in nombre field automatically
		nombre.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar is clicked
		//guardar.addClickListener(e -> h.onChange());
		guardar.addClickListener(e -> {
			if(binder.isValid())
				h.onChange();
		});
		borrar.addClickListener(e -> h.onChange());
	}

	public EstablecimientoRepository getRepoEst() {
		return repoEst;
	}

}