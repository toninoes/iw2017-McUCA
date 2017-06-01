package mcuca.producto;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.ingrediente.Ingrediente;
import mcuca.ingrediente.IngredienteRepository;
import mcuca.pedido.LineaPedido;
import mcuca.pedido.LineaPedidoRepository;
import mcuca.pedido.Pedido;
import mcuca.pedido.PedidoRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.SucceededEvent;


@SpringComponent
@UIScope
@SuppressWarnings("serial")
public class ProductoEditor extends VerticalLayout {
	
	private final ProductoRepository repoProducto;
	private final IngredienteRepository repoIngrediente;
	private final LineaPedidoRepository repoLinea;
	private final PedidoRepository repoPedido;
	
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
	public ProductoEditor(ProductoRepository repoProducto, IngredienteRepository repoIngrediente, LineaPedidoRepository linea,
			PedidoRepository ped) {
		this.repoPedido = ped;
		this.repoLinea = linea;
		this.repoProducto = repoProducto;
		this.repoIngrediente = repoIngrediente; 
		ingredientes.setItems((Collection<Ingrediente>) repoIngrediente.findAll());
		
		final Image imagen = new Image();
		class ImageUploader implements Receiver, SucceededListener {
			public File file;
			public OutputStream receiveUpload(String filename, String mimeType){
				FileOutputStream fos = null;
				try{
					file = new File("src/main/webapp/VAADIN/img/"+filename);
					fos = new FileOutputStream(file);
				}catch(final java.io.FileNotFoundException e){
					JOptionPane.showMessageDialog(null, "Error al subir el fichero");
					return null;
				}
				return fos;
			}
	
			public void uploadSucceeded(SucceededEvent event) {
				imagen.setVisible(true);
				imagen.setSource(new FileResource(file));
				imagen.setWidth(200, Unit.PIXELS);
				imagen.setHeight(200, Unit.PIXELS);
				foto.setValue(file.toString());
			}
		};
		
		ImageUploader receiver = new ImageUploader();
		Upload upload = new Upload("Subir fichero", receiver);
		upload.setImmediateMode(true);
		upload.addSucceededListener(receiver);
		foto.setVisible(false);
		
		nombre.setMaxLength(32);
		precio.setMaxLength(5);
		iva.setMaxLength(5);
		foto.setMaxLength(255);
		addComponents(title, nombre, precio, iva, upload, imagen, ingredientes, acciones, foto);

		// bind using naming convention
		//binder.bindInstanceFields(this);
		binder.forField(nombre)
		.asRequired("No puede estar vacío")
		.withValidator(new StringLengthValidator("Este campo debe ser una cadena entre 4 y 32 caracteres", 4, 32))
		.bind(Producto::getNombre, Producto::setNombre);
		
		binder.forField(precio)
		  .withNullRepresentation("")
		  .asRequired("No puede estar vacío")
		  .withConverter(
		    new StringToFloatConverter("Por favor introduce un número decimal"))
		  .bind("precio");
		
		binder.forField(iva)
		  .withNullRepresentation("")
		  .asRequired("No puede estar vacío")
		  .withConverter(
		    new StringToDoubleConverter("Por favor introduce un número decimal"))
		  .bind("iva");

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(this::salvar);
		guardar.addClickListener(e -> {
			if(binder.isValid()){				
				binder.setBean(producto);
				producto.setNombre(nombre.getValue());
				producto.setPrecio(Float.valueOf(precio.getValue().replace(',', '.')));
				producto.setIva(Double.valueOf(iva.getValue()));
				producto.setFoto(foto.getValue());
				producto.setIngredientes(ingredientes.getSelectedItems());
				repoProducto.save(producto);
				ProductoView.parrilla.asSingleSelect().setValue(producto);
			}else
				mostrarNotificacion(new Notification("Algunos campos del formulario deben corregirse"));
		});
		borrar.addClickListener(e -> borrar());
		cancelar.addClickListener(e -> editarProducto(producto));
		
		setVisible(false);
	}
	
	private void mostrarNotificacion(Notification notification) {
        notification.setDelayMsec(1500);
        notification.show(Page.getCurrent());
    }
	
	public void borrar()
	{
		List<LineaPedido> lineas = repoLinea.findByProducto(producto);
		for(LineaPedido linea : lineas)
		{
			Pedido pedido = linea.getPedido();
			pedido.setPrecio(pedido.getPrecio() - (linea.getCantidad() * linea.getProducto().getPrecio()));
			repoPedido.save(pedido);
			repoLinea.delete(linea);
		}
		repoProducto.delete(producto);
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
		nombre.selectAll();
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

	public IngredienteRepository getRepoIngrediente() {
		return repoIngrediente;
	}
}