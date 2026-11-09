package com.mtg.mtgDraft.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mtg.mtgDraft.dto.PlayerDTO;
import com.mtg.mtgDraft.model.Card;
import com.mtg.mtgDraft.service.PlayerService;

@RestController
public class PlayerController {

	PlayerService playerService;

	public PlayerController(PlayerService playerService) {
		this.playerService = playerService;
	}

	@GetMapping("/player/{id}")
	public PlayerDTO getPlayer(@PathVariable String id) {
		return playerService.getPlayer(id);
	}

	@GetMapping("/pickedcards/{player_id}")
	public List<Card> getPickedCards(@PathVariable String player_id) {
		return playerService.getCards(player_id);
	}

}
