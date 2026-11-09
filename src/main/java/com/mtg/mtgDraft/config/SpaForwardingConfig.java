package com.mtg.mtgDraft.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards client-side routes to index.html so React Router handles them.
 *
 * Without this, refreshing the browser on a React route like /draft
 * would result in a 404 because Spring Boot doesn't have a mapping for it.
 *
 * Only matches paths that are NOT:
 *  - API endpoints (/draft/**, /player/**, /scryfall/**, /pickedcards/**)
 *  - WebSocket endpoint (/ws/**)
 *  - Static resources (files with extensions like .js, .css, .png)
 */
@Controller
public class SpaForwardingConfig {

    @GetMapping(value = {
        "/{path:^(?!draft|player|scryfall|pickedcards|ws).*$}",
        "/{path:^(?!draft|player|scryfall|pickedcards|ws).*$}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
