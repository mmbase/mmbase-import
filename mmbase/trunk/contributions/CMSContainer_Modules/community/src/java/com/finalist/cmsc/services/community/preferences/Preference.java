/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.preferences;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.io.Serializable;

/**
 * @author Remco Bos
 */
@Entity
@Table(name = "preferences")
public class Preference implements Serializable {

   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Long id;

   private String module;

   private Long authenticationId;

   @Column(name = "preferenceKey")
   private String key;

   @Column(name = "preferenceValue")
   private String value;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getModule() {
      return module;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public Long getAuthenticationId() {
      return authenticationId;
   }

   public void setAuthenticationId(Long authenticationId) {
      this.authenticationId = authenticationId;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((module == null) ? 0 : module.hashCode());
      result = prime * result + ((authenticationId == null) ? 0 : authenticationId.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final Preference other = (Preference) obj;
      if (key == null) {
         if (other.key != null) return false;
      } else if (!key.equals(other.key)) return false;
      if (module == null) {
         if (other.module != null) return false;
      } else if (!module.equals(other.module)) return false;
      if (authenticationId == null) {
         if (other.authenticationId != null) return false;
      } else if (!authenticationId.equals(other.authenticationId)) return false;
      if (value == null) {
         if (other.value != null) return false;
      } else if (!value.equals(other.value)) return false;
      return true;
   }
}
