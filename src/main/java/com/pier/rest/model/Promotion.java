package com.pier.rest.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

@Entity
@Table(name="PROMOTION")
public class Promotion implements ObjectModel<Long>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="DISPLAY_NAME",length=30)
	@Size(min=5,max=30)	
	private String displayName;
	
	@Column(name = "START_DATE")	
	@Type(type="org.hibernate.type.ZonedDateTimeType")
	private ZonedDateTime startDate;
	
	
	@Column(name = "END_DATE")
	@Type(type="org.hibernate.type.ZonedDateTimeType")
	private ZonedDateTime endDate;
	
	@Column(name="DESCRIPTION", columnDefinition = "TEXT")
	@NotNull
	private String description;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private PromotionRule promotionrule;
	
	@Column(name="ALLOW_MULTIPLE")
	private Boolean allowMultiple;
	
	@Column(name="ENABLED")
	private Boolean enabled;
	
	@Column(name="INCLUSIVE", columnDefinition="tinyint(1) default 1")
	private Boolean inclusive;

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

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PromotionRule getPromotionRule() {
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

	public Boolean getInclusive() {
		return inclusive;
	}

	public void setInclusive(Boolean inclusive) {
		this.inclusive = inclusive;
	}
	
	

}
