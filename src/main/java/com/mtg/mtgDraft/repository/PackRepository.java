package com.mtg.mtgDraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mtg.mtgDraft.model.Pack;

public interface PackRepository extends JpaRepository<Pack, String> {

	@Query(value = """
			SELECT p.*
			FROM pack p
			LEFT JOIN pack_cards pc ON p.id = pc.pack_id
			WHERE p.player_id = :playerid
			GROUP BY p.id
			ORDER BY COUNT(pc.cards_id) DESC
			LIMIT 1
			""", nativeQuery = true)
	Pack findPack(String playerid);

}
