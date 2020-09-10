package es.uca.becogames.presentation.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;

import es.uca.becogames.business.entities.QuestionnaireItem;
import es.uca.becogames.business.entities.QuestionnaireItemType;
import es.uca.becogames.business.services.QuestionnaireItemService;

@SpringComponent
public class QuestionnaireItemForm extends FormLayout {
        private TextField code = new TextField("Code");
        private TextField text = new TextField("Text");
        private ComboBox<QuestionnaireItemType> type = new ComboBox<>("Type");
        private Button save = new Button("Save");
        private Button delete = new Button("Delete");

        private Binder<QuestionnaireItem> binder = new Binder<>(QuestionnaireItem.class);

        private QuestionnaireItemService service;
        private QuestionnaireItem cuestionnaireItem;
        private ChangeHandler changeHandler;
        
        
        public QuestionnaireItemForm(QuestionnaireItemService service) {
        	this.service=service;
            type.setItems(QuestionnaireItemType.values());
            binder.bindInstanceFields(this);
            HorizontalLayout buttons = new HorizontalLayout(save, delete);
            add(code, text, type,buttons);
            save.getElement().setAttribute("theme", "primary");
            setItem(null);
            save.addClickListener(e -> this.save());
            delete.addClickListener(e -> this.delete());
            
            
            type.setEnabled(false);
            code.setVisible(false);
            delete.setVisible(false);
            
        }

        
		public void setItem(QuestionnaireItem item) {
            this.cuestionnaireItem = item;
            binder.setBean(item);
            boolean enabled = item != null;
            save.setEnabled(enabled);
            delete.setEnabled(enabled);
            if (enabled) {
                code.focus();
            }
        }

        private void delete() {
            service.delete(cuestionnaireItem);
            setItem(null);
            changeHandler.onChange();
        }

        private void save() {
            service.save(cuestionnaireItem);
            setItem(null);
            changeHandler.onChange();
        }
        
    	public interface ChangeHandler {

    		void onChange();
    	}
    	
    	public void setChangeHandler(ChangeHandler h) {
    		// ChangeHandler is notified when either save or delete
    		// is clicked
    		changeHandler = h;
    	}


    }