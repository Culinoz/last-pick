package com.mtg.mtgDraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScryfallBulkData {

	private String type;
	private String jsonl_download_uri;

	public ScryfallBulkData() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getjsonl_download_uri() {
		return jsonl_download_uri;
	}

	@JsonProperty("jsonl_download_uri")
	public void setjsonl_download_uri(String jsonl_download_uri) {
		this.jsonl_download_uri = jsonl_download_uri;
	}
}