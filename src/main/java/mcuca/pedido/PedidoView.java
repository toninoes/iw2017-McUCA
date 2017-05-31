package mcuca.pedido;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.data.sort.SortDirection;

import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import mcuca.cliente.Cliente;
import mcuca.cliente.ClienteRepository;
import mcuca.establecimiento.Establecimiento;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;
import mcuca.usuario.Usuario;
import mcuca.usuario.UsuarioRepository;


@SuppressWarnings("serial")
@SpringView(name = PedidoView.VIEW_NAME)
public class PedidoView extends VerticalLayout implements View {
	
	public static final String VIEW_NAME = "pedidoView";
	
	public static Grid<Pedido> parrillaPedido;
	public static Grid<LineaPedido> parrillaLineasPedido;
	
	private static UsuarioRepository repoUsuario;
	private static ClienteRepository repoClientes;
	private static PedidoRepository repoPedido;
	private static LineaPedidoRepository repoLineaPedido;
	
	private final PedidoEditor editorPedido;
	private final LineaPedidoEditor editorLineasPedido;
	
	final TextField filtro;
	private final Button agregarNuevoBoton;
	
	@Autowired
	public PedidoView(PedidoRepository rP, ClienteRepository rC, PedidoEditor eP, LineaPedidoRepository rLP, 
			LineaPedidoEditor eLP, UsuarioRepository rU) {
		
		repoPedido = rP;
		repoClientes = rC;
		repoLineaPedido = rLP;
		repoUsuario = rU; 
		
		this.editorPedido = eP;
		this.editorPedido.cargarZonas();
		this.editorLineasPedido = eLP;
		
		parrillaPedido = new Grid<>(Pedido.class);
		parrillaLineasPedido = new Grid<>(LineaPedido.class);
		
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Pedido");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Pedidos");
		titulo.setStyleName("h2");
		addComponent(titulo);		
		
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(agregarNuevoBoton);
		addComponent(acciones);	
		
		parrillaPedido.setWidth("100%");
		parrillaPedido.setColumns("id", "usuario", "precio", "abierto", "tipo", "cliente", "fecha", "mesa", "zona");
		parrillaPedido.getColumn("usuario").setCaption("Atiende");
		parrillaPedido.getColumn("precio").setCaption("Precio");
		parrillaPedido.getColumn("abierto").setCaption("Abierto");
		parrillaPedido.getColumn("tipo").setCaption("Tipo");
		parrillaPedido.getColumn("cliente").setCaption("Cliente");
		parrillaPedido.getColumn("fecha").setCaption("Fecha");
		parrillaPedido.getColumn("mesa").setCaption("Mesa");
		parrillaPedido.getColumn("zona").setCaption("Zona");
		parrillaPedido.sort("fecha", SortDirection.DESCENDING);
		
		editorPedido.setWidth("100%");
		
		HorizontalLayout contenido = new HorizontalLayout();
		Responsive.makeResponsive(contenido);
		
		contenido.setSpacing(false);
		contenido.setMargin(false);
		contenido.setSizeFull();
		contenido.addComponent(parrillaPedido);
		contenido.addComponent(editorPedido);
		contenido.setExpandRatio(parrillaPedido, 0.7f);
		contenido.setExpandRatio(editorPedido, 0.3f);
		addComponent(contenido);
		
		editorLineasPedido.setWidth("100%");
		
		Button agregarLineas = new Button("Nueva línea de pedido");
		addComponent(agregarLineas);
		agregarLineas.setVisible(false);
		
		HorizontalLayout contenidoLineas = new HorizontalLayout();
		Responsive.makeResponsive(contenidoLineas);
		contenidoLineas.setSpacing(false);
		contenidoLineas.setMargin(false);
		contenidoLineas.setSizeFull();
		
		parrillaLineasPedido.setWidth("100%");
		parrillaLineasPedido.setColumns("id", "producto", "cantidad", "enCocina", "menu");
		parrillaLineasPedido.getColumn("producto").setCaption("Producto");
		parrillaLineasPedido.getColumn("cantidad").setCaption("Cantidad");
		parrillaLineasPedido.getColumn("enCocina").setCaption("En cocina");
		parrillaLineasPedido.getColumn("menu").setCaption("Menu");
		parrillaLineasPedido.sort("enCocina", SortDirection.ASCENDING);
		
		contenidoLineas.addComponent(parrillaLineasPedido);
		contenidoLineas.addComponent(editorLineasPedido);
		contenidoLineas.setExpandRatio(parrillaLineasPedido, 0.7f);
		contenidoLineas.setExpandRatio(editorLineasPedido, 0.3f);
		contenidoLineas.setVisible(false);
		addComponent(contenidoLineas);

		// Connect selected Pedido to editorPedido or hide if none is selected
		parrillaPedido.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() != null) {
				
				VaadinSessionSecurityContextHolderStrategy.getSession().setAttribute("pedido_id", e.getValue().getId());
				
				editorPedido.editarPedido(e.getValue());
				editorPedido.setVisible(true);
				contenidoLineas.setVisible(true);
				agregarLineas.setVisible(true);
				listarLineasPedidos();
			}
		});
		
		parrillaLineasPedido.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() != null) {
				VaadinSessionSecurityContextHolderStrategy.getSession().setAttribute("pedido_id", e.getValue().getId());
				editorLineasPedido.editarLineaPedido(e.getValue());
				editorLineasPedido.setVisible(true);
			}
		});

		// Instantiate and edit new Pedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> {
			parrillaLineasPedido.setVisible(false);
			editorLineasPedido.setVisible(false);
			agregarLineas.setVisible(false);
			editorPedido.editarPedido(new Pedido());	
		});
		
		agregarLineas.addClickListener(e -> { 
			parrillaLineasPedido.setVisible(true);
			editorLineasPedido.editarLineaPedido(new LineaPedido());
		});
		
		
		// Listen changes made by the editorPedido, refresh data from backend
		editorPedido.setChangeHandler(() -> {
			editorPedido.setVisible(false);
			Usuario u = repoUsuario.findByUsername(
				(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username")
			);
			listarPedidos(u);	
		});
		
		editorLineasPedido.setChangeHandler(() -> {
			editorPedido.setVisible(false);
			editorLineasPedido.setVisible(false);
			listarLineasPedidos();	
			
			Usuario u = repoUsuario.findByUsername(
				(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username")
			);
			listarPedidos(u);
		});
		
		// Listado de pedidos al iniciar la vista (los pedidos del establecimiento
		// al cual pertenece el usuario actual).
		Usuario u = repoUsuario.findByUsername(
			(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username")
		);
		listarPedidos(u);		
	}

	public static void listarPedidos(Usuario u) {
		if (u != null){
			Establecimiento est = u.getEstablecimiento();
			parrillaPedido.setItems((Collection<Pedido>) repoPedido.findByMiEstablecimiento(est));
		}else
			parrillaPedido.setItems((Collection<Pedido>) repoPedido.findAll());
	}
	
	public static void listarLineasPedidos() {
		if(repoLineaPedido != null)
			if((Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("pedido_id") != null)
			{
				Long id = (Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("pedido_id");
				Pedido ped = repoPedido.findOne(id);
				parrillaLineasPedido.setItems((Collection<LineaPedido>) repoLineaPedido.findByPedido(ped));
			}		
	}
	
	public static void listarPedidos(Long id, Tipo tipo) {
		Cliente cliente = repoClientes.findOne(id);
		parrillaPedido.setItems((Collection<Pedido>) repoPedido.findByCliente(cliente));
	}
	
	public Grid<Pedido> getParrillaPedido() {
		return parrillaPedido;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}