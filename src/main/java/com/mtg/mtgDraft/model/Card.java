package com.mtg.mtgDraft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Card {

	@Id
	private String id;

	private String name;

	@JsonProperty("mana_cost")
	private String manaCost;

	@JsonProperty("type_line")
	private String typeLine;

	@JsonProperty("oracle_text")
	@Column(columnDefinition = "TEXT")
	private String oracleText;
	private String rarity;
	private String set;
	private String imageUris;
	private String setName;

	public Card() {
	}

	public Card(String id, String name, String manaCost, String typeLine, String oracleText, String rarity,
			String set) {
		this.id = id;
		this.name = name;
		this.manaCost = manaCost;
		this.typeLine = typeLine;
		this.oracleText = oracleText;
		this.rarity = rarity;
		this.set = set;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManaCost() {
		return manaCost;
	}

	public void setManaCost(String manaCost) {
		this.manaCost = manaCost;
	}

	public String getTypeLine() {
		return typeLine;
	}

	public void setTypeLine(String typeLine) {
		this.typeLine = typeLine;
	}

	public String getOracleText() {
		return oracleText;
	}

	public void setOracleText(String oracleText) {
		this.oracleText = oracleText;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getImageUri() {
		return imageUris;
	}

	public void setImageUri(String imageUris) {
		this.imageUris = imageUris;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", name=" + name + ", manaCost=" + manaCost + ", typeLine=" + typeLine
				+ ", oracleText=" + oracleText + ", rarity=" + rarity + ", set=" + set + "]";
	}

}
