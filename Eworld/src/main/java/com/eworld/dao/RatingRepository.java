package com.eworld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String>{

}
