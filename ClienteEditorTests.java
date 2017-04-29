package mcuca;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import mcuca.Cliente;
import mcuca.ClienteEditor;
import mcuca.ClienteRepository;

import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.argThat;

@RunWith(MockitoJUnitRunner.class)
public class ClienteEditorTests {

	private static final String NOMBRE = "Marcin";
	private static final String APELLIDOS = "Grzejszczak";
	private static final String DOMICILIO = "calle no se que";
	private static final String TELEFONO = "956556677";

	@Mock ClienteRepository customerRepository;
	@InjectMocks ClienteEditor editor;

	@Test
	public void shouldStoreCustomerInRepoWhenEditorSaveClicked() {
		this.editor.nombre.setValue(NOMBRE);
		this.editor.apellidos.setValue(APELLIDOS);
		this.editor.domicilio.setValue(DOMICILIO);
		this.editor.telefono.setValue(TELEFONO);
		customerDataWasFilled();

		this.editor.guardar.click();

		then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()));
	}

	@Test
	public void shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		this.editor.nombre.setValue(NOMBRE);
		this.editor.apellidos.setValue(APELLIDOS);
		this.editor.domicilio.setValue(DOMICILIO);
		this.editor.telefono.setValue(TELEFONO);
		customerDataWasFilled();

		editor.borrar.click();

		then(this.customerRepository).should().delete(argThat(customerMatchesEditorFields()));
	}

	private void customerDataWasFilled() {
		this.editor.editarCliente(new Cliente(NOMBRE, APELLIDOS, DOMICILIO, TELEFONO));
	}

	private TypeSafeMatcher<Cliente> customerMatchesEditorFields() {
		return new TypeSafeMatcher<Cliente>() {
			@Override
			public void describeTo(Description description) {}

			@Override
			protected boolean matchesSafely(Cliente item) {
				return NOMBRE.equals(item.getNombre()) && APELLIDOS.equals(item.getApellidos())
						&& DOMICILIO.equals(item.getDomicilio()) && TELEFONO.equals(item.getTelefono());
			}
		};
	}

}
