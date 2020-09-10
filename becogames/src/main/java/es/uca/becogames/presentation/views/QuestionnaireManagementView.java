package es.uca.becogames.presentation.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import es.uca.becogames.business.entities.QuestionnaireItem;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.business.services.QuestionnaireItemService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.presentation.MainLayout;
import es.uca.becogames.presentation.components.QuestionnaireItemForm;

/**
 * The main view contains a button and a click listener.
 */
@Route(value = "questionanairemanagement", layout = MainLayout.class)
@Secured("Admin")
@PageTitle("Questionanaire Management")

public class QuestionnaireManagementView extends AbstractView {

	private static final long serialVersionUID = 520967220667388475L;

	private TextField filterText = new TextField();
	private Grid<QuestionnaireItem> grid = new Grid<>(QuestionnaireItem.class);
	private Button addQuestionBtn = new Button("Add new question");

	private Dialog dialog = new Dialog();

	private QuestionnaireItemService service;

	@Autowired
	public QuestionnaireManagementView(UserService userService, WebNotificationService broadcastingService,
			QuestionnaireItemService service, QuestionnaireItemForm form) {
		super(userService, broadcastingService);
		this.service = service;

		filterText.setPlaceholder("Filter by question...");
		filterText.setValueChangeMode(ValueChangeMode.EAGER);
		filterText.addValueChangeListener(e -> updateList());
		filterText.setClearButtonVisible(true);

		addQuestionBtn.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.setItem(new QuestionnaireItem());
			dialog.open();
		});
		addQuestionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		addQuestionBtn.setVisible(false);

		grid.setSizeFull();
		grid.setHeightByRows(true);
		
		grid.setColumns("code","text","type");
		
		updateList();

		grid.asSingleSelect().addValueChangeListener(event -> {

			if (event.getValue() != null) {
				form.setItem(event.getValue());
				dialog.open();
			}
		});

		// Dialog
		dialog.add(form);

		// Listen changes made by the editor, refresh data from backend
		form.setChangeHandler(() -> {
			dialog.close();
			updateList();
		});

		add(addQuestionBtn);
		add(filterText);
		add(new Anchor(new StreamResource("log.xls", Exporter.exportAsExcel(grid)), "Download As Excel"));

		add(grid);

	}

	public void updateList() {
		grid.setItems(service.findAll(filterText.getValue()));
	}

	@Override
	protected void enter() {
		// TODO Auto-generated method stub

	}

}