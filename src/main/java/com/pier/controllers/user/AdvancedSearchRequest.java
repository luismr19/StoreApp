package com.pier.controllers.user;

import java.util.List;

public class AdvancedSearchRequest {
	
	private List<Long> BrandIds;
	
	private List<Long> ProductTypeIds;
	
	private List<Long> CategoryIds;
	
	private String name;

	

	public List<Long> getBrandIds() {
		return BrandIds;
	}

	public void setBrandIds(List<Long> brandId) {
		BrandIds = brandId;
	}

	public List<Long> getProductTypeIds() {
		return ProductTypeIds;
	}

	public void setProductTypeIds(List<Long> productTypeId) {
		ProductTypeIds = productTypeId;
	}

	public List<Long> getCategoryIds() {
		return CategoryIds;
	}

	public void setCategoryIds(List<Long> categoryIds) {
		CategoryIds = categoryIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
