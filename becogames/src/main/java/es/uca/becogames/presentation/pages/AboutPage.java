package es.uca.becogames.presentation.pages;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("about")
@Theme(value = Lumo.class, variant = Lumo.DARK)

public class AboutPage extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8770558921783975441L;

	public AboutPage() {

		this.setHeight("90%");
		this.setJustifyContentMode(JustifyContentMode.START);
		this.setAlignItems(Alignment.CENTER);
	
		this.setMargin(false);
		this.add(new H1("About us"));
		this.add(new H3("Created by: "));

		this.add(new H6("Universidad de Cádiz"));
		this.add(new H6("UNED"));
		this.add(new H6("Fundación Universitaria Behaviour and Law"));

		this.add(new RouterLink("Back", WelcomePage.class));

	}

}
