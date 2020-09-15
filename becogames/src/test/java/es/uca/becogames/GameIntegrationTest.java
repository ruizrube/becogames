package es.uca.becogames;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.util.Asserts;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.Gender;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.CommonGoodsGameService;
import es.uca.becogames.business.services.OrganizationService;
import es.uca.becogames.business.services.UserService;

//@DataJpaTest

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "SERVER_PORT=???", "SERVER_URL=http://???:???", "MAIL_URL=???",
		"MAIL_PORT=???", "MAIL_USERNAME=???", "MAIL_PASSWORD=???", "MAIL_MAIN=???",
		"MAIL_HEADER=???", "MAIL_NOTIFICATIONS=???" })

public class GameIntegrationTest {

	// @MockBean
	// JavaMailSender d;

	private @Autowired UserService userService;
	private @Autowired CommonGoodsGameService gameService;
	private @Autowired OrganizationService organizationService;

	private static User admin;

	private static Game game;

	private static List<User> users = new ArrayList<User>();

	private static boolean dataLoaded = false;

	private static double PARAM_INITIAL_ALLOWANCE = 0.25d;
	private static int PARAM_TEST_NUM_PLAYERS = 50;
	private static double PARAM_WEIGHT = 2d;

	public void environmentSetUp() {

		if (!dataLoaded) {

			dataLoaded = true;

			System.out.println("\n>>>>PREPARING TESTING SETTINGS");

			admin = userService.findByUsername("admin");
			if (admin != null) {
				System.out.println("User admin loaded!!");
			} else {
				System.out.println("Unable to load user admin!!");
			}

			User demo = new User("John", "Johny", "password");
			demo.setPassword("password");
			demo.setRole(Role.Player);
			demo.setMail("johnjohn@gmail.com");
			demo.setOrganization(organizationService.findAll().get(0));
			demo.setBirthDate(LocalDate.of(1990, 11, 11));
			demo.setGender(Gender.MAN);
			demo.setDni("99999999");
			demo = userService.createAndActivate(demo);

			users = new ArrayList<User>();

			for (int i = 1; i <= PARAM_TEST_NUM_PLAYERS; i++) {
				User obj = new User("studentTest" + i, "test" + i, "test" + i);
				obj.setPassword("test" + i);
				obj.setRole(Role.Player);
				obj.setMail("studentTest" + i + "@mail.com");
				obj.setOrganization(organizationService.findAll().get(0));
				obj.setBirthDate(LocalDate.of(1801 + i, 11, i % 30 + 1));
				obj.setGender(Gender.WOMAN);
				obj.setDni("0000000" + i);
				obj = userService.createAndActivate(obj);
				System.out.println("User " + "test" + i + " created!!");
				users.add(obj);

			}

		}
	}

	@BeforeEach
	public void gameSetUp() {

		environmentSetUp();

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
		System.out.println("\n---------------------------------------------------------------------------------------------------------------------");
		System.out.println("TEST - dummy!");
		System.out.println("---------------------------------------------------------------------------------------------------------------------");

		System.out.println("\nNothing to do");

		
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNobodyRespondsInvitation() throws Exception {

		System.out.println("\n------------------------------------");
		System.out.println("TEST Nobody responds the invitation!");
		System.out.println("------------------------------------");

		gameSetUp();
		
		System.out.println("\n>>>>STARTING GAME");
		game = gameService.startGame(game, admin);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNobodyAcceptsInvitation() throws Exception {

		System.out.println("\n----------------------------------------------");
		System.out.println("TEST -	Nobody accepts the joining invitation!");
		System.out.println("----------------------------------------------");

		gameSetUp();
		
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

		gameSetUp();
		
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

		gameSetUp();
		
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

		gameSetUp();
		
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

		gameSetUp();
		
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

		gameSetUp();
		
		// Test parameters
		double userInvestmentProvided = this.PARAM_INITIAL_ALLOWANCE;
		double averageOtherInvestments = this.PARAM_INITIAL_ALLOWANCE;
		double userGainingProvided = this.PARAM_INITIAL_ALLOWANCE*this.PARAM_WEIGHT;
		
		double userGainingExpected = playTheGame(userInvestmentProvided,averageOtherInvestments);

		Asserts.check(userGainingProvided == userGainingExpected, "No match!");
		
		System.out.println("\n>>>>RESULTS");
		System.out.println(users.get(0).getUsername() + " obtains " + userGainingExpected + " points.");

	}

	
	@Test()
	public void testFreeRider() throws Exception {

		System.out.println("\n---------------------------------------------------------------------------------------------------------------------");
		System.out.println("TEST - There is more than one player, one of them (free rider) invests nothing and the rest invests at maximum !");
		System.out.println("---------------------------------------------------------------------------------------------------------------------");

		gameSetUp();
		
		// Test parameters
		double userInvestmentProvided = 0;
		double averageOtherInvestments = this.PARAM_INITIAL_ALLOWANCE;
		double userGainingProvided = this.PARAM_INITIAL_ALLOWANCE + this.PARAM_WEIGHT*this.PARAM_INITIAL_ALLOWANCE*(this.PARAM_TEST_NUM_PLAYERS-1)/this.PARAM_TEST_NUM_PLAYERS;  // PESO * (INITIAL*(N-1))/NUMBER   65;

		double userGainingExpected = playTheGame(userInvestmentProvided,averageOtherInvestments);
	
		Asserts.check(userGainingProvided == userGainingExpected, "No match!");
		
		System.out.println("\n>>>>RESULTS");
		System.out.println(users.get(0).getUsername() + " obtains " + userGainingExpected + " points.");

	}
	
	
	@Test()
	public void testSoleContributor() throws Exception {

		System.out.println("\n---------------------------------------------------------------------------------------------------------------------");
		System.out.println("TEST - There is more than one player, one of them (sole contributor) invests at maximum and the rest invests nothing !");
		System.out.println("---------------------------------------------------------------------------------------------------------------------");

		gameSetUp();
		
		// Test parameters
		double userInvestmentProvided = this.PARAM_INITIAL_ALLOWANCE;
		double averageOtherInvestments = 0;
		double userGainingProvided = this.PARAM_WEIGHT * this.PARAM_INITIAL_ALLOWANCE/this.PARAM_TEST_NUM_PLAYERS; //10

		double userGainingExpected = playTheGame(userInvestmentProvided,averageOtherInvestments);

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
