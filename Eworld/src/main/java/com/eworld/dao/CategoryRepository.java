package com.eworld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

	@Query("SELECT c.title FROM Category c WHERE c.id = :id")
	public String findTitleById(String id);

}
