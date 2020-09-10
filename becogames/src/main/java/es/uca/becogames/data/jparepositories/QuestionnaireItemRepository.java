package es.uca.becogames.data.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.becogames.business.entities.QuestionnaireItem;

public interface QuestionnaireItemRepository extends JpaRepository<QuestionnaireItem, Long> {

	
	List<QuestionnaireItem> findByText(String stringFilter);


}

