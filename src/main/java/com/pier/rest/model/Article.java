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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import com.pier.model.security.User;

@Entity
@Table(name = "ARTICLE")
public class Article implements ObjectModel<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "LINK", length = 300, unique = true)
	@NotNull
	@Size(max = 300)
	private String link;

	@Column(name = "TITLE", length = 40)
	@Size(max = 40)
	private String title;

	@ManyToMany
	@JoinTable(name = "ARTICLE_TAGS", joinColumns = @JoinColumn(name = "ARTICLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "TAG_ID", referencedColumnName = "ID"))
	@Cascade(CascadeType.SAVE_UPDATE)
	private Set<ArticleTag> tags;

	@ManyToOne
	@JoinColumn(name="OWNER",referencedColumnName="USER_ID")
	private User autor;
	
	@Column(name = "WRITE_DATE", columnDefinition = "DATETIME")
	@Type(type = "org.hibernate.type.LocalDateTimeType")
	private LocalDateTime writeDate;
	
	@Column(name="LAST_EDITED",columnDefinition="DATETIME")
	@Type(type="org.hibernate.type.LocalDateTimeType")
	private LocalDateTime lastEdited;
	
	@Column(name="ENABLED",columnDefinition="boolean default false")
	private Boolean enabled;
	
	@Column(name="ENABLED",columnDefinition="boolean default false")
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		return true;
	}

}
