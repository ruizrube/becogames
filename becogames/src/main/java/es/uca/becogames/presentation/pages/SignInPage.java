package es.uca.becogames.presentation.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.login.AbstractLogin.ForgotPasswordEvent;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import es.uca.becogames.business.entities.Profile;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.presentation.views.HomeView;
import es.uca.becogames.presentation.views.ProfileView;
import es.uca.becogames.security.SecurityUtils;

@Route("login")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class SignInPage extends VerticalLayout {

	UserService service;
	AuthenticationManager authenticationManager;
	LoginOverlay loginForm;

	@Autowired
	public SignInPage(UserService service, AuthenticationManager authenticationManager) {
		this.service = service;
		this.authenticationManager = authenticationManager;
		
		loginForm = new LoginOverlay();
		loginForm.setOpened(true);
		loginForm.setTitle("BECOGames");
		loginForm.setDescription("BEhaviour eCOnomics Games");
		loginForm.setForgotPasswordButtonVisible(true);
		loginForm.addLoginListener(e -> signIn(e));
		loginForm.addForgotPasswordListener(e->recoverPassword(e));
		add(loginForm);

	}

	private void recoverPassword(ForgotPasswordEvent e) {
		
		Dialog myDialog=new Dialog();
		
		H3 title=new H3("Password recovery");
		TextField field=new TextField("Type your email address");
		Button ok=new Button("Recover!");
		myDialog.add(title, field,ok);
		
		Notification notification = new Notification();
		notification.setDuration(0);
		notification.setPosition(Position.MIDDLE);
		
		NativeButton buttonInside = new NativeButton("OK");
		buttonInside.addClickListener(event -> notification.close());
		
		ok.addClickListener(ex->{
			if(service.recoverPassword(field.getValue())) {
				Label content = new Label("You will receive an email with instructions to change your password. If you do not see the email in your Inbox, check your Spam/Bulk/Junk folder.");
				notification.add(content, buttonInside);				
				notification.open();
			} else {
				Label content = new Label("That email is not registered in our system");
				notification.add(content, buttonInside);				
				notification.open();				
			}
			myDialog.getUI().ifPresent(ui -> ui.navigate(WelcomePage.class));
			myDialog.close();
	
		});
		
		myDialog.open();
	}

	private void signIn(LoginEvent e) {

		if (authenticate(e.getUsername(), e.getPassword())) {
			loginForm.close();
			navigateToMainView();
		} else {
			loginForm.setError(true);
		}
	}

	public boolean authenticate(String username, String password) {

		try {
			Authentication token = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			// Reinitialize the session to protect against session fixation
			// attacks. This does not work with websocket communication.
			SecurityContextHolder.getContext().setAuthentication(token);
			return true;
		} catch (AuthenticationException ex) {
			return false;
		}

	}

	private void navigateToMainView() {
		User currentUser = service.findByUsername(SecurityUtils.getUsername());

		UI.getCurrent().getSession().setAttribute(User.class, currentUser);
		UI.getCurrent().setPollInterval(5000);
		//VaadinSession.getCurrent().getSession().setMaxInactiveInterval(60);

		//VaadinSession.getCurrent().setErrorHandler(errorHandler);

		if (VaadinSession.getCurrent().getAttribute("intendedPath") != null) {
			UI.getCurrent().navigate(VaadinSession.getCurrent().getAttribute("intendedPath").toString());
		} else {
			UI.getCurrent().navigate(HomeView.class);
		}

		if (currentUser.getProfile().equals(Profile.NO_DEFINED_PROFILE)) {

			ConfirmDialog profileDialog = new ConfirmDialog("Hey! It seems you have not defined your profile yet...",
					"Do you want to define it now?", "Yes", event -> {
						UI.getCurrent().navigate(ProfileView.class);
					}, "Later", event -> {
					});

			profileDialog.open();

		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304492736395275231L;

}
