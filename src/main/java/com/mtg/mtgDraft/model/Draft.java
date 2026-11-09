package com.mtg.mtgDraft.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Draft {

	@Id
	String id;

	@OneToMany(mappedBy = "draft")
	List<Player> players = new ArrayList<Player>();

	@OneToMany(mappedBy = "draft")
	private List<Pack> packs;
	private String set;

	private String setName;
	private int index;
	private int round;

	public Draft(String id) {
		this.id = id;
	}

	public Draft() {
	}

	public void add(Player p) {
		players.add(p);
		p.setDraft(this);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public String getId() {
		return id;
	}

	public List<Pack> getPacks() {
		return packs;
	}

	public void setPacks(List<Pack> packs) {
		this.packs = packs;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
}
