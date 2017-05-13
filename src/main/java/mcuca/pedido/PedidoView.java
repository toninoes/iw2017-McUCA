package mcuca.pedido;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@SpringView(name = PedidoView.VIEW_NAME)
public class PedidoView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "pedidoView";

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
