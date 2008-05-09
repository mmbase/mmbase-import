/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * An Authentication is the "Subject"
 * 
 * @author Remco Bos
 */
@Entity
@Table(name = "authentication")
public class Authentication implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /** Authentication id
     *
     */
    @Id
    @GeneratedValue
    private Long id;
    
    /** account used for login
     * 
     */
    @Column(unique = true)
    private String userId;
    
    private String password;
    
    private boolean enabled;
    
    @ManyToMany
    @JoinTable(
        name = "authentication_authorities",
        joinColumns = {@JoinColumn(name = "authentication_id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_id")}
    )
    private Set<Authority> authorities = new HashSet<Authority>();
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Set<Authority> getAuthorities() { 
        return authorities; 
    }
    public void addAuthority(Authority authority) {
        if (authority == null) throw new IllegalArgumentException("Null authority!");
        authorities.add(authority);
        authority.getAuthentications().add(this);
    }
    public void removeAuthority(Authority authority) {
        if (authority == null) throw new IllegalArgumentException("Null authority!");
        authorities.remove(authority);
        authority.getAuthentications().remove(this);
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((userId == null) ? 0 : userId.hashCode());
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
		final Authentication other = (Authentication) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
    
    
}
