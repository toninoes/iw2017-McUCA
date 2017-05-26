package mcuca.producto;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.ingrediente.Ingrediente;
import mcuca.ingrediente.IngredienteRepository;

@SpringComponent
@UIScope
@SuppressWarnings("serial")
public class ProductoEditor extends VerticalLayout {
	
	private final ProductoRepository repoProducto;
	private final IngredienteRepository repoIngrediente;
	
	private Set<Ingrediente> sSelected;
	
	private Producto producto;
	
	/* Fields to edit properties in Mesa entity */
	Label title = new Label("Nuevo Producto");
	TextField nombre = new TextField("Nombre");
	TextField precio = new TextField("Precio");
	TextField iva = new TextField("I.V.A.");
	TextField foto = new TextField("Foto");
	TwinColSelect<Ingrediente> ingredientes = new TwinColSelect<>("Ingrediente");
	
	
	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Producto> binder = new Binder<>(Producto.class);
	
	@Autowired
	public ProductoEditor(ProductoRepository repoProducto, IngredienteRepository repoIngrediente) {
		this.repoProducto = repoProducto;
		this.repoIngrediente = repoIngrediente; 
		ingredientes.setItems((Collection<Ingrediente>) repoIngrediente.findAll());
		addComponents(title, nombre, precio, iva, foto, ingredientes, acciones);

		// bind using naming convention
		binder.forField(precio)
		  .withNullRepresentation("")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce un número"))
		  .bind("precio");
		
		binder.forField(iva)
		  .withNullRepresentation("")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce un número"))
		  .bind("iva");
		
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(this::salvar);
		borrar.addClickListener(e -> repoProducto.delete(producto));
		cancelar.addClickListener(e -> editarProducto(producto));
		
		ingredientes.addSelectionListener(e -> {
			sSelected = e.getAddedSelection();
		});
		
		setVisible(false);
	}
	
	public void salvar(ClickEvent event) {
		binder.setBean(producto);
		producto.setNombre(nombre.getValue());
		producto.setPrecio(Float.valueOf(precio.getValue().replace(',', '.')));
		producto.setIva(Float.valueOf(iva.getValue()));
		producto.setFoto(foto.getValue());
		producto.setIngredientes(null);
		producto.setIngredientes(sSelected);
		repoProducto.save(producto);
		ProductoView.parrilla.asSingleSelect().setValue(producto);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editarProducto(Producto c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			producto = repoProducto.findOne(c.getId());
		}
		else {
			producto = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(producto);

		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in nombre field automatically
		//nombre.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar
		// is clicked
		guardar.addClickListener(e -> h.onChange());
		borrar.addClickListener(e -> h.onChange());
	}

	public IngredienteRepository getRepoIngrediente() {
		return repoIngrediente;
	}
}
