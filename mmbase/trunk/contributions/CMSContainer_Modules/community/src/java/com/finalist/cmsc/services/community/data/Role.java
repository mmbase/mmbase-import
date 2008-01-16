package com.finalist.cmsc.services.community.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Role Object, this is a hibernate data/POJO class.
 * This class is a mapped database tabel
 * it contains the columns with getters en setters
 * 
 * @author menno menninga
 */
@Entity
@Table(name = "roles")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "id_sequence")
public class Role {

   private static final long serialVersionUID = 1L;
   private Long id;
   private String roleId;


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
    * Returns the value role_id of type String
    * 
    * @return Returns the role_id.
    */
   @Column(name = "role_id", nullable = false)
   public String getRoleId() {
      return roleId;
   }


   /**
    * Sets user_id of type String
    * 
    * @param firstname
    *           The user_id to set.
    */
   public void setRoleId(String role_id) {
      this.roleId = role_id;
   }

}