package es.uca.becogames.presentation.errors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;

import es.uca.becogames.presentation.views.GameView;

@Route(value = "org.springframework.orm.ObjectOptimisticLockingFailureException")
public class OptimisticLockingFailureView extends VerticalLayout implements HasErrorParameter<ObjectOptimisticLockingFailureException>{

    H1 lbl = new H1("There is an error");
    H2 msg = new H2();

    
    private static final long serialVersionUID = -5006182334763630785L;

	public OptimisticLockingFailureView() {
		
        setMargin(true);
        add(lbl,msg);
    }

	
	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<ObjectOptimisticLockingFailureException> parameter) {
		System.out.println("ERROR DE StaleObjectStateException!!!");
		
		event.rerouteTo(GameView.class);

	   return HttpServletResponse.SC_CONTINUE;
	}

}
