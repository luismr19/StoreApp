package com.pier.model.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.pier.rest.model.Address;
import com.pier.rest.model.PurchaseOrder;
import com.pier.rest.model.ObjectModel;
import com.pier.rest.model.Product;

@Entity
@Table(name = "USER")
public class User implements ObjectModel<Long>{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="USERNAME", length=50, unique=true)
	@NotNull
	@Size(min=4, max=20)
	private String username;
	
	@Column(name="PASSWORD", length=100)
	@NotNull
	@Size(min=4,max=100)	
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	 @Column(name = "FIRSTNAME", length = 50)
	 @NotNull
	 @Size(min = 4, max = 50)
	 private String firstname;

	 @Column(name = "LASTNAME", length = 50)
	 @NotNull
	 @Size(min = 4, max = 50)
	 private String lastname;

	 @Column(name = "EMAIL", length = 50)
	 @NotNull
	 @Size(min = 4, max = 50)
	 private String email;

	 @Column(name = "ENABLED")
	 @NotNull
	 private Boolean enabled;
	 
	 @Column(name="LASTPASSWORDRESETSTATE", nullable=false)	 
	 @NotNull	 
	 @JsonFormat(pattern="yyyy-MM-dd HH:mm")
	 @Type(type="org.hibernate.type.LocalDateTimeType")
	 @JsonProperty(access = Access.READ_ONLY)
	 private LocalDateTime lastPasswordResetDate;
	 
	 @Column(name="CREATION_DATE", nullable=false)	 
	 @NotNull	 
	 @JsonFormat(pattern="yyyy-MM-dd HH:mm")
	 @Type(type="org.hibernate.type.LocalDateTimeType")
	 @JsonProperty(access = Access.READ_ONLY)
	 private LocalDateTime createdDate;
	 
	 @JoinColumn(name="ADDRESS",updatable=true)	
	 @OneToOne
	 @Cascade(CascadeType.ALL)
	 @NotNull
	 private Address address;	 

	 @Column(name="PHONE")
	 private Long phoneNumber;
	 
	 @ManyToMany(fetch=FetchType.EAGER)
	 @JoinTable(name="USER_AUTHORITY", joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
	 inverseJoinColumns={@JoinColumn(name="AUTHORITY_ID", referencedColumnName="ID")})
	 private List<Authority> authorities;
	 
	 @OneToMany(mappedBy="owner",fetch=FetchType.LAZY)
	 @Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
	 @BatchSize(size=5)
	 @Fetch(FetchMode.SELECT) //since it is eagerly loaded using "join" expects an existing id
	 Set<PurchaseOrder> orders;
	 
	 @ManyToMany(fetch=FetchType.LAZY)	 
	 @JoinTable(name="USER_FAVORITES",
	        joinColumns = {@JoinColumn(name = "USER_ID")},
	        inverseJoinColumns = {@JoinColumn(name = "PRODUCT_ID")},
	        uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_ID", "PRODUCT_ID"})}
	)
	 @BatchSize(size=5)
	 @Fetch(FetchMode.SELECT)
	 Set<Product> favorites;
	 
	 @Column(name="POINTS")
	 Long points;
	 
	 public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getUsername() {
	        return username;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    
	    public String getPassword() {
	        return password;
	    }	    

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public String getFirstname() {
	        return firstname;
	    }

	    public void setFirstname(String firstname) {
	        this.firstname = firstname;
	    }

	    public String getLastname() {
	        return lastname;
	    }

	    public void setLastname(String lastname) {
	        this.lastname = lastname;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public Boolean getEnabled() {
	        return enabled;
	    }

	    public void setEnabled(Boolean enabled) {
	        this.enabled = enabled;
	    }

	    @JsonIgnore
	    public Collection<GrantedAuthority> getGrantedAuthorities() {
	    	return authorities.stream()
	                .map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
	                .collect(Collectors.toList());
	    }
	    
	    @JsonIgnore
	    public List<Authority> getAuthorities() {
	    	return authorities;
	    }

	    public void setAuthorities(List<Authority> authorities) {
	        this.authorities = authorities;
	    }

	    public LocalDateTime getLastPasswordResetDate() {
	        return lastPasswordResetDate;
	    }
	    
	    public void setLastPasswordResetDate(LocalDateTime lastPasswordResetDate) {
	        this.lastPasswordResetDate = lastPasswordResetDate;
	    }

		 public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public Long getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(Long phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		@JsonIgnore
		public Set<PurchaseOrder> getOrders() {
			return orders;
		}
		
		@JsonIgnore
		public void setOrders(Set<PurchaseOrder> orders) {
			this.orders = orders;
		}

		public Long getPoints() {
			return points;
		}

		public void setPoints(Long points) {
			this.points = points;
		}		
		
		
		public LocalDateTime getCreatedDate() {
			return createdDate;
		}
		
		
		public void setCreatedDate(LocalDateTime createdDate) {
			this.createdDate = createdDate;
		}
		
		@JsonIgnore		
		public Set<Product> getFavorites() {
			return favorites;
		}

		public void setFavorites(Set<Product> favories) {
			this.favorites = favories;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((email == null) ? 0 : email.hashCode());
			result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
			result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
			result = prime * result + ((username == null) ? 0 : username.hashCode());
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
			User other = (User) obj;
			if (email == null) {
				if (other.email != null)
					return false;
			} else if (!email.equals(other.email))
				return false;
			if (firstname == null) {
				if (other.firstname != null)
					return false;
			} else if (!firstname.equals(other.firstname))
				return false;
			if (lastname == null) {
				if (other.lastname != null)
					return false;
			} else if (!lastname.equals(other.lastname))
				return false;
			if (username == null) {
				if (other.username != null)
					return false;
			} else if (!username.equals(other.username))
				return false;
			return true;
		}
		
		
		
		

}
