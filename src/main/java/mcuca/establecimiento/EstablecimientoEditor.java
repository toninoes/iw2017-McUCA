package mcuca.establecimiento;

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

import mcuca.cierre.CierreCaja;
import mcuca.cierre.CierreCajaRepository;


@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class EstablecimientoEditor extends VerticalLayout {

	private final EstablecimientoRepository repoEst;
	
	private final CierreCajaRepository repoCierre;

	private Establecimiento est;

	/* Fields to edit properties in Establecimiento entity */
	Label title = new Label("Nuevo Establecimiento");
	TextField nombre = new TextField("Nombre");
	TextField domicilio = new TextField("Domicilio");

	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Establecimiento> binder = new Binder<>(Establecimiento.class);

	@Autowired
	public EstablecimientoEditor(EstablecimientoRepository almacen, CierreCajaRepository repoCierre) {
		this.repoCierre = repoCierre;
		repoEst = almacen;

		nombre.setMaxLength(32);
		domicilio.setMaxLength(128);
		
		addComponents(title, nombre, domicilio, acciones);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.forField(nombre)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 32))
		.bind(Establecimiento::getNombre, Establecimiento::setNombre);
		
		binder.forField(domicilio)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 128 caracteres", 4, 128))
		.bind(Establecimiento::getDomicilio, Establecimiento::setDomicilio);


		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(e -> almacen.save(est));
		guardar.addClickListener(e -> {
			if(binder.isValid())
			{
				repoEst.save(est);
				CierreCaja cierre = new CierreCaja(0.0f);
				cierre.setEstablecimiento(est);
				repoCierre.save(cierre);
			}
			else
				mostrarNotificacion(new Notification("Algunos campos del formulario deben corregirse"));
		});
		
		borrar.addClickListener(e -> almacen.delete(est));
		cancelar.addClickListener(e -> editarEstablecimiento(est));
		setVisible(false);
	}
	
	private void mostrarNotificacion(Notification notification) {
        notification.setDelayMsec(1500);
        notification.show(Page.getCurrent());
    }

	public interface ChangeHandler {
		void onChange();
	}

	public final void editarEstablecimiento(Establecimiento c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		
		final boolean persisted = c.getId() != null;
		
		if (persisted)
			est = repoEst.findOne(c.getId());
		else 
			est = c;
		
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(est);

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
