/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
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
 * A Permission
 * 
 * @author Remco Bos
 */
@Entity
@Table(name = "permissions")
public class Permission implements Serializable {

   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Long id;

   @Column(unique = true)
   private String name;

   @ManyToMany
   @JoinTable(name = "permission_authorities", joinColumns = { @JoinColumn(name = "permission_id") }, inverseJoinColumns = { @JoinColumn(name = "authority_id") })
   private Set < Authority > authorities = new HashSet < Authority >();

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

   public Set < Authority > getAuthorities() {
      return authorities;
   }

   public void addAuthority(Authority authority) {
      if (authority == null) throw new IllegalArgumentException("Null authority!");
      authorities.add(authority);
      authority.getPermissions().add(this);
   }

   public void removeAuthority(Authority authority) {
      if (authority == null) throw new IllegalArgumentException("Null authority!");
      authorities.remove(authority);
      authority.getPermissions().remove(this);
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
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final Permission other = (Permission) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      return true;
   }
}
