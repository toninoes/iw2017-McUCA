package mcuca.pedido;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import mcuca.MainScreen;
import mcuca.cliente.Cliente;
import mcuca.cliente.ClienteRepository;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;

@SpringView(name = PedidoView.VIEW_NAME)
public class PedidoView extends VerticalLayout implements View {
	
	private static final long serialVersionUID = 1L;
	
	public static final String VIEW_NAME = "pedidoView";
	
	public static Grid<Pedido> parrilla;
	public static Grid<LineaPedido> parrillaLineas;
	
	private static ClienteRepository clientes;
	private static PedidoRepository almacen;
	private static LineaPedidoRepository almacen2;
	private final PedidoEditor editor;
	private final LineaPedidoEditor editorLineas;
	final TextField filtro;
	private final Button agregarNuevoBoton;
	
	@Autowired
	public PedidoView(PedidoRepository alm, ClienteRepository cl, PedidoEditor editor, LineaPedidoRepository alm2, 
			          LineaPedidoEditor editor2) {
		almacen = alm;
		clientes = cl;
		almacen2 = alm2;
		this.editor = editor;
		this.editorLineas = editor2;
		parrilla = new Grid<>(Pedido.class);
		parrillaLineas = new Grid<>(LineaPedido.class);
		this.filtro = new TextField();
		this.agregarNuevoBoton = new Button("Nuevo Pedido");
	}

	@PostConstruct
	void init() {
		Label titulo = new Label("Pedidos");
		titulo.setStyleName("h2");
		addComponent(titulo);		
		
		filtro.setPlaceholder("Búsqueda por número");
		HorizontalLayout acciones = new HorizontalLayout();	
		Responsive.makeResponsive(acciones);
		acciones.setSpacing(false);
		acciones.setMargin(false);
		acciones.addComponent(filtro);
		acciones.addComponent(agregarNuevoBoton);
		addComponent(acciones);	
		
		parrilla.setWidth("100%");
		parrilla.setColumns("id", "nombre", "precio", "abierto", "tipo", "cliente", "fecha");
		parrilla.getColumn("nombre").setCaption("Nombre");
		parrilla.getColumn("precio").setCaption("Precio");
		parrilla.getColumn("abierto").setCaption("Abierto");
		parrilla.getColumn("tipo").setCaption("Tipo");
		parrilla.getColumn("cliente").setCaption("Cliente");
		parrilla.getColumn("fecha").setCaption("Fecha");
		
		editor.setWidth("100%");
		
		HorizontalLayout contenido = new HorizontalLayout();
		Responsive.makeResponsive(contenido);
		contenido.setSpacing(false);
		contenido.setMargin(false);
		contenido.setSizeFull();
		
		contenido.addComponent(parrilla);
		contenido.addComponent(editor);
		contenido.setExpandRatio(parrilla, 0.7f);
		contenido.setExpandRatio(editor, 0.3f);
		addComponent(contenido);
		
		editorLineas.setWidth("100%");
		
		Button agregarLineas = new Button("Nueva línea de pedido");
		addComponent(agregarLineas);
		agregarLineas.setVisible(false);
		
		HorizontalLayout contenidoLineas = new HorizontalLayout();
		Responsive.makeResponsive(contenidoLineas);
		contenidoLineas.setSpacing(false);
		contenidoLineas.setMargin(false);
		contenidoLineas.setSizeFull();
		
		parrillaLineas.setWidth("100%");
		parrillaLineas.setColumns("id", "producto", "cantidad", "enCocina");
		parrillaLineas.getColumn("producto").setCaption("Producto");
		parrillaLineas.getColumn("cantidad").setCaption("Cantidad");
		parrillaLineas.getColumn("enCocina").setCaption("En cocina");
		
		contenidoLineas.addComponent(parrillaLineas);
		contenidoLineas.addComponent(editorLineas);
		contenidoLineas.setExpandRatio(parrillaLineas, 0.7f);
		contenidoLineas.setExpandRatio(editorLineas, 0.3f);
		contenidoLineas.setVisible(false);
		addComponent(contenidoLineas);

		// Replace listing with filtered content when user changes filtro
		filtro.setValueChangeMode(ValueChangeMode.LAZY);
		filtro.addValueChangeListener(e -> {
			Long cliente_id = (Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("cliente_id");
			if(cliente_id == null && filtro.getValue().equals(""))
				listarPedidos(0);
			else if(cliente_id != null)
				listarPedidos(cliente_id, Tipo.DOMICILIO);
			else
				listarPedidos(Long.decode(filtro.getValue()).longValue());
		});
		

		// Connect selected Pedido to editor or hide if none is selected
		parrilla.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() != null) {
				
				VaadinSessionSecurityContextHolderStrategy.getSession().setAttribute("pedido_id", e.getValue().getId());
				
				editor.editarPedido(e.getValue());
				//if(e.isUserOriginated())
					//editor.tipos.setSelectedItem(e.getValue().getTipo());
				editor.setVisible(true);
				contenidoLineas.setVisible(true);
				agregarLineas.setVisible(true);
				listarLineasPedidos();
			}
		});
		
		parrillaLineas.asSingleSelect().addValueChangeListener(e -> {
			if(e.getValue() != null) {
				VaadinSessionSecurityContextHolderStrategy.getSession().setAttribute("pedido_id", e.getValue().getId());
				editorLineas.editarLineaPedido(e.getValue());
				//if(e.isUserOriginated())
					//editor.tipos.setSelectedItem(e.getValue().getTipo());
				editorLineas.setVisible(true);
			}
		});

		// Instantiate and edit new Cliente the new button is clicked
		// Instantiate and edit new Pedido the new button is clicked
		agregarNuevoBoton.addClickListener(e -> editor.editarPedido(new Pedido()));
		
		agregarLineas.addClickListener(e -> editorLineas.editarLineaPedido(new LineaPedido()));
		
		
		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			Long cliente_id = (Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("cliente_id");
			if(cliente_id == null && filtro.getValue().equals(""))
				listarPedidos(0);
			else if(cliente_id != null)
				listarPedidos(cliente_id, Tipo.DOMICILIO);
			else
				listarPedidos(Long.decode(filtro.getValue()).longValue());
		});
		
		editorLineas.setChangeHandler(() -> {
			editorLineas.setVisible(false);
			listarLineasPedidos();
		});
		
		// Initialize listing
		if((Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("cliente_id") == null)
			listarPedidos(0);
		else
			listarPedidos((Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("cliente_id"), Tipo.DOMICILIO);
		
		// Pedido reset view
		//MainScreen.navigationBar.addAttachListener(e -> {
		//	cliente_id = 0;
		//	pedido_id = 0;
		//	listarPedidos(0);
		//});
	}

	public static void listarPedidos(long id) {
		if(almacen != null)
			if(id == 0)
				parrilla.setItems((Collection<Pedido>) almacen.findAll());
			else
			{
				Cliente cliente = clientes.findOne(id);
				parrilla.setItems((Collection<Pedido>) almacen.findByCliente(cliente));
			}
	}
	
	public static void listarLineasPedidos() {
		if(almacen2 != null)
			if((Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("pedido_id") != null)
			{
				Long id = (Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("pedido_id");
				Pedido ped = almacen.findOne(id);
				parrillaLineas.setItems((Collection<LineaPedido>) almacen2.findByPedido(ped));
			}		
	}
	
	public static void listarPedidos(Long id, Tipo tipo) {
		Cliente cliente = clientes.findOne(id);
		parrilla.setItems((Collection<Pedido>) almacen.findByCliente(cliente));
	}
	
	public Grid<Pedido> getParrilla() {
		return parrilla;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}