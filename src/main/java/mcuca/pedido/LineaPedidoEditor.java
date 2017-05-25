package mcuca.pedido;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.producto.Producto;
import mcuca.producto.ProductoRepository;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class LineaPedidoEditor extends VerticalLayout {
	
	private final LineaPedidoRepository repoLineaPedido;
	
	private final PedidoRepository repoPedido;
	
	private final ProductoRepository repoProducto;
	
	private LineaPedido lineaPedido;
	/* Fields to edit properties in LineaPedido entity */
	Label title = new Label("Nueva Linea de Pedido");
	NativeSelect<Integer> cantidad = new NativeSelect<>("Cantidad");
	NativeSelect<Producto> producto = new NativeSelect<>("Producto");
	NativeSelect<Boolean> en_cocina = new NativeSelect<>("EnCocina");
	
	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<LineaPedido> binder = new Binder<>(LineaPedido.class);
	
	@Autowired
	public LineaPedidoEditor(LineaPedidoRepository repoLineaPedido, PedidoRepository repoPedido, ProductoRepository repoProducto) {
		this.repoLineaPedido = repoLineaPedido;
		this.repoPedido = repoPedido;
		this.repoProducto = repoProducto;
		cantidad.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		if(this.repoProducto.findAll().iterator().hasNext())	
			producto.setItems((Collection<Producto>) this.repoProducto.findAll());
		en_cocina.setItems(true, false);
		addComponents(title, cantidad, producto, en_cocina, acciones);
		
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
		//guardar.addClickListener(e -> repoLineaPedido.save(lineaPedido));
		guardar.addClickListener(this::salvar);
		borrar.addClickListener(e -> repoLineaPedido.delete(lineaPedido));
		cancelar.addClickListener(e -> editarLineaPedido(lineaPedido));
		setVisible(false);
	}
	
	public void salvar(ClickEvent e) {
		binder.setBean(lineaPedido);
		//pedido.setLineaPedido(linea_pedidos.getValue());
		lineaPedido.setCantidad(cantidad.getValue());
		lineaPedido.setProducto(producto.getValue());
		lineaPedido.setPedidoId(repoPedido.findOne(PedidoView.pedido_id).getId());
		repoLineaPedido.save(lineaPedido);
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
