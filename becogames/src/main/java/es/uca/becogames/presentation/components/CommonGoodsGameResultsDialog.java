package es.uca.becogames.presentation.components;

import java.util.List;

import org.vaadin.haijian.Exporter;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.server.StreamResource;

import es.uca.becogames.business.dto.CommonGoodsGameResult;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.security.SecurityUtils;

public class CommonGoodsGameResultsDialog extends Dialog {

	Grid<CommonGoodsGameResult> resultGrid;

	H3 header;


	public CommonGoodsGameResultsDialog(List<CommonGoodsGameResult> data) {

		header=new H3("Results of The Game of the Common Goods");
		add(header);

		resultGrid = new Grid<CommonGoodsGameResult>(CommonGoodsGameResult.class);
		resultGrid.setSizeFull();
		if (SecurityUtils.hasRole(Role.Admin.name())) {
			resultGrid.setColumns("ranking", "firstName", "lastName", "dni", "invested", "gained");
		
			add(new Anchor(new StreamResource("results.xls", Exporter.exportAsExcel(resultGrid)),
					"Download As Excel"));
		} else {
			resultGrid.setColumns("ranking", "username", "invested", "gained");
		}

		resultGrid.setItems(data);	
		
		add(resultGrid);

		setHeight("100vh");
		setWidth("100vh");
		
		resultGrid.setHeightByRows(true);

	}

	

}
