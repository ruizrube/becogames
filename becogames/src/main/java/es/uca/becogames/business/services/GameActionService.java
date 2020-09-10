package es.uca.becogames.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uca.becogames.business.entities.GameAction;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.data.jparepositories.GameActionRepository;

@Service
public class GameActionService {

	@Autowired
	private GameActionRepository repo;

	
	public List<GameAction> findByUser(User user){
		return repo.findByUserOrderByDateDesc(user);
	}
	
}
