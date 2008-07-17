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
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * An Authority is the "Role"
 * 
 * @author Remco Bos
 */
@Entity
@Table(name = "authorities")
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "authorities")
    private Set<Authentication> authentications = new HashSet<Authentication>();
    
    @ManyToMany(mappedBy = "authorities")
    private Set<Permission> permissions = new HashSet<Permission>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<Authentication> getAuthentications() {
        return authentications;
    }
    public void addAuthentication(Authentication authentication) {
        if (authentication == null) throw new IllegalArgumentException("Null authentication!");
        authentications.add(authentication);
        authentication.getAuthorities().add(this);
    }
    public void removeAuthentication(Authentication authentication) {
        if (authentication == null) throw new IllegalArgumentException("Null authentication!");
        authentications.remove(authentication);
        authentication.getAuthorities().remove(this);
    }
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    public void addPermission(Permission permission) {
        if (permission == null) throw new IllegalArgumentException("Null permission!");
        permissions.add(permission);
        permission.getAuthorities().add(this);
    }
    public void removPermission(Permission permission) {
        if (permission == null) throw new IllegalArgumentException("Null permission!");
        permissions.remove(permission);
        permission.getAuthorities().remove(this);
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final Authority other = (Authority) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
    
    
}
