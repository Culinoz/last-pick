package com.mtg.mtgDraft.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mtg.mtgDraft.dto.PlayerDTO;
import com.mtg.mtgDraft.model.Card;
import com.mtg.mtgDraft.repository.PlayerRepository;

@Service
public class PlayerService {

	PlayerRepository playerRepository;

	public PlayerService(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	public PlayerDTO getPlayer(String id) {
		return new PlayerDTO(playerRepository.findById(id).orElse(null));
	}

	public List<Card> getCards(String player_id) {
		return playerRepository.getPickedCards(player_id);
	}

}
