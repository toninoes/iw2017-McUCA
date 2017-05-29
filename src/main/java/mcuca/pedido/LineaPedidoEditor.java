package mcuca.pedido;

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
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.menu.Menu;
import mcuca.menu.MenuRepository;
import mcuca.producto.Producto;
import mcuca.producto.ProductoRepository;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class LineaPedidoEditor extends VerticalLayout {
	
	private final LineaPedidoRepository repoLineaPedido;
	private final PedidoRepository repoPedido;
	private final ProductoRepository repoProducto;
	private final MenuRepository repoMenu;

	private LineaPedido lineaPedido;
	
	private double Total;
	
	/* Fields to edit properties in LineaPedido entity */
	Label title = new Label("Nueva Linea de Pedido");
	NativeSelect<Integer> cantidad = new NativeSelect<>("Cantidad");
	NativeSelect<Producto> producto = new NativeSelect<>("Producto");
	NativeSelect<Menu> menu = new NativeSelect<>("Menu");

	
	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<LineaPedido> binder = new Binder<>(LineaPedido.class);
	
	@Autowired
	public LineaPedidoEditor(LineaPedidoRepository repoLineaPedido, PedidoRepository repoPedido, 
			                 ProductoRepository repoProducto, MenuRepository repoMenu) {
		this.repoLineaPedido = repoLineaPedido;
		this.repoPedido = repoPedido;
		this.repoProducto = repoProducto;
		this.repoMenu = repoMenu;
		cantidad.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);	
		producto.setItems((Collection<Producto>) this.repoProducto.findAll());
		menu.setItems((Collection<Menu>) this.repoMenu.findAll());
		addComponents(title, cantidad, producto, menu, acciones);
		
		//binder.bindInstanceFields(this);
		/*binder.forField(cantidad)
          .withConverter(new StringToIntegerConverter("Por favor introduce un número"))
          .bind("cantidad");
		
		binder.forField(en_cocina)
          .withConverter(new StringToBooleanConverter("Por favor introduce un número"))
          .bind("en_cocina");*/
		
		
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(e -> repoLineaPedido.save(lineaPedido))
		guardar.addClickListener(e -> {
			Total = 0.0f;
			salvar(e);
		});
		borrar.addClickListener(e -> repoLineaPedido.delete(lineaPedido));
		cancelar.addClickListener(e -> editarLineaPedido(lineaPedido));
		
		setVisible(false);
	}
	
	public void salvar(ClickEvent e) {
		binder.setBean(lineaPedido);
		Pedido pedido = repoPedido.findOne(
				(Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("pedido_id"));
		
		//pedido.setPrecio(Total);
		lineaPedido.setCantidad(cantidad.getValue());
		lineaPedido.setProducto(producto.getValue());
		lineaPedido.setEnCocina(false);
		pedido.setPrecio((float)(pedido.getPrecio() + (lineaPedido.getProducto().getPrecio() * lineaPedido.getCantidad())));
		lineaPedido.setPedido(pedido);
		repoLineaPedido.save(lineaPedido);
		
		
		repoPedido.save(pedido);
	}
	
	public interface ChangeHandler {

		void onChange();
	}
	
	public final void editarLineaPedido(LineaPedido c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			lineaPedido = repoLineaPedido.findOne(c.getId());
		}
		else {
			lineaPedido = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(lineaPedido);

		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in numero field automatically
		//cantidad.selectAll();
		if(persisted)
			producto.setSelectedItem(lineaPedido.getProducto());
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar
		// is clicked
		guardar.addClickListener(e -> h.onChange());
		borrar.addClickListener(e -> h.onChange());
	}
	
	public LineaPedidoRepository getRepoProducto() {
		return repoLineaPedido;
	}
}
