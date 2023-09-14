package com.eworld.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.CategoryPriorityRepository;
import com.eworld.entities.Category;

@Service
public class CategoryPriorityService {

	@Autowired
	private CategoryPriorityRepository categoryPriorityRepository;

	public List<Category> getHighPrioCategories() {
		return this.categoryPriorityRepository.findAll().stream()
				.map(categoryPriority -> categoryPriority.getCategory()).collect(Collectors.toList());
	}

}
