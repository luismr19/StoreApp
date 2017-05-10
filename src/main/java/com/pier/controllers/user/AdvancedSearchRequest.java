package com.pier.controllers.user;

import java.util.List;

public class AdvancedSearchRequest {
	
	private Long BrandId;
	
	private Long ProductTypeId;
	
	private List<Long> CategoryIds;
	
	private String name;

	public Long getBrandId() {
		return BrandId;
	}

	public void setBrandId(Long brandId) {
		BrandId = brandId;
	}

	public Long getProductTypeId() {
		return ProductTypeId;
	}

	public void setProductTypeId(Long productTypeId) {
		ProductTypeId = productTypeId;
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
