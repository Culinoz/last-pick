package com.mtg.mtgDraft.dto;

import java.util.ArrayList;
import java.util.List;

import com.mtg.mtgDraft.model.Draft;
import com.mtg.mtgDraft.model.Player;

public class DraftDTO {

	String id;
	List<PlayerDTO> players = new ArrayList<PlayerDTO>();
	int index;
	String setName;
	int round;

	public DraftDTO(Draft draft) {
		this.index = draft.getIndex();
		this.id = draft.getId();
		this.round = draft.getRound();
		for (Player p : draft.getPlayers()) {
			players.add(new PlayerDTO(p));
		}
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getId() {
		return id;
	}

	public List<PlayerDTO> getPlayers() {
		return players;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String getSetName() {
		return setName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPlayers(List<PlayerDTO> players) {
		this.players = players;
	}
	

}
