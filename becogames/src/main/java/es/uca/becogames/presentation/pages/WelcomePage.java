package es.uca.becogames.presentation.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
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
		this.setJustifyContentMode(JustifyContentMode.CENTER);
		this.setAlignItems(Alignment.CENTER);

		this.getElement().getStyle().set("background-image", "url('/images/puzzle.png')");
		this.setClassName("fondo");
		
		H1 h1 = new H1("BECOGames");
		this.add(h1);

		H2 h2 = new H2("BEhaviour eCOnomics Games");
		this.add(h2);

		
		
		Button login = new Button("Sign in", evt -> UI.getCurrent().navigate(SignInPage.class));
		login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.add(login);
				
		Button signUp = new Button("Is this your first time? Sign up here!", evt -> UI.getCurrent().navigate(SignUpPage.class));
		signUp.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		signUp.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		
		this.add(signUp);
		
		
//		Anchor linkMail = new Anchor(
//				"mailto:ivan.ruiz@uca.es?Subject=Consulta%20sobre%20BECOGAMES&body=Escriba%20su%20consulta...",
//				"Any problem? Contact Us");
//		linkMail.setTarget("_blank");
//		linkMail.addClassName("azul");

		//this.add(linkMail);

		
		
		
//		Image uca = new Image("/images/uca.jpeg", "Universidad de Cádiz");
//		Image uned = new Image("/images/uned.png", "UNED");
//		Image law = new Image("/images/law.png", "Fundación Universitaria Behaviour and Law");
//		uca.setSizeUndefined();
//		uned.setSizeUndefined();
//		law.setSizeUndefined();
//		
//		HorizontalLayout creators=new HorizontalLayout(uca,uned,law);
//		creators.setJustifyContentMode(JustifyContentMode.CENTER);
//		creators.setAlignItems(Alignment.STRETCH);
//		creators.setWidthFull();
//		creators.setMaxHeight("10vh");
//		creators.setMaxWidth("100%");
//		//creators.setWidth("100%");
//		
//		
//		this.add(creators);
				
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304492736395275231L;

}
