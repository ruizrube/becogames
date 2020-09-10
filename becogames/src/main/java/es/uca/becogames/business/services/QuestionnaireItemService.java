package es.uca.becogames.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.uca.becogames.business.entities.QuestionnaireItem;
import es.uca.becogames.data.jparepositories.QuestionnaireItemRepository;

@Service
public class QuestionnaireItemService {

	@Autowired
	private QuestionnaireItemRepository repo;

	
	private QuestionnaireItemService() {
	}

	/**
	 * @return all available QuestionnaireItem objects.
	 */
	public List<QuestionnaireItem> findAll() {
		Sort sort=Sort.by(Sort.Direction.ASC, "code").and(Sort.by(Sort.Direction.ASC, "id"));

		
		return repo.findAll(sort);

		//Sort sort = new Sort(Sort.Direction.ASC, "code").and(new Sort(Sort.Direction.ASC, "id"));
		//return repo.findAll(sort);
	}

	/**
	 * Finds all Customer's that match given filter.
	 *
	 * @param stringFilter filter that returned objects should match or null/empty
	 *                     string if all objects should be returned.
	 * @return list a Customer objects
	 */
	public List<QuestionnaireItem> findAll(String stringFilter) {
		
		if(stringFilter.equals("")) {
			return repo.findAll();
		
		} else {
			return repo.findByText(stringFilter);			
		}
	}

	/**
	 * @return the amount of all QuestionnaireItem in the system
	 */
	public long count() {
		return repo.count();
	}

	/**
	 * Deletes a QuestionnaireItem from a system
	 *
	 * @param value the QuestionnaireItem to be deleted
	 */
	public void delete(QuestionnaireItem value) {
		repo.delete(value);
	}

	public void deleteAll() {
		repo.deleteAll();
	}

	
	/**
	 * Persists or updates customer in the system. Also assigns an identifier for
	 * new Customer instances.
	 *
	 * @param entry
	 */
	public QuestionnaireItem save(QuestionnaireItem entry) {
		return repo.save(entry);
	}

}
