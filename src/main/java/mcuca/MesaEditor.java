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
public class MesaEditor extends VerticalLayout{

	private static final long serialVersionUID = 1L;

	private final MesaRepository almacen;

	private Mesa mesa;

	/* Fields to edit properties in Mesa entity */
	Label title = new Label("Nueva Mesa");
	TextField numero = new TextField("Número");
	
	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Mesa> binder = new Binder<>(Mesa.class);

	@Autowired
	public MesaEditor(MesaRepository almacen) {
		this.almacen = almacen;

		addComponents(title, numero, acciones);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.forField(numero)
		  .withConverter(
		    new StringToIntegerConverter("Por favor introduce un número"))
		  .bind("numero");

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(e -> almacen.save(mesa));
		borrar.addClickListener(e -> almacen.delete(mesa));
		cancelar.addClickListener(e -> editarMesa(mesa));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editarMesa(Mesa c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			mesa = almacen.findOne(c.getId());
		}
		else {
			mesa = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(mesa);

		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in numero field automatically
		numero.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar
		// is clicked
		guardar.addClickListener(e -> h.onChange());
		borrar.addClickListener(e -> h.onChange());
	}
}