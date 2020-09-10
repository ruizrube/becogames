package es.uca.becogames.presentation.views;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameAction;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.services.CommonGoodsGameService;
import es.uca.becogames.business.services.GameActionService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.components.CardList;
import es.uca.becogames.security.SecurityUtils;

@Route(value = "home", layout = MainLayout.class)
@VaadinSessionScope
@PageTitle("Home")

public class HomeView extends AbstractView {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7727216394179341275L;

	@Autowired
	private CommonGoodsGameService gameService;

	@Autowired
	private GameActionService gameActionService;

	public HomeView(UserService userService, WebNotificationService broadcastingService) {
		super(userService, broadcastingService);
		this.setAlignItems(Alignment.CENTER);
		this.setMinHeight("100%");

	}

	@Override
	protected void enter() {

		this.removeAll();

		H4 gameMsg = new H4("Hey " + SecurityUtils.getUsername() + ", welcome to BECOGames!");

		this.add(gameMsg);

		if (SecurityUtils.hasRole(Role.Admin.name())) {
			Button newGameButton = new Button("Create new game", e -> createNewGame());
			newGameButton.setIcon(new Icon(VaadinIcon.GAMEPAD));
			newGameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			this.add(newGameButton);
		}

		Button seeHelpButton = new Button("See help (PDF file)");
		seeHelpButton.setIcon(VaadinIcon.INFO.create());
		seeHelpButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
		this.add(seeHelpButton);

		seeHelpButton.addClickListener(e -> {
			openGameHelp();
		});

		List<GameAction> myActions = gameActionService.findByUser(this.currentUser);

		if (myActions.size() > 0) {

			H2 logLabel = new H2("My recent activities");
			this.add(logLabel);

			CardList cards=new CardList(myActions);
			cards.addMyListener(game->loadGame(game));
			
			this.add(cards);
			
 			
 			
		} else {
			H4 waitMsg = new H4("You will be notified when someone invites you to join a game...");
			this.add(waitMsg);
		}

	
	}

	private void createNewGame() {
		Game game = gameService.createNewGame(this.currentUser);
		UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(game.getId()));

	}

	private void loadGame(Game game) {

		if (SecurityUtils.hasRole(Role.Admin.name()) || game.getOwner().equals(this.currentUser)
				|| game.getPlayer(this.currentUser) != null) {

			UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(game.getId()));

		} else {
			ConfirmDialog dialog = new ConfirmDialog("Warning", "You are not playing in this game", "Understood", e -> {
			});
			dialog.open();

		}

	}


	protected void openGameHelp() {
		StreamResource streamResource = new StreamResource("report.pdf",
				() -> getClass().getResourceAsStream("/META-INF/resources/frontend/pdf/bienes_comunes.pdf")); // file in
																												// src/main/resources/
		
		PdfBrowserViewer viewer = new PdfBrowserViewer(streamResource);
		viewer.setWidth("100%");
		viewer.setHeight("100%");

		Dialog myDialog = new Dialog();
		myDialog.add(viewer);
		myDialog.setWidth("85vw");
		myDialog.setHeight("85vh");

		myDialog.open();

	}

	
	
}
