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
@Table(name = "module_prefs")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "id_sequence")
public class ModulePref {

   private static final long serialVersionUID = 1L;
   private Long id;
   private String module;
   private String userId;
   private String moduleKey;
   private String moduleValue;


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
    * Returns the value module of type String
    * 
    * @return Returns the module.
    */
   @Column(name = "module", nullable = false)
   public String getModule() {
      return module;
   }
   
   /**
    * Sets the module of type String.
    * 
    * @param module
    *           the module to set
    */
   public void setModule(String module) {
      this.module = module;
   }

   /**
    * Returns the value userId of type String
    * 
    * @return Returns the userId.
    */
   @Column(name = "userId", nullable = false)
   public String getUserId() {
      return userId;
   }


   /**
    * Sets userId of type String
    * 
    * @param userId
    *           The userId to set.
    */
   public void setUserId(String userId) {
      this.userId = userId;
   }
   
   /**
    * Returns the value moduleKey of type String
    * 
    * @return Returns the moduleKey.
    */
   @Column(name = "moduleKey", nullable = false)
   public String getModuleKey() {
      return moduleKey;
   }


   /**
    * Sets moduleKey of type String
    * 
    * @param moduleKey
    *           The moduleKey to set.
    */
   public void setModuleKey(String moduleKey) {
      this.moduleKey = moduleKey;
   }
   
   /**
    * Returns the value moduleValue of type String
    * 
    * @return Returns the moduleValue.
    */
   @Column(name = "moduleValue", nullable = false)
   public String getModuleValue() {
      return moduleValue;
   }


   /**
    * Sets moduleValue of type String
    * 
    * @param moduleValue
    *           The moduleValue to set.
    */
   public void setModuleValue(String moduleValue) {
      this.moduleValue = moduleValue;
   }
}
