/*
 * 
 * OSI Certified is a certification mark of the Open Source Initiative. This software is OSI Certified Open Source
 * Software.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.person;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * @author Remco Bos
 */
@Entity
@Table(name = "people")
public class Person {

   @Id
   @GeneratedValue
   private Long id;

   private Long authenticationId; // his/her credentials (usually an e-mail address and password)

   private String firstName;
   private String lastName;
   private String infix;
   private String nickname;
   private String email;
   private String uri;
   private String active;
   private Date registerDate;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Long getAuthenticationId() {
      return authenticationId;
   }

   public void setAuthenticationId(Long authenticationId) {
      this.authenticationId = authenticationId;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getInfix() {
      return infix;
   }

   public void setInfix(String infix) {
      this.infix = infix;
   }

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
      result = prime * result + ((infix == null) ? 0 : infix.hashCode());
      result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final Person other = (Person) obj;
      if (firstName == null) {
         if (other.firstName != null) return false;
      } else if (!firstName.equals(other.firstName)) return false;
      if (infix == null) {
         if (other.infix != null) return false;
      } else if (!infix.equals(other.infix)) return false;
      if (lastName == null) {
         if (other.lastName != null) return false;
      } else if (!lastName.equals(other.lastName)) return false;
      return true;
   }

   public String getFullName() {
      //Return infix too, when user has filled in an infix.
      if (StringUtils.isNotEmpty(this.getInfix())) {
         return this.getFirstName() + " " + this.getInfix() + " " + this.getLastName();
      }
      
      return this.getFirstName() + " " + this.getLastName();
   }

   public String getActive() {
      return active;
   }

   public void setActive(String active) {
      this.active = active;
   }

   public Date getRegisterDate() {
      return registerDate;
   }

   public void setRegisterDate(Date registerDate) {
      this.registerDate = registerDate;
   }
}
