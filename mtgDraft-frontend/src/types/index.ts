export interface Player {
    id: string;
    name: string;
    draftOrder: number;
    pickedCards: Card[];
    ready: boolean;
}

export interface Draft {
    id: string;
    set: string;
    setName: string;
    index: number;
    round?: number;
    players: Player[];
}

export interface Card {
    id: string;
    name: string;
    mana_cost: string;
    type_line: string;
    oracle_text: string;
    rarity: string;
    set: string;
    imageUri: string;
}

export interface Pack {
    id: string;
    cards: Card[];
    player: Player;
}

/** [setCode, setName] tuple */
export type MtgSet = [string, string];
