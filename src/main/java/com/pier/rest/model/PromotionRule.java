package com.pier.rest.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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
	Set<Product> products;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_TYPES", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="PRODUCT_TYPE_ID" ,referencedColumnName="ID")})
	Set<ProductType> productTypes;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_CATEGORIES", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID" ,referencedColumnName="ID")})
	Set<Category> categories;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="PROMO_CATEGORIES", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID" ,referencedColumnName="ID")})
	Set<Brand> brands;
	
	@Column(name="MINPURCHASE",nullable= false, precision=12, scale=2)
	@Digits(integer=12, fraction=2)  
	BigDecimal minPurchase;
	
	@Column(name="MIN_AMOUNT")
	Integer minAmount;
	
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="RULE_PRODUCT", joinColumns=@JoinColumn(name="RULE_ID" ,referencedColumnName="ID"),
	inverseJoinColumns={@JoinColumn(name="PRODUCT_ID", referencedColumnName="PRODUCT_ID")})
	private List<Product> giveAway;
	
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

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public Set<ProductType> getProductTypes() {
		return productTypes;
	}

	public void setProductTypes(Set<ProductType> productTypes) {
		this.productTypes = productTypes;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public Set<Brand> getBrands() {
		return brands;
	}

	public void setBrands(Set<Brand> brands) {
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

	public List<Product> getGiveAway() {
		return giveAway;
	}

	public void setGiveAway(List<Product> giveAway) {
		this.giveAway = giveAway;
	}
	
	

	public Integer getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(Integer minAmount) {
		this.minAmount = minAmount;
	}

	public PromotionRule(PromotionBehavior behavior, Set<Product> products, Set<ProductType> productTypes,
			Set<Category> categories, Set<Brand> brands, BigDecimal minPurchase, Integer minAmount,
			List<Product> giveAway, Long points, int percentage) {
		super();
		this.behavior = behavior;
		this.products = products;
		this.productTypes = productTypes;
		this.categories = categories;
		this.brands = brands;
		this.minPurchase = minPurchase;
		this.minAmount = minAmount;
		this.giveAway = giveAway;
		this.points = points;
		this.percentage = percentage;
	}

	
	
	
	

}
