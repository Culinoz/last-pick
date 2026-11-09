package com.mtg.mtgDraft.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Pack {

	@Id
	String id;

	@ManyToMany
	List<Card> cards = new ArrayList<Card>();

	@JsonIgnore
	@ManyToOne
	private Draft draft;

	@ManyToOne
	private Player player;

	public Pack() {
		id = UUID.randomUUID().toString();
	}

	public void add(List<Card> cardList) {
		cards.addAll(cardList);
	}

	public void setDraft(Draft draft) {
		this.draft = draft;
	}

	@Override
	public String toString() {
		return "Pack [id=" + id + ", cards=" + cards + ", draft=" + draft + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public Draft getDraft() {
		return draft;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
