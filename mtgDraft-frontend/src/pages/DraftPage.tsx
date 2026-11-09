import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { DraftOrderBar } from "../components/DraftOrderBar/DraftOrderBar";
import { PackView } from "../components/PackView/PackView";
import { PickedCards } from "../components/PickedCards/PickedCards";
import { useWebSocket } from "../hooks/useWebSocket";
import type { Draft, Pack, Player } from "../types";
import { getApiBaseUrl } from "../utils/apiConfig";

interface DraftPageProps {
    draft: Draft | null;
    player: Player | null;
    pack: Pack | null;
    draftFinished: boolean;
    isLoading?: boolean;
    isPicking?: boolean;
    onPickCard: (cardId: string) => Promise<void>;
    onPackUpdated: (playerId: string) => void;
    onDraftFinished: () => void;
}

export function DraftPage({
    draft,
    player,
    pack,
    draftFinished,
    isLoading,
    isPicking,
    onPickCard,
    onPackUpdated,
    onDraftFinished,
}: DraftPageProps) {
    const navigate = useNavigate();

    // Set of selected card indices
    const [selectedIndices, setSelectedIndices] = useState<Set<number>>(new Set());
    const [isDownloading, setIsDownloading] = useState(false);

    // Initialize all cards as selected when draft finished
    useEffect(() => {
        if (draftFinished && player?.pickedCards) {
            setSelectedIndices(new Set(player.pickedCards.map((_, idx) => idx)));
        }
    }, [draftFinished, player?.pickedCards]);

    // Redirect to lobby if no player and not loading
    useEffect(() => {
        if (!player && !isLoading) {
            navigate("/");
        }
    }, [player, isLoading, navigate]);

    // On session restore: do a one-time pack fetch so the player sees their
    // current pack immediately, without polling. The WebSocket handles subsequent updates.
    const playerIdRef = useRef<string | undefined>(undefined);
    useEffect(() => {
        if (player?.id && player.id !== playerIdRef.current) {
            playerIdRef.current = player.id;
            onPackUpdated(player.id);
        }
    }, [player?.id, onPackUpdated]);

    useWebSocket({
        playerId: player?.id,
        onPackUpdated,
        onDraftFinished,
    });

    if (isLoading && !player) {
        return (
            <div className="draft-layout">
                <p>Restoring draft session...</p>
            </div>
        );
    }

    if (!player) return null;

    if (draftFinished) {
        const pickedCards = player.pickedCards ?? [];

        const toggleCardSelect = (index: number) => {
            setSelectedIndices((prev) => {
                const next = new Set(prev);
                if (next.has(index)) {
                    next.delete(index);
                } else {
                    next.add(index);
                }
                return next;
            });
        };

        const selectAll = () => {
            setSelectedIndices(new Set(pickedCards.map((_, idx) => idx)));
        };

        const deselectAll = () => {
            setSelectedIndices(new Set());
        };

        const getSelectedCardIds = (): string[] => {
            return pickedCards
                .filter((_, idx) => selectedIndices.has(idx))
                .map((card) => card.id);
        };

        const totalSelectedCount = selectedIndices.size;

        const handleDownloadSelectedPdf = async () => {
            const cardIds = getSelectedCardIds();
            if (cardIds.length === 0) return;

            setIsDownloading(true);
            try {
                const baseUrl = getApiBaseUrl();

                const res = await fetch(`${baseUrl}/draft/pdf`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(cardIds),
                });

                if (!res.ok) throw new Error("Failed to generate PDF");

                const blob = await res.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = `selected-cards-${player.name}.pdf`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            } catch (err) {
                console.error("PDF download error:", err);
                alert("Error downloading PDF");
            } finally {
                setIsDownloading(false);
            }
        };

        return (
            <div className="draft-finished">
                <h2>🎉 Draft finished!</h2>
                <p>Click on cards to select or deselect them for your PDF:</p>

                <div style={{ display: "flex", gap: "10px", justifyContent: "center", marginBottom: "16px" }}>
                    <button onClick={selectAll} style={{ width: "auto" }}>Select All</button>
                    <button onClick={deselectAll} style={{ width: "auto" }}>Deselect All</button>
                </div>

                <div className="pdf-selection-grid">
                    {pickedCards.map((card, idx) => {
                        const isSelected = selectedIndices.has(idx);
                        return (
                            <div
                                key={`${card.id}-${idx}`}
                                className={`pdf-card-item ${isSelected ? "selected" : ""}`}
                                onClick={() => toggleCardSelect(idx)}
                                style={{ cursor: "pointer" }}
                            >
                                <img
                                    src={card.imageUri}
                                    alt={card.name}
                                    className="draft-card"
                                />
                                <span className="select-indicator">
                                    {isSelected ? "✓ Selected" : "Click to select"}
                                </span>
                            </div>
                        );
                    })}
                </div>

                <div style={{ display: "flex", gap: "12px", justifyContent: "center", marginTop: "24px" }}>
                    <button
                        onClick={handleDownloadSelectedPdf}
                        disabled={totalSelectedCount === 0 || isDownloading}
                        style={{ width: "auto", fontWeight: "bold" }}
                    >
                        {isDownloading ? "Generating PDF..." : `📄 Download PDF (${totalSelectedCount} cards)`}
                    </button>
                    <button onClick={() => navigate("/")} style={{ width: "auto" }}>
                        Back to Lobby
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div style={{ width: "100%" }}>
            <DraftOrderBar draft={draft} currentPlayer={player} />
            <div className="draft-layout">
                {pack ? (
                    <PackView pack={pack} onPickCard={onPickCard} isPicking={isPicking} isReady={player.ready} />
                ) : (
                    <div className="pack-column">
                        <h2>Your Pack</h2>
                        <p>Waiting for pack...</p>
                    </div>
                )}
                <PickedCards cards={player.pickedCards ?? []} />
            </div>
        </div>
    );
}
