import type { Card } from "../../types";

interface PickedCardsProps {
    cards: Card[];
}

export function PickedCards({ cards }: PickedCardsProps) {
    return (
        <div className="picked-column">
            <h2>My Cards ({cards.length})</h2>
            <div className="picked-grid">
                {cards.map((card: Card) => (
                    <img
                        key={card.id}
                        className="draft-card"
                        src={card.imageUri}
                        alt={card.name}
                        title={card.name}
                    />
                ))}
            </div>
        </div>
    );
}
