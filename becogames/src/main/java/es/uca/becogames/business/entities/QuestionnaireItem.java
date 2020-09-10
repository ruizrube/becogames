package es.uca.becogames.business.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class QuestionnaireItem implements Serializable
{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1831418920034768783L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private String code;
    
    private String text;
    
    private QuestionnaireItemType type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public QuestionnaireItemType getType() {
		return type;
	}

	public void setType(QuestionnaireItemType type) {
		this.type = type;
	}

	
    
}

