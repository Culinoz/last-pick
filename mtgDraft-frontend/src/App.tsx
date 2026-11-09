import { useEffect } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import { Lobby } from "./components/Lobby/Lobby";
import { NetworkAddresses } from "./components/NetworkAddresses/NetworkAddresses";
import { useDraft } from "./hooks/useDraft";
import { useWebSocket } from "./hooks/useWebSocket";
import { DraftPage } from "./pages/DraftPage";

function App() {
    const {
        draft, setDraft,
        drafts,
        player,
        pack,
        sets,
        draftFinished, setDraftFinished,
        error,
        isLoading,
        hasPickedCard,
        fetchDrafts,
        fetchSets,
        createDraft,
        startDraft,
        joinDraft,
        fetchPack,
        pickCard,
        restoreSession,
        refreshCurrentDraft,
    } = useDraft();

    useEffect(() => {
        fetchSets();
        restoreSession();
    }, [fetchSets, restoreSession]);

    useWebSocket({
        playerId: player?.id,
        draftId: draft?.id,
        onDraftUpdated: () => {
            refreshCurrentDraft();
            fetchDrafts();
        },
        onDraftsListUpdated: () => {
            fetchDrafts();
            refreshCurrentDraft();
        },
    });

    return (
        <BrowserRouter>
            <div className="app">
                <h1>MTG Draft</h1>

                <NetworkAddresses />

                {error && (
                    <div className="error-banner" role="alert">
                        ⚠️ {error}
                    </div>
                )}

                <Routes>
                    <Route
                        path="/"
                        element={
                            <Lobby
                                drafts={drafts}
                                sets={sets}
                                selectedDraft={draft}
                                onSelectDraft={setDraft}
                                onCreateDraft={createDraft}
                                onStartDraft={startDraft}
                                onJoinDraft={joinDraft}
                                onRefreshDrafts={fetchDrafts}
                            />
                        }
                    />
                    <Route
                        path="/draft"
                        element={
                            <DraftPage
                                draft={draft}
                                player={player}
                                pack={pack}
                                draftFinished={draftFinished}
                                isLoading={isLoading}
                                isPicking={hasPickedCard}
                                onPickCard={pickCard}
                                onPackUpdated={fetchPack}
                                onDraftFinished={() => setDraftFinished(true)}
                            />
                        }
                    />
                </Routes>
            </div>
        </BrowserRouter>
    );
}

export default App;
