package com.finalist.cmsc.services.community.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Users Object, this is a hibernate class.
 * 
 * @author menno menninga
 */
@Entity
@Table(name = "users")
// @SequenceGenerator(name = "SEQ_ID", sequenceName = "\"id_sequence\"")
public class User {

   private static final long serialVersionUID = 1L;
   private Long id;
   private String userId;
   private String password;
   private String firstname;
   private String lastname;
   private String emailAdress;


   /**
    * Get the id.
    * 
    * @return Long representing the id
    */
   // @Id(generate = GeneratorType.SEQUENCE, generator = "SEQ_ID")
   @Id
   @Column(name = "id", nullable = false)
   public Long getId() {
      return id;
   }


   /**
    * Sets the id of type Long.
    * 
    * @param id
    *           the id to set
    */
   public void setId(Long id) {
      this.id = id;
   }


   /**
    * Returns the value user_id of type String
    * 
    * @return Returns the user_id.
    */
   @Column(name = "user_id", nullable = false)
   public String getUserId() {
      System.out.println("Username: " + userId);
      return userId;
   }


   /**
    * Sets user_id of type String
    * 
    * @param firstname
    *           The user_id to set.
    */
   public void setUserId(String userId) {
      this.userId = userId;
   }


   /**
    * Returns the value password of type String
    * 
    * @return Returns the password.
    */
   @Column(name = "password", nullable = false)
   public String getPassword() {
      return password;
   }


   /**
    * Sets user_id of type String
    * 
    * @param firstname
    *           The user_id to set.
    */
   public void setPassword(String password) {
      this.password = password;
   }


   /**
    * Returns the value firstname of type String
    * 
    * @return Returns the firstname.
    */
   @Column(name = "firstname", nullable = false)
   public String getName() {
      System.out.println("Voornaam: " + firstname);
      return firstname;
   }


   /**
    * Sets firstname of type String
    * 
    * @param firstname
    *           The firstname to set.
    */
   public void setName(String firstname) {
      this.firstname = firstname;
   }


   /**
    * Returns the value lastname of type String
    * 
    * @return Returns the lastname.
    */
   @Column(name = "lastname", nullable = false)
   public String getLastname() {
      System.out.println("Achternaam: " + lastname);
      return lastname;
   }


   /**
    * Sets lastname of type String
    * 
    * @param lastname
    *           The name to set.
    */
   public void setLastname(String lastname) {
      this.lastname = lastname;
   }


   /**
    * Returns the value emailadress of type String
    * 
    * @return Returns the emailadress.
    */
   @Column(name = "emailAdress", nullable = true)
   public String getEmailadress() {
      System.out.println("Email-Adres: " + emailAdress);
      return emailAdress;
   }


   /**
    * Sets name of type String
    * 
    * @param name
    *           The name to set.
    */
   public void setEmailadress(String emailAdress) {
      this.emailAdress = emailAdress;
   }
}