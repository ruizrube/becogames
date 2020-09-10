package es.uca.becogames.presentation.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import es.uca.becogames.business.entities.Organization;
import es.uca.becogames.business.services.OrganizationService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.components.OrganizationForm;

@Route(value = "organizationmanagement", layout = MainLayout.class)
@Secured("Admin")
@PageTitle("Organization Management")

public class OrganizationManagementView extends AbstractView {

	
	private Button addBtn = new Button("Add new Organization");

	private TextField filterText = new TextField();

	private Grid<Organization> grid = new Grid<Organization>(Organization.class);

	private OrganizationForm form = new OrganizationForm();
	private Dialog dialog = new Dialog();

	private OrganizationService orgService;

	@Autowired
	public OrganizationManagementView(UserService userService, WebNotificationService broadcastingService,
			OrganizationService orgService) {

		super(userService, broadcastingService);
		this.orgService = orgService;

	
		addBtn.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.setBean(new Organization());
			dialog.open();
		});
		addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		add(addBtn);

		filterText.setPlaceholder("Filter by name...");
		filterText.setValueChangeMode(ValueChangeMode.EAGER);
		filterText.addValueChangeListener(e -> updateList());
		filterText.setClearButtonVisible(true);
		add(filterText);

		grid.setSizeFull();
		grid.setHeightByRows(true);
		grid.setColumns("name");

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
		form.setBean(new Organization());

		// Listen changes made by the editor
		form.addSaveListener((organization) -> {

			orgService.save(organization);
			Notification.show("Organization saved");
			dialog.close();
			updateList();

		});

		// Listen changes made by the editor
		form.addCancelListener(() -> {
			dialog.close();
		});

		dialog.add(form);

	}

	private void updateList() {
		grid.setItems(orgService.findByName(filterText.getValue()));

	}

	@Override
	protected void enter() {
		// TODO Auto-generated method stub

	}

}
