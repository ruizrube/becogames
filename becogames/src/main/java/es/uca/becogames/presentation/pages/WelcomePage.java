package es.uca.becogames.presentation.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@CssImport("./styles/welcome.css")

public class WelcomePage extends VerticalLayout {

	public WelcomePage() {
		this.setSizeFull();		
		this.setAlignItems(Alignment.CENTER);
		
		this.getElement().getStyle().set("background-image", "url('/images/puzzle.png')");
		this.setClassName("fondo");
		
		
		
		VerticalLayout topLayout=new VerticalLayout();
		topLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		topLayout.setAlignItems(Alignment.CENTER);
		topLayout.setHeight("85%");
		
		H1 h1 = new H1("BECOGames");
		topLayout.add(h1);

		H2 h2 = new H2("BEhaviour eCOnomics Games");
		topLayout.add(h2);

		Button login = new Button("Sign in", evt -> UI.getCurrent().navigate(SignInPage.class));
		login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);		
		topLayout.add(login);
				
		Button signUp = new Button("Is this your first time? Sign up here!", evt -> UI.getCurrent().navigate(SignUpPage.class));
		signUp.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		signUp.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		topLayout.add(signUp);
			
		Anchor linkMail = new Anchor(
				"mailto:ivan.ruiz@uca.es?Subject=Consulta%20sobre%20BECOGAMES&body=Escriba%20su%20consulta...",
				"Any problem? Contact Us");
		linkMail.setTarget("_blank");
		linkMail.addClassName("azul");
		topLayout.add(linkMail);

		
		Image uca = new Image("/images/uca.jpeg", "Universidad de Cádiz");
		Image uned = new Image("/images/uned.png", "UNED");
		Image law = new Image("/images/law.jpg", "Fundación Universitaria Behaviour and Law");
		uca.setMaxWidth("25vw");
		uned.setMaxWidth("25vw");
		law.setMaxWidth("30vw");
		
		HorizontalLayout bottomLayout=new HorizontalLayout(uca,uned,law);
		bottomLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		bottomLayout.setAlignItems(Alignment.STRETCH);
		bottomLayout.setMaxHeight("10%");
		bottomLayout.setMaxWidth("80vw");
		//bottomLayout.setMargin(true);		
		
		this.add(topLayout);
		this.add(bottomLayout);
				
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304492736395275231L;

}
