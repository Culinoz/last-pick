import { useCallback, useState } from "react";
import type { Draft, MtgSet, Pack, Player } from "../types";
import { getApiBaseUrl } from "../utils/apiConfig";

export function useDraft() {
    const [draft, setDraft] = useState<Draft | null>(null);
    const [drafts, setDrafts] = useState<Draft[]>([]);
    const [player, setPlayer] = useState<Player | null>(null);
    const [pack, setPack] = useState<Pack | null>(null);
    const [sets, setSets] = useState<MtgSet[]>([]);
    const [draftFinished, setDraftFinished] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [hasPickedCard, setHasPickedCard] = useState(false);

    const fetchDrafts = useCallback(async () => {
        try {
            const res = await fetch(`${getApiBaseUrl()}/draft`);
            if (!res.ok) throw new Error("Error loading drafts");
            const text = await res.text();
            if (!text || !text.trim()) return;
            const data: Draft[] = JSON.parse(text);
            setDrafts(data);
            // Also keep the currently selected draft in sync
            setDraft((prev) => {
                if (!prev) return prev;
                const updated = data.find((d) => d.id === prev.id);
                return updated ?? prev;
            });
        } catch (e) {
            setError((e as Error).message);
        }
    }, []);

    const fetchSets = useCallback(async () => {
        try {
            const res = await fetch(`${getApiBaseUrl()}/draft/sets`);
            if (!res.ok) throw new Error("Error loading sets");
            const text = await res.text();
            if (!text || !text.trim()) return;
            const data: MtgSet[] = JSON.parse(text);
            setSets(data);
        } catch (e) {
            setError((e as Error).message);
        }
    }, []);

    const createDraft = useCallback(async (setCode: string) => {
        try {
            const res = await fetch(`${getApiBaseUrl()}/draft/${setCode}`, { method: "POST" });
            if (!res.ok) throw new Error("Error creating draft");
            const text = await res.text();
            if (!text || !text.trim()) return null;
            const newDraft: Draft = JSON.parse(text);
            setDraft(newDraft);
            setDrafts((prev) => [...prev, newDraft]);
            return newDraft;
        } catch (e) {
            setError((e as Error).message);
            return null;
        }
    }, []);

    const startDraft = useCallback(async () => {
        if (!draft) return;
        try {
            const res = await fetch(`${getApiBaseUrl()}/draft/${draft.id}/start`, { method: "POST" });
            if (!res.ok) throw new Error("Error starting draft");
        } catch (e) {
            setError((e as Error).message);
        }
    }, [draft]);

    const joinDraft = useCallback(async (playerName: string) => {
        if (!draft) return null;
        try {
            setIsLoading(true);
            const existingPlayer = draft.players?.find(
                (p) => p.name.trim().toLowerCase() === playerName.trim().toLowerCase()
            );

            let joinedPlayer: Player;

            if (existingPlayer) {
                const resPlayer = await fetch(`${getApiBaseUrl()}/player/${existingPlayer.id}`);
                if (!resPlayer.ok) throw new Error("Error re-entering draft");
                const text = await resPlayer.text();
                if (!text || !text.trim()) return null;
                joinedPlayer = JSON.parse(text);
            } else {
                const res = await fetch(`${getApiBaseUrl()}/draft/${draft.id}/join/${playerName}`, { method: "POST" });
                if (!res.ok) throw new Error("Error joining draft");
                const text = await res.text();
                if (!text || !text.trim()) return null;
                joinedPlayer = JSON.parse(text);
            }

            const resDraft = await fetch(`${getApiBaseUrl()}/draft/${draft.id}`);
            if (!resDraft.ok) throw new Error("Error loading draft");
            const draftText = await resDraft.text();
            if (!draftText || !draftText.trim()) return joinedPlayer;
            const updatedDraft: Draft = JSON.parse(draftText);

            setPlayer(joinedPlayer);
            setDraft(updatedDraft);
            localStorage.setItem("mtg_player_id", joinedPlayer.id);
            localStorage.setItem("mtg_draft_id", draft.id);
            return joinedPlayer;
        } catch (e) {
            setError((e as Error).message);
            return null;
        } finally {
            setIsLoading(false);
        }
    }, [draft]);

    const restoreSession = useCallback(async () => {
        const savedPlayerId = localStorage.getItem("mtg_player_id");
        const savedDraftId = localStorage.getItem("mtg_draft_id");
        if (!savedPlayerId) return null;

        try {
            setIsLoading(true);
            const resPlayer = await fetch(`${getApiBaseUrl()}/player/${savedPlayerId}`);
            if (!resPlayer.ok) {
                localStorage.removeItem("mtg_player_id");
                localStorage.removeItem("mtg_draft_id");
                return null;
            }
            const text = await resPlayer.text();
            if (!text || !text.trim()) return null;
            const restoredPlayer: Player = JSON.parse(text);

            if (savedDraftId) {
                const resDraft = await fetch(`${getApiBaseUrl()}/draft/${savedDraftId}`);
                if (resDraft.ok) {
                    const draftText = await resDraft.text();
                    if (draftText && draftText.trim()) {
                        const restoredDraft: Draft = JSON.parse(draftText);
                        setDraft(restoredDraft);
                    }
                }
            }

            setPlayer(restoredPlayer);
            return restoredPlayer;
        } catch (e) {
            console.error("Failed to restore session:", e);
            localStorage.removeItem("mtg_player_id");
            localStorage.removeItem("mtg_draft_id");
            return null;
        } finally {
            setIsLoading(false);
        }
    }, []);

    const clearSession = useCallback(() => {
        localStorage.removeItem("mtg_player_id");
        localStorage.removeItem("mtg_draft_id");
        setPlayer(null);
        setDraft(null);
        setPack(null);
    }, []);

    const fetchPack = useCallback(async (playerId: string) => {
        try {
            const res = await fetch(`${getApiBaseUrl()}/draft/${playerId}/pack`);
            if (!res.ok) throw new Error("Error loading pack");
            const text = await res.text();
            // Empty response means no pack is ready yet — the WebSocket PACK_UPDATED
            // event will trigger this function again when a pack is available.
            if (!text || !text.trim()) return;
            const data: Pack = JSON.parse(text);
            setPack(data);
            setPlayer((prev) => (prev ? { ...prev, ready: data.player?.ready ?? false } : prev));
            setHasPickedCard(false);
        } catch (e) {
            setError((e as Error).message);
        }
    }, []);

    const pickCard = useCallback(async (cardId: string) => {
        if (!pack || !player || hasPickedCard || player.ready) return;
        try {
            setHasPickedCard(true);
            const res = await fetch(`${getApiBaseUrl()}/draft/${pack.id}/${cardId}`, { method: "POST" });
            if (!res.ok) throw new Error("Error picking card");
            const text = await res.text();
            if (!text || !text.trim()) return;
            const updatedPlayer: Player = JSON.parse(text);
            console.log("Player restituito dal backend:", updatedPlayer);
            setPlayer(updatedPlayer);
        } catch (e) {
            setError((e as Error).message);
            setHasPickedCard(false);
        }
    }, [pack, player, hasPickedCard]);

    const refreshCurrentDraft = useCallback(async (targetDraftId?: string) => {
        const idToFetch = targetDraftId ?? draft?.id;
        if (!idToFetch) return;
        try {
            const resDraft = await fetch(`${getApiBaseUrl()}/draft/${idToFetch}`);
            if (!resDraft.ok) return;
            const draftText = await resDraft.text();
            if (!draftText || !draftText.trim()) return;
            const updatedDraft: Draft = JSON.parse(draftText);
            setDraft(updatedDraft);
            setDrafts((prev) =>
                prev.map((d) => (d.id === updatedDraft.id ? updatedDraft : d))
            );
        } catch (e) {
            console.error("Error refreshing current draft:", e);
        }
    }, [draft?.id]);

    return {
        // state
        draft, setDraft,
        drafts,
        player,
        pack, setPack,
        sets,
        draftFinished, setDraftFinished,
        error, setError,
        isLoading,
        hasPickedCard,
        // actions
        fetchDrafts,
        fetchSets,
        createDraft,
        startDraft,
        joinDraft,
        fetchPack,
        pickCard,
        restoreSession,
        clearSession,
        refreshCurrentDraft,
    };
}
