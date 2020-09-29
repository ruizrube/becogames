package es.uca.becogames;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.Gender;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.CommonGoodsGameService;
import es.uca.becogames.business.services.OrganizationService;
import es.uca.becogames.business.services.UserService;

@SpringBootTest(properties = { "SERVER_PORT=???", "SERVER_URL=???", "MAIL_URL=???",
		"MAIL_PORT=???", "MAIL_USERNAME=???", "MAIL_PASSWORD=???", "MAIL_MAIN=???",
		"MAIL_HEADER=???", "MAIL_NOTIFICATIONS=no", "LRS_ENDPOINT=???",
		"LRS_AUTH=???" }, classes = BeCoGamesApplication.class)

abstract public class BecoGamesBaseTest {

	protected @Autowired UserService userService;
	protected @Autowired CommonGoodsGameService gameService;
	protected @Autowired OrganizationService organizationService;

	protected static Game game;

	protected static List<User> users;
	protected static User admin;

	protected void loadAdminUser() {

		if (admin == null) {
			System.out.println("\n>>>>LOADING ADMIN USER");
			admin = userService.findByUsername("admin");
			if (admin != null) {
				System.out.println("User admin loaded!!");
			} else {
				System.out.println("Unable to load user admin!!");
			}
		}
	}

	protected void createTestUsers(int numPlayers) {

		if (users == null || users.size() < numPlayers) {

			System.out.println("\n>>>>CREATING TEST USERS");

			users = new ArrayList<User>();

			for (int i = 1; i <= numPlayers; i++) {
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

}
