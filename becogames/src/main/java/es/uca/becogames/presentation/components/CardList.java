package es.uca.becogames.presentation.components;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameAction;

@CssImport(value = "./styles/views/cardlist/card-list-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class CardList extends Composite<Div> {

	private MyEventHandler myEventHandler;
	
	Grid<GameAction> grid = new Grid<>();

	public CardList(List<GameAction> myActions) {
		this.getContent().setWidthFull();
		this.getContent().setHeightFull();

		setId("card-list-view");
		grid.addClassName("card-list-view");
		// grid.setSizeFull();
		// grid.setHeight("100%");
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
		grid.setItems(myActions);
		grid.addComponentColumn(gameAction -> createCard(gameAction));

		getContent().add(grid);
	}

	private HorizontalLayout createCard(GameAction gameAction) {
		HorizontalLayout card = new HorizontalLayout();
		card.addClassName("card");
		card.setSpacing(false);
		card.getThemeList().add("spacing-s");

		Image image = new Image("/images/person.png", "Person");
	
		
		VerticalLayout description = new VerticalLayout();
		description.addClassName("description");
		description.setSpacing(false);
		description.setPadding(false);

		HorizontalLayout header = new HorizontalLayout();
		header.addClassName("header");
		header.setSpacing(false);
		header.getThemeList().add("spacing-s");

		Span name = new Span("Game #" + gameAction.getGame().getId());
		name.addClassName("name");
		Span date = new Span(gameAction.getDate().format(DateTimeFormatter.ISO_DATE_TIME));
		date.addClassName("date");
		header.add(name, date);

		Span post = new Span(gameAction.getExplanation());
		post.addClassName("post");

		HorizontalLayout actions = new HorizontalLayout();
		actions.addClassName("actions");
		actions.setSpacing(false);
		actions.getThemeList().add("spacing-s");

		Icon logoV = new Icon(VaadinIcon.PLAY_CIRCLE);
		Span message = new Span("Go");
		logoV.getStyle().set("cursor", "pointer");
		logoV.addClickListener(event -> {
			if(myEventHandler!=null) {
				System.out.println("va bien");
				this.myEventHandler.onGoGame(gameAction.getGame());
			} else {
				System.out.println("no nulo");
				
			}
			
		});

		actions.add(logoV, message);

		description.add(header, post, actions);
		card.add(image, description);
		return card;
	}


	public interface MyEventHandler {
		void onGoGame(Game game);
	}


	public void addMyListener(MyEventHandler eventHandler) {
		System.out.println("se registra el liste");
		this.myEventHandler=eventHandler;
		
		if(myEventHandler!=null) {
			System.out.println("asignamos");
		} else {
			System.out.println("chungo");
			
		}
	}

}
