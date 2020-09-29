package es.uca.becogames.business.services;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rusticisoftware.tincan.Activity;
import com.rusticisoftware.tincan.ActivityDefinition;
import com.rusticisoftware.tincan.Agent;
import com.rusticisoftware.tincan.RemoteLRS;
import com.rusticisoftware.tincan.Result;
import com.rusticisoftware.tincan.Score;
import com.rusticisoftware.tincan.Statement;
import com.rusticisoftware.tincan.TCAPIVersion;
import com.rusticisoftware.tincan.Verb;
import com.rusticisoftware.tincan.lrsresponses.StatementsResultLRSResponse;

import es.uca.becogames.business.entities.CommonGoodsGame;
import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.GameActionType;
import es.uca.becogames.business.entities.Player;

@Component
public class XAPIService {

	@Value("${MAIL_MAIN}")
	public String mainMailAccount;

	@Value("${LRS_ENDPOINT}")
	public String lrsEndpoint;

	@Value("${LRS_AUTH}")
	public String lrsAuth;

	public boolean sendStatements(Game game) {

		List<Statement> statements = new ArrayList<Statement>();
		RemoteLRS lrs = new RemoteLRS();

		try {

			for (Player player : game.getJoinedPlayers()) {
				statements.addAll(createStatementsOfUser(game, player));
			}

			lrs.setEndpoint(lrsEndpoint);
			lrs.setAuth(lrsAuth);
			lrs.setVersion(TCAPIVersion.V100);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}

		StatementsResultLRSResponse lrsRes = lrs.saveStatements(statements);
		if (lrsRes.getSuccess()) {
			System.out.println("xAPI statement sent");
			return true;
		} else {
			System.out.println("Error sending xAPI statement: " + lrsRes.getErrMsg());
			// failure, error information is available in lrsRes.getErrMsg()
			return false;
		}

	}

	public List<Statement> createStatementsOfUser(Game game, Player player) throws URISyntaxException {

		List<Statement> result = new ArrayList<Statement>();

		// SUBJECT
		Agent agent = new Agent();
		agent.setMbox("mailto:" + player.getUser().getMail());
		agent.setName(player.getUser().getUsername());

		// OBJECT
		ActivityDefinition actDef = new ActivityDefinition();
		actDef.setType("https://w3id.org/xapi/seriousgames/activity-types/serious-game");
		Activity activity = new Activity();
		activity.setId("http://purl.org/becogames/TheCommonGoods/" + game.getId());
		activity.setDefinition(actDef);

		Statement st;
		Verb verb;

		// FIRST STATEMENT
		verb = new Verb("http://adlnet.gov/expapi/verbs/initialized");
		st = new Statement();
		st.setActor(agent);
		st.setVerb(verb);
		st.setObject(activity);
		result.add(st);

		// SECOND STATEMENT
		verb = new Verb("http://adlnet.gov/expapi/verbs/completed");
		st = new Statement();
		st.setActor(agent);
		st.setVerb(verb);
		st.setObject(activity);

		Result dataResult = new Result();
		CommonGoodsGame runningGame = (CommonGoodsGame) game;
		Score score = new Score();
		score.setMax(runningGame.checkMaximumObtainable());
		score.setMin(runningGame.checkMinimumObtainable());
		score.setRaw(runningGame.checkBenefit(player.getUser()));
		dataResult.setScore(score);
		dataResult.setResponse(String.valueOf(runningGame.checkInvestment(player.getUser())));

		st.setResult(dataResult);
		result.add(st);

		return result;

	}

}
