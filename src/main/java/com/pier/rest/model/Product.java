package com.pier.rest.model;

import java.math.BigDecimal;
import java.util.List;

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
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "PRODUCT")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="name")
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
	
	@Column(name="NAME", length=50, unique=true)
	@NotNull
	@Size(min=4, max=40)
	private String name;
	
	@Column(name="DESCRIPTION", columnDefinition = "TEXT")		
	private String description;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="PRODUCT_CATEGORY", joinColumns={@JoinColumn(name="PRODUCT_ID" ,referencedColumnName="PRODUCT_ID")},
	inverseJoinColumns={@JoinColumn(name="CATEGORY_ID", referencedColumnName="ID")})
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	private List<Category> categories;	
	
	
	@ManyToOne
	@JoinColumn(name="TYPE_ID")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	private ProductType productType;
	
	@Column(name="EXISTENCE", length=50, unique=false, nullable=false)
	@NotNull
	private Long existence;
	
	@Column(name="ENABLED" , columnDefinition="boolean default true")
	@NotNull
	private Boolean enabled;
	
	/*@OneToMany(mappedBy="id.product")
	private List<OrderDetail> purchaseItems;*/
	
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

	public Long getExistence() {
		return existence;
	}

	public void setExistence(Long existence) {
		this.existence = existence;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	
	
	

}
