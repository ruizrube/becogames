package es.uca.becogames.business.services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uca.becogames.business.entities.Organization;
import es.uca.becogames.data.jparepositories.OrganizationRepository;


@Service
public class OrganizationService {

	private OrganizationRepository repo;

	@Autowired
	public OrganizationService(OrganizationRepository repo) {
		this.repo=repo;
		
	}
	
	public Organization save(Organization entry) {
		return repo.save(entry);
	}

	public Optional<Organization> findById(Long id) {
		return repo.findById(id);
	}

	public List<Organization> findAll() {
		return repo.findAll();
	}

	
	public void delete(Organization value) {
		repo.delete(value);
	}

	public long count() {
		return repo.count();
	}

	public List<Organization> findByName(String name) {
		if(name.equals("")) {
			return repo.findAll();
		} else {
			return repo.findByName(name);
		}		
	}

	public void deleteAll() {
		this.repo.deleteAllInBatch();
		
	}

}
