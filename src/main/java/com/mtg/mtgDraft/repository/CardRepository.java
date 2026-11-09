package com.mtg.mtgDraft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mtg.mtgDraft.model.Card;

public interface CardRepository extends JpaRepository<Card, String> {

	@Query(value = """
			SELECT *
			FROM card
			WHERE set = :set and rarity = :rarity and type_line NOT LIKE '%Basic Land%'
			ORDER BY RANDOM()
			LIMIT :number
			""", nativeQuery = true)
	List<Card> findRandomCardsBySet(String set, int number, String rarity);

	@Query(value = """
			SELECT set,max(set_name)
			FROM card
			GROUP BY set
			""", nativeQuery = true)
	Object[] getAllSets();
	
	@Query(value = """
			SELECT max(set_name)
			FROM card
			WHERE set = :set
			""", nativeQuery = true)
	String getSetName(String set);

}