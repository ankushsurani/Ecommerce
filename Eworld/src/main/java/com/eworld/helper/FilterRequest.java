package com.eworld.helper;

public class FilterRequest {

	private String sortBy;

	private Integer categoryId;

	private Integer minPrice;

	private Integer maxPrice;

	private String brandName;

	public FilterRequest() {
		super();
	}

	public FilterRequest(String sortBy, Integer categoryId, Integer minPrice, Integer maxPrice, String brandName) {
		super();
		this.sortBy = sortBy;
		this.categoryId = categoryId;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.brandName = brandName;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	@Override
	public String toString() {
		return "FilterRequest [sortBy=" + sortBy + ", categoryId=" + categoryId + ", minPrice=" + minPrice
				+ ", maxPrice=" + maxPrice + ", brandName=" + brandName + "]";
	}

}
