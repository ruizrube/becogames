package es.uca.becogames;

import java.util.HashSet;

import org.junit.Assert;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class StepDefinitions extends BecoGamesBaseTest {

	private Exception exception;

	@Given("I created a new game with an initial allowance of {float} and a weight of {float}")
	public void i_created_a_new_game(double initialAllowance, double weight) throws Throwable {
		
		System.out.println("\n>>>>CREATING GAME");
		
		game = gameService.createNewGame(admin);
		game.addParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE, initialAllowance);
		game.addParameter(CommonGoodsGame.PARAM_WEIGHT, weight);
		game.addParameter(CommonGoodsGame.PARAM_RESOLVE_AUTOMATICALLY, 1.0);
		game.addParameter(Game.PARAM_SHOW_GAME_RESULTS, 1.0);

		System.out.println("Game created");

	}

	@Given("I have sent game invitations to {int} users")
	public void i_have_sent_game_invitations(int numUsers) throws Throwable {

		createTestUsers(numUsers);

		System.out.println("\n>>>>INVITING USERS");
		game = gameService.inviteUsers(game, new HashSet<User>(users));
		System.out.println("Users invited");
	}
	

	@Given("{int} user(s) accepted to play")
	public void users_accepted_to_play(Integer numPlayers) {
		System.out.println("\n>>>>USERS ACCEPTING TO PLAY");
		for (int i = 1; i <= numPlayers; i++) {
			game = gameService.acceptGame(game, users.get(i - 1));
			System.out.println(users.get(i - 1).getUsername() + " joined the game");
		}

	}

	@When("I select the option to start the game")
	public void i_select_the_option_to_start_the_game() throws Throwable {

		System.out.println("\n>>>>STARTING GAME");

		try {
			game = gameService.startGame(game, admin);
		} catch (Exception ex) {
			this.exception = ex;
		}

	}

	@When("{int} user(s) invested {double}")
	public void users_invested(Integer numPlayers, double quantity) {
		for (int i = 1; i <= numPlayers; i++) {
			game = gameService.invest((CommonGoodsGame) game, users.get(i - 1), String.valueOf(quantity));
			System.out.println(users.get(i - 1).getUsername() + " invested " + String.valueOf(quantity));
		}

	}

	@When("user {int} invested {double}")
	public void user_invested(Integer idUser, double quantity) {
		game = gameService.invest((CommonGoodsGame) game, users.get(idUser - 1), String.valueOf(quantity));
		System.out.println(users.get(idUser - 1).getUsername() + " invested " + String.valueOf(quantity));

	}

	@When("I select the option to resolve the game")
	public void i_select_the_option_to_resolve_the_game() {

		try {
			game = gameService.resolveGame(game);
		} catch (Exception ex) {
			this.exception = ex;
		}

	}

	@Then("I got an error message with the text {string}")
	public void i_got_an_error_message_with_the_text(String expectedMessage) throws Throwable {
	
		String actualMessage = exception.getMessage();

		Assert.assertTrue(actualMessage.contains(expectedMessage));
	}

	@Then("the user {int} gains {double}")
	public void the_user_gains(int idUser, double userGainingExpected) {

		Double userGainingProvided = ((CommonGoodsGame) game).checkBenefit(users.get(idUser - 1));

		System.out.println(users.get(idUser - 1).getUsername() + " obtains " + userGainingProvided + " points.");

		Assert.assertTrue(userGainingProvided==userGainingExpected);

	}
}
