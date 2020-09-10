package es.uca.becogames.business.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class GameAction implements Serializable
{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 239676752189156537L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    
    private String data;
    
    private String explanation;
    
    @ManyToOne
    Game game;
    
    @ManyToOne
    User user;
    
    @Enumerated(EnumType.STRING)
	GameActionType type;
	
    private LocalDateTime date;

    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public GameActionType getType() {
		return type;
	}

	public void setType(GameActionType type) {
		this.type = type;
	}

	public String getExplanation() {
		
		if(this.explanation!=null) {
			return this.explanation;			
		} else {
			return this.toString();
		}
	}
	
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	
	@Override
    public String toString() {
    	
		return user.getUsername() + " " + this.getType() + " " + this.getData() + " on " + this.getDate();
    }
}

