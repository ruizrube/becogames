package es.uca.becogames.business.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import es.uca.becogames.business.dto.CommonGoodsGameResult;
import es.uca.becogames.business.dto.GameResult;

@Entity
@DiscriminatorValue("CommonGoods")
public class CommonGoodsGame extends Game {

	public static final String PARAM_INITIAL_ALLOWANCE = "Initial investment value";
	public static final String PARAM_WEIGHT = "Weight";
	public static final String PARAM_RESOLVE_AUTOMATICALLY = "Resolve automatically";
	
	@Override
	public String toString() {
		return "The Game of the Common Goods";
	}

	
	public Double getParamInitialAllowance(){
		return this.getParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE);
	}
	
	public Double getParamWeight(){
		return this.getParameter(CommonGoodsGame.PARAM_WEIGHT);
	}

	public boolean getParamResolveAutomatically(){
		return this.getParameter(CommonGoodsGame.PARAM_RESOLVE_AUTOMATICALLY)> 0.9;
	}
	
	
	public Optional<Double> getPlayerInvestment(User user) {

		Optional<Double> result = Optional.empty();

		for (GameAction action : this.getActions()) {
			if (action.getUser().equals(user) && action.getType().equals(GameActionType.Invested)) {
				result = Optional.of(Double.valueOf(action.getData()));
			}
		}
		return result;
	}

	
	
	
	public Double checkTotalInvestments() {
		return this.getActions().stream().filter(a -> a.getType().equals(GameActionType.Invested))
				.mapToDouble(a -> Double.valueOf(a.getData())).sum();
	}

	public OptionalDouble checkAverageInvestments() {
		return this.getActions().stream().filter(a -> a.getType().equals(GameActionType.Invested))
				.mapToDouble(a -> Double.valueOf(a.getData())).average();
	}
	
	
	
	public OptionalDouble checkAverageInvestmentOfTheRemainingUsers(User user) {

		return this.getActions().stream().filter(a -> !a.getUser().equals(user) && a.getType().equals(GameActionType.Invested))
		.mapToDouble(a -> Double.valueOf(a.getData())).average();
					
	}
	
	
	public OptionalDouble checkAverageBenefitOfTheRemainingUsers(User user) {
		return this.getJoinedPlayers().stream().filter(a -> !a.getUser().equals(user))
		.mapToDouble(player -> Double.valueOf(this.checkBenefit(player.getUser()))).average();
					
	}


	
	public Double checkHypotheticalAverageBenefitOfTheRemainingUsersInASoleScenario(User user) {
		
		Optional<Double> playerInvestment=getPlayerInvestment(user); 
		return this.getParamInitialAllowance() + this.getParamWeight() * playerInvestment.get()/this.countInvestments();
					
	}
	
	public Double checkHypotheticalAverageBenefitOfTheRemainingUsersInAFreeRiderScenario(User user) {
		
		return this.getParamWeight() * this.getParamInitialAllowance()*(this.countInvestments()-1)/this.countInvestments();
					
	}
	
	
	public long countInvestments() {

		return this.getActions().stream().filter(a -> a.getType().equals(GameActionType.Invested)).count();
	}

	public boolean hasEverybodyInvested() {

		return this.countInvestments() == this.countJoinedPlayers();

	}

	public Double checkMinimumObtainable() {

		Double initialAllowance = this.getParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE);

		return (double) this.getParamWeight() * initialAllowance / this.countInvestments();
	}

	public Double checkSocialOptimal() {

		Double initialAllowance = this.getParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE);

		return (double) this.getParamWeight() * initialAllowance;
	}

	public Double checkMaximumObtainable() {

		Double initialAllowance = this.getParameter(CommonGoodsGame.PARAM_INITIAL_ALLOWANCE);

		Double sumatorio = initialAllowance * (this.countInvestments() - 1);
				
		return initialAllowance + this.getParamWeight() * sumatorio / this.countInvestments();
	}

	public Double checkInvestment(User user) {

		Double result = Double.valueOf(this.getPlayerActions(user).stream()
				.filter(a -> a.getType().equals(GameActionType.Invested)).findFirst().get().getData());

		return result;
	}

	
	public Double checkBenefit(User user) {

		Double result = Double.valueOf(this.getPlayerActions(user).stream()
				.filter(a -> a.getType().equals(GameActionType.Gained)).findFirst().get().getData());

		return result;
	}

	public int checkUserRanking(User user) {

		int ranking = 99999;
		List<Player> players = this.getJoinedPlayers().stream()
				.sorted((o1, o2) -> checkBenefit(o1.getUser()).compareTo(checkBenefit(o2.getUser())))
				.collect(Collectors.toList());

		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getUser().equals(user)) {
				ranking = players.size()-i;
				i=players.size();
			}
		}

		return ranking;
	}

	public List<CommonGoodsGameResult> getResults() {

		Map<Long, CommonGoodsGameResult> data = new HashMap<Long, CommonGoodsGameResult>();

		CommonGoodsGameResult aux = null;

		for (GameAction action : this.getActions()) {

			if (data.containsKey(action.getUser().getId())) {
				aux = data.get(action.getUser().getId());
			} else {
				aux = new CommonGoodsGameResult();
				aux.setRanking(this.checkUserRanking(action.getUser()));
				aux.setUsername(action.getUser().getUsername());
				aux.setDni(action.getUser().getDni());
				aux.setLastName(action.getUser().getLastName());
				aux.setFirstName(action.getUser().getFirstName());
			}

			if (action.getType().equals(GameActionType.Invested)) {
				aux.setInvested(action.getData());
			}

			if (action.getType().equals(GameActionType.Gained)) {
				aux.setGained(action.getData());
			}

			data.put(action.getUser().getId(), aux);

			if (action.getType().equals(GameActionType.Rejected_Invitation)) {  // SI el usuario rechazo la partida, lo suprimimos de los resultados
				data.remove(action.getUser().getId());
			} 

			

		}
		
		
				
		List<CommonGoodsGameResult> result = new ArrayList<CommonGoodsGameResult>(data.values());

		result.sort(Comparator.comparingInt(CommonGoodsGameResult::getRanking));
		
		return result;
	}
}
