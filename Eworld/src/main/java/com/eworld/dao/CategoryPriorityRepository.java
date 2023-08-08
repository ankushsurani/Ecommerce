package com.eworld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.CategoryPriority;

@Repository
public interface CategoryPriorityRepository extends JpaRepository<CategoryPriority, Integer>{

}
