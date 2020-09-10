package es.uca.becogames.presentation.views;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.session.SessionRegistry;
import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.OrganizationService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.components.UserForm;

@Route(value = "usermanagement", layout = MainLayout.class)
@Secured("Admin")
@PageTitle("User Management")

public class UserManagementView extends AbstractView {

	
	private Button addBtn = new Button("Add new user");

	private Checkbox showEnabledUsers= new Checkbox("Show enabled users");
	private Checkbox showNonEnabledUsers = new Checkbox("Show non-enabled users");

	private TextField filterText = new TextField();

	private Grid<User> grid = new Grid<User>(User.class);

	private UserForm form = new UserForm();

	private Dialog dialog = new Dialog();

	
	@Autowired
	public UserManagementView(UserService userService, WebNotificationService broadcastingService,
			OrganizationService orgService) {

		super(userService, broadcastingService);

	
		addBtn.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.setBean(new User());
			dialog.open();
		});

		addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add(addBtn);

		filterText.setPlaceholder("Filter by name...");
		filterText.setValueChangeMode(ValueChangeMode.EAGER);
		filterText.addValueChangeListener(e -> updateList());
		filterText.setClearButtonVisible(true);
		add(filterText);

		showEnabledUsers.setValue(true);
		showEnabledUsers.addValueChangeListener(e -> updateList());
		
		showNonEnabledUsers.setValue(false);
		showNonEnabledUsers.addValueChangeListener(e -> updateList());
		
	
		add(new HorizontalLayout(showEnabledUsers,showNonEnabledUsers));
		
		
		
		grid.setSizeFull();
		grid.setHeightByRows(true);
		grid.setColumns("role", "enabled", "firstName", "lastName", "organization", "profile", "extroversion", "cordiality",
				"responsibility", "stability", "openness");

		grid.getColumnByKey("organization").setComparator((o1,o2)->o1.getOrganization().getId().compareTo(o2.getOrganization().getId()));
		
		
		grid.asSingleSelect().addValueChangeListener(event -> {

			if (event.getValue() != null) {
				form.setBean(event.getValue());
				dialog.open();
			}
		});

		updateList();

		add(new Anchor(new StreamResource("log.xls", Exporter.exportAsExcel(grid)), "Download As Excel"));
		add(grid);

		// Dialog
		form.setOrganizationItems(orgService.findAll());
		form.setBean(new User());
		form.setRoleVisible(true);
		form.setPasswordVisible(false);
		form.setEnabledVisible(true);

		// Listen changes made by the editor
		form.addSaveListener((user) -> {

			userService.update(user);
			Notification.show("User saved");
			dialog.close();
			updateList();

		});

		// Listen changes made by the editor
		form.addCancelListener(() -> {
			dialog.close();
		});

		dialog.add(form);
		
		//this.add(new Text(broadcastingService.getUsers()));
		System.out.println("--->USERS CURRENTLY CONNECTED:"+ broadcastingService.getUsers());

	}

	private void updateList() {
		
		List<User> users = userService.findByAnyNameIncludingDisabled(filterText.getValue());
		
		if(showEnabledUsers.getValue() && showNonEnabledUsers.getValue()) {
			grid.setItems(users);	
		} else if(showEnabledUsers.getValue()) {
			grid.setItems(users.stream().filter(u->u.isEnabled()).collect(Collectors.toList()));
		} else if(showNonEnabledUsers.getValue()) {
			grid.setItems(users.stream().filter(u->!u.isEnabled()).collect(Collectors.toList()));			
		} else {
			grid.setItems(new ArrayList<User>());
		}
	}

	@Override
	protected void enter() {
		// TODO Auto-generated method stub

	}

}
