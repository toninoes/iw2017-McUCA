package mcuca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.security.AccessDeniedView;
import mcuca.security.ErrorView;
import mcuca.security.LoginScreen;
import mcuca.security.SecurityUtils;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;

@Title("McUCA")
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("material")
@SuppressWarnings("serial")
@SpringUI
public class VaadinUI extends UI {

	@Autowired
	SpringViewProvider viewProvider;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	MainScreen mainScreen;


	@Override
	protected void init(VaadinRequest request) {
		
		Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);
        

	   	this.getUI().getNavigator().setErrorView(ErrorView.class);
		viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);

		if (SecurityUtils.isLoggedIn()) {
			showMainScreen();
		} else {
			showLoginScreen();
		}

	}

	private void showLoginScreen() {
		setContent(new LoginScreen(this::login));
	}

	private void showMainScreen() {
		int rol = -1;
		if(SecurityUtils.hasRole("GERENTE"))
			rol = 0;
		else if(SecurityUtils.hasRole("ENCARGADO"))
			rol = 1;
		else if(SecurityUtils.hasRole("CAMARERO"))
			rol = 2;
		mainScreen.setAuth(rol);
		setContent(mainScreen);
	}


	private boolean login(String username, String password) {
		try {
			Authentication token = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			// Reinitialize the session to protect against session fixation
			// attacks. This does not work with websocket communication.
			VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
			SecurityContextHolder.getContext().setAuthentication(token);
			VaadinSessionSecurityContextHolderStrategy.getSession().setAttribute("username", username);
			// Show the main UI
			showMainScreen();
			return true;
		} catch (AuthenticationException ex) {
			return false;
		}
	}


}
