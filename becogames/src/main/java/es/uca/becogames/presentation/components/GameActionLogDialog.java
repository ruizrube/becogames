package es.uca.becogames.presentation.components;

import java.util.List;

import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.server.StreamResource;

import es.uca.becogames.business.entities.GameAction;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.security.SecurityUtils;

public class GameActionLogDialog extends Dialog {

	Grid<GameAction> actionsGrid;
	
	H3 header;

	
	public GameActionLogDialog(String title) {

		header=new H3(title);
		add(header);
		
			
		actionsGrid = new Grid<GameAction>(GameAction.class);
		actionsGrid.setSizeFull();

		actionsGrid.removeColumnByKey("id");
		actionsGrid.removeColumnByKey("explanation");
		actionsGrid.removeColumnByKey("game");
		actionsGrid.removeColumnByKey("user");

		actionsGrid.addColumn("user.firstName");
		actionsGrid.addColumn("user.lastName");
		actionsGrid.addColumn("user.dni");
		actionsGrid.setColumns("date", "user.firstName", "user.lastName", "user.dni", "type", "data");
		
		
		if (SecurityUtils.hasRole(Role.Admin.name())) {
			add(new Anchor(new StreamResource("detailedlog.xls", Exporter.exportAsExcel(actionsGrid)),
					"Download As Excel"));
		}


		add(actionsGrid);

		setHeight("100vh");
		setWidth("100vh");
		actionsGrid.setHeightByRows(true);

	}

	public void setItems(List<GameAction> actions) {
		actionsGrid.setItems(actions);	
		
	}

}
