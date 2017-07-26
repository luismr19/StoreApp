package com.pier.rest.model;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="PRODUCT_FLAVOR")
public class ProductFlavor {
	
	@EmbeddedId
	@JsonIgnore
	private ProductFlavorId id;	
	
	@Column(name="EXISTENCE", length=50, unique=false, nullable=false)
	@NotNull
	private Long existence;

	public ProductFlavor(){
		super();
	}
	
	
	public ProductFlavorId getId() {
		return id;
	}
	
	public ProductFlavor(Product product, Flavor flavor, Long existence){
		id=new ProductFlavorId();
		id.setProduct(product);
		id.setFlavor(flavor);
		
		this.setExistence(existence);
	}

	
	public void setId(ProductFlavorId id) {
		this.id = id;
	}

	public Product getProduct() {
		return id.getProduct();
	}

	public void setProduct(Product product) {
		if(id==null)
		id=new ProductFlavorId();
		
		this.id.setProduct(product);
	}

	public Flavor getFlavor() {
		return id.getFlavor();
	}

	public void setFlavor(Flavor flavor) {
		if(id==null)
		id=new ProductFlavorId();
		this.id.setFlavor(flavor);
	}

	public Long getExistence() {
		return existence;
	}

	public void setExistence(Long existence) {
		this.existence = existence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ProductFlavor other = (ProductFlavor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
