package es.uca.becogames.presentation.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.RouterLink;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameStatus;
import es.uca.becogames.business.entities.Player;
import es.uca.becogames.business.entities.PlayerStatus;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.CommonGoodsGameService;
import es.uca.becogames.business.services.GameService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.presentation.components.GameActionLogDialog;
import es.uca.becogames.security.SecurityUtils;


public abstract class GameView extends AbstractView implements HasUrlParameter<String> {
	
	private static final long serialVersionUID = 8869653427059745870L;

	VerticalLayout headerPanel = new VerticalLayout();
	VerticalLayout contentPanel = new VerticalLayout();

	VerticalLayout panelInvitations;

	Button startGameButton;
	Button resolveGameButton;
	Button stopGameButton;
	Button pauseGameButton;
	Button resumeGameButton;
	Button seeLogButton;
	Button seeResultsButton;
	
	
	Checkbox selfPlayButton;
	Grid<User> usersGrid;
	Button sendInvitationsButton;
	Button selectRandomUsersButton;

	protected Game runningGame;
	protected Player runningPlayer;
	protected Long idGame;
	protected CommonGoodsGameService gameService;

	protected GameView(UserService userService, WebNotificationService broadcastingService,
			CommonGoodsGameService gameService) {

		super(userService, broadcastingService);
		this.gameService = gameService;
		
		this.setAlignItems(Alignment.CENTER);
		this.setJustifyContentMode(JustifyContentMode.START);
		this.setWidth("100%");		
		this.setMinHeight("100%");
		

				
		headerPanel.setWidth("100vw");
		headerPanel.setAlignItems(Alignment.CENTER);
		contentPanel.setWidth("100vw");
		contentPanel.setHeightFull();
		contentPanel.setAlignItems(Alignment.CENTER);
		
		this.getElement().getStyle().set("background-image", "url('/images/coins.png')");
		this.setClassName("fondo");

		
		add(headerPanel);
		add(contentPanel);
		
		
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if (parameter != null) {
			idGame = Long.parseLong(parameter);
			// Necessary to handle the top navigation bar
			UI.getCurrent().getSession().setAttribute("idRunningGame", idGame);

		}
	}

	@Override
	protected void enter() {

		idGame = (Long) UI.getCurrent().getSession().getAttribute("idRunningGame");
		if (idGame != null) {

			runningGame = this.getGameService().findById(idGame);
			if (runningGame != null) {

				runningPlayer = runningGame.getPlayer(this.currentUser);

				if (runningPlayer != null || runningGame.getOwner().equals(this.currentUser)) {
					refreshGamePanels();

				} else {
					showPanelError();
				}
			} else {
				showPanelError();
			}
		} else {
			showPanelError();
		}
	}

	protected void refreshGamePanels() {
		headerPanel.removeAll();
		refreshGameHeaderPanel();

		contentPanel.removeAll();
		refreshGameContentPanel();

	}

	private void refreshGameHeaderPanel() {

		
		
		H2 h2=new H2("Game #"+String.valueOf(runningGame.getId()) + " [" + runningGame.getStatus() + "]");
		H5 firstLine=new H5("Joined Players: " + runningGame.countJoinedPlayers() + ". Invited Players: " + runningGame.countInvitedPlayers());
		H5 secondLine=new H5("Creator: " + runningGame.getOwner());
		
		VerticalLayout panel = new VerticalLayout(h2,firstLine,secondLine);
		
		//Details details = new Details("Game Details", panel);
		//details.addThemeVariants(DetailsVariant.SMALL);
		panel.setAlignItems(Alignment.CENTER);
		headerPanel.add(panel);
		
		headerPanel.setAlignItems(Alignment.CENTER);


	}

	private void showPanelButtons() {
		startGameButton = new Button("Start Game");
		resolveGameButton = new Button("Resolve Game");
		stopGameButton = new Button("Stop Game");
		pauseGameButton = new Button("Pause Game");
		resumeGameButton = new Button("Resume Game");
		seeLogButton = new Button("See Game Log");
		seeResultsButton = new Button("See Results");
		
		startGameButton.setIcon(VaadinIcon.PLAY.create());
		resolveGameButton.setIcon(VaadinIcon.ABACUS.create());
		stopGameButton.setIcon(VaadinIcon.STOP.create());
		pauseGameButton.setIcon(VaadinIcon.PAUSE.create());
		resumeGameButton.setIcon(VaadinIcon.PLAY.create());
		seeLogButton.setIcon(VaadinIcon.FILE.create());
		seeResultsButton.setIcon(VaadinIcon.BAR_CHART_H.create());
		
		startGameButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		resolveGameButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		stopGameButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		pauseGameButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		resumeGameButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		seeLogButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		seeResultsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		
		startGameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		resolveGameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		stopGameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		pauseGameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		resumeGameButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		seeLogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		seeResultsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		manageButtonListeners();

		manageButtonVisibility();

		
		FormLayout layout = new FormLayout(startGameButton, resolveGameButton, pauseGameButton,
				resumeGameButton, stopGameButton, seeLogButton, seeResultsButton);
		
		layout.setResponsiveSteps(
		        new ResponsiveStep("25em", 1),
		        new ResponsiveStep("32em", 2),
		        new ResponsiveStep("40em", 3),
		 		new ResponsiveStep("47em", 4));
		
		
		
		VerticalLayout buttonsPanel = new VerticalLayout();
		buttonsPanel.add(layout);
		buttonsPanel.setAlignItems(Alignment.CENTER);
		buttonsPanel.setSizeUndefined();
		//buttonsPanel.setWidthFull();
		buttonsPanel.add(layout);
			
		contentPanel.add(buttonsPanel);
	}

	private void manageButtonListeners() {
		startGameButton.addClickListener(e -> {

			String errorDescription = this.checkConditionsToStartGame();

			if (!errorDescription.isEmpty()) {

				ConfirmDialog dialog = new ConfirmDialog("Errors", errorDescription, "Understood", ev -> {
				});
				dialog.open();
				return;
			}

			String warningDescription = this.checkWarningsBeforeStartingGame();

			ConfirmDialog dialog = new ConfirmDialog("Starting game..",
					warningDescription + " Do you really want to start the game?", "Confirm", event -> {
						this.addParametersToGame();
						try {
							runningGame = getGameService().startGame(runningGame, this.currentUser);
						} catch (Exception ex) {
							Notification.show("We could not perform the operation requested!");
						}
						UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

					}, "Cancel", ev -> {
					});

			dialog.open();

		});

		resolveGameButton.addClickListener(e -> {

			String errorDescription = getGameService().checkConditionsToResolveGame(runningGame);

			if (!errorDescription.isEmpty()) {

				ConfirmDialog dialog = new ConfirmDialog("Errors", errorDescription, "Understood", ev -> {
				});
				dialog.open();
				return;
			}

			String warningDescription = this.checkWarningsBeforeResolvingGame();

			ConfirmDialog dialog = new ConfirmDialog("Resolving game..",
					warningDescription + " Do you really want to resolve the game?", "Confirm", event -> {
						try {
							runningGame = getGameService().resolveGame(runningGame);
						} catch (Exception ex) {
							Notification.show("We could not perform the operation requested!");
						}
						UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));
					}, "Cancel", ev -> {
					});

			dialog.open();
		});

		stopGameButton.addClickListener(e -> {
			ConfirmDialog dialog = new ConfirmDialog("Stopping game..", "Do you want to stop the game?", "Confirm",
					event -> {
						try {
							runningGame = getGameService().stopGame(runningGame, this.currentUser);
						} catch (Exception ex) {
							Notification.show("We could not perform the operation requested!");
						}

						UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));
					}, "Cancel", ev -> {
					});

			dialog.open();
		});

		pauseGameButton.addClickListener(e -> {
			ConfirmDialog dialog = new ConfirmDialog("Pausing game..", "Do you want to pause the game?", "Confirm",
					event -> {
						try {
							runningGame = getGameService().pauseGame(runningGame, this.currentUser);
						} catch (Exception ex) {
							Notification.show("We could not perform the operation requested!");
						}

						UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

					}, "Cancel", ev -> {
					});

			dialog.open();
		});

		resumeGameButton.addClickListener(e -> {
			ConfirmDialog dialog = new ConfirmDialog("Resuming game..", "Do you want to resume the game?", "Confirm",
					event -> {
						try {
							runningGame = getGameService().resumeGame(runningGame, this.currentUser);
						} catch (Exception ex) {
							Notification.show("We could not perform the operation requested!");
						}

						UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

					}, "Cancel", ev -> {
					});

			dialog.open();
		});

		seeLogButton.addClickListener(e -> {
			openGameLog(this.runningGame);
		});

		seeResultsButton.addClickListener(e -> {
			openGameResults(this.runningGame);
		});

		
	}

	private void manageButtonVisibility() {
		if (runningGame.getOwner().equals(this.currentUser)) { // SOY EL CREADOR
			if (runningGame.getStatus().equals(GameStatus.New)) {
				startGameButton.setVisible(true);
				resolveGameButton.setVisible(false);
				stopGameButton.setVisible(true);
				pauseGameButton.setVisible(true);
				resumeGameButton.setVisible(false);
				seeResultsButton.setVisible(false);
			} else if (runningGame.getStatus().equals(GameStatus.Running)) {
				startGameButton.setVisible(false);
				resolveGameButton.setVisible(true);
				stopGameButton.setVisible(true);
				pauseGameButton.setVisible(true);
				resumeGameButton.setVisible(false);
				seeResultsButton.setVisible(false);
			} else if (runningGame.getStatus().equals(GameStatus.Paused)) {
				startGameButton.setVisible(false);
				resolveGameButton.setVisible(false);
				stopGameButton.setVisible(true);
				pauseGameButton.setVisible(false);
				resumeGameButton.setVisible(true);
				seeResultsButton.setVisible(false);
			} else if (runningGame.getStatus().equals(GameStatus.Stopped)) {
				startGameButton.setVisible(false);
				resolveGameButton.setVisible(false);
				stopGameButton.setVisible(false);
				pauseGameButton.setVisible(false);
				resumeGameButton.setVisible(false);
				seeResultsButton.setVisible(false);
			} else if (runningGame.getStatus().equals(GameStatus.Resolved)) {
				startGameButton.setVisible(false);
				resolveGameButton.setVisible(false);
				stopGameButton.setVisible(false);
				pauseGameButton.setVisible(false);
				resumeGameButton.setVisible(false);
				seeResultsButton.setVisible(true);
			}
		} else { // SOY UN JUGADOR (NO CREADOR)
			startGameButton.setVisible(false);
			resolveGameButton.setVisible(false);
			stopGameButton.setVisible(false);
			pauseGameButton.setVisible(false);
			resumeGameButton.setVisible(false);

			if (runningGame.getParamShowGameResults() && runningGame.getStatus().equals(GameStatus.Resolved)
					&& runningGame.isUserJoined(this.currentUser)) {
				seeResultsButton.setVisible(true);
			} else {
				seeResultsButton.setVisible(false);
			}
		}
	}

	protected void refreshGameContentPanel() {

		// PARTIDA NUEVA

		if (runningGame.getStatus().equals(GameStatus.New)) {

			if (runningGame.getOwner().equals(this.currentUser)) { // SOY PROPIETARIO
				showPanelSendInvitations();
				showPanelParameters();
			}

			if (runningPlayer != null) { // SOY JUGADOR

				if (runningPlayer.getStatus().equals(PlayerStatus.Invited)) {
					showPanelManageInvitation();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Joined)) {
					showPanelWaitingStartGame();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Rejected)) {
					showPanelRejected();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Removed)) {
					showPanelRemoved();
				} else {
					showPanelError();
				}

			}

			// PARTIDA EN EJECUCION

		} else if (runningGame.getStatus().equals(GameStatus.Running)) {

			// No mostramos el panel de invitaciones si la partida ya ha empezado
//			if (runningGame.getOwner().equals(this.currentUser)) { // SOY PROPIETARIO
//				showPanelSendInvitations();
//			}

			if (runningPlayer != null) { // SOY JUGADOR

				if (runningPlayer.getStatus().equals(PlayerStatus.Invited)) {
					showPanelManageInvitation();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Joined)) {
					showPanelRunningGame();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Rejected)) {
					showPanelRejected();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Removed)) {
					showPanelRemoved();
				} else {
					showPanelError();
				}

			}

			// PARTIDA FINALIZADA RESUELTA

		} else if (runningGame.getStatus().equals(GameStatus.Resolved)) {

			if (runningPlayer != null) { // SOY JUGADOR

				if (runningPlayer.getStatus().equals(PlayerStatus.Invited)) {
					showPanelExpiredInvitation();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Joined)) {
					showPanelResults();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Rejected)) {
					showPanelRejected();
				} else if (runningPlayer.getStatus().equals(PlayerStatus.Removed)) {
					showPanelRemoved();
				} else {
					showPanelError();
				}
			}

			// PARTIDA EN PAUSA

		} else if (runningGame.getStatus().equals(GameStatus.Paused)) {

			showPanelPaused();

			// PARTIDA FINALIZADA NO RESUELTA

		} else if (runningGame.getStatus().equals(GameStatus.Stopped)) {

			showPanelStopped();

			// ERROR EN PARTIDA

		} else {

			showPanelError();

		} 
		

		// Botones comunes del juego
		showPanelButtons();


	}

	private void showPanelRemoved() {

		contentPanel.add(new H3("I'm sorry. Your participation has expired due to inactivity"));

	}

	private void showPanelRejected() {

		contentPanel.add(new H3("I'm sorry. You rejected playing this game"));

	}

	protected void showPanelPaused() {

		contentPanel.add(new H3("This game is currently paused"));

	}

	protected void showPanelStopped() {

		contentPanel.add(new H3("This game has been stopped"));

	}

	protected void showPanelError() {

		contentPanel.add(new H3("This page is not currently available"), new RouterLink("Home", HomeView.class));

	}

	
	protected void showPanelNoGame() {

		contentPanel.add(new H3("There is no a game loaded"), new RouterLink("Home", HomeView.class));

	}

	protected void showPanelExpiredInvitation() {

		contentPanel.add(new H3("I'm sorry. The invitation to this game has already expired"));

	}

	protected void showPanelWaitingStartGame() {

		contentPanel.add(
				new H3("You have been sucessfully joined to this game, but now we have to wait until the game starts"));

	}

	private void showPanelManageInvitation() {

		ConfirmDialog dialog = new ConfirmDialog("You have been invited for participating in this game",
				"Do you want to join?", "Accept", event -> {

					try {
						runningGame = getGameService().acceptGame(runningGame, this.currentUser);
					} catch (Exception ex) {
						Notification.show("We could not perform the operation requested!");
					}

					UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

				}, "Cancel", event -> {

					try {
						runningGame = getGameService().rejectGame(runningGame, this.currentUser);
					} catch (Exception ex) {
						Notification.show("We could not perform the operation requested!");
					}

					UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

				});

		dialog.open();
	}

	private void showPanelSendInvitations() {
		panelInvitations = new VerticalLayout();
		panelInvitations.setWidth("100%");
		panelInvitations.setAlignItems(Alignment.CENTER);

		H2 invitationsLabel = new H2("Game Invitations");

		buildSelfPlayButton();

		buildInvitationGrid();

		buildSendInvitationsButton();

		buildSelectRandomUsersButton();

		VerticalLayout form = new VerticalLayout(selfPlayButton, usersGrid,
				new HorizontalLayout(selectRandomUsersButton, sendInvitationsButton));
		form.setWidthFull();
		form.setAlignItems(Alignment.CENTER);

		panelInvitations.add(invitationsLabel, form);

		contentPanel.add(panelInvitations);

	}

	private void buildSelectRandomUsersButton() {
		selectRandomUsersButton = new Button("Select random users");
		selectRandomUsersButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

		selectRandomUsersButton.addClickListener(e -> {

			Set<User> availableUsers = usersGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toSet());

			Dialog numberUserDialog = new Dialog();
			H3 question = new H3("How many users?");
			NumberField number = new NumberField("Number (max. " + availableUsers.size() + ")");
			number.setMax(availableUsers.size());
			number.setValue((double) 0);
			number.setMin((double) 0);

			Button ok = new Button("OK", ex -> {
				usersGrid.deselectAll();

				List<User> randomUsers = new ArrayList<User>(availableUsers);

				Collections.shuffle(randomUsers);

				usersGrid.asMultiSelect()
						.setValue(new HashSet<User>(randomUsers.subList(0, number.getValue().intValue())));

				numberUserDialog.close();
			});

			numberUserDialog.add(question, number, ok);
			numberUserDialog.open();

		});
	}

	private void buildSendInvitationsButton() {
		sendInvitationsButton = new Button("Send invitations");
		sendInvitationsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		sendInvitationsButton.addClickListener(e -> {

			if (usersGrid.getSelectedItems().size() > 0) {
				ConfirmDialog dialog = new ConfirmDialog("Send invitations",
						"We are going to send invitations to the selected users...", "OK", ev -> {

							try {
								runningGame = getGameService().inviteUsers(runningGame, usersGrid.getSelectedItems());
							} catch (Exception ex) {
								Notification.show("We could not perform the operation requested!");
							}

							UI.getCurrent().navigate(CommonGoodsGameView.class,
									String.valueOf(this.runningGame.getId()));

						}, "Cancel", ev -> {
						});

				dialog.open();

			} else {
				ConfirmDialog dialog = new ConfirmDialog("Send invitations", "You have to select one or more users..",
						"Understood", ev -> {
						});
				dialog.open();
			}

		});
	}

	private void buildSelfPlayButton() {
		// SELF PLAY

		selfPlayButton = new Checkbox("I want to enroll in the game");
		selfPlayButton.setValue(runningGame.isUserJoined(this.currentUser));

		selfPlayButton.addValueChangeListener(e -> {
			if (e.getValue()) {
				try {
					getGameService().joinOwnerToTheGame(runningGame);
				} catch (Exception ex) {
					Notification.show("We could not perform the operation requested!");
				}

				UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

			} else {
				try {
					getGameService().removeOwnerFromTheGame(runningGame);
				} catch (Exception ex) {
					Notification.show("We could not perform the operation requested!");
				}

				refreshGamePanels();

			}
		});
	}

	private void buildInvitationGrid() {
		// GRID

		List<User> usersToInvite = userService.findAll();
		usersToInvite.remove(this.currentUser);

		usersGrid = new Grid<User>();

		ListDataProvider<User> dataProvider = new ListDataProvider<>(usersToInvite);

		// usersGrid.setItems(usersToInvite);
		usersGrid.setDataProvider(dataProvider);
		usersGrid.setWidthFull();
		usersGrid.setHeight("50vh");
		usersGrid.setSelectionMode(SelectionMode.MULTI);
		//usersGrid.setSizeUndefined();
		
		// GridMultiSelectionModel<?> multiSelection = (GridMultiSelectionModel<?>)
		// usersGrid.setSelectionMode(Grid.SelectionMode.MULTI);
		// ((GridMultiSelectionModel<User>) usersGrid).setSelectionColumnFrozen(true);

		// USERNAME (En realidad el NOMBRE COMPLETO-TOSTRING)
		TextField usernameField = new TextField();

		usernameField.addValueChangeListener(event -> {
			dataProvider.addFilter(user -> StringUtils.containsIgnoreCase(user.toString(), usernameField.getValue()));
		});

		usernameField.setValueChangeMode(ValueChangeMode.EAGER);
		usernameField.setSizeFull();
		usernameField.setPlaceholder("Filter");

		Column<User> usernameColumn = usersGrid.addColumn(User::toString).setHeader("Username") // .setFrozen(true)
				.setComparator((p1, p2) -> p1.toString().compareToIgnoreCase(p2.toString()));

		// STATUS
		TextField statusField = new TextField();
		statusField.addValueChangeListener(event -> {
			dataProvider.addFilter(
					user -> StringUtils.containsIgnoreCase(runningGame.getPlayerStatus(user), statusField.getValue()));
		});
		statusField.setValueChangeMode(ValueChangeMode.EAGER);
		statusField.setSizeFull();
		statusField.setPlaceholder("Filter");

		Column<User> statusColumn = usersGrid.addColumn(user -> runningGame.getPlayerStatus(user)).setHeader("Status") // .setFrozen(true)
				.setComparator((p1, p2) -> runningGame.getPlayerStatus(p1)
						.compareToIgnoreCase(runningGame.getPlayerStatus(p2)));

		// GENDER
		TextField genderField = new TextField();

		genderField.addValueChangeListener(event -> {
			dataProvider
					.addFilter(user -> StringUtils.containsIgnoreCase(user.getGender().name(), genderField.getValue()));
		});

		genderField.setValueChangeMode(ValueChangeMode.EAGER);
		genderField.setSizeFull();
		genderField.setPlaceholder("Filter");

		Column<User> genderColumn = usersGrid.addColumn(User::getGender).setHeader("Gender")
				.setComparator((p1, p2) -> p1.getGender().name().compareToIgnoreCase(p2.getGender().name()));

		// AGE

		TextField ageField = new TextField();
		ageField.addValueChangeListener(event -> {
			dataProvider.addFilter(
					user -> StringUtils.containsIgnoreCase(String.valueOf(user.getAge()), ageField.getValue()));
		});

		ageField.setValueChangeMode(ValueChangeMode.EAGER);
		ageField.setSizeFull();
		ageField.setPlaceholder("Filter");

		Column<User> ageColumn = usersGrid.addColumn(User::getAge).setHeader("Age")
				.setComparator(Comparator.comparingInt(User::getAge));

		// PROFILE
		TextField profileField = new TextField();
		profileField.addValueChangeListener(event -> {
			dataProvider.addFilter(
					user -> StringUtils.containsIgnoreCase(user.getProfile().name(), profileField.getValue()));
		});
		profileField.setValueChangeMode(ValueChangeMode.EAGER);
		profileField.setSizeFull();
		profileField.setPlaceholder("Filter");

		Column<User> profileColumn = usersGrid.addColumn(User::getProfile).setHeader("Profile")
				.setComparator((p1, p2) -> p1.getProfile().name().compareToIgnoreCase(p2.getProfile().name()));

		// ORGANIZATION
		TextField organizationField = new TextField();
		organizationField.addValueChangeListener(event -> {
			dataProvider.addFilter(user -> StringUtils.containsIgnoreCase(user.getOrganization().getName(),
					organizationField.getValue()));
		});
		organizationField.setValueChangeMode(ValueChangeMode.EAGER);
		organizationField.setSizeFull();
		organizationField.setPlaceholder("Filter");

		Column<User> organizationColumn = usersGrid.addColumn(User::getOrganization).setHeader("Organization")
				.setComparator(
						(p1, p2) -> p1.getOrganization().getName().compareToIgnoreCase(p2.getOrganization().getName()));

		// INVITED GAMES
		TextField invitedGamesField = new TextField();
		invitedGamesField.addValueChangeListener(event -> {
			dataProvider.addFilter(user -> StringUtils.containsIgnoreCase(String.valueOf(user.countInvitedGames()),
					invitedGamesField.getValue()));
		});
		invitedGamesField.setValueChangeMode(ValueChangeMode.EAGER);
		invitedGamesField.setSizeFull();
		invitedGamesField.setPlaceholder("Filter");

		Column<User> invitedGamesColumn = usersGrid.addColumn(user -> user.countInvitedGames())
				.setHeader("Invited Games")
				.setComparator((p1, p2) -> p1.countInvitedGames().compareTo(p2.countInvitedGames()));

		// JOINED GAMES
		TextField joinedGamesField = new TextField();
		joinedGamesField.addValueChangeListener(event -> {
			dataProvider.addFilter(user -> StringUtils.containsIgnoreCase(String.valueOf(user.countJoinedGames()),
					joinedGamesField.getValue()));
		});
		joinedGamesField.setValueChangeMode(ValueChangeMode.EAGER);
		joinedGamesField.setSizeFull();
		joinedGamesField.setPlaceholder("Filter");

		Column<User> joinedGamesColumn = usersGrid.addColumn(user -> user.countJoinedGames()).setHeader("Joined Games")
				.setComparator((p1, p2) -> p1.countJoinedGames().compareTo(p2.countJoinedGames()));

		HeaderRow filterRow = usersGrid.appendHeaderRow();

		filterRow.getCell(usernameColumn).setComponent(usernameField);
		filterRow.getCell(statusColumn).setComponent(statusField);
		filterRow.getCell(genderColumn).setComponent(genderField);
		filterRow.getCell(ageColumn).setComponent(ageField);
		filterRow.getCell(profileColumn).setComponent(profileField);
		filterRow.getCell(organizationColumn).setComponent(organizationField);
		filterRow.getCell(invitedGamesColumn).setComponent(invitedGamesField);
		filterRow.getCell(joinedGamesColumn).setComponent(joinedGamesField);

	}

	private void openGameLog(Game game) {

		GameActionLogDialog dialog = new GameActionLogDialog("Game " + game.getId() + ": " + game.getStatus());

		if (SecurityUtils.hasRole(Role.Admin.name()) || game.getOwner().equals(currentUser)) {
			dialog.setItems(game.getActions());
		} else {
			dialog.setItems(game.getPlayerActions(currentUser));
		}

		dialog.setWidth("85vw");
		dialog.setHeight("75vh");

		dialog.open();
	}

	
	
	
	
	
	
	protected abstract void addParametersToGame();

	
	protected abstract String checkWarningsBeforeStartingGame();


	protected abstract String checkWarningsBeforeResolvingGame();

	protected abstract void openGameResults(Game game);

	protected abstract void showPanelRunningGame();

	protected abstract void showPanelParameters();

	protected abstract void showPanelResults();

	protected abstract GameService getGameService();

	protected abstract String checkConditionsToStartGame();
	

}
