import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { Draft, MtgSet } from "../../types";

interface LobbyProps {
    drafts: Draft[];
    sets: MtgSet[];
    selectedDraft: Draft | null;
    onSelectDraft: (draft: Draft | null) => void;
    onCreateDraft: (setCode: string) => Promise<Draft | null>;
    onStartDraft: () => Promise<void>;
    onJoinDraft: (playerName: string) => Promise<unknown>;
    onRefreshDrafts: () => Promise<void>;
}

export function Lobby({
    drafts,
    sets,
    selectedDraft,
    onSelectDraft,
    onCreateDraft,
    onStartDraft,
    onJoinDraft,
    onRefreshDrafts,
}: LobbyProps) {
    const navigate = useNavigate();
    const [setSearch, setSetSearch] = useState("");
    const [selectedSet, setSelectedSet] = useState<MtgSet | null>(null);
    const [playerName, setPlayerName] = useState("");
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [highlightedIndex, setHighlightedIndex] = useState(-1);

    useEffect(() => {
        onRefreshDrafts();
    }, [onRefreshDrafts]);

    const filteredSets = sets.filter(
        (s) =>
            s[1].toLowerCase().includes(setSearch.toLowerCase()) ||
            s[0].toLowerCase().includes(setSearch.toLowerCase()),
    );

    const handleCreate = async () => {
        if (!selectedSet) return;
        await onCreateDraft(selectedSet[0]);
    };

    const selectSet = (s: MtgSet) => {
        setSelectedSet(s);
        setSetSearch(s[1]);
        setDropdownOpen(false);
        setHighlightedIndex(-1);
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (!dropdownOpen || filteredSets.length === 0) return;
        if (e.key === "ArrowDown") {
            e.preventDefault();
            setHighlightedIndex((i) => Math.min(i + 1, filteredSets.length - 1));
        } else if (e.key === "ArrowUp") {
            e.preventDefault();
            setHighlightedIndex((i) => Math.max(i - 1, 0));
        } else if (e.key === "Enter") {
            e.preventDefault();
            if (highlightedIndex >= 0) selectSet(filteredSets[highlightedIndex]);
        } else if (e.key === "Escape") {
            setDropdownOpen(false);
            setHighlightedIndex(-1);
        }
    };

    const handleJoin = async () => {
        if (!playerName.trim() || !selectedDraft) return;
        const joinedPlayer = await onJoinDraft(playerName.trim());
        if (joinedPlayer) {
            navigate("/draft");
        }
    };

    return (
        <div className="lobby">
            <h2>Create a Draft</h2>

            {/* Set search */}
            <div className="set-search">
                <input
                    id="set-search-input"
                    type="text"
                    value={setSearch}
                    onChange={(e) => {
                        setSetSearch(e.target.value);
                        setDropdownOpen(e.target.value !== "");
                        setHighlightedIndex(-1);
                    }}
                    onBlur={() => setTimeout(() => setDropdownOpen(false), 100)}
                    onKeyDown={handleKeyDown}
                    placeholder="Search for a set..."
                    autoComplete="off"
                />
                {dropdownOpen && filteredSets.length > 0 && (
                    <ul className="set-dropdown" role="listbox">
                        {filteredSets.map((s, i) => (
                            <li
                                key={s[0]}
                                role="option"
                                aria-selected={i === highlightedIndex}
                                className={i === highlightedIndex ? "highlighted" : ""}
                                onMouseDown={(e) => e.preventDefault()}
                                onMouseEnter={() => setHighlightedIndex(i)}
                                onClick={() => selectSet(s)}
                            >
                                {s[1]} <span className="set-code">({s[0]})</span>
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            <button id="create-draft-btn" onClick={handleCreate} disabled={!selectedSet}>
                Create Draft
            </button>

            <h2>Join a Draft</h2>

            <select
                id="draft-select"
                value={selectedDraft?.id ?? ""}
                onChange={(e) => {
                    const found = drafts.find((d) => d.id === e.target.value);
                    onSelectDraft(found ?? null);
                }}
            >
                <option value="">Select a draft</option>
                {drafts.map((d) => (
                    <option key={d.id} value={d.id}>
                        {d.index} - {d.setName}
                    </option>
                ))}
            </select>

            {selectedDraft && (
                <div className="draft-info">
                    <h3>Players in draft</h3>
                    <ul>
                        {selectedDraft.players.map((p) => (
                            <li key={p.id} style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "4px" }}>
                                <span>{p.name}</span>
                                <button
                                    style={{ marginLeft: "8px", padding: "4px 10px", fontSize: "0.85em", width: "auto" }}
                                    onClick={async () => {
                                        const joined = await onJoinDraft(p.name);
                                        if (joined) navigate("/draft");
                                    }}
                                >
                                    Re-enter
                                </button>
                            </li>
                        ))}
                    </ul>
                    <button id="start-draft-btn" onClick={onStartDraft}>
                        Start Draft
                    </button>
                </div>
            )}

            <div className="join-form">
                <input
                    id="player-name-input"
                    type="text"
                    value={playerName}
                    onChange={(e) => setPlayerName(e.target.value)}
                    placeholder="Your name"
                />
                <button
                    id="join-draft-btn"
                    onClick={handleJoin}
                    disabled={!playerName.trim() || !selectedDraft}
                >
                    Join Draft
                </button>
            </div>
        </div>
    );
}
