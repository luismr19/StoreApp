package com.pier.controllers.user;

import java.util.List;

public class AdvancedSearchRequest {
	
	private List<Long> brandIds;
	
	private List<Long> productTypeIds;
	
	private List<Long> categoryIds;
	
	private String name;

	

	public List<Long> getBrandIds() {
		return brandIds;
	}

	public void setBrandIds(List<Long> brandId) {
		brandIds = brandId;
	}

	public List<Long> getProductTypeIds() {
		return productTypeIds;
	}

	public void setProductTypeIds(List<Long> productTypeId) {
		productTypeIds = productTypeId;
	}

	public List<Long> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
