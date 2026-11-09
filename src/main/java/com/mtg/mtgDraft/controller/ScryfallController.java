package com.mtg.mtgDraft.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtg.mtgDraft.service.ScryfallService;

@RequestMapping("/scryfall")
@RestController
public class ScryfallController {

	private final ScryfallService scryfallService;

	public ScryfallController(ScryfallService scryfallService) {
		this.scryfallService = scryfallService;
	}

	@GetMapping("/bulk")
	public String getBulkData() {
		return scryfallService.getBulkData();
	}

	@GetMapping("/updateDBCard")
	public String updateDBCard() {

		String url = scryfallService.getBulkData();

		try {
			scryfallService.downloadFile(url);
			scryfallService.importCards();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@GetMapping("/download")
	public InputStream downloadBulkData(@RequestParam String url) {
		return scryfallService.downloadBulkData(url);
	}

}