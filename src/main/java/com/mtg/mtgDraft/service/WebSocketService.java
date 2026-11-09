package com.mtg.mtgDraft.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private final SimpMessagingTemplate messagingTemplate;

	public WebSocketService(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void notifyPlayer(String playerId) {
		messagingTemplate.convertAndSend("/topic/player/" + playerId, "PACK_UPDATED");
	}

	public void notifyDraftFinished(String playerId) {
		messagingTemplate.convertAndSend("/topic/player/" + playerId, "DRAFT_FINISHED");
	}

	public void notifyDraftUpdated(String draftId) {
		messagingTemplate.convertAndSend("/topic/draft/" + draftId, "DRAFT_UPDATED");
	}

	public void notifyDraftsListUpdated() {
		messagingTemplate.convertAndSend("/topic/drafts", "DRAFTS_UPDATED");
	}
}