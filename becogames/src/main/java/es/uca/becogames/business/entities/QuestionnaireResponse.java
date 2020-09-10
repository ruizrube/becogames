package es.uca.becogames.business.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class QuestionnaireResponse implements Serializable
{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = -3294810833525851960L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private java.lang.Float value;
     
    @ManyToOne
    QuestionnaireItem questionnaireItem;
    
    @ManyToOne
    User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.lang.Float getValue() {
		return value;
	}

	public void setValue(java.lang.Float value) {
		this.value = value;
	}

	public QuestionnaireItem getQuestionnaireItem() {
		return questionnaireItem;
	}

	public void setQuestionnaireItem(QuestionnaireItem questionnaireItem) {
		this.questionnaireItem = questionnaireItem;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    
}

