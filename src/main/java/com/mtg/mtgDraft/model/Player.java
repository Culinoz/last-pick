package com.mtg.mtgDraft.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Player {

	@Id
	String id;

	String name;

	@ManyToMany
	private List<Card> pickedCards = new ArrayList<>();

	@JsonIgnore
	@ManyToOne
	Draft draft;
	int draftOrder;
	boolean ready;

	public Player(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Player() {
	}

	@Override
	public String toString() {
		return "Player [name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public Draft getDraft() {
		return draft;
	}

	public void setDraft(Draft draft) {
		this.draft = draft;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void pickCard(Card card) {
		pickedCards.add(card);
	}

	public List<Card> getPickedCards() {
		return pickedCards;
	}

	public void setPickedCards(List<Card> pickedCards) {
		this.pickedCards = pickedCards;
	}

	public int getDraftOrder() {
		return draftOrder;
	}

	public void setDraftOrder(int draftOrder) {
		this.draftOrder = draftOrder;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

}
