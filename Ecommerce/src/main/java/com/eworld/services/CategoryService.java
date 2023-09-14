package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public String getTitleById(String id) {
		return this.categoryRepository.findTitleById(id);
	}

	public Page<Category> findAllByPage(Pageable pageable) {
		return this.categoryRepository.findAll(pageable);
	}

	public void removeCategoryFromView(Category category) {
		category.setActive(false);
		this.categoryRepository.save(category);
	}

}
