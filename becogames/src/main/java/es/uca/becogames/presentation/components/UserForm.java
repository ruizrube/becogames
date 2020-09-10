package es.uca.becogames.presentation.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import es.uca.becogames.business.entities.Gender;
import es.uca.becogames.business.entities.Organization;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.business.entities.User;

public class UserForm extends FormLayout {
	
	
	private BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

	private User user;
	
	private TextField firstName;

	private TextField lastName;

	private TextField dni;

	private TextField username;

	private PasswordField password;

	private PasswordField password2;

	private EmailField mail;

	@Enumerated(EnumType.STRING)
	private ComboBox<Role> role;

	@Enumerated(EnumType.STRING)
	private ComboBox<Organization> organization;

	private ComboBox<Gender> gender;

	private DatePicker birthDate;

	private Checkbox enabled;

	private Checkbox consent;

	private Button save;
	private Button cancel;
	
	private SaveEventHandler saveEventHandler;
	private CancelEventHandler cancelEventHandler;
     

	
	@Autowired
	public UserForm() {
		
		firstName = new TextField("First name");

		lastName = new TextField("Last name");

		dni = new TextField("National identity number");

		gender = new ComboBox<Gender>("Gender");
		gender.setItems(Gender.values());

		birthDate = new DatePicker("Birthdate");
		birthDate.setInitialPosition(LocalDate.of(2000, 1, 1)); 
		
		mail = new EmailField("Email");

		role = new ComboBox<Role>("Role");
		role.setItems(Role.values());

		organization = new ComboBox<Organization>("Organization");
		organization.setItems(new ArrayList<Organization>());
		organization.setItemLabelGenerator(org -> org.getName());

		username = new TextField("Username");

		password = new PasswordField("Password");

		password2 = new PasswordField("Re-type password");
		
		enabled = new Checkbox("Enabled");
		
		consent = new Checkbox(
				"I give my consent to the staff from the Universidad de Cádiz, UNED and Fundación Universitaria Behaviour and Law to record my activity data for research purposes. In addition, you allow us to send emails to you with notifications. You will be able to revoke this consent at any time.");

	
		save = new Button("Save", evt -> this.save());
		cancel = new Button("Cancel", evt -> this.cancel());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        
  
		// entre mail y orgnization: role
		add(firstName, lastName, dni, gender, birthDate, mail, role, organization, username, password, password2, enabled, consent,save,cancel);
		binder.bindInstanceFields(this);		
	
	}

	


	public void setBean(User user) {
		this.user=user;
		binder.setBean(user);
		password2.setValue(password.getValue());

		
	}
	public void setOrganizationItems(List<Organization> organizations) {
		this.organization.setItems(organizations);
		
	}
	
	public void setRoleVisible(boolean b) {
		this.role.setVisible(b);
		
	}


	public void setEnabledVisible(boolean b) {
		this.enabled.setVisible(b);
		
	}

	public void setPasswordVisible(boolean b) {
		this.password.setVisible(b);
		this.password2.setVisible(b);
		
		// TODO Auto-generated method stub
		
	}


	private void save() {
		
		if (binder.validate().isOk() && password.getValue().equals(password2.getValue()) && consent.getValue()) {
			ConfirmDialog dialog = new ConfirmDialog("Save user", "Do you want to save?", "OK", ev -> {

				try {
				    saveEventHandler.onSave(user);

				} catch (DataIntegrityViolationException ex) {
					Notification.show("The username selected already exists!");
					username.focus();
				}

			}, "Cancel", e -> {
			});
			dialog.open();

		} else {
			Notification.show("Check the data!");
			password.focus();
			return;
		}

    
	}
	
	
	private void cancel() {
	    cancelEventHandler.onCancel();
	}

	
	
	public interface SaveEventHandler {
		void onSave(User user);
	}
	public interface CancelEventHandler {
		void onCancel();
	}
	
	
	public void addSaveListener(SaveEventHandler listener) {
		saveEventHandler = listener;
	}


	public void addCancelListener(CancelEventHandler listener) {
		cancelEventHandler = listener;
	}




	

	
}
