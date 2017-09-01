package com.pier.rest.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "PRODUCT")
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="name")
public class Product implements ObjectModel<Long>{
	
	@Id
	@Column(name="PRODUCT_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
		
	@ManyToOne
	@JoinColumn(name="BRAND_ID")
	@Cascade(CascadeType.SAVE_UPDATE)
	@Fetch(FetchMode.SELECT)
	private Brand brand;
	
	@Column(name="PRICE",nullable= false, precision=7, scale=2)    // Creates the database field with this size.
	@Digits(integer=7, fraction=2)  
	private BigDecimal price;
	
	@Column(name="NAME", length=50)
	@NotNull
	@Size(min=4, max=40)
	private String name;
	
	@Column(name="DESCRIPTION", columnDefinition = "TEXT")		
	private String description;
	
	@Column(name="SECONDARY_DESC", columnDefinition = "TEXT")		
	private String secondaryDescription;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="PRODUCT_CATEGORY", joinColumns={@JoinColumn(name="PRODUCT_ID" ,referencedColumnName="PRODUCT_ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID", referencedColumnName="ID")})
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=30)//I want to load 30 sets of categories for 30 products in one query
	private Set<Category> categories;	
	
	
	@ManyToOne
	@JoinColumn(name="TYPE_ID")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	@Fetch(FetchMode.SELECT)
	private ProductType productType;

	@JsonIgnore
	@OneToMany(mappedBy="id.product")
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	Set<ProductFlavor> productFlavors;
	
	@Transient	
	private List<Flavor> flavors;
	
	//not persisted- only used to keep a hold of existence for single flavor products
	@Transient	
	private Long existence;
	
	@Column(name="ENABLED" , columnDefinition="boolean default true")
	@NotNull
	private Boolean enabled;
	
	
	
	@Override
	public Long getId() {
		return id;
	}

	public Product(Brand brand, BigDecimal price, String name, String description, Set<Category> categories,
			ProductType productType, Long existence, Boolean enabled) {
		super();
		this.brand = brand;
		this.price = price;
		this.name = name;
		this.description = description;
		this.categories = categories;
		this.productType = productType;
		this.existence = existence;
		this.enabled = enabled;
	}
	
	public Product(Brand brand, BigDecimal price, String name, String description, String secondaryDescription, Set<Category> categories,
			ProductType productType, Long existence, Boolean enabled) {
		super();
		this.brand = brand;
		this.price = price;
		this.name = name;
		this.description = description;
		this.categories = categories;
		this.productType = productType;
		this.secondaryDescription=secondaryDescription;
		this.existence = existence;
		this.enabled = enabled;
	}
	
	public Product(){		
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSecondaryDescription() {
		return secondaryDescription;
	}

	public void setSecondaryDescription(String secondaryDescription) {
		this.secondaryDescription = secondaryDescription;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}  
	@JsonProperty
	public void setFlavors(List<Flavor> flavors) {
		this.flavors = flavors;
		setProductFlavors(new HashSet(flavors.stream().map(flavor->new ProductFlavor(this,flavor,flavor.getExistence())).collect(Collectors.toList())));
	}
	
	@JsonIgnore
	public List<Flavor> getFlavors() {
		return productFlavors.stream().map(pflav->pflav.getFlavor()).collect(Collectors.toList());
	}	
	
	@JsonIgnore
	public Set<ProductFlavor> getProductFlavors() {
			return productFlavors;
	}

	 @JsonIgnore
	private void setProductFlavors(Set<ProductFlavor> productFlavors) {
	  this.productFlavors = productFlavors;
	}	 
	
	//used only when there's no flavor provided see Product controller "create product"
	public Long getExistence() {
		return existence;
	}

	//used only when there's no flavor provided see Product controller "create product"
	public void setExistence(Long existence) {
		this.existence = existence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Product other = (Product) obj;
		if (brand == null) {
			if (other.brand != null)
				return false;
		} else if (!brand.equals(other.brand))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}	
	
	

}
