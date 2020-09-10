package es.uca.becogames.business.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Version;

@Entity
@Inheritance
@DiscriminatorColumn(name = "GAME_TYPE")
public class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2518206850461780454L;

	public static final String SHOW_GAME_RESULTS = "Show game results";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Version
	private Long version;
	
	private LocalDateTime creationDate;

	private LocalDateTime endDate;

	private LocalDateTime lastActionDate;

	private Float score;

	@Enumerated(EnumType.STRING)
	private GameStatus previousStatus;

	@Enumerated(EnumType.STRING)
	private GameStatus status;

	@ManyToOne
	private User owner;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	protected Set<Player> players;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	private Set<GameParameter> parameters;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	@OrderBy("date")
	private List<GameAction> actions;

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public LocalDateTime getLastActionDate() {
		return lastActionDate;
	}

	public void setLastActionDate(LocalDateTime lastActionDate) {
		this.lastActionDate = lastActionDate;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public GameStatus getPreviousStatus() {
		return previousStatus;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.previousStatus = this.status;
		this.status = status;
	}

	public Set<GameParameter> getParameters() {
		if (parameters == null) {
			parameters = new LinkedHashSet<GameParameter>();
		}
		return parameters;
	}

	public void setParameters(Set<GameParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String param, Double value) {
		if (parameters == null) {
			parameters = new LinkedHashSet<GameParameter>();
		}

		GameParameter gp = new GameParameter();
		gp.setParam(param);
		gp.setValue(value);
		gp.setGame(this);

		parameters.add(gp);
	}

	public void removeParameter(String param) {
		for (GameParameter p : this.parameters) {
			if (p.getValue().equals(param)) {
				parameters.remove(p);
			}
		}

	}

	protected Double getParameter(String paramName) {
		Double value = 0.0;

		for (GameParameter gp : this.getParameters()) {
			if (gp.getParam().equals(paramName)) {
				value = gp.getValue();
			}
		}

		return value;
	}

	public boolean getParamShowGameResults() {
		return this.getParameter(Game.SHOW_GAME_RESULTS) > 0.9;
	}

	public List<GameAction> getActions() {
		return actions;
	}

	public List<GameAction> getPlayerActions(User user) {
		return this.actions.stream().filter(a -> a.getUser().equals(user)).collect(Collectors.toList());
	}

	public void setActions(List<GameAction> actions) {
		this.actions = actions;
	}

	public void addGameAction(User user, GameActionType type, String data, String explanation) {
		if (actions == null) {
			actions = new ArrayList<GameAction>();
		}

		LocalDateTime now = LocalDateTime.now();
		GameAction action = new GameAction();
		action.setType(type);
		action.setUser(user);
		action.setData(data);
		action.setGame(this);
		action.setDate(now);
		action.setExplanation(explanation);
		actions.add(action);

		this.setLastActionDate(now);

	}

	public void addPlayer(User user, PlayerStatus status) {

		if (players == null) {
			players = new LinkedHashSet<Player>();
		}
		Player ug = new Player(user, this, null);
		ug.setStatus(status);
		players.add(ug);

		user.addUserGame(ug);

	}

	public void removePlayer(User user) {

		for (Player p : this.players) {
			if (p.getUser().equals(user)) {
				players.remove(p);
			}
		}
	}

	public Player getPlayer(User usr) {
		if (players == null) {
			players = new LinkedHashSet<Player>();
		}

		Player result = null;
		for (Player p : this.players) {
			if (p.getUser().equals(usr)) {
				result = p;
			}
		}
		return result;
	}

	public String getPlayerStatus(User user) {
		Player p = this.getPlayer(user);
		if (p != null) {
			return p.getStatus().name();
		} else {
			return "Available";
		}
	}

	public boolean isUserJoined(User user) {

		Player p = getPlayer(user);

		return p != null && p.getStatus().equals(PlayerStatus.Joined);

	}

	public boolean isUserInvited(User user) {
		Player p = getPlayer(user);

		return p != null && p.getStatus().equals(PlayerStatus.Invited);
	}

	public boolean isUserRejected(User user) {
		Player p = getPlayer(user);

		return p != null && p.getStatus().equals(PlayerStatus.Rejected);
	}

	public boolean isUserRemoved(User user) {
		Player p = getPlayer(user);

		return p != null && p.getStatus().equals(PlayerStatus.Removed);
	}

	public boolean isUserInvolved(User user) {

		return this.getPlayer(user) != null;

	}

	public Set<Player> getJoinedPlayers() {

		if (players != null) {
			return players.stream().filter(p -> p.getStatus().equals(PlayerStatus.Joined)).collect(Collectors.toSet());
		} else {
			return new HashSet<Player>();
		}
	}

	public long countJoinedPlayers() {
		return players.stream().filter(p -> p.getStatus().equals(PlayerStatus.Joined)).count();
	}

	public Set<Player> getInvitedPlayers() {

		if (players != null) {
			return players.stream().filter(p -> p.getStatus().equals(PlayerStatus.Invited)).collect(Collectors.toSet());
		} else {
			return new HashSet<Player>();
		}
	}

	public long countInvitedPlayers() {
		return players.stream().filter(p -> p.getStatus().equals(PlayerStatus.Invited)).count();
	}

	public Set<Player> getInvitedOrJoinedPlayers() {

		if (players != null) {

			return players.stream().filter(
					p -> p.getStatus().equals(PlayerStatus.Invited) || p.getStatus().equals(PlayerStatus.Joined))
					.collect(Collectors.toSet());
		} else {
			return new HashSet<Player>();
		}
	}

	public boolean hasEverybodyRespondedInvitations() {

		return countInvitedPlayers() == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
