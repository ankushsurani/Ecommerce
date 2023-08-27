package com.eworld.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class ProductReview {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	@Size(min = 3, message = "Title Must have minimum 3 characters")
	private String title;

	@Size(min = 5, max = 8000, message = "Description length Must Between 5 to 8000 Characters")
	private String description;

	@OneToOne(optional = false) // Review must have an associated Rating
	private Rating rating;

	public ProductReview() {
		super();
	}

	public ProductReview(String id, String title, String description, Rating rating) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.rating = rating;
	}

	public ProductReview(String title, String description, Rating rating) {
		super();
		this.title = title;
		this.description = description;
		this.rating = rating;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

}
