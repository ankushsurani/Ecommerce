package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.CategoryRepository;
import com.eworld.entities.Category;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public void saveCategory(Category category) {
		this.categoryRepository.save(category);
	}

	public List<Category> getAllCategories() {
		return this.categoryRepository.findAll();
	}

	public Category getCategory(String catId) {
		return this.categoryRepository.findById(catId).get();
	}

}
