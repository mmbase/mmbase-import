package com.finalist.cmsc.services.community.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratorType;

/**
 * Users Object, this is a hibernate class.
 * 
 * @author menno menninga
 */
@Entity
@Table(name = "user_newsletter_prefs")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "id_sequence")
public class NewsPref {

   private static final long serialVersionUID = 1L;
   private Long id;
   private String userId;
   private String newsletterKey;
   private String newsletterValue;


   /**
    * Get the id.
    * 
    * @return Long representing the id
    */
   @Column(name = "id", nullable = false)
   @Id(generate = GeneratorType.AUTO)
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
    * Returns the value user_group_id of type String
    * 
    * @return Returns the user_group_id.
    */
   @Column(name = "userId", nullable = false)
   public String getUserId() {
      return userId;
   }


   /**
    * Sets newsletterKey of type String
    * 
    * @param newsletterKey
    *           The newsletterKey to set.
    */
   public void setUserId(String userId) {
      this.userId = userId;
   }
   
   @Column(name = "newsletterKey", nullable = false)
   public String getNewsletterKey() {
      return newsletterKey;
   }


   /**
    * Sets newsletterValue of type String
    * 
    * @param newsletterValue
    *           The newsletterValue to set.
    */
   public void setNewsletterKey(String newsletterKey) {
      this.newsletterKey = newsletterKey;
   }
   
   @Column(name = "newsletterValue", nullable = false)
   public String getNewsletterValue() {
      return newsletterValue;
   }


   /**
    * Sets newsletterValue of type String
    * 
    * @param newsletterValue
    *           The newsletterValue to set.
    */
   public void setNewsletterValue(String newsletterValue) {
      this.newsletterValue = newsletterValue;
   }
}
