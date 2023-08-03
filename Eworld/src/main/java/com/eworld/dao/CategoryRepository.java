package com.eworld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
