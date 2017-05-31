package mcuca.mesa;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.establecimiento.EstablecimientoRepository;
import mcuca.zona.Zona;
import mcuca.zona.ZonaRepository;

@SpringComponent
@UIScope
public class MesaEditor extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final MesaRepository repoMesa;
	private final ZonaRepository repoZona;
	private final EstablecimientoRepository repoEst;

	private Mesa mesa;

	/* Fields to edit properties in Mesa entity */
	Label title = new Label("Nueva Mesa");
	TextField numero = new TextField("Número");
	ComboBox<Zona> zonas = new ComboBox<>("Zona");
	
	
	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Mesa> binder = new Binder<>(Mesa.class);

	@Autowired
	public MesaEditor(MesaRepository repoMesa, ZonaRepository repoZona, EstablecimientoRepository repoEst) {
		this.repoMesa = repoMesa;
		this.repoZona = repoZona;
		this.repoEst = repoEst;
		zonas.setItems((Collection<Zona>) repoZona.findAll());
		numero.setMaxLength(2);
		addComponents(title, numero, zonas, acciones);
		
		//binder.bindInstanceFields(this);
		binder.forField(numero)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 1 y 2 caracteres", 1, 2))
		.bind(Mesa::getNumero, Mesa::setNumero);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(this::salvar);
		guardar.addClickListener(e -> {
			if(binder.isValid())
				repoMesa.save(mesa);
			else
				mostrarNotificacion(new Notification("Algunos campos del formulario deben corregirse"));
		});
		borrar.addClickListener(e -> repoMesa.delete(mesa));
		cancelar.addClickListener(e -> editarMesa(mesa));
		setVisible(false);
	}
	
	private void mostrarNotificacion(Notification notification) {
        notification.setDelayMsec(1500);
        notification.show(Page.getCurrent());
    }
	
	public void salvar(ClickEvent e) {
		binder.setBean(mesa);
		mesa.setZona(zonas.getValue());
		repoMesa.save(mesa);
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
			mesa = repoMesa.findOne(c.getId());
		}
		else {
			mesa = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(mesa);
		zonas.setSelectedItem(mesa.getZona());

		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in numero field automatically
		numero.selectAll();
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

	public ZonaRepository getRepoZona() {
		return repoZona;
	}

	public EstablecimientoRepository getRepoEst() {
		return repoEst;
	}
}
