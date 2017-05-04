package mcuca;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class ZonaEditor extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final ZonaRepository almacen;

	private Zona zona;

	/* Fields to edit properties in Zona entity */
	Label title = new Label("Nueva Zona");
	TextField nombre = new TextField("Nombre");
	TextField aforo = new TextField("Aforo");

	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Zona> binder = new Binder<>(Zona.class);

	@Autowired
	public ZonaEditor(ZonaRepository almacen) {
		this.almacen = almacen;

		addComponents(title, nombre, aforo, acciones);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.bind(nombre, "nombre");
		binder.forField(aforo)
		  .withConverter(
		    new StringToIntegerConverter("Por favor introduce un número"))
		  .bind("aforo");

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(e -> almacen.save(zona));
		borrar.addClickListener(e -> almacen.delete(zona));
		cancelar.addClickListener(e -> editarZona(zona));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editarZona(Zona c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			zona = almacen.findOne(c.getId());
		}
		else {
			zona = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(zona);

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
