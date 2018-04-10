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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.pier.business.PromotionBehavior;

@Entity
@Table(name="promotion_rule")
public class PromotionRule implements ObjectModel<Long> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="PROMOTION_TYPE")
	@Enumerated(EnumType.STRING)
	PromotionBehavior behavior;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="promo_product", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns=@JoinColumn(name="PRODUCT_ID" ,referencedColumnName="PRODUCT_ID"))
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=5)
	Set<Product> products;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="promo_types", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="PRODUCT_TYPE_ID" ,referencedColumnName="ID")})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=5)
	Set<ProductType> productTypes;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="promo_categories", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID" ,referencedColumnName="ID")})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=5)
	Set<Category> categories;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="promo_brands", joinColumns={@JoinColumn(name="PROMO_RULE_ID", referencedColumnName="ID")},
	inverseJoinColumns={@JoinColumn(name="BRAND_ID" ,referencedColumnName="ID")})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=5)
	Set<Brand> brands;
	
	@Column(name="MINPURCHASE",nullable= false, precision=12, scale=2)
	@Digits(integer=12, fraction=2)  
	BigDecimal minPurchase;
	
	@Column(name="MIN_AMOUNT")
	Integer minAmount;
	
	@Column(name="PROMOTION_CODE")
	String promotionCode;
	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="rule_product", joinColumns=@JoinColumn(name="RULE_ID" ,referencedColumnName="ID"),
	inverseJoinColumns={@JoinColumn(name="PRODUCT_ID", referencedColumnName="PRODUCT_ID")})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=5)
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
		if(percentage>100){
			percentage=100;
		}
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

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		if(promotionCode.length()>0)
		this.promotionCode = promotionCode;
		else
			this.promotionCode=null;	
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

	public PromotionRule() {		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((behavior == null) ? 0 : behavior.hashCode());
		result = prime * result + ((brands == null) ? 0 : brands.hashCode());
		result = prime * result + ((categories == null) ? 0 : categories.hashCode());
		result = prime * result + ((giveAway == null) ? 0 : giveAway.hashCode());
		result = prime * result + ((minAmount == null) ? 0 : minAmount.hashCode());
		result = prime * result + ((minPurchase == null) ? 0 : minPurchase.hashCode());
		result = prime * result + percentage;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((productTypes == null) ? 0 : productTypes.hashCode());
		result = prime * result + ((products == null) ? 0 : products.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PromotionRule other = (PromotionRule) obj;
		if (behavior != other.behavior)
			return false;
		if (brands == null) {
			if (other.brands != null)
				return false;
		} else if (!brands.equals(other.brands))
			return false;
		if (categories == null) {
			if (other.categories != null)
				return false;
		} else if (!categories.equals(other.categories))
			return false;
		if (giveAway == null) {
			if (other.giveAway != null)
				return false;
		} else if (!giveAway.equals(other.giveAway))
			return false;
		if (minAmount == null) {
			if (other.minAmount != null)
				return false;
		} else if (!minAmount.equals(other.minAmount))
			return false;
		if (minPurchase == null) {
			if (other.minPurchase != null)
				return false;
		} else if (!minPurchase.equals(other.minPurchase))
			return false;
		if (percentage != other.percentage)
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (productTypes == null) {
			if (other.productTypes != null)
				return false;
		} else if (!productTypes.equals(other.productTypes))
			return false;
		if (products == null) {
			if (other.products != null)
				return false;
		} else if (!products.equals(other.products))
			return false;
		return true;
	}
	
	
	

}
