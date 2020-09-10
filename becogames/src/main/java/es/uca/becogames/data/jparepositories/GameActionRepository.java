package es.uca.becogames.data.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.becogames.business.entities.GameAction;
import es.uca.becogames.business.entities.User;


public interface GameActionRepository extends JpaRepository<GameAction, Long> {
	
	
	
	List<GameAction> findByUserOrderByDateDesc(User user);

}
