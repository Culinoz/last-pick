package com.mtg.mtgDraft.dto;

import java.util.List;

public class ScryfallBulkResponse {

	private List<ScryfallBulkData> data;

	public List<ScryfallBulkData> getData() {
		return data;
	}

	public void setData(List<ScryfallBulkData> data) {
		this.data = data;
	}
}
