package es.uca.becogames.presentation.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameActionType;
import es.uca.becogames.business.services.CommonGoodsGameService;
import es.uca.becogames.business.services.GameService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.components.CommonGoodsGameResultsDialog;

@PageTitle("The Game of the Common Goods")
@Route(value = "game", layout = MainLayout.class)
public class CommonGoodsGameView extends GameView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4705769182468369260L;

	private VerticalLayout panelParameters;

	private NumberField initialAllowanceField;

	private NumberField weightField;

	private Checkbox automaticallyResolveField;

	private Checkbox showGameResultsForAllField;

	@Autowired
	public CommonGoodsGameView(UserService userService, WebNotificationService broadcastingService,
			CommonGoodsGameService gameService) {
		super(userService, broadcastingService, gameService);

	}

	@Override
	protected GameService getGameService() {
		return this.gameService;
	}

	@Override
	protected String checkWarningsBeforeStartingGame() {
		String text = "Currently, there are " + runningGame.countJoinedPlayers() + " joined players.";

		if (runningGame.countInvitedPlayers() > 0) {
			text += ". Additionally, there are " + runningGame.countInvitedPlayers()
					+ " players who are pending to join the game. ";

		} else {
			text += " and there are no players pending to join the game.";
		}

		return text;
	}

	@Override
	protected String checkWarningsBeforeResolvingGame() {

		CommonGoodsGame myGame = (CommonGoodsGame) runningGame;

		String text = "Currently, we have collected " + myGame.countInvestments() + " investments from "
				+ myGame.countJoinedPlayers() + " joined players";

		if (myGame.countInvitedPlayers() > 0) {
			text += ". Additionally, there are " + myGame.countInvitedPlayers()
					+ " players who are pending to join the game. ";

		} else {
			text += " and there are no players pending to join the game.";
		}

		return text;

	}

	@Override
	protected String checkConditionsToStartGame() {

		try {

			Double initialAllowanceValue = initialAllowanceField.getValue();

			Double weightValue = weightField.getValue();

			if (initialAllowanceValue == null || initialAllowanceValue.doubleValue() == 0) {
				return "Missing parameter: initialAllowance";
			}

			if (weightValue == null || weightValue.doubleValue() == 0) {
				return "Missing parameter: weight";
			}
			
			
			if(runningGame.getJoinedPlayers().size()<2) {
				return "Two users are required to play the game at least";
			}
			
			
			

		} catch (NumberFormatException ex) {
			return "Parameter with wrong format";
		}

		return "";

	}

	@Override
	protected void addParametersToGame() {

		Double initialAllowanceValue = initialAllowanceField.getValue();

		Double weightValue = weightField.getValue();

		Boolean resolveAutomatically = automaticallyResolveField.getValue();

		Boolean showGameResultsForAll = showGameResultsForAllField.getValue();

		runningGame.addParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE, initialAllowanceValue);
		runningGame.addParameter(CommonGoodsGame.PARAM_WEIGHT, weightValue);
		runningGame.addParameter(CommonGoodsGame.PARAM_RESOLVE_AUTOMATICALLY, resolveAutomatically ? 1.0 : 0.0);
		runningGame.addParameter(Game.PARAM_SHOW_GAME_RESULTS, showGameResultsForAll ? 1.0 : 0.0);

	}

	

	protected void showPanelParameters() {
		CommonGoodsGame myGame = (CommonGoodsGame) runningGame;

		panelParameters = new VerticalLayout();
		panelParameters.setWidth("100%");
		panelParameters.setAlignItems(Alignment.CENTER);

		H2 parametersLabel = new H2("Game Parameters");

		initialAllowanceField = new NumberField("Initial Allowance");
		initialAllowanceField.setPlaceholder("Example: 25");
		initialAllowanceField.setStep(1.0);
		initialAllowanceField.setMin(0);
		initialAllowanceField.setMax(100);
		initialAllowanceField.setHasControls(true);
		initialAllowanceField.setPreventInvalidInput(true);
		initialAllowanceField.setValue(myGame.getParamInitialAllowance());

		weightField = new NumberField("Weight");
		weightField.setPlaceholder("Example: 2");
		weightField.setStep(0.25);
		weightField.setMin(0);
		weightField.setMax(100);
		weightField.setHasControls(true);
		weightField.setPreventInvalidInput(true);
		weightField.setValue(myGame.getParamWeight());

		automaticallyResolveField = new Checkbox(
				"Automatically resolving the game when there are no pending invitations and all joined players have invested");
		automaticallyResolveField.setValue(myGame.getParamResolveAutomatically());

		showGameResultsForAllField = new Checkbox("Show game results for all users when resolving the game");
		showGameResultsForAllField.setValue(myGame.getParamShowGameResults());

		panelParameters.add(parametersLabel, initialAllowanceField, weightField, automaticallyResolveField,
				showGameResultsForAllField);

		contentPanel.add(panelParameters);

	}

	@Override
	protected void showPanelRunningGame() {
		// if the user already invested...
		if (runningGame.getActions().stream()
				.filter(a -> a.getUser().equals(runningPlayer.getUser()) && a.getType().equals(GameActionType.Invested))
				.count() == 0) {
			showPanelInvest();
		} else {
			showPanelAlreadyInvested();
		}

	}

	private void showPanelAlreadyInvested() {

		H3 msg = new H3("You have already invested. You will be notified when the game is resolved.");

		contentPanel.add(msg);

	}

	private void showPanelInvest() {
		CommonGoodsGame myGame = (CommonGoodsGame) runningGame;

		H3 msg = new H3("Currently, you have an initial allowance of " + myGame.getParamInitialAllowance()+ ". Now, it's your turn to invest!");

		NumberField investmentField = new NumberField("Investment");
		investmentField.setPlaceholder("20");
		investmentField.setStep(0.5);
		investmentField.setMin(0);
		investmentField.setMax(100);
		investmentField.setHasControls(true);
		investmentField.setPreventInvalidInput(true);
		

		Button okButton = new Button("Register investment", e -> {

			try {

				Double investmentValue = investmentField.getValue();

				if (investmentValue <= myGame.getParamInitialAllowance()) {

					try {
						runningGame = gameService.invest((CommonGoodsGame) runningGame, this.currentUser,
								String.valueOf(investmentValue));
					} catch (Exception ex) {
						Notification.show("We could not perform the operation requested!");
					}

					UI.getCurrent().navigate(CommonGoodsGameView.class, String.valueOf(this.runningGame.getId()));

				} else {
					ConfirmDialog dialog = new ConfirmDialog("Error", "You cannot exceed the initial allowance...",
							"Understood", ev -> {
							});
					dialog.open();

					return;

				}

			} catch (NumberFormatException ex) {
				investmentField.setErrorMessage("Number format incorrect");
			}

		});

		okButton.setIcon(VaadinIcon.DOLLAR.create());
		okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		contentPanel.add(msg, investmentField, okButton);

	
	
	}
	
	
	@Override
	protected void showPanelResults() {

		CommonGoodsGame myGame = (CommonGoodsGame) runningGame;

		H3 msg = new H3("The game is finished. Click the button to find out your benefit!");

		Double initialAllowance = myGame.getParamInitialAllowance();

		Button okButton = new Button("Find out!");
		okButton.addClickListener(e -> {

			CommonGoodsGame game = (CommonGoodsGame) runningGame;

			H3 header = new H3("Results of The Game of the Common Goods");

			StringBuffer message =new StringBuffer();
			message.append("Hey, your initial allowance was " + initialAllowance);
			message.append(" points and you invested " + game.checkInvestment(this.currentUser));
			message.append(", so that you have finally obtained " + game.checkBenefit(this.currentUser) + " points.");
			H5 resultMsg = new H5(message.toString());
			
			//H5 resultMsg = new H5("Your initial allowance was: " + initialAllowance);

			//H5 investMsg = new H5("You invested: " + game.checkInvestment(this.currentUser));

			//H4 benefitMsg = new H4("You have finally obtained: " + game.checkBenefit(this.currentUser));

			H4 rankingMsg = new H4("Your position in the user ranking: " + game.checkUserRanking(this.currentUser) + "/"
					+ game.countInvestments());
			
			Double myInvestment = game.checkInvestment(this.currentUser);
			
			
			Chart chart = new Chart();

	        Configuration configuration = chart.getConfiguration();
	        configuration.setTitle("Game scenarios");
	       // configuration.setSubTitle("");
	        chart.getConfiguration().getChart().setType(ChartType.COLUMN);

	        
	        
	        //configuration.addSeries(new ListSeries("Initial Allowance", initialAllowance, initialAllowance, initialAllowance, initialAllowance));
	        configuration.addSeries(new ListSeries("My Investment", myInvestment, game.getParamInitialAllowance(), 0, game.getParamInitialAllowance()));
	        configuration.addSeries(new ListSeries("Average Partners's Investment", game.checkAverageInvestmentOfTheRemainingUsers(this.currentUser).getAsDouble(), 0, game.getParamInitialAllowance(), game.getParamInitialAllowance()));
	        configuration.addSeries(new ListSeries("My Performance", game.checkBenefit(this.currentUser), game.checkMinimumObtainable(), game.checkMaximumObtainable(), game.checkSocialOptimal()));
	        configuration.addSeries(new ListSeries("Average Partners's Performance", game.checkAverageInvestmentOfTheRemainingUsers(this.currentUser).getAsDouble(), game.checkHypotheticalAverageBenefitOfTheRemainingUsersInASoleScenario(this.currentUser), game.checkHypotheticalAverageBenefitOfTheRemainingUsersInAFreeRiderScenario(this.currentUser), game.checkSocialOptimal() ));

	        
	        // TODO AQUI ME QUEDAO
	        XAxis x = new XAxis();
	        x.setCrosshair(new Crosshair());
	        x.setCategories("Current situation", "Sole contributor", "Free rider", "Everyone contributes at maximium");
	        configuration.addxAxis(x);

	        YAxis y = new YAxis();
	        y.setMin(0);
	        y.setTitle("Points");
	        configuration.addyAxis(y);

	        Tooltip tooltip = new Tooltip();
	        tooltip.setShared(true);
	        configuration.setTooltip(tooltip);

	        add(chart);
	        
	        
//			H5 socialMsg = new H5("Social optimal value: " + String.format("%.2f", game.checkSocialOptimal()));
//			H5 minMsg = new H5("Minimum Obtainable value: " + String.format("%.2f", game.checkMinimumObtainable()));
//			H5 maxMsg = new H5("Maximum Obtainable value: " + String.format("%.2f", game.checkMaximumObtainable()));

			VerticalLayout layout = new VerticalLayout(header, resultMsg, rankingMsg, chart);
			Dialog dialog = new Dialog();
			dialog.add(layout);
			dialog.open();

		});

		okButton.setIcon(VaadinIcon.DOLLAR.create());
		okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		contentPanel.add(msg, okButton);

	}

	@Override
	protected void openGameResults(Game game) {

		CommonGoodsGame myGame = (CommonGoodsGame) game;

		CommonGoodsGameResultsDialog dialog = new CommonGoodsGameResultsDialog(myGame.getResults());
		dialog.setWidth("85vw");
		dialog.setHeight("85vh");

		dialog.open();

	}

	

	

}
