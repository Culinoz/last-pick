package com.mtg.mtgDraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mtg.mtgDraft.model.Draft;

public interface DraftRepository extends JpaRepository<Draft, String> {
	
	@Query("SELECT COALESCE(MAX(d.index), 0) + 1 FROM Draft d")
	int getNextIndex();

}