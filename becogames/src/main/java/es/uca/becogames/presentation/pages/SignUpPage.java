package es.uca.becogames.presentation.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.OrganizationService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.presentation.components.UserForm;

@Route("signup")
@Theme(value = Lumo.class, variant = Lumo.DARK)


public class SignUpPage extends VerticalLayout {

	H1 label = new H1("Register new user");
	UserForm userForm=new UserForm();
	
	
	@Autowired
	public SignUpPage(UserService userService, OrganizationService orgService) {

		this.setAlignItems(Alignment.CENTER);

		add(label);
		
		userForm.setOrganizationItems(orgService.findAll());
		userForm.setBean(new User());
		userForm.setRoleVisible(false);
		userForm.setEnabledVisible(false);
		
		Notification notification = new Notification();
		notification.setDuration(0);
		notification.setPosition(Position.MIDDLE);
		
		NativeButton buttonInside = new NativeButton("OK");
		buttonInside.addClickListener(event -> notification.close());
		
		
		// Listen changes made by the editor
		userForm.addSaveListener(user -> {			
			userService.create(user);
			Label content = new Label("You will receive an email with instructions to activate your account. If you do not see the email in your Inbox, check your Spam/Bulk/Junk folder. ");
			notification.add(content, buttonInside);				
			notification.open();
			UI.getCurrent().navigate(WelcomePage.class);
		});

		userForm.addCancelListener(() -> {			
			UI.getCurrent().navigate(WelcomePage.class);
		});

		add(userForm);
		
			
	}

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5304492736395275231L;

}
