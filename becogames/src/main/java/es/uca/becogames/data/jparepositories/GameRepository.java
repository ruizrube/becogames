package es.uca.becogames.data.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.User;


public interface GameRepository extends JpaRepository<Game, Long> {

	List<Game> findByPlayers_User(User user);

	List<Game> findByOwner(User user);

}
