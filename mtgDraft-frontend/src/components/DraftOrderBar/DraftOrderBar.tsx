import type { Draft, Player } from "../../types";

interface DraftOrderBarProps {
    draft: Draft | null;
    currentPlayer: Player | null;
}

export function DraftOrderBar({ draft, currentPlayer }: DraftOrderBarProps) {
    if (!draft || !draft.players || draft.players.length === 0) return null;

    const round = draft.round ?? 0;
    // Round 0 (Pack 1): Right (0 -> 1 -> 2), Round 1 (Pack 2): Left (2 -> 1 -> 0), Round 2 (Pack 3): Right
    const isPassingRight = round % 2 === 0;
    const arrowSymbol = isPassingRight ? "➡️" : "⬅️";
    const roundName = `Pack ${round + 1}`;

    // Sort players by draftOrder
    const sortedPlayers = [...draft.players].sort(
        (a, b) => (a.draftOrder ?? 0) - (b.draftOrder ?? 0)
    );

    return (
        <div className="draft-order-bar">
            <div className="draft-order-header">
                <span className="round-badge">{roundName}</span>
                <span className="direction-label">
                    Pack Direction: <strong>{isPassingRight ? "Passing Right ➡️" : "Passing Left ⬅️"}</strong>
                </span>
            </div>

            <div className="player-order-list">
                {sortedPlayers.map((p, index) => {
                    const isYou = p.id === currentPlayer?.id;
                    const isLast = index === sortedPlayers.length - 1;

                    return (
                        <div key={p.id} className="player-order-item-wrapper">
                            <div className={`player-order-card ${isYou ? "current-player" : ""} ${p.ready ? "ready" : "picking"}`}>
                                <div className="player-name">
                                    {p.name} {isYou && <span className="you-badge">(You)</span>}
                                </div>
                                <div className="player-status">
                                    {p.ready ? "✅ Ready" : "⏳ Picking..."}
                                </div>
                            </div>

                            {!isLast && (
                                <div className="pass-arrow" title={isPassingRight ? "Passes pack to right" : "Passes pack to left"}>
                                    {arrowSymbol}
                                </div>
                            )}

                            {isLast && sortedPlayers.length > 1 && (
                                <div className="pass-arrow loop-arrow" title="Pack loops around">
                                    {isPassingRight ? "🔄 ➡️" : "🔄 ⬅️"}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
