package com.eworld.entities;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class CategoryPriority {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	@Column(name = "is_high_priority")
	private boolean highPriority;

	@OneToOne
	@JoinColumn(name = "category_id")
	private Category category;

	public CategoryPriority() {
		super();
	}

	public CategoryPriority(String id, boolean highPriority, Category category) {
		super();
		this.id = id;
		this.highPriority = highPriority;
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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