package com.eworld.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

	@Query("SELECT c.title FROM Category c WHERE c.id = :id")
	public String findTitleById(String id);

	@Query("SELECT c FROM Category c WHERE c.active=true")
	public Page<Category> findAll(Pageable pageable);
	
	@Query("SELECT c FROM Category c WHERE c.active=true")
	public List<Category> findAll();

}
