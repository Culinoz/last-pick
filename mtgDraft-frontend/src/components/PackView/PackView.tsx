import type { Card, Pack } from "../../types";

interface PackViewProps {
    pack: Pack;
    onPickCard: (cardId: string) => Promise<void>;
    isPicking?: boolean;
    isReady?: boolean;
}

export function PackView({ pack, onPickCard, isPicking, isReady }: PackViewProps) {
    const disabled = Boolean(isPicking || isReady);

    return (
        <div className="pack-column">
            <h2>Your Pack</h2>
            {disabled && (
                <div style={{ padding: "8px 12px", background: "rgba(255, 215, 0, 0.15)", border: "1px solid #ffd700", borderRadius: "6px", marginBottom: "12px", color: "#ffd700", fontWeight: "bold" }}>
                    ⏳ Card picked! Waiting for other players...
                </div>
            )}
            <div className="card-grid" style={{ opacity: disabled ? 0.5 : 1, pointerEvents: disabled ? "none" : "auto" }}>
                {pack.cards.map((card: Card) => (
                    <img
                        key={card.id}
                        className="draft-card"
                        src={card.imageUri}
                        alt={card.name}
                        title={card.name}
                        onClick={() => !disabled && onPickCard(card.id)}
                    />
                ))}
            </div>
        </div>
    );
}
