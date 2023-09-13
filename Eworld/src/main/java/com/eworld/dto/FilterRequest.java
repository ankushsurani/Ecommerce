package com.eworld.dto;

public class FilterRequest {

	private String sortType;

	private String categoryId;

	private Integer minPrice;

	private Integer maxPrice;

	private String search;

	private String brandName;

	public FilterRequest() {
		super();
	}

	public FilterRequest(String sortType, String categoryId, Integer minPrice, Integer maxPrice, String search,
			String brandName) {
		super();
		this.sortType = sortType;
		this.categoryId = categoryId;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.search = search;
		this.brandName = brandName;
	}

	public FilterRequest(String sortType) {
		super();
		this.sortType = sortType;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
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

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	@Override
	public String toString() {
		return "FilterRequest [sortType=" + sortType + ", categoryId=" + categoryId + ", minPrice=" + minPrice
				+ ", maxPrice=" + maxPrice + ", brandName=" + brandName + "]";
	}

}
