package mcuca.pedido;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
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

import mcuca.cliente.ClienteRepository;
import mcuca.mesa.Mesa;
import mcuca.mesa.MesaRepository;
import mcuca.security.SecurityUtils;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;
import mcuca.usuario.UsuarioRepository;
import mcuca.zona.Zona;
import mcuca.zona.ZonaRepository;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class PedidoEditor extends VerticalLayout {
	
	@SuppressWarnings("unused")
	private final UsuarioRepository repoUsuario;
	@SuppressWarnings("unused")
	private final ZonaRepository repoZona;
	private final ClienteRepository repoCliente;
	private final PedidoRepository repoPedido;
	@SuppressWarnings("unused")
	private final MesaRepository repoMesa;
	
	private Pedido pedido;
	
	/* Fields to edit properties in Pedido entity */
	Label title = new Label("Nuevo Pedido");
	TextField nombre = new TextField("Nombre");
	Label precio = new Label();
	NativeSelect<Tipo> tipos = new NativeSelect<>("Tipo");
	NativeSelect<Zona> zonas = new NativeSelect<>("Zona");
	NativeSelect<Mesa> mesas = new NativeSelect<>("Mesa");
	
	/* Action buttons */
	Button abierto = new Button("Cerrar Pedido");
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Pedido> binder = new Binder<>(Pedido.class);
	
	@Autowired
	public PedidoEditor(PedidoRepository repoPedido, ClienteRepository repoCliente, UsuarioRepository repoUsuario, 
			            ZonaRepository repoZona, MesaRepository repoMesa) {
		
		this.repoPedido = repoPedido;
		this.repoCliente = repoCliente;
		this.repoUsuario = repoUsuario;
		this.repoZona = repoZona;
		this.repoMesa = repoMesa;
		
		tipos.setItems(Tipo.class.getEnumConstants()); 
		//ArrayList<Cliente> ac = (ArrayList<Cliente>) repoCliente.findAll();
		//if(ac.size() > 0)
			//clientes.setItems(ac.get(0).getId());
		zonas.setItems((Collection<Zona>) repoZona.findAll());
		mesas.setItems((Collection<Mesa>) repoMesa.findAll());
		addComponents(title, abierto, nombre, precio, tipos, zonas, mesas, acciones);
		
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(this::salvar);
		borrar.addClickListener(e -> repoPedido.delete(pedido));
		cancelar.addClickListener(e -> editarPedido(pedido));
		abierto.addClickListener(this::cerrar);
		
		zonas.setVisible(false);
		mesas.setVisible(false);
		
		tipos.addSelectionListener(e -> {
			zonas.setVisible(false);
			mesas.setVisible(false);
			if(tipos.getValue() == Tipo.ESTABLECIMIENTO) {
				zonas.setVisible(true);
			}
		});
		
		zonas.addSelectionListener(e -> {
			mesas.setVisible(true);
		});
		
		setVisible(false);
	}
	
	public void salvar(ClickEvent e) {
		binder.setBean(pedido);
		pedido.setNombre(nombre.getValue());
		pedido.setPrecio(pedido.getPrecio());
		pedido.setTipo(tipos.getValue());
		pedido.setUsuario(
				this.repoUsuario.findByUsername(
						(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username")));
		if(tipos.getValue() == Tipo.DOMICILIO)
			pedido.setCliente(repoCliente.findOne(PedidoView.cliente_id));
		else if(tipos.getValue() == Tipo.ESTABLECIMIENTO) {
			pedido.setZona(zonas.getValue());
			pedido.setMesa(mesas.getValue());
		}
		pedido.setFecha(new Date());
		repoPedido.save(pedido);
	}
	
	public void cerrar(ClickEvent e) {
		binder.setBean(pedido);
		pedido.setAbierto(false);
		repoPedido.save(pedido);
	}
	
	public interface ChangeHandler {

		void onChange();
	}
	
	public final void editarPedido(Pedido c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			pedido = repoPedido.findOne(c.getId());
		}
		else {
			pedido = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(pedido);
		
		tipos.setSelectedItem(pedido.getTipo());
		
		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar
		// is clicked
		guardar.addClickListener(e -> h.onChange());
		borrar.addClickListener(e -> h.onChange());
		abierto.addClickListener(e -> h.onChange());
	}
	
	public PedidoRepository getRepoPedido() {
		return repoPedido;
	}
}
