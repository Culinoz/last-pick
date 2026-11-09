package com.mtg.mtgDraft.service;

import com.mtg.mtgDraft.repository.CardRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mtg.mtgDraft.dto.ScryfallBulkData;
import com.mtg.mtgDraft.dto.ScryfallBulkResponse;
import com.mtg.mtgDraft.model.Card;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

@Service
public class ScryfallService {

	private final CardRepository cardRepository;
	private final RestClient restClient;

	public ScryfallService(CardRepository cardRepository) {
		this.restClient = RestClient.create();
		this.cardRepository = cardRepository;
	}

	public InputStream downloadBulkData(String url) {
		return restClient.get().uri(url).header("User-Agent", "mtgDraft/1.0").retrieve().body(InputStream.class);
	}

	public void importCards() throws IOException {
		Path file = Path.of("default-cards.jsonl.gz");
		ObjectMapper mapper = new ObjectMapper();
		try (GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(file));
				BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream))) {
			String line;
			long start = System.nanoTime(); // tempo iniziale

			while ((line = reader.readLine()) != null) {
				JsonNode json = mapper.readTree(line);
				Card card = mapper.treeToValue(json, Card.class);
				String imageUrl = json.path("image_uris").path("normal").asString();
				card.setImageUri(imageUrl);
				String setName = json.path("set_name").asString();
				card.setSetName(setName);
				cardRepository.save(card);
			}
			long end = System.nanoTime(); // tempo finale
			long duration = end - start; // durata in nanosecondi
			System.out.println("Tempo di esecuzione: " + duration + " ns " + "≈ " + (duration / 1_000_000.0) + " ms"
					+ " ≈ " + (duration / 1_000_000_000.0) + " s");

		}

		Files.delete(file);

		System.out.println("Import completato. File eliminato.");
	}

	public void downloadFile(String url) throws IOException {
		Path file = Path.of("default-cards.jsonl.gz");
		restClient.get().uri(url).header("User-Agent", "mtgDraft/1.0").exchange((_, response) -> {
			try (InputStream inputStream = response.getBody()) {
				Files.copy(inputStream, file);
			}
			return null;
		});
		System.out.println("File scaricato: " + file.toAbsolutePath());
	}

	public String getBulkData() {
		ScryfallBulkResponse response = restClient.get().uri("https://api.scryfall.com/bulk-data")
				.header("User-Agent", "mtgDraft/1.0").header("Accept", "application/json").retrieve()
				.body(ScryfallBulkResponse.class);

		for (ScryfallBulkData s : response.getData()) {
			if (s.getType().equals("default_cards"))
				return s.getjsonl_download_uri();
		}

		return "Non trovato nulla";
	}

}
