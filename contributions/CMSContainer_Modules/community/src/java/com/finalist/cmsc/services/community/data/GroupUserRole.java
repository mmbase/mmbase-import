package com.finalist.cmsc.services.community.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratorType;
import javax.persistence.Table;

/**
 * GroupUserRole Object, this is a hibernate class.
 * 
 * @author menno menninga
 */
@Entity
@Table(name = "user_groups")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "id_sequence", allocationSize=1)
public class GroupUserRole {

   private static final long serialVersionUID = 1L;
   private Long id;
   private String groupId;
   private String userId;
   private String roleId;
   
   /**
    * Get the id.
    * 
    * @return Long representing the id
    */
   @Id(generate = GeneratorType.AUTO)
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
    * Returns the value groupId of type String
    * 
    * @return Returns the groupId.
    */
   @Column(name = "group_id", nullable = false)
   public String getGroupId() {
      return groupId;
   }


   /**
    * Sets groupId of type String
    * 
    * @param groupId
    *           The groupId to set.
    */
   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   /**
    * Returns the value user_id of type String
    * 
    * @return Returns the user_id.
    */
   @Column(name = "user_id", nullable = false)
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
    * Returns the value roleId of type String
    * 
    * @return Returns the roleId.
    */
   @Column(name = "role_id", nullable = false)
   public String getRoleId() {
      return roleId;
   }


   /**
    * Sets roleId of type String
    * 
    * @param roleId
    *           The roleId to set.
    */
   public void setRoleId(String roleId) {
      this.roleId = roleId;
   }
}
