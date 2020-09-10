package es.uca.becogames;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import es.uca.becogames.business.dto.CommonGoodsGameResult;
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
@SpringBootTest(properties = { "SERVER_PORT=???", "SERVER_URL=http://???:???",
		"MAIL_URL=???", "MAIL_PORT=???", "MAIL_USERNAME=???", "MAIL_PASSWORD=???",
		"MAIL_MAIN=???", "MAIL_HEADER=???" })

public class GameIntegrationTest {

	// @MockBean
	// JavaMailSender d;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonGoodsGameService gameService;

	@Autowired
	private OrganizationService organizationService;

	private static User admin;

	private static List<User> users = new ArrayList<User>();

	private static boolean dataLoaded = false;

	@Before
	public void setUp() {

		if (!dataLoaded) {

			dataLoaded = true;

			System.out.println("\n>>>>PREPARAMOS ENTORNO DE PRUEBAS");

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
			demo.setOrganization(organizationService.findByName("UCA").get(0));
			demo.setBirthDate(LocalDate.of(1990, 11, 11));
			demo.setGender(Gender.MAN);
			demo.setDni("99999999");
			demo = userService.createAndActivate(demo);

			users = new ArrayList<User>();

			for (int i = 1; i <= 200; i++) {
				User obj = new User("test" + i, "test" + i, "test" + i);
				obj.setPassword("test" + i);
				obj.setRole(Role.Player);
				obj.setMail("test" + i + "@hotmail.com");
				obj.setOrganization(organizationService.findByName("UCA").get(0));
				obj.setBirthDate(LocalDate.of(1801 + i, 11, i % 30 + 1));
				obj.setGender(Gender.WOMAN);
				obj.setDni("0000000" + i);
				obj = userService.createAndActivate(obj);
				System.out.println("User " + "test" + i + " created!!");
				users.add(obj);

			}

		}
	}

	@Test
	public void testDummy() throws Exception {
		System.out.println("Test dummy!");

	}

	@Test
	public void testCreateAndResolveGame() throws Exception {

		System.out.println("\n>>>>CREAMOS JUEGO");
		Game game = gameService.createNewGame(admin);
		System.out.println("Partida creada");

		System.out.println("\n>>>>INVITAMOS A JUGADORES");
		game = gameService.inviteUsers(game, new HashSet<User>(users));

		System.out.println("\n>>>>JUGADORES ACEPTAN");
		for (int i = 1; i < users.size(); i++) {
			game = gameService.acceptGame(game, users.get(i - 1));
			System.out.println(users.get(i - 1).getUsername() + " joined the game");
		}

		System.out.println("\n>>>>JUGADORES RECHAZAN");

		game = gameService.rejectGame(game, users.get(users.size() - 1));
		System.out.println(users.get(users.size() - 1).getUsername() + " rejected the game");

		System.out.println("\n>>>>ARRANCAMOS LA PARTIDA");
		game.addParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE, 25d);
		game.addParameter(CommonGoodsGame.PARAM_WEIGHT, 2d);
		game.addParameter(CommonGoodsGame.PARAM_RESOLVE_AUTOMATICALLY, 1.0);
		game.addParameter(Game.SHOW_GAME_RESULTS, 1.0);
		game = gameService.startGame(game, admin);
		System.out.println("Partida arrancada");

		System.out.println("\n>>>>JUGADORES QUE NO INVIERTEN");
		System.out.println(users.get(0).getUsername() + " did not invest ");

		System.out.println("\n>>>>JUGADORES INVIERTEN");
		for (int i = 2; i < users.size(); i++) {
			game = gameService.invest((CommonGoodsGame) game, users.get(i - 1), String.valueOf(i % 25));
			System.out.println(users.get(i - 1).getUsername() + " invested " + String.valueOf(i % 25));
		}

		System.out.println("\n>>>>RESOLVEMOS");
		game = gameService.resolveGame(game);

		System.out.println("\n>>>>RESULTADOS");
		for (CommonGoodsGameResult result : ((CommonGoodsGame) game).getResults()) {
			System.out.println(result);
		}

		System.out.println("game.checkMaximumObtainable: " + ((CommonGoodsGame) game).checkMaximumObtainable());
		System.out.println("game.checkMinimumObtainable: " + ((CommonGoodsGame) game).checkMinimumObtainable());
		System.out.println("game.checkSocialOptimal: " + ((CommonGoodsGame) game).checkSocialOptimal());

	}

}
