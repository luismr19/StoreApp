package com.pier.rest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Promotion implements ObjectModel<Long>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="DISPLAY_NAME",length=30)
	@Size(min=5,max=30)	
	private String displayName;
	
	@Column(name = "START_DATE", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name = "END_DATE", columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(name="DESCRIPTION", columnDefinition = "TEXT")
	@NotNull
	private String description;
	
	@OneToOne(mappedBy="promotion")
	private PromotionRule promotionrule;
	
	@Column(name="ALLOW_MULTIPLE")
	private Boolean allowMultiple;
	
	@Column(name="ENABLED")
	private Boolean enabled;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PromotionRule getPromotionrule() {
		return promotionrule;
	}

	public void setPromotionrule(PromotionRule promotionrule) {
		this.promotionrule = promotionrule;
	}

	public Boolean getAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(Boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}	
	

}
