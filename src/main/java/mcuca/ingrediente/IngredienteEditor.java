package mcuca.ingrediente;

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

import mcuca.cliente.Cliente;

@SpringComponent
@UIScope
public class IngredienteEditor extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final IngredienteRepository almacen;

	private Ingrediente ingrediente;

	
	/* Fields to edit properties in Ingrediente entity */
	Label title = new Label("Nuevo Ingrediente");
	TextField nombre = new TextField("Nombre");	

	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Ingrediente> binder = new Binder<>(Ingrediente.class);

	
	@Autowired
	public IngredienteEditor(IngredienteRepository almacen) {
		this.almacen = almacen;

		nombre.setMaxLength(32);
		addComponents(title, nombre, acciones);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.forField(nombre)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 32))
		.bind(Ingrediente::getNombre, Ingrediente::setNombre);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(e -> almacen.save(ingrediente));
		guardar.addClickListener(e -> {
			if(binder.isValid())
				almacen.save(ingrediente);
			else
				mostrarNotificacion(new Notification("Algunos campos del formulario deben corregirse"));
		});
		
		borrar.addClickListener(e -> almacen.delete(ingrediente));
		cancelar.addClickListener(e -> editarIngrediente(ingrediente));
		setVisible(false);
	}
	
	private void mostrarNotificacion(Notification notification) {
        notification.setDelayMsec(1500);
        notification.show(Page.getCurrent());
    }

	public interface ChangeHandler {
		void onChange();
	}

	public final void editarIngrediente(Ingrediente c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		
		final boolean persisted = c.getId() != null;
		
		if (persisted)
			ingrediente = almacen.findOne(c.getId());
		else
			ingrediente = c;
		
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(ingrediente);

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
