package com.mtg.mtgDraft.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.mtg.mtgDraft.dto.DraftDTO;
import com.mtg.mtgDraft.exception.DraftNotFoundException;
import com.mtg.mtgDraft.model.Card;
import com.mtg.mtgDraft.model.Draft;
import com.mtg.mtgDraft.model.Pack;
import com.mtg.mtgDraft.model.Player;
import com.mtg.mtgDraft.repository.CardRepository;
import com.mtg.mtgDraft.repository.DraftRepository;
import com.mtg.mtgDraft.repository.PackRepository;
import com.mtg.mtgDraft.repository.PlayerRepository;

@Service
public class DraftService {

	private final static int COMMON_CARDS = 1;
	private final static int UNCOMMON_CARDS = 3;
	private final static int RARE_CARDS = 1;

	private final DraftRepository draftRepository;
	private final PlayerRepository playerRepository;
	private final PackRepository packRepository;
	private final CardRepository cardRepository;
	private final WebSocketService webSocketService;

	public DraftService(DraftRepository draftRepository, PlayerRepository playerRepository,
			PackRepository packRepository, CardRepository cardRepository, WebSocketService webSocketService) {
		this.draftRepository = draftRepository;
		this.playerRepository = playerRepository;
		this.packRepository = packRepository;
		this.cardRepository = cardRepository;
		this.webSocketService = webSocketService;
	}

	public Draft createDraft(String set) {
		String id = UUID.randomUUID().toString();
		Draft draft = new Draft(id);
		draft.setSet(set);
		draft.setIndex(draftRepository.getNextIndex());
		Draft saved = draftRepository.save(draft);
		webSocketService.notifyDraftsListUpdated();
		return saved;
	}

	public List<DraftDTO> getAllDraft() {
		List<Draft> dl = draftRepository.findAll();
		List<DraftDTO> dlto = new ArrayList<DraftDTO>();
		for (Draft d : dl) {
			DraftDTO draftDTO = new DraftDTO(d);
			draftDTO.setSetName(cardRepository.getSetName(d.getSet()));
			dlto.add(draftDTO);
		}
		return dlto;

	}

	public Object[] getSets() {
		return cardRepository.getAllSets();
	}

	public Draft getDraft(String id) {
		return draftRepository.findById(id).orElseThrow(() -> new DraftNotFoundException(id));
	}

	public Player joinDraft(String draftId, String name) {
		Player player = new Player(UUID.randomUUID().toString(), name);
		Draft draft = draftRepository.findById(draftId).orElseThrow(() -> new DraftNotFoundException(draftId));

		draft.add(player);
		playerRepository.save(player);

		webSocketService.notifyDraftUpdated(draftId);
		webSocketService.notifyDraftsListUpdated();

		return player;

	}

	public void start(String draftId) {

		Draft draft = draftRepository.findById(draftId).orElseThrow(() -> new DraftNotFoundException(draftId));
		int ordinamento = 0;
		String set = draft.getSet();
		Collections.shuffle(draft.getPlayers());

		for (Player player : draft.getPlayers()) {
			this.fillPack(set, draft, player);
			player.setDraftOrder(ordinamento++);
			player.setReady(false);
			playerRepository.save(player);
		}
		draft.setSet(set);
		draftRepository.save(draft);

		webSocketService.notifyDraftUpdated(draftId);
		webSocketService.notifyDraftsListUpdated();

		// Notify all players that their pack is ready
		for (Player player : draft.getPlayers()) {
			System.out.println("henlo");
			webSocketService.notifyPlayer(player.getId());
		}
	}

	public Pack fillPack(String set, Draft draft, Player player) {
		Pack pack = new Pack();
		Random random = new Random();

		pack.add(cardRepository.findRandomCardsBySet(set, COMMON_CARDS, "common"));
		pack.add(cardRepository.findRandomCardsBySet(set, UNCOMMON_CARDS, "uncommon"));
		if (random.nextInt(8) == 0) {
			List<Card> cards = cardRepository.findRandomCardsBySet(set, RARE_CARDS, "mythic");
			if (cards.isEmpty()) {
				cards = cardRepository.findRandomCardsBySet(set, RARE_CARDS, "rare");
			}
			pack.add(cards);
		} else {
			pack.add(cardRepository.findRandomCardsBySet(set, RARE_CARDS, "rare"));
		}
		pack.setDraft(draft);
		pack.setPlayer(player);
		packRepository.save(pack);

		return pack;
	}

	public Pack getPack(String playerId) {
		return packRepository.findPack(playerId);
	}

	public Player pickCard(String packId, String cardId) {
		boolean allready = true;
		Pack pack = packRepository.findById(packId).orElse(null);
		if (pack == null)
			return null;

		Card card = cardRepository.findById(cardId).orElse(null);
		Player player = playerRepository.findById(pack.getPlayer().getId()).orElse(null);
		if (player == null || player.isReady()) {
			return player;
		}

		pack.getCards().remove(card);
		player.pickCard(card);
		player.setReady(true);

		packRepository.save(pack);
		playerRepository.save(player);

		webSocketService.notifyDraftUpdated(player.getDraft().getId());

		allready = this.isTurnFinished(player.getDraft());

		System.out.println("PLAYER: " + player.getName());
		System.out.println("READY: " + player.isReady());
		System.out.println("ALL READY: " + allready);

		if (allready) {

			Draft draft = player.getDraft();

			this.passPack(draft);

			// Notify everyone that the draft state (round, direction) has changed
			webSocketService.notifyDraftUpdated(draft.getId());

			for (Player p : draft.getPlayers()) {

				if (draft.getRound() == 3) {
					webSocketService.notifyDraftFinished(p.getId());
				} else {
					webSocketService.notifyPlayer(p.getId());
				}
			}
			player = playerRepository.findById(player.getId()).orElse(player);
		}
		return player;

	}

	public void passPack(Draft draft) {

		if (draft.getRound() >= 3) {
			return;
		}

		int numPlayers = draft.getPlayers().size();

		// sono a fine giro, i pacchetti sono vuoti, li devo rigenerare e ridistribuire
		if (draft.getRound() < 3 && draft.getPacks().get(0).getCards().isEmpty()) {
			draft.setRound(draft.getRound() + 1);
			draftRepository.save(draft);
			String set = draft.getSet();
			for (Pack pack : draft.getPacks()) {
				Random random = new Random();
				pack.add(cardRepository.findRandomCardsBySet(set, COMMON_CARDS, "common"));
				pack.add(cardRepository.findRandomCardsBySet(set, UNCOMMON_CARDS, "uncommon"));
				if (random.nextInt(8) == 0) {
					pack.add(cardRepository.findRandomCardsBySet(set, RARE_CARDS, "mythic"));
				} else {
					pack.add(cardRepository.findRandomCardsBySet(set, RARE_CARDS, "rare"));
				}
				packRepository.save(pack);
			}
		}

		List<Pack> packs = draft.getPacks();

		if (draft.getRound() % 2 == 0) {
			for (Pack p : packs) {
				int targetOrder = (p.getPlayer().getDraftOrder() + 1) % numPlayers;
				p.setPlayer(playerRepository.getPlayerByOrder(targetOrder, p.getDraft().getId()));
				packRepository.save(p);
			}
		} else {
			for (Pack p : packs) {
				int targetOrder = (p.getPlayer().getDraftOrder() - 1 + numPlayers) % numPlayers;
				p.setPlayer(playerRepository.getPlayerByOrder(targetOrder, p.getDraft().getId()));
				packRepository.save(p);
			}
		}

		for (Player p : draft.getPlayers()) {
			p.setReady(false);
			playerRepository.save(p);
		}

	}

	private boolean isTurnFinished(Draft draft) {
		for (Player player : draft.getPlayers())
			if (!player.isReady())
				return false;

		return true;
	}

	public WebSocketService getWebSocketService() {
		return webSocketService;
	}

}