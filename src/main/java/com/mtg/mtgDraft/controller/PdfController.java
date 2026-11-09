package com.mtg.mtgDraft.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtg.mtgDraft.model.Card;
import com.mtg.mtgDraft.model.Player;
import com.mtg.mtgDraft.repository.CardRepository;
import com.mtg.mtgDraft.repository.PlayerRepository;
import com.mtg.mtgDraft.service.PdfService;

@CrossOrigin(origins = "*")
@RequestMapping("/draft")
@RestController
public class PdfController {

    private final PdfService pdfService;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;

    public PdfController(PdfService pdfService, PlayerRepository playerRepository, CardRepository cardRepository) {
        this.pdfService = pdfService;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
    }

    @GetMapping("/player/{playerId}/pdf")
    public ResponseEntity<byte[]> getPlayerPdf(@PathVariable String playerId) throws IOException {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found: " + playerId));
        byte[] pdfBytes = pdfService.generateCardsPdf(player.getPickedCards());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"draft-cards-" + player.getName() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generateSelectedCardsPdf(@RequestBody List<String> cardIds) throws IOException {
        java.util.Map<String, Card> cardMap = cardRepository.findAllById(cardIds).stream()
                .collect(java.util.stream.Collectors.toMap(Card::getId, c -> c, (a, b) -> a));

        List<Card> cards = cardIds.stream()
                .map(cardMap::get)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        byte[] pdfBytes = pdfService.generateCardsPdf(cards);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"selected-cards.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}