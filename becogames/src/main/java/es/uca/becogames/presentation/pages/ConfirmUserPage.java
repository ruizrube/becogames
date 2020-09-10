package es.uca.becogames.presentation.pages;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import es.uca.becogames.business.services.UserService;

@Route("confirmuser")
@Theme(value = Lumo.class, variant = Lumo.DARK)


public class ConfirmUserPage extends VerticalLayout implements HasUrlParameter<String> {

	UserService service;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8770558921783975441L;

	@Autowired
	public ConfirmUserPage(UserService service) {
		this.service=service;

		this.setHeight("90%");
		this.setJustifyContentMode(JustifyContentMode.START);
		this.setAlignItems(Alignment.CENTER);

		this.setMargin(false);
		this.add(new H1("BecoGAMES"));

		this.add(new RouterLink("Login", SignInPage.class));

	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

			Map<String, List<String>> parametersMap = event.getLocation().getQueryParameters().getParameters();

			String token = parametersMap.get("token").get(0);
			Long userId = Long.valueOf(parametersMap.get("userId").get(0));

			
			if(service.checkValidToken(userId,token)) {
				service.activate(userId);
				this.add(new H6("Your account has been activated"));
			} else {
				this.add(new H6("Ups! Your account has NOT been activated"));
				
			}
		
		
	}

}
