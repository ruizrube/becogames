package es.uca.becogames.presentation.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import es.uca.becogames.business.entities.Profile;
import es.uca.becogames.business.entities.QuestionnaireItem;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.business.services.QuestionnaireItemService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.pages.SignInPage;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")

public class ProfileView extends AbstractView {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3455536686696493213L;

	@Autowired
	QuestionnaireItemService questionnaireService;

	@Autowired
	UserService userService;

	static Float psychoticismThreshold = 5.32f;
	static Float emotionalStabilityThresholdPsychoticism = 4.83f;
	static Float extroversionThreshold = 4.44f;

	String[] titles = { 
			
			"Disagree strongly", "Disagree moderately", "Disagree a little", "Neither agree nor disagree", "Agree a little", "Agree moderately", "Agree strongly"};
	
	
	Map<String, ComboBox<Entry<Float, String>>> combos;

	
	@Autowired
	public ProfileView(UserService userService, WebNotificationService broadcastingService) {
		super(userService, broadcastingService);
	}

	@Override
	protected void enter() {
	
		this.removeAll();

	
		
		if (currentUser.getProfile() == Profile.NO_DEFINED_PROFILE) {

			prepareProfileForm();

		} else {

			add(new H3("Your profile is already defined. Thank you"));

			Button defineBtn=new Button("Click here if you want to define it again", e -> {
				e.getSource().setVisible(false);
				prepareProfileForm();
			});
			defineBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			
			add(defineBtn);

			
			

		}

		// Baja usuario
		Button revoke = new Button("Warning: Deactivate my account from this web application", x -> {
			ConfirmDialog dialog = new ConfirmDialog("Account deactivation", "Are you sure?", "Deactivate",
					ev -> {

						userService.delete(currentUser);
						UI.getCurrent().navigate(SignInPage.class);
						Notification.show("User deactivate from the system");

					}, "Cancel", e -> {
					});
			dialog.open();

		});
		revoke.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		add(revoke);
	

	}

	private void prepareProfileForm() {
		Map<Float, String> items = new HashMap<Float, String>();
		for (int i = 0; i < titles.length; i++) {
			items.put(new Float(i + 1), titles[i] + " (" + (i + 1) + ")");
		}

		String textoEN ="Below are a number of personality traits that may describe you to a greater or lesser extent. Please indicate the extent to which you agree or disagree with the statement. You must indicate the extent to which any of the terms of each trait pair apply to you, even if one of the two traits reflects you better than the other.";
		Text headlineLabelEN = new Text(textoEN);
		add(headlineLabelEN);
		
//		String textoES = "A continuación se presentan una serie de rasgos de personalidad que puede que le describan a usted en mayor o menor medida. Por favor, indique en qué medida está de acuerdo o en desacuerdo con la afirmación. Debe indicar en qué medida alguno de los términos de cada par de rasgos se aplica a usted, incluso aunque alguna de las dos características le refleje mejor que la otra.";
//		Text headlineLabelES = new Text(textoES);
//		add(headlineLabelES);
//		
		
		// Combos
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultHorizontalComponentAlignment(Alignment.START);

		combos = new HashMap<String, ComboBox<Entry<Float, String>>>();
		for (QuestionnaireItem item : questionnaireService.findAll()) {
			ComboBox<Entry<Float, String>> combo = new ComboBox<Entry<Float, String>>();
			combo.setItems(items.entrySet());
			combo.setItemLabelGenerator(e -> e.getValue());
			combo.setRequired(true);
			combo.setPreventInvalidInput(true);
			combo.setSizeUndefined();
			layout.add(new H6(item.getCode() + ": " + item.getText()));
			layout.add(combo);
			combos.put(item.getCode(), combo);
		}

		this.add(layout);

		// BOtón OK
		Button ok = new Button("Save profile", e -> {

			for (ComboBox<Entry<Float, String>> combo : combos.values()) {
				if (combo.isEmpty()) {
					Notification.show("Complete all the fields");
					return;
				}
			}

			currentUser.setProfile(computeProfile());

			currentUser = userService.update(this.currentUser);
			UI.getCurrent().getSession().setAttribute(User.class, currentUser);
			UI.getCurrent().navigate(HomeView.class);
			Notification.show("Profile updated");
		});

		ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.add(ok);
	}

	private Profile computeProfile() {

		Float extroversion; // SUM(TIPI1 + 8 - TIPI6)/2
		extroversion = (Float.valueOf(combos.get("TIPI-1").getValue().getKey()) + 8
				- Float.valueOf(combos.get("TIPI-6").getValue().getKey())) / 2;

		Float cordialidad; // SUM(8 - TIPI2 + TIPI7)/2
		cordialidad = (8 - Float.valueOf(combos.get("TIPI-2").getValue().getKey())
				+ Float.valueOf(combos.get("TIPI-7").getValue().getKey())) / 2;

		Float responsabilidad; // SUM(TIPI3 + 8 - TIPI8)/2
		responsabilidad = (Float.valueOf(combos.get("TIPI-3").getValue().getKey()) + 8
				- Float.valueOf(combos.get("TIPI-8").getValue().getKey())) / 2;

		Float estabilidad; // SUM(8 - TIPI4 + TIPI9)/2
		estabilidad = (8 - Float.valueOf(combos.get("TIPI-4").getValue().getKey())
				+ Float.valueOf(combos.get("TIPI-9").getValue().getKey())) / 2;

		Float apertura; // SUM(TIPI5 + 8 - TIPI10)/2
		apertura = (Float.valueOf(combos.get("TIPI-5").getValue().getKey()) + 8
				- Float.valueOf(combos.get("TIPI-10").getValue().getKey())) / 2;

		currentUser.setExtroversion(extroversion);
		currentUser.setCordiality(cordialidad);
		currentUser.setResponsibility(responsabilidad);
		currentUser.setStability(estabilidad);
		currentUser.setOpenness(apertura);

		// String result = "\n Extroversion:" + extroversion + "\n Cordialidad:" +
		// cordialidad + "\n Responsabilidad:"
		// + responsabilidad + "\n Estabilidad:" + estabilidad + "\n Apertura:" +
		// apertura;

		// Notification.show(result, 10000, Position.MIDDLE);

		Float mediaCordialidadResponsabilidad = (cordialidad + responsabilidad) / 2;

		boolean psicotico = (mediaCordialidadResponsabilidad <= psychoticismThreshold);
		boolean extrovertido = (extroversion > extroversionThreshold);
		boolean racional = (estabilidad > emotionalStabilityThresholdPsychoticism);

		if (psicotico) {

			if (extrovertido) {

				if (racional) {
					return Profile.TIGER;
				} else {
					return Profile.COYOTE;
				}

			} else {

				if (racional) {
					return Profile.SHARK;
				} else {
					return Profile.CROCODILE;
				}

			}

		} else {

			if (extrovertido) {

				if (racional) {
					return Profile.LION;
				} else {
					return Profile.DOG;
				}

			} else {

				if (racional) {
					return Profile.EAGLE;
				} else {
					return Profile.OWL;
				}

			}

		}

		// TODO Auto-generated method stub

	}

	
}
