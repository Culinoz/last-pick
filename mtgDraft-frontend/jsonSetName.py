import json
import csv

with open("default-cards.jsonl", "r", encoding="utf-8") as file, \
     open("sets.csv", "w", newline="", encoding="utf-8") as csvfile:

    writer = csv.writer(csvfile)

    # Intestazioni
    writer.writerow(["set", "set_name"])

    for line in file:
        card = json.loads(line)

        writer.writerow([
            card["set"],
            card["set_name"]
        ])

print("CSV creato con successo!")