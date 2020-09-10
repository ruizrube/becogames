/**
 * 
 */
package es.uca.becogames.business.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uca.becogames.business.entities.PasswordResetToken;
import es.uca.becogames.business.entities.User;
import es.uca.becogames.data.jparepositories.PasswordTokenRepository;
import es.uca.becogames.data.jparepositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private UserRepository repo;
	private PasswordEncoder passwordEncoder;
	private MailNotificationService notificationService;
	private PasswordTokenRepository passwordTokenRepository;

	@Autowired
	public UserService(UserRepository repo, PasswordEncoder passwordEncoder,
			MailNotificationService notificationService, PasswordTokenRepository passwordTokenRepository) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
		this.notificationService = notificationService;
		this.passwordTokenRepository = passwordTokenRepository;

	}

	public void delete(User user) {

		repo.delete(user);
	}

	public void deleteAll() {
		this.repo.deleteAllInBatch();

	}

	
	public User update(User user) {
		User usr = repo.save(user);
		return usr;
	}

	@Transactional
	public User create(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User usr = repo.save(user);
		String token = this.createPasswordResetTokenForUser(usr);
		notificationService.sendMailConfirmUser(user, token);
		return usr;
	}

	
	public User activate(Long id) {
		User usr = this.findOne(id);
		usr.setEnabled(true);
		this.update(usr);
		notificationService.sendMailCreateUser(usr);
		return usr;
	}

	public User createAndActivate(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(true);
		User usr = repo.save(user);
		return usr;
	}

	public void deactivate(User user) {
		user.setEnabled(false);
		this.update(user);
		notificationService.sendMailDeactivateUser(user);

	}


	public boolean recoverPassword(String mail) {
		List<User> users = this.findByMail(mail);

		if (users.size() > 0) {
			for (User usr : users) {
				String token = this.createPasswordResetTokenForUser(usr);
				notificationService.sendMailRecoverPassword(usr, token);
			}
			return true;

		} else {
			return false;

		}

	}

	public boolean updatePassword(Long userId, String password) {

		User user = this.findOne(userId);

		if (user != null) {
			user.setPassword(passwordEncoder.encode(password));
			user = repo.save(user);
			return true;
		} else {
			return false;
		}

	}

	public String createPasswordResetTokenForUser(User user) {
		String token = UUID.randomUUID().toString();

		PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordTokenRepository.save(myToken);
		return token;
	}

	public boolean checkValidToken(Long userId, String token) {

		PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
		if (passToken == null) {
			System.out.println("!!Error al validar passToken: "+passToken);
			return false;
		}

		if (!passToken.getUser().getId().equals(userId)) {
			System.out.println("!!Error al validar usuario " + userId + ". El usuario asociado al token es: " + passToken.getUser().getId());

			return false;
		}

		return true;

	}

	public boolean checkValidTokenNonExpired(Long userId, String token) {
		PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
		if (passToken == null) {
			System.out.println("!!Error al validar passToken: "+passToken);
			return false;
		}

		if (!passToken.getUser().getId().equals(userId)) {
			System.out.println("!!Error al validar usuario " + userId + ". El usuario asociado al token es: " + passToken.getUser().getId());
			return false;
		}

		if (passToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			System.out.println("!!Error en fecha de expiracion de token: " + passToken.getExpiryDate());
			return false;
		}

		return true;
	}

	public User loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = repo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return user;
	}


	public User findOne(Long arg0) {
		return repo.findById(arg0).get();

	}

	public User findByUsername(String username) {
		return repo.findByUsername(username);
	}

	

	public List<User> findByMail(String mail) {
		return repo.findByMailAndEnabledTrue(mail);
	}

	
	public List<User> findAll() {
		return repo.findByEnabled(true);
	}

	
	public List<User> findByAnyNameIncludingDisabled(String name) {
		if (name.equals("")) {
			return repo.findAll();
		} else {
			return repo.findByFirstNameContainsOrLastNameContainsOrUsernameContainsAllIgnoreCase(name, name, name);
		}
	}


}
