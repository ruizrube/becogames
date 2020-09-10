package es.uca.becogames.presentation.pages;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import es.uca.becogames.business.services.UserService;

@Route("passwordrecovery")
@Theme(value = Lumo.class, variant = Lumo.DARK)

public class ChangeUserPasswordPage extends VerticalLayout implements HasUrlParameter<String> {

	UserService service;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8770558921783975441L;

	@Autowired
	public ChangeUserPasswordPage(UserService service) {
		this.service = service;

		this.setHeight("90%");
		this.setJustifyContentMode(JustifyContentMode.START);
		this.setAlignItems(Alignment.CENTER);

		this.setMargin(false);
		this.add(new H1("BecoGAMES"));

	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

	
			Map<String, List<String>> parametersMap = event.getLocation().getQueryParameters().getParameters();

			String token = parametersMap.get("token").get(0);
			Long userId = Long.valueOf(parametersMap.get("userId").get(0));

			if (service.checkValidTokenNonExpired(userId, token)) {

				PasswordField password = new PasswordField("Type your new password");
				this.add(password);

				PasswordField password2 = new PasswordField("Re-type the password");
				this.add(password2);

				Button save = new Button("Change password", evt -> save(userId, password, password2));

				this.add(save);

			} else {
				this.add(new H6("Ups! The password recovery request is not valid"));
				this.add(new RouterLink("Login", SignInPage.class));

			}
		}

	private void save(Long userId, PasswordField password, PasswordField password2) {
		if (password.getValue().equals(password2.getValue())) {
			ConfirmDialog dialog = new ConfirmDialog("Save user", "Do you want to save the new password?", "OK", ev -> {

				if (service.updatePassword(userId, password.getValue())) {
					Notification.show("Password updated!");
				} else {
					Notification.show("Ups! The password was not updated");
				}
				
				UI.getCurrent().navigate(SignInPage.class);


			}, "Cancel", e -> {
			});
			dialog.open();

		} else {
			password.focus();

			Notification.show("Check the data!");
		}

	}

}
