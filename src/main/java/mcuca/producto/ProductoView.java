package mcuca.producto;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
@SpringView(name = ProductoView.VIEW_NAME)
public class ProductoView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "productoView";

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
