package es.uca.becogames.presentation.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameStatus;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.GameService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.components.CommonGoodsGameResultsDialog;
import es.uca.becogames.presentation.components.GameActionLogDialog;
import es.uca.becogames.security.SecurityUtils;

/**
 * The main view contains a button and a click listener.
 */
@Route(value = "gamelist", layout = MainLayout.class)
@PageTitle("Game List")
public class GameListView extends AbstractView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5435881979712417593L;
	private Grid<Game> grid = new Grid<>(Game.class);

	private GameService gameService;

	@Autowired
	public GameListView(UserService userService, WebNotificationService broadcastingService, GameService service) {
		super(userService, broadcastingService);
		this.gameService = service;

		if (SecurityUtils.hasRole(Role.Admin.name())) {
			ComboBox<User> userCombo = new ComboBox<User>("Player");
			userCombo.setItemLabelGenerator(u -> u.getUsername());
			userCombo.setPlaceholder("No player selected");
			userCombo.setItems(userService.findAll());
			userCombo.addValueChangeListener(e -> updateList(e.getValue()));
			add(userCombo);
		}

		grid.setSizeFull();
		grid.removeColumnByKey("creationDate");
		grid.removeColumnByKey("endDate");
		grid.removeColumnByKey("score");
		grid.removeColumnByKey("previousStatus");
		grid.removeColumnByKey("owner");
		grid.removeColumnByKey("parameters");
		grid.removeColumnByKey("actions");
		grid.removeColumnByKey("invitedPlayers");
		grid.removeColumnByKey("joinedPlayers");
		grid.removeColumnByKey("invitedOrJoinedPlayers");
		grid.removeColumnByKey("paramShowGameResults");

		grid.addComponentColumn(item -> createButtons(grid, item)).setHeader("Actions");

		setHeight("100vh");

		if (SecurityUtils.hasRole(Role.Admin.name())) {
			updateList(null);
		} else {
			updateList(userService.findByUsername(SecurityUtils.getUsername()));
		}

		add(new Anchor(new StreamResource("log.xls", Exporter.exportAsExcel(grid)), "Download As Excel"));

		add(grid);

	}

	private Div createButtons(Grid<Game> grid, Game item) {

		Div div = new Div();

		if(item.isUserInvolved(currentUser) || item.getOwner().equals(currentUser)) {
			div.add(new Button("Go!", clickEvent -> {
				loadGame(item);
			}));
			
		}

		if (item.getStatus().equals(GameStatus.Resolved)) {
			if (SecurityUtils.hasRole(Role.Admin.name()) || item.getOwner().equals(currentUser)
					|| (item.getParamShowGameResults() && item.isUserJoined(this.currentUser))) {
				div.add(new Button("Results", clickEvent -> {
					openGameResults(item);
				}));
			}
		}

		div.add(new Button("Log", clickEvent -> {
			openGameLog(item);
		}));

		return div;
	}

	public void updateList(User user) {

		if (user != null) {
			grid.setItems(gameService.findByUser(user).stream().sorted((g1,g2)->g2.getLastActionDate().compareTo(g1.getLastActionDate())));
		} else {
			grid.setItems(gameService.findAll().stream().sorted((g1,g2)->g2.getLastActionDate().compareTo(g1.getLastActionDate())));

		}
	}

	private void openGameLog(Game game) {

		GameActionLogDialog dialog = new GameActionLogDialog("Game " + game.getId() + ": " + game.getStatus());

		if (SecurityUtils.hasRole(Role.Admin.name()) || game.getOwner().equals(currentUser)) {
			dialog.setItems(game.getActions());
		} else {
			dialog.setItems(game.getPlayerActions(currentUser));
		}

		dialog.open();
	}

	private void openGameResults(Game game) {

		CommonGoodsGame myGame = (CommonGoodsGame) game;

		CommonGoodsGameResultsDialog dialog = new CommonGoodsGameResultsDialog(myGame.getResults());


		dialog.open();

	}

	private void loadGame(Game game) {

		if (SecurityUtils.hasRole(Role.Admin.name()) || game.getOwner().equals(this.currentUser)
				|| game.getPlayer(this.currentUser) != null) {

			UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(game.getId()));

		} else {
			Dialog dialog = new Dialog();
			dialog.add(new H2("You are not playing in this game"));
			dialog.open();

		}

	}

	

	@Override
	protected void enter() {
		// TODO Auto-generated method stub

	}

}