package es.uca.becogames.data.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.becogames.business.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public List<User> findByEnabled(boolean enabled);
	
	public User findByUsername(String username);
	
	public List<User> findByFirstNameContainsOrLastNameContainsOrUsernameContainsAllIgnoreCase(String name, String firstName, String lastName);

	public List<User> findByMailAndEnabledTrue(String mail);

	
	
}

