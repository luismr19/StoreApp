package com.pier.rest.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="FLAVOR")
public class Flavor implements ObjectModel<Long> {
	
	@Id
	@Column(name="FLAVOR_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="DESCRIPTION")
	private String flavorName;	
	
	@OneToMany(mappedBy="id.flavor")
	@Cascade(CascadeType.SAVE_UPDATE)
	Set<ProductFlavor> productFlavors;
	
	@Transient
	Long existence;
	
	public Flavor(){
		super();
	}
	
	public Flavor(String flavorName, Long existence) {
		super();
		this.flavorName = flavorName;
		this.existence = existence;
	}


	@Override
	public Long getId() {
		return id;
	}


	public String getFlavorName() {
		return flavorName;
	}


	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
	}


	@JsonIgnore
	public Set<ProductFlavor> getProductFlavors() {
		return productFlavors;
	}


	public void setProductFlavors(Set<ProductFlavor> productFlavors) {
		this.productFlavors = productFlavors;
	}


	public void setId(Long id) {
		this.id = id;
	}

    @JsonIgnore
	public Long getExistence() {
		return existence;
	}
	
	public void setExistence(Long existence){
		this.existence=existence;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flavorName == null) ? 0 : flavorName.hashCode());
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
		Flavor other = (Flavor) obj;
		if (flavorName == null) {
			if (other.flavorName != null)
				return false;
		} else if (!flavorName.equals(other.flavorName))
			return false;
		return true;
	}
	
	

}
