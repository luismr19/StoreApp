package com.pier.model.security;

import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pier.rest.model.Address;
import com.pier.rest.model.PurchaseOrder;
import com.pier.rest.model.ObjectModel;

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
	 
	 @Column(name="LASTPASSWORDRESETSTATE")
	 @Temporal(TemporalType.TIMESTAMP)
	 @NotNull
	 @JsonFormat(pattern="yyyy-MM-dd HH:mm")
	 private Date lastPasswordResetDate;
	 
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
	 
	 @OneToMany(mappedBy="owner")
	 List<PurchaseOrder> orders;
	 
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

	    @JsonIgnore
	    public String getPassword() {
	        return password;
	    }

	    @JsonProperty("password")
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
	    
	    public List<Authority> getAuthorities() {
	    	return authorities;
	    }

	    public void setAuthorities(List<Authority> authorities) {
	        this.authorities = authorities;
	    }

	    public Date getLastPasswordResetDate() {
	        return lastPasswordResetDate;
	    }

	    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
	        this.lastPasswordResetDate = lastPasswordResetDate;
	    }

		 public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}
		

}
