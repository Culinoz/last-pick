package com.mtg.mtgDraft.model;

import jakarta.validation.constraints.NotBlank;

public class JoinRequest {

	@NotBlank
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
