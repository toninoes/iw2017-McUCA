package mcuca.security;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial", "deprecation" })
public class LoginScreen extends VerticalLayout {

  
	public LoginScreen(LoginCallback callback) {
        setMargin(true);
        setSpacing(true);
        
        Label titulo = new Label("McUCA");
		titulo.setStyleName("h1");
		addComponent(titulo);
		
		FormLayout form = new FormLayout();
		form.setMargin(true);
        TextField username = new TextField("Usuario");
        username.setIcon(FontAwesome.USER);
        form.addComponent(username);

        PasswordField password = new PasswordField("Contraseña");
        password.setIcon(FontAwesome.KEY);
        form.addComponent(password);

        Button login = new Button("Entrar", evt -> {
            String pword = password.getValue();
            password.setValue("");
            if (!callback.login(username.getValue(), pword)) {
                Notification.show("Autenticación fallida");
                username.focus();
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        form.addComponent(login);
        addComponent(form);
    }

    @FunctionalInterface
    public interface LoginCallback {

        boolean login(String username, String password);
    }
	
}
