package com.mtg.mtgDraft.service;

import org.springframework.stereotype.Service;

import com.mtg.mtgDraft.model.Draft;
import com.mtg.mtgDraft.model.Pack;
import com.mtg.mtgDraft.repository.CardRepository;
import com.mtg.mtgDraft.repository.PackRepository;

import java.util.Random;

@Service
public class CardService {

	CardRepository cardRepository;
	PackRepository packRepository;

	public CardService(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	public Pack fillPack(String set, Draft draft) {
		Pack pack = new Pack();
		Random random = new Random();

		pack.add(cardRepository.findRandomCardsBySet(set, 10, "common"));
		System.out.println(cardRepository.findRandomCardsBySet(set, 3, "uncommon"));
		pack.add(cardRepository.findRandomCardsBySet(set, 3, "uncommon"));
		if (random.nextInt(8) == 0) {
			pack.add(cardRepository.findRandomCardsBySet(set, 1, "mythic"));
		} else {
			pack.add(cardRepository.findRandomCardsBySet(set, 1, "rare"));
		}

		pack.setDraft(draft);
		packRepository.save(pack);

		return pack;
	}

}
