package com.pier.rest.model;

import java.time.LocalDateTime;
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
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name="promotion")
public class Promotion implements ObjectModel<Long>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="DISPLAY_NAME",length=30)
	@Size(min=5,max=30)	
	private String displayName;
	
	@Column(name = "START_DATE")	
	@Type(type="org.hibernate.type.LocalDateTimeType")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startDate;
	
	
	@Column(name = "END_DATE")
	@Type(type="org.hibernate.type.LocalDateTimeType")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endDate;
	
	@Column(name="DESCRIPTION", columnDefinition = "TEXT")
	@NotNull
	private String description;
	
	@Column(name="VISIBLE")
	private Boolean visible;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private PromotionRule promotionRule;
		
	@Column(name="ENABLED")
	private Boolean enabled;
	
	@Column(name="INCLUSIVE", columnDefinition="tinyint(1) default 0")
	private Boolean inclusive=false;
	
	@Column(name="FEATURED", columnDefinition="tinyint(1) default 0")
	private Boolean featured=false;
	
	@Column(name="REACHED")
	private Long reached=0L;
	
	@Column(name="SPONSORSHIP")
	private String sponsorship;

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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	public LocalDateTime getStartDate() {
		return startDate;
	}

	
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	public LocalDateTime getEndDate() {
		return endDate;
	}

	
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PromotionRule getPromotionRule() {
		return promotionRule;
	}

	public void setPromotionrule(PromotionRule promotionrule) {
		this.promotionRule = promotionrule;
	}	
	
	
	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
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

	public Boolean getFeatured() {
		return featured;
	}

	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}

	public long getReached() {
		return reached;
	}

	public void setReached(Long reached) {
		this.reached = reached;
	}

	public String getSponsorship() {
		return sponsorship;
	}

	public void setSponsorship(String sponsorship) {
		this.sponsorship = sponsorship;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((inclusive == null) ? 0 : inclusive.hashCode());
		result = prime * result + ((promotionRule == null) ? 0 : promotionRule.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		Promotion other = (Promotion) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (inclusive == null) {
			if (other.inclusive != null)
				return false;
		} else if (!inclusive.equals(other.inclusive))
			return false;
		if (promotionRule == null) {
			if (other.promotionRule != null)
				return false;
		} else if (!promotionRule.equals(other.promotionRule))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	

}
