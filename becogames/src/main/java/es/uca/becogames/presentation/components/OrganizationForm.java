package es.uca.becogames.presentation.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import es.uca.becogames.business.entities.Organization;

public class OrganizationForm extends FormLayout {
	
	
	private BeanValidationBinder<Organization> binder = new BeanValidationBinder<>(Organization.class);

	private Organization organization;
	private TextField name;

	private Button save;
	private Button cancel;
	
	private SaveEventHandler saveEventHandler;
	private CancelEventHandler cancelEventHandler;
     

	
	@Autowired
	public OrganizationForm() {
		
		name = new TextField("Name");

		save = new Button("Save", evt -> this.save());
		cancel = new Button("Cancel", evt -> this.cancel());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        
  
		// entre mail y orgnization: role
		add(name,save,cancel);
		binder.bindInstanceFields(this);		
	
	}

	


	public void setBean(Organization organization) {
		this.organization=organization;
		binder.setBean(organization);

		
	}
	
	private void save() {
		
		if (binder.validate().isOk()) {
			ConfirmDialog dialog = new ConfirmDialog("Save Organization", "Do you want to save?", "OK", ev -> {

				try {
				    saveEventHandler.onSave(organization);

				} catch (DataIntegrityViolationException ex) {
					Notification.show("The Organization already exists!");
					name.focus();
				}

			}, "Cancel", e -> {
			});
			dialog.open();

		} else {
			Notification.show("Check the data!");
			name.focus();
			return;
		}

    
	}
	
	
	private void cancel() {
	    cancelEventHandler.onCancel();
	}

	
	
	public interface SaveEventHandler {
		void onSave(Organization Organization);
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
