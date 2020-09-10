package es.uca.becogames.data.jparepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.becogames.business.entities.PasswordResetToken;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	PasswordResetToken findByToken(String token);



}

