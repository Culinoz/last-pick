package com.mtg.mtgDraft.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtg.mtgDraft.dto.DraftDTO;
import com.mtg.mtgDraft.dto.PlayerDTO;
import com.mtg.mtgDraft.model.Pack;
import com.mtg.mtgDraft.service.DraftService;

@CrossOrigin(origins = "*")
@RequestMapping("/draft")
@RestController
public class DraftController {

	private final DraftService draftService;

	public DraftController(DraftService draftService) {
		this.draftService = draftService;
	}

	@PostMapping("/{set}")
	public DraftDTO createDraft(@PathVariable String set) {
		DraftDTO draftDTO = new DraftDTO(draftService.createDraft(set));
		draftDTO.setSetName(set);
		return draftDTO;
	}

	@GetMapping("")
	public List<DraftDTO> getAllDraft() {
		return draftService.getAllDraft();
	}

	@GetMapping("/{id}")
	public DraftDTO getDraft(@PathVariable String id) {
		return new DraftDTO(draftService.getDraft(id));
	}

	@GetMapping("/sets")
	public Object[] getSets() {
		return draftService.getSets();
	}

	@PostMapping("/{id}/join/{name}")
	public PlayerDTO joinDraft(@PathVariable String id, @PathVariable String name) {
		return new PlayerDTO(draftService.joinDraft(id, name));
	}

	@GetMapping("/{playerid}/pack")
	public Pack getPack(@PathVariable String playerid) {
		return draftService.getPack(playerid);
	}

	@PostMapping("/{draftId}/start")
	public void start(@PathVariable String draftId) {
		draftService.start(draftId);
	}

	@PostMapping("/{packId}/{cardId}")
	public PlayerDTO pickCard(@PathVariable String packId, @PathVariable String cardId) {
		return new PlayerDTO(draftService.pickCard(packId, cardId));
	}

}