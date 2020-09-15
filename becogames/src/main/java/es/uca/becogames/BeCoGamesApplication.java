package es.uca.becogames;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.spring.SpringVaadinServletService;

import es.uca.becogames.business.entities.Gender;
import es.uca.becogames.business.entities.Organization;
import es.uca.becogames.business.entities.QuestionnaireItem;
import es.uca.becogames.business.entities.QuestionnaireItemType;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.OrganizationService;
import es.uca.becogames.business.services.QuestionnaireItemService;
import es.uca.becogames.business.services.UserService;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "es.uca.becogames.data")
public class BeCoGamesApplication {
	
	
	
	public static void main(String[] args) {

		SpringApplication.run(BeCoGamesApplication.class, args);
	}


	
	
	
	@Bean
	public CommandLineRunner loadData(UserService userService,QuestionnaireItemService questionnaireService,OrganizationService organizationService) {
		return (args) -> {
			
			System.out.println("PREPARING INITIAL DATA (if necessary...)");

			createInitialData(userService, organizationService);
			
			createQuestionnaire(questionnaireService);
			
			System.out.println("BECOGAME IS RUNNING!!!");

		};

	}

	private void createQuestionnaire(QuestionnaireItemService questionnaireService) {
		if (questionnaireService.findAll().isEmpty()) {
			System.out.println("Creating questionnaire items...");
			
			String []titles= {"Extravertido, entusiasta",
					"Crítico, combativo",
					"Fiable, auto-disciplinado",
					"Ansioso, fácilmente alterable",
					"Abierto a experiencias nuevas, polifacético",
					"Reservado, tranquilo",
					"Comprensivo, afectuoso",
					"Desorganizado, descuidado",
					"Sereno, emocionalmente estable",
					"Tradicional, poco imaginativo", };
			
			for(int i=0;i<titles.length;i++) {
				QuestionnaireItem item=new QuestionnaireItem();
				item.setCode("TIPI-"+(i+1));
				item.setText(titles[i]);
				item.setType(QuestionnaireItemType.TIPI);
				questionnaireService.save(item);
					
			}
			
			System.out.println("Questionnaire items created.");
			
			
		}
	}

	
	
	public void createInitialData(UserService userService,OrganizationService organizationService) {
		
		
		if (userService.findAll().isEmpty() && organizationService.findAll().isEmpty()) {
			
			System.out.println("Creating org: UCA...");
			Organization orgUCA=new Organization("UCA");
			orgUCA=organizationService.save(orgUCA);
			
			System.out.println("Creating org: UNED...");
			Organization orgUNED=new Organization("UNED - Análisis Económico del Turismo");
			orgUNED=organizationService.save(orgUNED);
			
			System.out.println("Creating org: BL...");
			Organization orgBL=new Organization("BEHAVIOUR &  LAW");
				orgBL=organizationService.save(orgBL);
			
			
			System.out.println("Creating user ADMIN...");
			User admin = new User("Admin", "BECOGames", "admin");
			admin.setPassword("CHANGE_THIS_PASSWORD");
			admin.setRole(Role.Admin);
			admin.setMail("CHANGE_THIS@mail.com");
			admin.setOrganization(orgUCA);
			admin.setBirthDate(LocalDate.of(2000, 11, 11));
			admin.setGender(Gender.MAN);	
			admin.setDni("00000000");
			admin=userService.createAndActivate(admin);
						
			System.out.println("Users and orgs created.");
			
		}
		
		
	
	}

		

}
