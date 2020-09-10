package es.uca.becogames.business.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameActionType;
import es.uca.becogames.business.entities.GameParameter;
import es.uca.becogames.business.entities.GameStatus;
import es.uca.becogames.business.entities.Player;
import es.uca.becogames.business.entities.PlayerStatus;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.data.jparepositories.GameRepository;

public abstract class GameService {

	@Autowired
	MailNotificationService mailNotificationService;

	// @Autowired
	// PushNotificationService pushNotificationService;

	@Autowired
	WebNotificationService webNotificationService;

	@Autowired
	private GameRepository repo;

	public Game save(Game entry) {
		return repo.save(entry);
	}

	public Game findById(Long id) {
		return repo.findById(id).orElse(null);
	}

	public List<Game> findAll() {
		return repo.findAll();
	}

	public List<Game> findByUser(User user) {
		Set<Game> aux = new LinkedHashSet<Game>();
		aux.addAll(repo.findByOwner(user));
		aux.addAll(repo.findByPlayers_User(user));
		return new ArrayList<Game>(aux);
	}

	public void delete(Game value) {
		repo.delete(value);
	}

	public long count() {
		return repo.count();
	}

	@Transactional
	protected Game createNewGame(Game game, User user) {

		game.setCreationDate(LocalDateTime.now());
		game.setStatus(GameStatus.New);
		game.setOwner(user);
		game = this.save(game);

		String explanation = "You have created " + game.toString();

		game.addGameAction(user, GameActionType.Created, null, explanation);
		game = this.save(game);

		// Notificamos al usuario
		webNotificationService.broadcast(game.getOwner().getUsername(), explanation);

//		if(game.getOwner().getSubscription()!=null) {
//			pushNotificationService.broadcast("[BECOGAMES]", explanation, "www.google.com", game.getOwner().getSubscription());
//	
//		}
//	
		return game;
	}

	@Transactional
	public Game startGame(Game game, User user) {

		String explanation = "You have started " + game.toString();

		game.setStatus(GameStatus.Running);

		String parameterData = "";
		for (GameParameter gp : game.getParameters()) {
			parameterData += "\"" + gp.getParam() + "\":" + gp.getValue() + ", ";
		}

		game.addGameAction(user, GameActionType.Started, parameterData, explanation);
		game = this.save(game);

		// Notificamos al usuario
		webNotificationService.broadcast(game.getOwner().getUsername(), explanation);

		String broadcastExplanation = game.toString() + " has started!";

		// Notificamos a los jugadores unidos
		mailNotificationService.sendMailGameRunning(game);
		for (Player p : game.getJoinedPlayers()) {
			webNotificationService.broadcast(p.getUser().getUsername(), broadcastExplanation);
		}

		return game;
	}

	@Transactional
	public Game pauseGame(Game game, User user) {
		String explanation = "You have paused " + game.toString();

		game.setStatus(GameStatus.Paused);
		game.addGameAction(user, GameActionType.Paused, null, explanation);
		game = this.save(game);

		// Notificamos al usuario
		webNotificationService.broadcast(game.getOwner().getUsername(), explanation);

		// Notificamos al resto de jugadores
		String broadcastExplanation = game.toString() + " has been paused!";
		for (Player p : game.getJoinedPlayers()) {
			webNotificationService.broadcast(p.getUser().getUsername(), broadcastExplanation);
		}

		return game;
	}

	@Transactional
	public Game resumeGame(Game game, User user) {
		String explanation = "You have resumed " + game.toString();

		game.setStatus(game.getPreviousStatus());
		game.addGameAction(user, GameActionType.Resumed, null, explanation);
		game = this.save(game);

		// Notificamos al usuario
		webNotificationService.broadcast(game.getOwner().getUsername(), explanation);

		// Notificamos al resto de jugadores
		String broadcastExplanation = game.toString() + " has been resumed!";
		for (Player p : game.getJoinedPlayers()) {
			webNotificationService.broadcast(p.getUser().getUsername(), broadcastExplanation);
		}

		return game;
	}

	@Transactional
	public Game stopGame(Game game, User user) {
		String explanation = "You have stopped " + game.toString();

		game.setStatus(GameStatus.Stopped);
		game.addGameAction(user, GameActionType.Stopped, null, explanation);
		game = this.save(game);

		// Notificamos al usuario
		webNotificationService.broadcast(game.getOwner().getUsername(), explanation);

		// Notificamos al resto de jugadores
		String broadcastExplanation = game.toString() + " has been stopped!";
		for (Player p : game.getJoinedPlayers()) {
			webNotificationService.broadcast(p.getUser().getUsername(), broadcastExplanation);
		}

		return game;
	}

	@Transactional
	public Game resolveGame(Game game) {
		String broadcastExplanation = game.toString() + " has been resolved!";

		game.setStatus(GameStatus.Resolved);
		game.addGameAction(game.getOwner(), GameActionType.Resolved, null, broadcastExplanation);

		markRemovedPlayers(game);

		computeBenefits(game);

		game =  this.save(game);

		// Notificamos via broadcasting a todos los jugadores unidos (incluyendo al
		// creator si este estuviese unido ya)
		for (Player player : game.getJoinedPlayers()) {
			webNotificationService.broadcast(player.getUser().getUsername(), broadcastExplanation);
		}

		// Si el owner NO participa en la partida, lo notificamos
		if (!game.isUserJoined(game.getOwner())) {
			webNotificationService.broadcast(game.getOwner().getUsername(), broadcastExplanation);

		}

		// Notificamos por mail al creator y al resto de joined players
		mailNotificationService.sendMailGameResolved(game);

		return game;
	}

	@Async
	@Transactional
	public Game inviteUsers(Game game, Set<User> users) {

		// Registramos la acción de invitar
		String usersString;
		if (users.size() > 5) {
			usersString = users.stream().map(u -> u.getUsername()).limit(5).collect(Collectors.joining(","));
			usersString += " and other users";
		} else {
			usersString = users.stream().map(u -> u.getUsername()).collect(Collectors.joining(","));
		}

		String explanationOwner = "You have invited " + usersString + " to join " + game.toString();
		game.addGameAction(game.getOwner(), GameActionType.Sent_Invitation, null, explanationOwner);

		String explanation = game.getOwner() + " has invited you to " + game.toString();

		// Para cada receptor de la invitacion..
		for (User user : users) {

			if (!game.isUserInvolved(user)) { // Si el usuario no participa en la partida, lo invitamos..
				game.addGameAction(user, GameActionType.Received_Invitation, null, explanation);
				game.addPlayer(user, PlayerStatus.Invited);
				game = this.save(game);
				webNotificationService.broadcast(user.getUsername(), explanation);

			} else if (game.isUserInvited(user)) { // Si el usuario ya tenia una invitación pendiente, se lo
													// recordamos...

				webNotificationService.broadcast(user.getUsername(), explanation);

			} else if (game.isUserRejected(user)) { // SI el usuario ya rechazó la partida, le actualizamos el status..

				game.addGameAction(user, GameActionType.Received_Invitation, null, explanation);
				game.getPlayer(user).setStatus(PlayerStatus.Invited);
				game = this.save(game);
				webNotificationService.broadcast(user.getUsername(), explanation);

			} else if (game.isUserJoined(user)) { // Si el usuario ya está unido no hacemos nada

			} else if (game.isUserRemoved(user)) { // Si el usuario fue eliminado de la partida no hacemos nada

			}

		}

		// Email a todos los usuarios que esten actualmente invitados (los que ya estaban se le manda recordatorio)
		mailNotificationService.sendMailInvitateUsers(game);

		// Notificamos al creador
		webNotificationService.broadcast(game.getOwner().getUsername(), explanationOwner);

		return game;

	}

	public Game joinOwnerToTheGame(Game game) {

		if (!game.isUserJoined(game.getOwner())) {

			game.addPlayer(game.getOwner(), PlayerStatus.Invited);
			return acceptGame(game, game.getOwner());

		} else {
			return game;
		}

	}

	public Game removeOwnerFromTheGame(Game game) {
		if (!game.isUserJoined(game.getOwner())) {
			return game;
		} else {
			return rejectGame(game, game.getOwner());
		}

	}

	@Transactional
	public Game acceptGame(Game game, User user) {

		String explanation = "You have joined " + game.toString();
		String broadcastExplanation = "Someone has joined " + game.toString() + " !";

		Player player = game.getPlayer(user);
		if (player != null) {

			player.setStatus(PlayerStatus.Joined);
			game.addGameAction(user, GameActionType.Accepted_Invitation, null, explanation);
			game = this.save(game);

			// Notificamos al usuario
			webNotificationService.broadcast(user.getUsername(), explanation);

			// Notificamos a los demás
			for (Player p : game.getJoinedPlayers()) {
				if (!p.getUser().equals(user)) {
					webNotificationService.broadcast(p.getUser().getUsername(), broadcastExplanation);
				}
			}

			// Notificaciones sólo cuando el usuario que se agrega NO es el propio creator
			if (!user.equals(game.getOwner())) {

				// Notificamos al creator de que alguien se ha unido
				webNotificationService.broadcast(game.getOwner().getUsername(),
						user.getUsername() + " has joined " + game.toString());

				// Notificamos al creator que todos los usuarios invitados han respondido si
				// fuera el caso
				if (game.hasEverybodyRespondedInvitations()) {
					mailNotificationService.sendMailGameEveryBodyRespondedInvitations(game);
					webNotificationService.broadcast(game.getOwner().getUsername(), game.toString()
							+ ": There are no pending invitations to manage. All the invited users have responded to the invitations to join/reject the game");
				}

			}
		}

		return game;

	}

	@Transactional
	public Game rejectGame(Game game, User user) {

		String explanation = "You have rejected " + game.toString();

		Player player = game.getPlayer(user);

		if (player != null) {
			player.setStatus(PlayerStatus.Rejected);
			game.addGameAction(user, GameActionType.Rejected_Invitation, null, explanation);
			game = this.save(game);

			// Notificamos al usuario
			webNotificationService.broadcast(user.getUsername(), explanation);

			// Notificaciones sólo cuando el usuario que rechaza NO es el propio creator
			if (!user.equals(game.getOwner())) {

				// Notificamos al creator de que el usuario ha rechazado unirse a la partida
				webNotificationService.broadcast(game.getOwner().getUsername(),
						user.getUsername() + " has rejected " + game.toString());

				// Notificamos al creator que todos los usuarios invitados han respondido si
				// fuera el caso
				if (game.hasEverybodyRespondedInvitations()) {
					mailNotificationService.sendMailGameEveryBodyRespondedInvitations(game);
					webNotificationService.broadcast(game.getOwner().getUsername(), game.toString()
							+ ": There are no pending invitations to manage. All the invited users have responded to the invitations to join/reject the game");

				}
			}

		}

		return game;

	}

	public void deleteAll() {
		this.repo.deleteAllInBatch();

	}

	protected abstract void markRemovedPlayers(Game game);

	public abstract void computeBenefits(Game game);

}
