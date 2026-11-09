package com.mtg.mtgDraft.dto;

import java.util.List;
import com.mtg.mtgDraft.model.Card;
import com.mtg.mtgDraft.model.Player;

public class PlayerDTO {

	String id;
	String name;
	List<Card> pickedCards;
	boolean ready;
	int draftOrder;

	public PlayerDTO(Player player) {
		id = player.getId();
		name = player.getName();
		pickedCards = player.getPickedCards();
		ready = player.isReady();
		draftOrder = player.getDraftOrder();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name; 
	}

	public List<Card> getPickedCards() {
		return pickedCards;
	}

	public boolean isReady() {
		return ready;
	}

	public int getDraftOrder() {
		return draftOrder;
	}

}
