import json

with open("cards.json", "r", encoding="utf-8") as file:
    data = json.load(file)

for card in data:
    print(f"Set: {card['set']}, Set Name: {card['set_name']}")