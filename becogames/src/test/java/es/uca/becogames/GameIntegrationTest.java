package es.uca.becogames;

import java.util.HashSet;

import org.apache.http.util.Asserts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.User;

@RunWith(SpringRunner.class)
public class GameIntegrationTest extends BecoGamesBaseTest {

	protected static int PARAM_TEST_NUM_PLAYERS = 5;
	protected static double PARAM_INITIAL_ALLOWANCE = 0.25d;
	protected static double PARAM_WEIGHT = 2d;

	@Before
	public void setUp() {

		
		loadAdminUser();

		createTestUsers(PARAM_TEST_NUM_PLAYERS);

	}

	private void prepareGame() {
		System.out.println("\n>>>>CREATING GAME");
		game = gameService.createNewGame(admin);
		game.addParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE, PARAM_INITIAL_ALLOWANCE);
		game.addParameter(CommonGoodsGame.PARAM_WEIGHT, PARAM_WEIGHT);
		game.addParameter(CommonGoodsGame.PARAM_RESOLVE_AUTOMATICALLY, 1.0);
		game.addParameter(Game.PARAM_SHOW_GAME_RESULTS, 1.0);

		System.out.println("Game created");

		System.out.println("\n>>>>INVITING USERS");
		game = gameService.inviteUsers(game, new HashSet<User>(users));
		System.out.println("Users invited");
	}

	@Test
	public void testDummy() throws Exception {
		System.out.println(
				"\n---------------------------------------------------------------------------------------------------------------------");
		System.out.println("TEST - dummy!");
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------");

		System.out.println("\nNothing to do");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNobodyRespondsInvitation() throws Exception {

		System.out.println("\n------------------------------------");
		System.out.println("TEST Nobody responds the invitation!");
		System.out.println("------------------------------------");

		prepareGame();

		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNobodyAcceptsInvitation() throws Exception {

		System.out.println("\n----------------------------------------------");
		System.out.println("TEST -	Nobody accepts the joining invitation!");
		System.out.println("----------------------------------------------");

		prepareGame();

		System.out.println("\n>>>>USERS REJECTING TO PLAY");
		for (int i = 1; i < users.size(); i++) {
			game = gameService.rejectGame(game, users.get(users.size() - 1));
			System.out.println(users.get(i - 1).getUsername() + " joined the game");
		}

		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnlyOneJoinedPlayer() throws Exception {

		System.out.println("\n--------------------------------------");
		System.out.println("TEST -	There is only a joined player!");
		System.out.println("--------------------------------------");

		prepareGame();

		System.out.println("\n>>>>USER 1 ACCEPT TO PLAY");
		game = gameService.acceptGame(game, users.get(0));
		System.out.println(users.get(0).getUsername() + " joined the game");

		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNobodyRegisterAnInvestment() throws Exception {

		System.out.println("\n---------------------------------------------------------------------------");
		System.out.println("TEST -	There is more than one player, but nobody registers an investment!");
		System.out.println("---------------------------------------------------------------------------");

		prepareGame();

		System.out.println("\n>>>>USERS ACCEPTING TO PLAY");
		for (int i = 1; i < users.size(); i++) {
			game = gameService.acceptGame(game, users.get(i - 1));
			System.out.println(users.get(i - 1).getUsername() + " joined the game");
		}

		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

		game = gameService.resolveGame(game);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnlyOneInvestment() throws Exception {

		System.out.println("\n-------------------------------------------------------------------------------");
		System.out.println("TEST -There is more than one player, but only one user registers an investment!");
		System.out.println("-------------------------------------------------------------------------------");

		prepareGame();

		System.out.println("\n>>>>USERS ACCEPTING TO PLAY");
		for (int i = 1; i < users.size(); i++) {
			game = gameService.acceptGame(game, users.get(i - 1));
			System.out.println(users.get(i - 1).getUsername() + " joined the game");
		}

		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

		System.out.println("\n>>>>PLAYER 1 INVESTS");
		game = gameService.invest((CommonGoodsGame) game, users.get(0), String.valueOf(PARAM_INITIAL_ALLOWANCE / 2));
		System.out.println(users.get(0).getUsername() + " invested " + String.valueOf(PARAM_INITIAL_ALLOWANCE / 2));

		game = gameService.resolveGame(game);
	}

	@Test()
	public void testAllInvestNothing() throws Exception {

		System.out.println("\n--------------------------------------------------------------------");
		System.out.println("TEST - There is more than one player and everybody invests nothing !");
		System.out.println("--------------------------------------------------------------------");

		prepareGame();

		double userInvestmentProvided = 0;
		double averageOtherInvestments = 0;
		double userGainingProvided = this.PARAM_INITIAL_ALLOWANCE;

		double userGainingExpected = playTheGame(userInvestmentProvided, averageOtherInvestments);

		Asserts.check(userGainingProvided == userGainingExpected, "No match!");

		System.out.println("\n>>>>RESULTS");
		System.out.println(users.get(0).getUsername() + " obtains " + userGainingExpected + " points.");

	}

	@Test()
	public void testAllInvestMaximum() throws Exception {

		System.out.println("\n----------------------------------------------------------------------------");
		System.out.println("TEST - There is more than one player and everybody invests at maximum !");
		System.out.println("----------------------------------------------------------------------------");

		prepareGame();

		// Test parameters
		double userInvestmentProvided = this.PARAM_INITIAL_ALLOWANCE;
		double averageOtherInvestments = this.PARAM_INITIAL_ALLOWANCE;
		double userGainingProvided = this.PARAM_INITIAL_ALLOWANCE * this.PARAM_WEIGHT;

		double userGainingExpected = playTheGame(userInvestmentProvided, averageOtherInvestments);

		Asserts.check(userGainingProvided == userGainingExpected, "No match!");

		System.out.println("\n>>>>RESULTS");
		System.out.println(users.get(0).getUsername() + " obtains " + userGainingExpected + " points.");

	}

	@Test()
	public void testFreeRider() throws Exception {

		System.out.println(
				"\n---------------------------------------------------------------------------------------------------------------------");
		System.out.println(
				"TEST - There is more than one player, one of them (free rider) invests nothing and the rest invests at maximum !");
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------");

		prepareGame();

		// Test parameters
		double userInvestmentProvided = 0;
		double averageOtherInvestments = this.PARAM_INITIAL_ALLOWANCE;
		double userGainingProvided = this.PARAM_INITIAL_ALLOWANCE + this.PARAM_WEIGHT * this.PARAM_INITIAL_ALLOWANCE
				* (this.PARAM_TEST_NUM_PLAYERS - 1) / this.PARAM_TEST_NUM_PLAYERS; // PESO * (INITIAL*(N-1))/NUMBER 65;

		double userGainingExpected = playTheGame(userInvestmentProvided, averageOtherInvestments);

		Asserts.check(userGainingProvided == userGainingExpected, "No match!");

		System.out.println("\n>>>>RESULTS");
		System.out.println(users.get(0).getUsername() + " obtains " + userGainingExpected + " points.");

	}

	@Test()
	public void testSoleContributor() throws Exception {

		System.out.println(
				"\n---------------------------------------------------------------------------------------------------------------------");
		System.out.println(
				"TEST - There is more than one player, one of them (sole contributor) invests at maximum and the rest invests nothing !");
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------");

		prepareGame();

		// Test parameters
		double userInvestmentProvided = this.PARAM_INITIAL_ALLOWANCE;
		double averageOtherInvestments = 0;
		double userGainingProvided = this.PARAM_WEIGHT * this.PARAM_INITIAL_ALLOWANCE / this.PARAM_TEST_NUM_PLAYERS; // 10

		double userGainingExpected = playTheGame(userInvestmentProvided, averageOtherInvestments);

		Asserts.check(userGainingProvided == userGainingExpected, "No match!");

		System.out.println("\n>>>>RESULTS");
		System.out.println(users.get(0).getUsername() + " obtains " + userGainingExpected + " points.");

	}

	public Double playTheGame(double userInvestment, double averageOtherInvestments) {
		System.out.println("\n>>>>USERS ACCEPTING TO PLAY");
		for (int i = 1; i < users.size(); i++) {
			game = gameService.acceptGame(game, users.get(i - 1));
			System.out.println(users.get(i - 1).getUsername() + " joined the game");
		}

		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

		System.out.println("\n>>>>PLAYER INVESTING");

		int i = 1;
		game = gameService.invest((CommonGoodsGame) game, users.get(i - 1), String.valueOf(userInvestment));
		System.out.println(users.get(i - 1).getUsername() + " invested " + String.valueOf(userInvestment));

		for (i = 2; i <= users.size(); i++) {
			game = gameService.invest((CommonGoodsGame) game, users.get(i - 1),
					String.valueOf(averageOtherInvestments));
			System.out.println(users.get(i - 1).getUsername() + " invested " + String.valueOf(averageOtherInvestments));
		}

		game = gameService.resolveGame(game);

		return ((CommonGoodsGame) game).checkBenefit(users.get(0));

	}

}
