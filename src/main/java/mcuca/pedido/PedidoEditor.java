package mcuca.pedido;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.VaadinUI;
import mcuca.cierre.CierreCajaRepository;
import mcuca.cliente.ClienteRepository;
import mcuca.establecimiento.Establecimiento;
import mcuca.mesa.Mesa;
import mcuca.mesa.MesaRepository;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;
import mcuca.usuario.Usuario;
import mcuca.usuario.UsuarioRepository;
import mcuca.zona.Zona;
import mcuca.zona.ZonaRepository;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class PedidoEditor extends VerticalLayout {
	
	private final UsuarioRepository repoUsuario;
	private final ZonaRepository repoZona;
	private final ClienteRepository repoCliente;
	private final PedidoRepository repoPedido;
	private final LineaPedidoRepository repoLinea;
	
	private PedidoService pedService;
	@SuppressWarnings("unused")
	private final MesaRepository repoMesa;
	
	VaadinUI ui;
	
	private Pedido pedido;
	
	/* Fields to edit properties in Pedido entity */
	Label title = new Label("Nuevo Pedido");
	TextField nombre = new TextField("Nombre");
	Label precio = new Label();
	NativeSelect<Tipo> tipos = new NativeSelect<>("Tipo");
	ComboBox<Zona> zonas = new ComboBox<>("Zona");
	ComboBox<Mesa> mesas = new ComboBox<>("Mesa");
	
	/* Action buttons */
	Button pdf = new Button("Mandar a cocina");
	Button abierto = new Button("Cerrar Pedido");
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<Pedido> binder = new Binder<>(Pedido.class);
	
	@Autowired
	public PedidoEditor(PedidoRepository repoPedido, ClienteRepository repoCliente, UsuarioRepository repoUsuario, 
			            ZonaRepository repoZona, MesaRepository repoMesa, LineaPedidoRepository linea,
			            CierreCajaRepository cierr, VaadinUI ui) {
		this.ui = ui;
		pedService = new PedidoService(repoPedido, cierr, linea, repoUsuario);
		this.repoLinea = linea;
		this.repoPedido = repoPedido;
		this.repoCliente = repoCliente;
		this.repoUsuario = repoUsuario;
		this.repoZona = repoZona;
		this.repoMesa = repoMesa;
		
		tipos.setItems(Tipo.class.getEnumConstants()); 

		HorizontalLayout layout = new HorizontalLayout(pdf, abierto);
		addComponents(title, layout, nombre, precio, tipos, zonas, mesas, acciones);
		
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		guardar.addClickListener(this::salvar);
		borrar.addClickListener(this::borrar);
		cancelar.addClickListener(e -> editarPedido(pedido));
		abierto.addClickListener(this::cerrar);
		pdf.addClickListener(this::mandarComanda);
		
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
			mesas.setItems((Collection<Mesa>) repoMesa.findByZona(e.getValue()));
			mesas.setVisible(true);
		});
		
		setVisible(false);
	}
	
	
	public void cargarZonas() {
		Usuario u = repoUsuario.findByUsername(
				(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username"));
		
		if (u != null){
			Establecimiento est = u.getEstablecimiento();
			zonas.setItems((Collection<Zona>) repoZona.findByEstablecimiento(est));
		}		
	}
	
	public void borrar(ClickEvent e) {
		pedService.deletePedido(pedido);
	}
	
	public void mandarComanda(ClickEvent e) {
		List<LineaPedido> lineas = repoLinea.findByPedido(pedido);
		pedService.mandarComanda(pedido, lineas);
		for(LineaPedido lp : lineas)
		{
			if(!lp.isEnCocina())
			{
				lp.setEnCocina(true);
				repoLinea.save(lp);
			}			
		}
		
		PedidoView.listarLineasPedidos();
		getUI().getNavigator().navigateTo(PedidoView.VIEW_NAME);
	}
	
	public void salvar(ClickEvent e) {
		binder.setBean(pedido);
		pedido.setNombre(nombre.getValue());
		if(pedido.getId() == null)
			pedido.setPrecio(0.0f);
		
		pedido.setTipo(tipos.getValue());
		pedido.setAbierto(true);
		pedido.setUsuario(
				this.repoUsuario.findByUsername(
						(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username")));
		if(tipos.getValue() == Tipo.DOMICILIO)
			pedido.setCliente(repoCliente.findOne((Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("cliente_id")));
		else if(tipos.getValue() == Tipo.ESTABLECIMIENTO) {
			pedido.setZona(zonas.getValue());
			pedido.setMesa(mesas.getValue());
		}
		pedido.setFecha(new Date());
		repoPedido.save(pedido);
	}
	
	public void cerrar(ClickEvent e) {		
		VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        ui.subwindow.setContent(layout);
        
        Label lab = new Label("Elija el método de pago que el cliente desea usar.");
        layout.addComponent(lab);
        NativeSelect<String> pagos = new NativeSelect<>("Tipo de pago");
        pagos.setItems("En efectivo", "Tarjeta de crédito");
        layout.addComponent(pagos);
        Button but = new Button("Realizar pago", f -> cerrarPedido(pagos.getSelectedItem().get()));
        layout.addComponent(but);
        ui.subwindow.setVisible(true);
	}
	
	private void cerrarPedido(String pago)
	{
		pedido.setAbierto(false);
		repoPedido.save(pedido);
		List<LineaPedido> lineas = repoLinea.findByPedido(pedido);
		pedService.cerrarPedido(pedido, lineas, pago);
		for(LineaPedido lp : lineas)
		{
			if(!lp.isEnCocina())
			{
				lp.setEnCocina(true);
				repoLinea.save(lp);
			}			
		}
		getUI().getNavigator().navigateTo(PedidoView.VIEW_NAME);
	}
	
	public interface ChangeHandler {

		void onChange();
	}
	
	public final void editarPedido(Pedido c) {
		List<LineaPedido> lineas = null;
		boolean lineaAbierta = false;
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			pedido = repoPedido.findOne(c.getId());
			lineas = repoLinea.findByPedido(pedido);
			for(LineaPedido lp : lineas)
				lineaAbierta = !lp.isEnCocina();
			
		}
		else {
			pedido = c;
			
		}
		pdf.setVisible(persisted && lineas != null && lineaAbierta);
		abierto.setVisible(persisted && lineas != null && lineas.size() != 0 && !lineaAbierta);
		cancelar.setVisible(persisted);
		
		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(pedido);
		zonas.setSelectedItem(pedido.getZona());
		if(pedido.getMesa() != null)
			mesas.setSelectedItem(pedido.getMesa());
		
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
