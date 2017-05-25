package mcuca.menu;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToFloatConverter;
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

public class MenuEditor  extends VerticalLayout{

	private final MenuRepository almacen;

	private Menu menu;

	/* Fields to edit properties in Menu entity */
	Label title = new Label("Nuevo Menu");
	TextField nombre = new TextField("Nombre");
	TextField descripcion = new TextField("Descripcion");
	TextField descuento = new TextField("Descuento");
	TextField precio = new TextField("Precio");
	TextField iva = new TextField("IVA");
	//TextField esOferta = new TextField("Oferta");
	
	/* Action buttons */	
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Menu> binder = new Binder<>(Menu.class);
	
	@Autowired
	public MenuEditor(MenuRepository almacen) {
		this.almacen = almacen;

		addComponents(title, nombre, descripcion, descuento, precio, iva,  acciones);
		
		binder.forField(descuento)
		  .withNullRepresentation("")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce el descuento"))
		  .bind("descuento");
		
		binder.forField(iva)
		  .withNullRepresentation("")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce el iva"))
		  .bind("iva");
		
		binder.forField(precio)
		  .withNullRepresentation("")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce el precio"))
		  .bind("precio");
		
		/*binder.forField(esOferta)
		  .withNullRepresentation("")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce si es oferta"))
		  .bind("esOferta");*/
		
		binder.bindInstanceFields(this);
		
		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		
		// wire action buttons to guardar, borrar and reset
				guardar.addClickListener(e -> guardarMenu(menu));
				borrar.addClickListener(e -> almacen.delete(menu));
				cancelar.addClickListener(e -> editarMenu(menu));
				setVisible(false);
			}
	
	public interface ChangeHandler {

		void onChange();
	}
	
	public void guardarMenu(Menu menu){
		menu.setEsOferta(menu.getDescuento() != 0);
		almacen.save(menu);
	}
	
	public final void editarMenu(Menu m) {
		if (m == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = m.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			menu = almacen.findOne(m.getId());
		}
		else {
			menu = m;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(menu);

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
