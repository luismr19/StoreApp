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
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="PRODUCT_CATEGORY", joinColumns={@JoinColumn(name="PRODUCT_ID" ,referencedColumnName="PRODUCT_ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID", referencedColumnName="ID")})
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=50)//I want to load 50 sets of categories for 50 products in one query
	private List<Category> categories;	
	
	
	@ManyToOne
	@JoinColumn(name="TYPE_ID")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	private ProductType productType;
	
	/*@Column(name="EXISTENCE", length=50, unique=false, nullable=false)
	@NotNull
	private Long existence;*/
	
	@JsonIgnore
	@OneToMany(mappedBy="id.product")
	@Cascade(CascadeType.ALL)
	Set<ProductFlavor> productFlavors;
	
	@Transient
	private List<Flavor> flavors;
	
	@Column(name="ENABLED" , columnDefinition="boolean default true")
	@NotNull
	private Boolean enabled;
	
	
	
	@Override
	public Long getId() {
		return id;
	}

	public Product(Brand brand, BigDecimal price, String name, String description, List<Category> categories,
			ProductType productType, Long existence, Boolean enabled) {
		super();
		this.brand = brand;
		this.price = price;
		this.name = name;
		this.description = description;
		this.categories = categories;
		this.productType = productType;
		//this.existence = existence;
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

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	/*public Long getExistence() {
		return existence;
	}*/

	/*public void setExistence(Long existence) {
		this.existence = existence;
	}*/

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	
    @JsonIgnore
	public Set<ProductFlavor> getProductFlavors() {
		return productFlavors;
	}

	private void setProductFlavors(Set<ProductFlavor> productFlavors) {
		this.productFlavors = productFlavors;
	}
	
	
	@JsonIgnore
	public List<Flavor> getFlavors() {
		return productFlavors.stream().map(pflav->pflav.getFlavor()).collect(Collectors.toList());
	}

	public void setFlavors(List<Flavor> flavors) {
		this.flavors = flavors;
		setProductFlavors(new HashSet(flavors.stream().map(flavor->new ProductFlavor(this,flavor,flavor.getExistence())).collect(Collectors.toList())));
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
