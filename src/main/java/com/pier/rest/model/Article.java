package com.pier.rest.model;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="ARTICLE")
public class Article implements ObjectModel<Long>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="LINK", length=300, unique=true)
	@NotNull
	@Size(max=300)
	private String link;
	
	@ManyToMany
	@JoinTable(name="ARTICLE_TAGS",joinColumns=@JoinColumn(name="ARTICLE_ID", referencedColumnName="ID"),
	inverseJoinColumns=@JoinColumn(name="TAG_ID", referencedColumnName="ID"))
	@Cascade(CascadeType.SAVE_UPDATE)
	private Set<ArticleTag> tags;

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

	
	
}
