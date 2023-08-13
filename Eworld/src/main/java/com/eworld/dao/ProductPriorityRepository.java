package com.eworld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.ProductPriority;

@Repository
public interface ProductPriorityRepository extends JpaRepository<ProductPriority, String>{

}
