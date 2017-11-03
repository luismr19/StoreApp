package com.pier.rest.model;

import java.time.LocalDateTime;
import java.util.Set;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pier.model.security.User;

@Entity
@Table(name = "ARTICLE")
public class Article implements ObjectModel<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "LINK", length = 300, unique = true)	
	@Size(max = 300)
	private String link;

	@Column(name = "TITLE", length = 40)
	@Size(max = 40)
	private String title;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "ARTICLE_TAGS", joinColumns = @JoinColumn(name = "ARTICLE_ID", referencedColumnName = "ID"), 
	inverseJoinColumns = @JoinColumn(name = "TAG_ID", referencedColumnName = "ID"))
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	@Fetch(FetchMode.SELECT)
	@BatchSize(size=30)//I want to load 30 sets of categories for 30 products in one query
	private Set<ArticleTag> tags;

	@ManyToOne
	@JoinColumn(name="AUTOR",referencedColumnName="ID")
	@Fetch(FetchMode.SELECT)
	private User autor;
	
	@Column(name = "WRITE_DATE", columnDefinition = "DATETIME")
	@Type(type = "org.hibernate.type.LocalDateTimeType")
	private LocalDateTime writeDate;
	
	@Column(name="LAST_EDITED",columnDefinition="DATETIME")
	@Type(type="org.hibernate.type.LocalDateTimeType")
	private LocalDateTime lastEdited;
	
	@Column(name="ENABLED",columnDefinition="boolean default false")
	private Boolean enabled;
	
	@Column(name="FEATURED",columnDefinition="boolean default false")
	private Boolean featured;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Set<ArticleTag> getTags() {
		return tags;
	}
	
	public void setTags(Set<ArticleTag> tag) {
		this.tags = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

	public User getAutor() {
		return autor;
	}

	@JsonIgnore
	public void setAutor(User autor) {
		this.autor = autor;
	}
	

	public LocalDateTime getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(LocalDateTime writeDate) {
		this.writeDate = writeDate;
	}

	public LocalDateTime getLastEdited() {
		return lastEdited;
	}

	public void setLastEdited(LocalDateTime lastEdited) {
		this.lastEdited = lastEdited;
	}
	

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}
	
	public Boolean getFeatured(){
		return this.featured;
	}
	
	@JsonProperty("autor")
	public String getAutorName(){
		return this.autor.getFirstname()+" "+autor.getLastname();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((autor == null) ? 0 : autor.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((featured == null) ? 0 : featured.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastEdited == null) ? 0 : lastEdited.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((writeDate == null) ? 0 : writeDate.hashCode());
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
		Article other = (Article) obj;
		if (autor == null) {
			if (other.autor != null)
				return false;
		} else if (!autor.equals(other.autor))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (featured == null) {
			if (other.featured != null)
				return false;
		} else if (!featured.equals(other.featured))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastEdited == null) {
			if (other.lastEdited != null)
				return false;
		} else if (!lastEdited.equals(other.lastEdited))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (writeDate == null) {
			if (other.writeDate != null)
				return false;
		} else if (!writeDate.equals(other.writeDate))
			return false;
		return true;
	}

}
