package com.eworld.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class CategoryPriority {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int categoryPriorityId;
	
	@Column(name = "is_high_priority")
	private boolean highPriority;
	
	@OneToOne
	@JoinColumn(name = "id")
	private Category category;

	public CategoryPriority() {
		super();
	}

	public CategoryPriority(int categoryPriorityId, boolean highPriority, Category category) {
		super();
		this.categoryPriorityId = categoryPriorityId;
		this.highPriority = highPriority;
		this.category = category;
	}

	public int getCategoryPriorityId() {
		return categoryPriorityId;
	}

	public void setCategoryPriorityId(int categoryPriorityId) {
		this.categoryPriorityId = categoryPriorityId;
	}

	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
}
