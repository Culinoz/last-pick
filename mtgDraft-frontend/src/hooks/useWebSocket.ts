import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { getWsUrl } from "../utils/apiConfig";

interface UseWebSocketOptions {
    playerId?: string;
    draftId?: string;
    onPackUpdated?: (playerId: string) => void;
    onDraftFinished?: () => void;
    onDraftUpdated?: (draftId: string) => void;
    onDraftsListUpdated?: () => void;
}

export function useWebSocket({
    playerId,
    draftId,
    onPackUpdated,
    onDraftFinished,
    onDraftUpdated,
    onDraftsListUpdated,
}: UseWebSocketOptions) {
    const onPackUpdatedRef = useRef(onPackUpdated);
    const onDraftFinishedRef = useRef(onDraftFinished);
    const onDraftUpdatedRef = useRef(onDraftUpdated);
    const onDraftsListUpdatedRef = useRef(onDraftsListUpdated);

    useEffect(() => {
        onPackUpdatedRef.current = onPackUpdated;
    }, [onPackUpdated]);

    useEffect(() => {
        onDraftFinishedRef.current = onDraftFinished;
    }, [onDraftFinished]);

    useEffect(() => {
        onDraftUpdatedRef.current = onDraftUpdated;
    }, [onDraftUpdated]);

    useEffect(() => {
        onDraftsListUpdatedRef.current = onDraftsListUpdated;
    }, [onDraftsListUpdated]);

    useEffect(() => {
        const client = new Client({
            brokerURL: getWsUrl(),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("WebSocket connected");

                if (playerId) {
                    client.subscribe(`/topic/player/${playerId}`, async (message) => {
                        if (message.body === "PACK_UPDATED" && onPackUpdatedRef.current) {
                            onPackUpdatedRef.current(playerId);
                        }
                        if (message.body === "DRAFT_FINISHED" && onDraftFinishedRef.current) {
                            onDraftFinishedRef.current();
                        }
                    });
                }

                if (draftId) {
                    client.subscribe(`/topic/draft/${draftId}`, async () => {
                        if (onDraftUpdatedRef.current) {
                            onDraftUpdatedRef.current(draftId);
                        }
                    });
                }

                client.subscribe(`/topic/drafts`, async () => {
                    if (onDraftsListUpdatedRef.current) {
                        onDraftsListUpdatedRef.current();
                    }
                });
            },
            onStompError: (frame) => {
                console.error("STOMP error:", frame);
            },
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [playerId, draftId]);
}
