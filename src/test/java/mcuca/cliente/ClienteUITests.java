package mcuca.cliente;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.*;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.boot.VaadinAutoConfiguration;

import mcuca.cliente.Cliente;
import mcuca.cliente.ClienteEditor;
import mcuca.cliente.ClienteRepository;
import mcuca.cliente.ClienteUI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClienteUITests.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClienteUITests {

	@Autowired ClienteRepository repository;

	VaadinRequest vaadinRequest = Mockito.mock(VaadinRequest.class);

	ClienteEditor editor;

	ClienteUI vaadinUI;

	@Before
	public void setup() {
		this.editor = new ClienteEditor(this.repository);
		this.vaadinUI = new ClienteUI(this.repository, editor);
	}

	@Test
	public void shouldInitializeTheGridWithCustomerRepositoryData() {
		int customerCount = (int) this.repository.count();

		vaadinUI.init(this.vaadinRequest);

		then(vaadinUI.parrilla.getColumns()).hasSize(3);
		then(getCustomersInGrid()).hasSize(customerCount);
	}

	private List<Cliente> getCustomersInGrid() {
		ListDataProvider<Cliente> ldp = (ListDataProvider) vaadinUI.parrilla.getDataProvider();
		return new ArrayList<>(ldp.getItems());
	}

	@Test
	public void shouldFillOutTheGridWithNewData() {
		int initialCustomerCount = (int) this.repository.count();
		this.vaadinUI.init(this.vaadinRequest);
		customerDataWasFilled(editor, "Marcin", "Grzejszczak", "calle cualquiera", "956887845");

		this.editor.guardar.click();

		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1);

		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
			.extracting("nombre", "apellidos")
			.containsExactly("Marcin", "Grzejszczak");

	}

	@Test
	public void shouldFilterOutTheGridWithTheProvidedLastName() {
		this.vaadinUI.init(this.vaadinRequest);
		this.repository.save(new Cliente("Josh", "Long", "calle abc", "123456789"));

		vaadinUI.listarClientes("Long");

		then(getCustomersInGrid()).hasSize(1);
		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
			.extracting("nombre", "apellidos", "domicilio", "telefono")
			.containsExactly("Josh", "Long", "calle abc", "123456789");
	}

	@Test
	public void shouldInitializeWithInvisibleEditor() {
		this.vaadinUI.init(this.vaadinRequest);

		then(this.editor.isVisible()).isFalse();
	}

	@Test
	public void shouldMakeEditorVisible() {
		this.vaadinUI.init(this.vaadinRequest);
		Cliente first = getCustomersInGrid().get(0);
		this.vaadinUI.parrilla.select(first);

		then(this.editor.isVisible()).isTrue();
	}

	private void customerDataWasFilled(ClienteEditor editor, String nombre,
			String apellidos, String domicilio, String telefono) {
		this.editor.nombre.setValue(nombre);
		this.editor.apellidos.setValue(apellidos);
		this.editor.domicilio.setValue(domicilio);
		this.editor.telefono.setValue(telefono.toString());
		editor.editarCliente(new Cliente(nombre, apellidos, domicilio, telefono));
	}

	@Configuration
	@EnableAutoConfiguration(exclude = VaadinAutoConfiguration.class)
	static class Config {

		@Autowired
		ClienteRepository repository;

		@PostConstruct
		public void initializeData() {
			this.repository.save(new Cliente("Jack", "Bauer", "calle a", "111111111"));
			this.repository.save(new Cliente("Chloe", "O'Brian", "calle b", "222222222"));
			this.repository.save(new Cliente("Kim", "Bauer", "calle c", "333333333"));
			this.repository.save(new Cliente("David", "Palmer", "calle d", "444444444"));
			this.repository.save(new Cliente("Michelle", "Dessler", "calle e", "555555555"));
		}
	}
}
