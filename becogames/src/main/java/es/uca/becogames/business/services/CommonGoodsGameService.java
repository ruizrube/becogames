package es.uca.becogames.business.services;

import org.springframework.stereotype.Component;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameActionType;
import es.uca.becogames.business.entities.Player;
import es.uca.becogames.business.entities.PlayerStatus;
import es.uca.becogames.business.entities.User;

@Component
public class CommonGoodsGameService extends GameService {

	public Game createNewGame(User user) {
		return super.createNewGame(new CommonGoodsGame(), user);
	}

	public CommonGoodsGame invest(CommonGoodsGame game, User user, String value) {
		String explanation = "You have invested " + value + "p on " + game.toString();

		game.addGameAction(user, GameActionType.Invested, value, explanation);
		game = (CommonGoodsGame) this.save(game);

		// Notificamos al usuario
		webNotificationService.broadcast(user.getUsername(), explanation);

		// Notificamos al resto de jugadores
		String broadcastExplanation = "Someone has invested on " + game.toString() + "!";
		for (Player p : game.getJoinedPlayers()) {
			if (!p.getUser().equals(user)) {
				webNotificationService.broadcast(p.getUser().getUsername(), broadcastExplanation);
			}
		}

		if (game.hasEverybodyRespondedInvitations() && game.hasEverybodyInvested()) {

			if (game.getParamResolveAutomatically()) {
				resolveGame(game);

			} else {

				// Notificamos al creator
				String text = "There are no pending invitations to manage and all the joined users have invested in the game. Thus, the game could be prepared to be resolved. ";
				webNotificationService.broadcast(game.getOwner().getUsername(), text);
				mailNotificationService.sendMailGameEverybodyInvested(game);

			}

		}

		return game;

	}

	@Override
	protected void markRemovedPlayers(Game theGame) {

		CommonGoodsGame game = (CommonGoodsGame) theGame;

		for (Player player : game.getInvitedOrJoinedPlayers()) {

			if (!game.getPlayerInvestment(player.getUser()).isPresent()) {
				player.setStatus(PlayerStatus.Removed);

				String explanation = "You have been removed from " + game.toString()
						+ " because no investment was done before resolving the game";

				game.addGameAction(player.getUser(), GameActionType.Removed, null, explanation);
				webNotificationService.broadcast(player.getUser().getUsername(), explanation);
			}
		}

	}

	@Override
	public void computeBenefits(Game theGame) {

		CommonGoodsGame game = (CommonGoodsGame) theGame;

		for (Player player : game.getJoinedPlayers()) {

			// Sólo computamos aquellos que hayan invertido
			game.getPlayerInvestment(player.getUser()).ifPresent(personalInvestment -> {

				Double totalInvestments = game.checkTotalInvestments();

				Double performance = game.getParamInitialAllowance() - personalInvestment
						+ game.getParamWeight() * totalInvestments / game.countInvestments();

				String explanation = "You have gained " + String.format("%.2f", performance) + "p on "
						+ game.toString();

				game.addGameAction(player.getUser(), GameActionType.Gained, String.format("%.2f", performance),
						explanation);

			});

		}

		// RENDIMIENTO = DOTACIÓN_INICIAL - APORTACIÓN + FACTOR* SUMATORIO(APORTACIONES)

	}

	@Override
	public String checkConditionsToResolveGame(Game game) {

		CommonGoodsGame myGame = (CommonGoodsGame) game;
		
		
		if (myGame.countInvestments() == 0) {
			return "It is not possible to resolve the game because there are no investments.";
		} else if (myGame.countInvestments() == 1) {
			return "It is not possible to resolve the game because there is only one investment.";
		} else {
			return "";

		}

	}
	
	
	

}
