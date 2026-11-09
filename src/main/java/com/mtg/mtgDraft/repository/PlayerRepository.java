package com.mtg.mtgDraft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mtg.mtgDraft.model.Card;
import com.mtg.mtgDraft.model.Player;

public interface PlayerRepository extends JpaRepository<Player, String> {

	@Query(value = """
			SELECT *
			FROM player
			WHERE draft_order = :draftOrder
			AND draft_id = :draftId
			""", nativeQuery = true)
	Player getPlayerByOrder(int draftOrder, String draftId);

	@Query(value = """
			SELECT c.*
			FROM card c
			JOIN player_picked_cards ppc
			    ON ppc.picked_cards_id = c.id
			WHERE ppc.player_id = :player_id
			""", nativeQuery = true)
	List<Card> getPickedCards(String player_id);

} 	