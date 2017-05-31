package mcuca.cliente;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.cierre.CierreCajaRepository;
import mcuca.pedido.LineaPedidoRepository;
import mcuca.pedido.PedidoRepository;
import mcuca.pedido.PedidoService;
import mcuca.usuario.UsuarioRepository;

@SuppressWarnings({"serial" })
@SpringComponent
@UIScope
public class ClienteEditor extends VerticalLayout {
	

	private final ClienteRepository repoCliente;
	
	private PedidoService pedService;

	private Cliente cliente;

	/* Fields to edit properties in Cliente entity */
	Label title = new Label("Nuevo Cliente");
	TextField nombre = new TextField("Nombre");
	TextField apellidos = new TextField("Apellidos");
	TextField domicilio = new TextField("Domicilio");
	TextField telefono = new TextField("Telefono");

	/* Action buttons */	
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Cliente> binder = new Binder<>(Cliente.class);

	@Autowired
	public ClienteEditor(ClienteRepository almacen, PedidoRepository ped, 
			CierreCajaRepository cierre,
			LineaPedidoRepository lp, UsuarioRepository userRepo) {
		
		pedService = new PedidoService(ped, cierre, lp, userRepo);
		repoCliente = almacen;

		nombre.setMaxLength(32);
		apellidos.setMaxLength(64);
		domicilio.setMaxLength(128);
		telefono.setMaxLength(13);
		
		addComponents(title, nombre, apellidos, domicilio, telefono, acciones);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.forField(nombre)
			.asRequired("No puede estar vacío")
			.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 32))
			.bind(Cliente::getNombre, Cliente::setNombre);

		binder.forField(apellidos)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 64 caracteres", 4, 64))
		.bind(Cliente::getApellidos, Cliente::setApellidos);

		binder.forField(domicilio)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 128 caracteres", 4, 128))
		.bind(Cliente::getDomicilio, Cliente::setDomicilio);

		binder.forField(telefono)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 9 y 13 caracteres", 9, 13))
		.bind(Cliente::getTelefono, Cliente::setTelefono);
		
		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(e -> {
			if(binder.isValid()){
				cliente.setTelefono(cliente.getTelefono().replaceAll("\\s+",""));
				repoCliente.save(cliente);
			}else
				mostrarNotificacion(new Notification("Algunos campos del formulario deben corregirse"));
		});
		
		borrar.addClickListener(e -> borrarCliente());
		cancelar.addClickListener(e -> editarCliente(cliente));
		setVisible(false);
	}

	private void mostrarNotificacion(Notification notification) {
        notification.setDelayMsec(1500);
        notification.show(Page.getCurrent());
    }
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public void borrarCliente() {
		pedService.deletePedidosByCliente(cliente);
		repoCliente.delete(cliente);
	}

	public final void editarCliente(Cliente c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		
		final boolean persisted = c.getId() != null;
		
		if (persisted)
			cliente = repoCliente.findOne(c.getId());
		else
			cliente = c;
		
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
		// ChangeHandler is notified when either guardar or borrar is clicked
		//guardar.addClickListener(e -> h.onChange());
		guardar.addClickListener(e -> {
			if(binder.isValid())
				h.onChange();
		});
		
		borrar.addClickListener(e -> h.onChange());
	}

}