package com.pier.rest.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import com.pier.business.PromotionBehavior;

@Entity
@Table(name="PROMOTION_RULE")
public class PromotionRule implements ObjectModel<Long> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="PROMOTION_TYPE")
	@Enumerated(EnumType.STRING)
	PromotionBehavior behavior;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_PRODUCT", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns=@JoinColumn(name="PRODUCT_ID" ,referencedColumnName="PRODUCT_ID"))
	List<Product> products;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_TYPES", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="PRODUCT_TYPE_ID" ,referencedColumnName="ID")})
	List<ProductType> productTypes;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_CATEGORIES", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID" ,referencedColumnName="ID")})
	List<Category> categories;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_CATEGORIES", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID" ,referencedColumnName="ID")})
	List<Brand> brands;
	
	@Column(name="MINPURCHASE",nullable= false, precision=12, scale=2)
	@Digits(integer=12, fraction=2)  
	BigDecimal minPurchase;
	
	@Column(name="POINTS")
	Long points;
	
	@Column(name="PERCENTAGE")
	int percentage;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromotionBehavior getBehavior() {
		return behavior;
	}

	public void setBehavior(PromotionBehavior behavior) {
		this.behavior = behavior;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public List<ProductType> getProductTypes() {
		return productTypes;
	}

	public void setProductTypes(List<ProductType> productTypes) {
		this.productTypes = productTypes;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Brand> getBrands() {
		return brands;
	}

	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}

	public BigDecimal getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(BigDecimal minPurchase) {
		this.minPurchase = minPurchase;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	

}
