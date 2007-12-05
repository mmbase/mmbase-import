package com.finalist.cmsc.services.community.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Users Object, this is a hibernate class.
 * 
 * @author menno menninga
 */
@Entity
@Table(name = "groups")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "id_sequence")
public class Group {

   private static final long serialVersionUID = 1L;
   private Long id;
   private String groupId;


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
   @Column(name = "group_id", nullable = false)
   public String getUserGroupId() {
      return groupId;
   }


   /**
    * Sets user_id of type String
    * 
    * @param firstname
    *           The user_id to set.
    */
   public void setUserGroupId(String groupId) {
      this.groupId = groupId;
   }
}
