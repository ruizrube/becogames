package es.uca.becogames.data.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.becogames.business.entities.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	List<Organization> findByName(String name);

	
	
}

