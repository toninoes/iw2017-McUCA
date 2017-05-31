package mcuca;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.cierre.CierreCaja;
import mcuca.cierre.CierreCajaRepository;
import mcuca.cliente.ClienteView;
import mcuca.establecimiento.EstablecimientoView;
import mcuca.ingrediente.IngredienteView;
import mcuca.menu.MenuView;
import mcuca.mesa.MesaView;
import mcuca.pedido.LineaPedidoRepository;
import mcuca.pedido.PedidoRepository;
import mcuca.pedido.PedidoService;
import mcuca.pedido.PedidoView;
import mcuca.producto.ProductoView;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;
import mcuca.usuario.Usuario;
import mcuca.usuario.UsuarioManagementView;
import mcuca.usuario.UsuarioRepository;
import mcuca.zona.ZonaView;


@SuppressWarnings({ "serial", "deprecation" })
@SpringViewDisplay
public class MainScreen extends VerticalLayout implements ViewDisplay {

	private Panel springViewDisplay;
	
	private PedidoService pedidoService;
	
	@Autowired
	private PedidoRepository pedidoRepo;
	
	@Autowired
	private CierreCajaRepository cierresCaja;
	
	@Autowired
	private LineaPedidoRepository lps;
	
	@Autowired
	private UsuarioRepository userRepo;
	
	public static CssLayout navigationBar;
	
	private Button cerrarCaja;
	
	@Override
    public void attach() {
        super.attach();
        this.getUI().getNavigator().navigateTo("");
    }

	@PostConstruct
	void init() {

		final VerticalLayout root = new VerticalLayout();
		Responsive.makeResponsive(root);
		root.setSizeFull();

		// Creamos la cabecera
		Label titulo = new Label("McUCA");
		titulo.setStyleName("h1");
		root.addComponent(titulo);
		root.setComponentAlignment(titulo, Alignment.MIDDLE_CENTER);
		/*
		Button probar = new Button("PDF", event -> {
			try {
				topdf();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		root.addComponent(probar);
*/
		Button logoutButton = new Button("Salir", event -> logout());
		logoutButton.setIcon(FontAwesome.POWER_OFF);
		root.addComponent(logoutButton);
		
		cerrarCaja = new Button("Cerrar caja", event -> cerrarCaja());
		cerrarCaja.setIcon(FontAwesome.CLOCK_O);
		cerrarCaja.setVisible(false);
		HorizontalLayout btn = new HorizontalLayout(logoutButton, cerrarCaja);
		root.addComponent(btn);
		
		// Creamos la barra de navegación
		navigationBar = new CssLayout();
		navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		root.addComponent(navigationBar);

		// Creamos el panel
		springViewDisplay = new Panel();
		springViewDisplay.setSizeFull();
		root.addComponent(springViewDisplay);
		root.setExpandRatio(springViewDisplay, 1.0f);

		addComponent(root);
	}
	
	public void setAuth(int rol)
	{
		switch(rol) {
		case 0: addBarraGerente();
				break;
		case 1: addBarraEncargado();
				break;
		case 2: addBarraCamarero();
				break;
		default: break;
		}
	}
	
	private void addBarraGerente() 
	{
		navigationBar.addComponent(createNavigationButton("Estb", EstablecimientoView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Zona", ZonaView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Mesa", MesaView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Ingr", IngredienteView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Prod", ProductoView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Menu", MenuView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("UsMan", UsuarioManagementView.VIEW_NAME));
	}
	
	private void addBarraEncargado()
	{
		cerrarCaja.setVisible(true);
		addBarraCamarero();
	}
	
	private void addBarraCamarero()
	{
		navigationBar.addComponent(createNavigationButton("Clte", ClienteView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Pedi", PedidoView.VIEW_NAME));
	}

	private Button createNavigationButton(String caption, final String viewName) {
		Button button = new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_SMALL);

		button.addClickListener(event -> {
			//PedidoView.cliente_id = 0;
			//PedidoView.pedido_id = 0;
			//PedidoView.listarPedidos(0L);
			getUI().getNavigator().navigateTo(viewName);
		});
		return button;
	}
	
	private void cerrarCaja() 
	{
		Usuario u = userRepo.findByUsername(
			(String)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("username")
		);
		
		if(u != null) {
			pedidoService = new PedidoService(pedidoRepo, cierresCaja, lps, userRepo);
			CierreCaja cierre = new CierreCaja(pedidoService.getRecaudacion());
			cierre.setEstablecimiento(u.getEstablecimiento());
			cierresCaja.save(cierre);
			Notification.show("Recaudado: " + cierre.getRecaudacion() + " €");
		}
	}


	@Override
	public void showView(View view) {
		springViewDisplay.setContent((Component) view);
	}


	private void logout() {
		getUI().getPage().reload();
		getSession().close();
	}
	
	
	
	/*
	private void topdf() throws DocumentException, FileNotFoundException {
		Document doc = new Document();
		FileOutputStream fichero = new FileOutputStream("fichero.pdf");
		
		PdfWriter.getInstance(doc, fichero).setInitialLeading(40);
		doc.open();
		
		doc.add(new Paragraph("McUCA - Ticket"));
		doc.add(new Paragraph(""));
		PdfPTable tabla = new PdfPTable(3);
		tabla.addCell("Producto"); tabla.addCell("Cantidad"); tabla.addCell("Precio");
		tabla.addCell("Hamburguesa de queso"); tabla.addCell("1"); tabla.addCell("4.95 €");
		tabla.addCell("Patatas"); tabla.addCell("2"); tabla.addCell("3.50 €");
		tabla.addCell("Coca-Cola"); tabla.addCell("1"); tabla.addCell("1.20 €");
		tabla.addCell("Total"); tabla.addCell(""); tabla.addCell("9.65 €");
		doc.add(tabla);
		doc.close();
		Notification.show("PDF generado");
	}*/
}