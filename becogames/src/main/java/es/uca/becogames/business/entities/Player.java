/**
 * 
 */
package es.uca.becogames.business.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author ivanruizrube
 *
 */
@Entity
public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498039523939586380L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	User user;

	@ManyToOne
	Game game;

	@Enumerated(EnumType.STRING)
	PlayerStatus status;
	
	protected Player() {

	}

	public Player(User user, Game game, PlayerStatus status) {
		this.user = user;
		this.game = game;
		this.status=status;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	
}
